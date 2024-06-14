package com.breadsticksmod.client.commands;

import com.breadsticksmod.client.commands.subcommands.LootrunCommand;
import com.breadsticksmod.client.features.AutoStreamFeature;
import com.breadsticksmod.client.features.AutoUpdateFeature;
import com.breadsticksmod.client.features.BombBellFeature;
import com.breadsticksmod.client.features.war.WeeklyWarCountOverlay;
import com.breadsticksmod.client.models.territory.TerritoryModel;
import com.breadsticksmod.client.util.ChatUtil;
import com.breadsticksmod.core.http.api.Find;
import com.breadsticksmod.core.http.api.guild.Guild;
import com.breadsticksmod.core.http.api.guild.GuildType;
import com.breadsticksmod.core.http.api.player.Player;
import com.breadsticksmod.core.http.api.player.StoreRank;
import com.breadsticksmod.core.http.requests.mapstate.Territory;
import com.breadsticksmod.core.http.requests.serverlist.ServerList;
import com.breadsticksmod.core.http.requests.serverlist.World;
import com.breadsticksmod.core.text.TextBuilder;
import com.breadsticksmod.core.time.Duration;
import com.breadsticksmod.core.tuples.Pair;
import com.breadsticksmod.core.util.StringUtil;
import com.breadsticksmod.core.util.iterators.Iter;
import com.essentuan.acf.core.annotations.Alias;
import com.essentuan.acf.core.annotations.Argument;
import com.essentuan.acf.core.annotations.Command;
import com.essentuan.acf.core.annotations.Inherit;
import com.essentuan.acf.core.annotations.Subcommand;
import com.essentuan.acf.core.command.arguments.builtin.primitaves.String.StringType;
import com.wynntils.core.components.Handlers;
import com.wynntils.core.components.Models;
import com.wynntils.models.worlds.type.BombType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import com.mojang.brigadier.context.CommandContext;
import com.wynntils.core.components.Managers;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.ClickEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import static com.breadsticksmod.client.BreadsticksMain.CONFIG;
import static com.breadsticksmod.core.time.ChronoUnit.MINUTES;
import static com.breadsticksmod.core.time.ChronoUnit.SECONDS;
import static com.breadsticksmod.core.time.FormatFlag.COMPACT;
import static com.mojang.brigadier.arguments.StringArgumentType.StringType.GREEDY_PHRASE;
import static net.minecraft.ChatFormatting.*;

@Alias("bs")
@Command("breadsticks")
@Inherit({RaidCommand.class, LootrunCommand.class})
public class BreadsticksCommand {
   @Subcommand("config")
   private static void onConfig(CommandContext<FabricClientCommandSource> context) {
      CONFIG.open();
   }

   @Alias("as")
   @Subcommand("autostream")
   private static void onAutoStream(
           CommandContext<FabricClientCommandSource> context
   ) {
      AutoStreamFeature.get().toggle();
   }

   @Alias("om")
   @Subcommand("onlinemembers")
   private static void getOnlineMembers(
           CommandContext<FabricClientCommandSource> context,
           @Argument("Guild") @StringType(GREEDY_PHRASE) String string
   ) {
      ChatUtil.message("Finding guild %s...".formatted(string), GREEN);

      Date start = new Date();
      new Guild.Request(string).thenAccept(optional -> optional.ifPresentOrElse(guild -> {
         List<Guild.Member> online = guild.stream().filter(member -> member.world().isPresent()).toList();

         ChatUtil.message(TextBuilder.of(guild.name(), AQUA).space()
                 .append("[", DARK_AQUA).append(guild.prefix(), AQUA).append("]", DARK_AQUA).space()
                 .append("has ", GRAY)
                 .append(online.size(), AQUA)
                 .append(" of ", GRAY)
                 .append(guild.size(), AQUA)
                 .append(" members online: ", GRAY)
                 .append(online, (member, b) -> b
                                 .append(StringUtil.nCopies("★", member.rank().countStars()) + member.username(), AQUA)
                                 .onPartHover(builder -> builder
                                         .append("Click to switch to ", GRAY)
                                         .append(member.world().orElseThrow(), WHITE)
                                         .line()
                                         .append("(Requires ", DARK_PURPLE)
                                         .append("HERO", LIGHT_PURPLE)
                                         .append(" rank)", DARK_PURPLE))
                                 .onPartClick(ClickEvent.Action.RUN_COMMAND, "/switch " + member.world().orElseThrow()),
                         b -> b.append(", ", AQUA)
                 ));
      }, () -> {
         if (Duration.since(start).greaterThan(10, SECONDS)) {
            ChatUtil.message("Timeout finding guild %s".formatted(string), RED);
         } else ChatUtil.message("Could not find guild %s".formatted(string), RED);
      }));
   }

   @Subcommand("find")
   private static void onFindPlayer(
           CommandContext<FabricClientCommandSource> context,
           @Argument("Player") String string
   ) {
      ChatUtil.message("Finding player %s...".formatted(string), GREEN);

      new Find.Request(string).thenAccept(optional -> optional.ifPresentOrElse(player -> player.getWorld().ifPresentOrElse(world -> ChatUtil.message(TextBuilder.of(player.getUsername(), AQUA)
                      .append(" is on ", GRAY)
                      .append(world, AQUA)), () -> ChatUtil.message(TextBuilder.of(player.getUsername(), AQUA)
                      .append(" is not ", GRAY).append("online", AQUA))),
              () -> ChatUtil.message("Could not find player %s".formatted(string), RED)));
   }

   @Alias("ls")
   @Subcommand("lastseen")
   private static void getLastSeen(
           CommandContext<FabricClientCommandSource> context,
           @Argument("Player") String string
   ) {
      getPlayer(string, player -> {
         Duration lastSeen = Duration.since(player.lastJoin());

         player.world().ifPresentOrElse(world ->
                 ChatUtil.message(TextBuilder.of(player.username(), AQUA)
                         .append(" is on ", GRAY)
                         .append(world)), () -> ChatUtil.message(TextBuilder.of(player.username(), AQUA)
                 .append(" was last seen ", GRAY)
                 .append(lastSeen.getPart(MINUTES) > 1 ? lastSeen.toString(COMPACT, MINUTES) : lastSeen.toString(COMPACT, SECONDS), AQUA)
                 .append(" ago", GRAY)));
      });
   }

   @Alias("sp")
   @Subcommand("soulpoints")
   private static void getSoulPointWorlds(
           CommandContext<FabricClientCommandSource> context
   ) {
      new ServerList.Request().thenApply(Optional::orElseThrow).thenAccept(servers -> {
         TextBuilder builder = TextBuilder.of("Worlds near Sunrise: ", GRAY).line();

         builder.append(Iter.of(
                 servers.stream()
                         .filter(world -> !world.getWorld().isBlank())
                         .map(world -> Pair.of(
                                 world,
                                 Duration.of(20, MINUTES).minus(world.getUptime().mod(20, MINUTES)).minus(90, SECONDS)
                         )).sorted(Comparator.comparing(Pair::two))
                         .limit(10)
                         .iterator()
         ), pair -> builder.append(pair.one().getWorld(), AQUA)
                 .append(" is ", GRAY)
                 .append(pair.two().toString(COMPACT, SECONDS), AQUA)
                 .append(" away from Sunrise", GRAY)
                 .onHover(b -> b
                         .append("Click to switch to ", GRAY)
                         .append(pair.one().getWorld(), WHITE)
                         .line()
                         .append("(Requires ", DARK_PURPLE)
                         .append("HERO", LIGHT_PURPLE)
                         .append(" rank)", DARK_PURPLE))
                 .onClick(ClickEvent.Action.RUN_COMMAND, "/switch " + pair.one().getWorld()));

         Managers.TickScheduler.scheduleNextTick(() -> ChatUtil.message(builder));
      });
   }

   private static final List<String> serverTypes = List.of("any", "recent", "dxp", "dloot", "dungeon", "pxp", "pspd", "profs", "hub");
   @Subcommand("switch")
   private static void serverSwitch(
           CommandContext<FabricClientCommandSource> context,
           @Argument("Types") @StringType(GREEDY_PHRASE) String string
   ) {
      List<String> types = List.of(string.split(" "));
      for (String type : types) {
         if (!serverTypes.contains(type)) {
            TextBuilder builder = TextBuilder.of("Server type " + type + " is not a valid type. Valid types include: ", RED);
            for (int i = 0; i < serverTypes.size(); i++) {
               builder.append(serverTypes.get(i) + (i + 1 != serverTypes.size() ? ", " : ""), RED);
            }
            ChatUtil.message(builder);
            return;
         }
      }
      new ServerList.Request().thenApply(Optional::orElseThrow).thenAccept(servers -> {
         for (String type : types) {
            AtomicBoolean exit = new AtomicBoolean(false);
            List<String> bombServers;
            switch (type) {
               case "any":
                  servers.stream()
                          .filter(world -> !world.getWorld().isBlank() && world.size() < 40)
                          .filter(world -> !Objects.equals(world.getWorld(), Models.WorldState.getCurrentWorldName()))
                          .limit(1)
                          .forEach(world -> {
                             Handlers.Command.queueCommand("switch " + world);
                             exit.set(true);
                          });
                  if (exit.get()) return;
                  ChatUtil.message("Could not find any servers that aren't full", GRAY);
                  break;
               case "recent":
                  servers.stream()
                          .filter(world -> !world.getWorld().isBlank() && world.size() < 40 && world.getUptime().toHours() <= 2)
                          .filter(world -> !Objects.equals(world.getWorld(), Models.WorldState.getCurrentWorldName()))
                          .sorted(Comparator.comparing(World::getUptime))
                          .limit(1)
                          .forEach(world -> {
                             Handlers.Command.queueCommand("switch " + world);
                             exit.set(true);
                          });
                  if (exit.get()) return;
                  ChatUtil.message("Could not find any servers started in the last 2 hours that aren't full", GRAY);
                  break;
               case "dxp":
                  bombServers = new ArrayList<>();
                  BombBellFeature.ACTIVE_BOMBS.stream()
                          .filter(bombInfo -> bombInfo.bomb() == BombType.COMBAT_XP)
                          .forEach(bombInfo -> bombServers.add(bombInfo.server()));
                  servers.stream()
                          .filter(world -> !world.getWorld().isBlank() && world.size() < 40 && bombServers.contains(world.getWorld()))
                          .filter(world -> !Objects.equals(world.getWorld(), Models.WorldState.getCurrentWorldName()))
                          .limit(1)
                          .forEach(world -> {
                             Handlers.Command.queueCommand("switch " + world);
                             exit.set(true);
                          });
                  if (exit.get()) return;
                  ChatUtil.message("Could not find any double xp servers that aren't full", GRAY);
                  break;
               case "dloot":
                  bombServers = new ArrayList<>();
                  BombBellFeature.ACTIVE_BOMBS.stream()
                          .filter(bombInfo -> bombInfo.bomb() == BombType.LOOT)
                          .forEach(bombInfo -> bombServers.add(bombInfo.server()));
                  servers.stream()
                          .filter(world -> !world.getWorld().isBlank() && world.size() < 40 && bombServers.contains(world.getWorld()))
                          .filter(world -> !Objects.equals(world.getWorld(), Models.WorldState.getCurrentWorldName()))
                          .limit(1)
                          .forEach(world -> {
                             Handlers.Command.queueCommand("switch " + world);
                             exit.set(true);
                          });
                  if (exit.get()) return;
                  ChatUtil.message("Could not find any double loot servers that aren't full", GRAY);
                  break;
               case "dungeon":
                  bombServers = new ArrayList<>();
                  BombBellFeature.ACTIVE_BOMBS.stream()
                          .filter(bombInfo -> bombInfo.bomb() == BombType.DUNGEON)
                          .forEach(bombInfo -> bombServers.add(bombInfo.server()));
                  servers.stream()
                          .filter(world -> !world.getWorld().isBlank() && world.size() < 40 && bombServers.contains(world.getWorld()))
                          .filter(world -> !Objects.equals(world.getWorld(), Models.WorldState.getCurrentWorldName()))
                          .limit(1)
                          .forEach(world -> {
                             Handlers.Command.queueCommand("switch " + world);
                             exit.set(true);
                          });
                  if (exit.get()) return;
                  ChatUtil.message("Could not find any free dungeon servers that aren't full", GRAY);
                  break;
               case "pxp":
                  bombServers = new ArrayList<>();
                  BombBellFeature.ACTIVE_BOMBS.stream()
                          .filter(bombInfo -> bombInfo.bomb() == BombType.PROFESSION_XP)
                          .forEach(bombInfo -> bombServers.add(bombInfo.server()));
                  servers.stream()
                          .filter(world -> !world.getWorld().isBlank() && world.size() < 40 && bombServers.contains(world.getWorld()))
                          .filter(world -> !Objects.equals(world.getWorld(), Models.WorldState.getCurrentWorldName()))
                          .limit(1)
                          .forEach(world -> {
                             Handlers.Command.queueCommand("switch " + world);
                             exit.set(true);
                          });
                  if (exit.get()) return;
                  ChatUtil.message("Could not find any double profession xp servers that aren't full", GRAY);
                  break;
               case "pspd":
                  bombServers = new ArrayList<>();
                  BombBellFeature.ACTIVE_BOMBS.stream()
                          .filter(bombInfo -> bombInfo.bomb() == BombType.PROFESSION_SPEED)
                          .forEach(bombInfo -> bombServers.add(bombInfo.server()));
                  servers.stream()
                          .filter(world -> !world.getWorld().isBlank() && world.size() < 40 && bombServers.contains(world.getWorld()))
                          .filter(world -> !Objects.equals(world.getWorld(), Models.WorldState.getCurrentWorldName()))
                          .limit(1)
                          .forEach(world -> {
                             Handlers.Command.queueCommand("switch " + world);
                             exit.set(true);
                          });
                  if (exit.get()) return;
                  ChatUtil.message("Could not find any double profession speed servers that aren't full", GRAY);
                  break;
               case "profs":
                  List<String> pxpServers = new ArrayList<>();
                  bombServers = new ArrayList<>();
                  BombBellFeature.ACTIVE_BOMBS.stream()
                          .filter(bombInfo -> bombInfo.bomb() == BombType.PROFESSION_XP)
                          .filter(world -> !Objects.equals(world.server(), Models.WorldState.getCurrentWorldName()))
                          .forEach(bombInfo -> pxpServers.add(bombInfo.server()));
                  BombBellFeature.ACTIVE_BOMBS.stream()
                          .filter(bombInfo -> bombInfo.bomb() == BombType.PROFESSION_SPEED)
                          .forEach(bombInfo -> {
                             if (pxpServers.contains(bombInfo.server())) {
                                bombServers.add(bombInfo.server());
                             }
                          });
                  if (exit.get()) return;
                  ChatUtil.message("Could not find any double profession xp/speed servers that aren't full", GRAY);
                  break;
               case "hub":
                  Handlers.Command.queueCommand("hub");
                  break;
            }
         }
      });
   }

   @Alias("gi")
   @Subcommand("guildinfo")
   private static void getGuildInfo(
           CommandContext<FabricClientCommandSource> context,
           @Argument("Guild") String string
   ) {
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
      ChatUtil.message("Finding guild %s...".formatted(string), GREEN);

      Date start = new Date();
      new Guild.Request(string).thenAccept(optional -> optional.ifPresentOrElse(guild -> {
         List<Guild.Member> online = guild.stream().filter(member -> member.world().isPresent()).toList();

         ChatUtil.message(TextBuilder.of(guild.name(), AQUA)
                 .append(" [", DARK_AQUA).append(guild.prefix(), AQUA).append("] ", DARK_AQUA)
                 .append("is level ", GRAY).append(guild.level(), AQUA)
                 .append(" and has ", GRAY).append(online.size(), AQUA).append("/", DARK_AQUA).append(guild.size(), AQUA)
                 .append(" members online. It was created on ", GRAY).append(formatter.format(guild.createdAt())).append(".", GRAY));
      }, () -> {
         if (Duration.since(start).greaterThan(10, SECONDS)) {
            ChatUtil.message("Timeout finding guild %s".formatted(string), RED);
         } else ChatUtil.message("Could not find guild %s".formatted(string), RED);
      }));
   }

   @Alias("pg")
   @Subcommand("playerguild")
   private static void getPlayerGuild(
           CommandContext<FabricClientCommandSource> context,
           @Argument("Player") String string
   ) {
      getPlayer(string, player -> {
         if (player.guild().isEmpty()) {
            ChatUtil.message(TextBuilder.of(player.username(), AQUA).append(" is not in a ", GRAY).append("guild", AQUA));
         } else {
            player.guild().map(GuildType::name).map(Guild.Request::new).orElseThrow().thenAccept(optional -> {
               Player.Guild playerGuild = player.guild().orElseThrow();

               TextBuilder builder = TextBuilder.of(player.username(), AQUA)
                       .append(" is a ", GRAY).append(playerGuild.rank().prettyPrint(), AQUA)
                       .append(" in ", GRAY)
                       .append(playerGuild.name(), AQUA)
                       .append(" [", DARK_AQUA)
                       .append(playerGuild.prefix(), AQUA)
                       .append("]", DARK_AQUA);

               optional.ifPresent(guild -> {
                  if (guild.contains(player)) {
                     Guild.Member member = guild.get(player);
                     Duration duration = Duration.since(member.joinedAt());

                     builder.append(". They have been in the guild for ", GRAY)
                             .append(duration.getPart(MINUTES) > 1 ? duration.toString(COMPACT, MINUTES) : duration.toString(COMPACT, SECONDS), AQUA)
                             .append(".", GRAY);
                  }
               });

               ChatUtil.message(builder);
            });
         }
      });
   }

   @Alias("pw")
   @Subcommand("playerwars")
   private static void getPlayerWars(
           CommandContext<FabricClientCommandSource> context,
           @Argument("Player") String string
   ) {
      getPlayer(string, player -> {
         TextBuilder builder = TextBuilder.of(player.username(), AQUA)
                 .append(" has done ", GRAY)
                 .append(player.countWars(), AQUA)
                 .append(" wars total.", GRAY);
         ChatUtil.message(builder);
      });
   }

   @Alias("pi")
   @Subcommand("playerinfo")
   private static void getPlayerInfo(
           CommandContext<FabricClientCommandSource> context,
           @Argument("Player") String string
   ) {
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
      getPlayer(string, player -> {
         TextBuilder builder = TextBuilder.of(player.username(), AQUA)
                 .append(" is a ", GRAY);

         if (player.rank() != Player.Rank.PLAYER && player.storeRank() != StoreRank.REGULAR) {
            builder = builder
                    .append(player.rank().prettyPrint(), AQUA)
                    .append(" (", DARK_AQUA)
                    .append(player.storeRank().prettyPrint(), AQUA)
                    .append( ")", DARK_AQUA);
         } else if (player.rank() != Player.Rank.PLAYER) {
            builder = builder.append(player.rank().prettyPrint(), AQUA);
         } else if (player.storeRank() != StoreRank.REGULAR) {
            builder = builder.append(player.storeRank().prettyPrint(), AQUA);
         } else {
            builder = builder.append("Player", AQUA);
         }

         builder = builder
                 .append(" who has played for ", GRAY)
                 .append(player.playtime().toHours(), AQUA)
                 .append( " hours. They first joined on ", GRAY)
                 .append(formatter.format(player.firstJoin()), AQUA);

         if (player.world().isPresent()) {
            builder = builder.append(" and are currently on ", GRAY)
                    .append(player.world().get(), AQUA);
         } else {
            builder = builder.append(" and last logged on ", GRAY)
                    .append(formatter.format(player.lastJoin()), AQUA);
         }

         if (player.guild().isPresent()) {
            Player.Guild guild = player.guild().get();
            String rank = player.guild().get().rank().toString();
            rank = rank.substring(0, 1).toUpperCase() + rank.substring(1).toLowerCase();
            builder = builder.append(". They are " + (rank.equalsIgnoreCase("owner") ? "the " : "a "), GRAY)
                    .append(rank, AQUA)
                    .append(rank.equalsIgnoreCase("owner") ? " of " : " in ", GRAY)
                    .append(guild.name(), AQUA)
                    .append(" [", DARK_AQUA)
                    .append(guild.prefix(), AQUA)
                    .append("].", DARK_AQUA);
         } else {
            builder = builder.append(". They are not in a guild.", GRAY);
         }

         ChatUtil.message(builder);
      });
   }

   @Alias("tc")
   @Subcommand("terrcheck help")
   private static void terrCheckHelp(
           CommandContext<FabricClientCommandSource> context
   ) {
      ChatUtil.message(TextBuilder.of("The terrcheck command requires a guild prefix (or \"-\" for your own guild) along with a territory code as input.", GRAY));
      TextBuilder builder = TextBuilder.of("You can use this spreadsheet to generate a code.", AQUA, UNDERLINE)
              .onClick(ClickEvent.Action.OPEN_URL, "https://docs.google.com/spreadsheets/d/1zZhAEYI247FDhfx7j72muBX1xRWodsZGpIDG196mHnM/edit?usp=sharing");
      ChatUtil.send(builder);
      ChatUtil.send(TextBuilder.of("You can also use the terrcheckfile (tcf) command to do this automatically based on a file. Terrcheck files can be used from the breadsticks\\terrcheckfiles folder in your config folder; for more info, see the FFA.txt file in that directory.", GRAY));
   }

   @Alias("tc")
   @Subcommand("terrcheck")
   private static void terrCheckHelper(
           CommandContext<FabricClientCommandSource> context,
           @Argument("Prefix") String prefix,
           @Argument("Territories") String string
   ) {
      if (prefix.equals("-")) {
         terrCheck(context, Models.Guild.getGuildName(), string, true);
      } else {
         terrCheck(context, prefix, string, false);
      }
   }

   @Alias("tcf")
   @Subcommand("terrcheckfile")
   private static void terrCheckFile(
           CommandContext<FabricClientCommandSource> context,
           @Argument("Filename") String file
   ) {
      try {
         File tcFile = FabricLoader.getInstance().getConfigDir().resolve("breadsticks\\terrcheckfiles\\" + file + ".txt").toFile();
         Scanner scanner = new Scanner(tcFile);
         String prefix = scanner.nextLine();
         String code = scanner.nextLine();
         scanner.close();
         terrCheckHelper(context, prefix, code);
      } catch (FileNotFoundException e) {
         ChatUtil.message(TextBuilder.of("Could not find file", RED));
      } catch (Exception e) {
         ChatUtil.message(TextBuilder.of("Failed to open file", RED));
      }
   }

   private static void terrCheck(
           CommandContext<FabricClientCommandSource> context,
           String prefix,
           String string,
           boolean byName
   ) {
      if (TerritoryModel.getTerritoryList() == null) {
         ChatUtil.message(TextBuilder.of("Failed to get territories", RED));
         return;
      }
      List<Territory> territories = new ArrayList<>(TerritoryModel.getTerritoryList().stream().toList());
      territories.sort(Comparator.comparing(territory -> territory.getName().toLowerCase()));

      List<Territory> caughtTerrs = new ArrayList<>();
      for (int i = 0; i < string.length(); i++) {
         int num = Integer.parseInt(String.valueOf(string.charAt(i))); // bleh

         // first terr per digit in input code
         if (territories.get(i * 2).getOwner() != null) {
            if (num % 3 == 1) {
               if (!terrCheckPrefix(territories.get(i * 2), prefix, byName)) {
                  caughtTerrs.add(territories.get(i * 2));
               }
            } else if (num % 3 == 2) {
               if (terrCheckPrefix(territories.get(i * 2), prefix, byName)) {
                  caughtTerrs.add(territories.get(i * 2));
               }
            }
         }

         // second terr per digit
         if (territories.get(i * 2 + 1).getOwner() != null && i + 1 != string.length()) {
            if (num / 3 == 1) {
               if (!terrCheckPrefix(territories.get(i * 2 + 1), prefix, byName)) {
                  caughtTerrs.add(territories.get(i * 2 + 1));
               }
            } else if (num / 3 == 2) {
               if (terrCheckPrefix(territories.get(i * 2 + 1), prefix, byName)) {
                  caughtTerrs.add(territories.get(i * 2 + 1));
               }
            }
         }
      }

      TextBuilder builder;
      if (caughtTerrs.isEmpty()) {
         builder = TextBuilder.of("No territories matched the criteria.", GRAY);
         ChatUtil.message(builder);

      } else if (caughtTerrs.size() == 1) {
         builder = TextBuilder.of("Found ", GRAY)
                 .append("1", AQUA)
                 .append(" territory:", GRAY);
         ChatUtil.message(builder);

         boolean highlightTime = caughtTerrs.get(0).getHeldFor().toMinutes() >= 10;
         builder = TextBuilder.of("| ", YELLOW)
                 .append(caughtTerrs.get(0).getName(), AQUA)
                 .append(" is owned by ", GRAY)
                 .append("[", DARK_AQUA)
                 .append(caughtTerrs.get(0).getOwner().prefix(), AQUA)
                 .append("] ", DARK_AQUA)
                 .append("(", highlightTime ? DARK_AQUA : GRAY)
                 .append(caughtTerrs.get(0).getHeldFor().toString(COMPACT, SECONDS), highlightTime ? AQUA : GRAY)
                 .append(")", highlightTime ? DARK_AQUA : GRAY)
                 .line();
         ChatUtil.send(builder);

      } else {
         builder = TextBuilder.of("Found ", GRAY)
                 .append(caughtTerrs.size(), AQUA)
                 .append(" territories:", GRAY);
         ChatUtil.message(builder);

         builder = TextBuilder.empty();
         for (Territory caughtTerr : caughtTerrs) {
            boolean highlightTime = caughtTerr.getHeldFor().toMinutes() >= 10;
            builder.append("| ", YELLOW)
                    .append(caughtTerr.getName(), AQUA)
                    .append(" is owned by ", GRAY)
                    .append("[", DARK_AQUA)
                    .append(caughtTerr.getOwner().prefix(), AQUA)
                    .append("] ", DARK_AQUA)
                    .append("(", highlightTime ? DARK_AQUA : GRAY)
                    .append(caughtTerr.getHeldFor().toString(COMPACT, SECONDS), highlightTime ? AQUA : GRAY)
                    .append(")", highlightTime ? DARK_AQUA : GRAY)
                    .line();
         }
         ChatUtil.send(builder);
      }
   }

   private static boolean terrCheckPrefix(Territory territory, String prefix, boolean byName) {
      if (byName) {
         return territory.getOwner().name().equals(prefix);
      } else {
         return territory.getOwner().prefix().equals(prefix);
      }
   }

   @Alias("tl")
   @Subcommand("terrleaderboard")
   private static void terrLeaderboard(
           CommandContext<FabricClientCommandSource> context
   ) {
      try {
         List<Territory> territories = new ArrayList<>(TerritoryModel.getTerritoryList().stream().toList());
         HashMap<Territory.Owner, Integer> owners = new HashMap<>();
         territories.forEach(territory -> owners.put(territory.getOwner(), owners.getOrDefault(territory.getOwner(), 0) + 1));

         ArrayList<Integer> list = new ArrayList<>();
         LinkedHashMap<Territory.Owner, Integer> sortedMap = new LinkedHashMap<>();
         for (Map.Entry<Territory.Owner, Integer> entry : owners.entrySet()) {
            list.add(entry.getValue());
         }
         list.sort(Collections.reverseOrder());
         for (int num : list) {
            for (Map.Entry<Territory.Owner, Integer> entry : owners.entrySet()) {
               if (entry.getValue().equals(num)) {
                  sortedMap.put(entry.getKey(), num);
               }
            }
         }

         AtomicInteger place = new AtomicInteger(0);
         AtomicInteger total = new AtomicInteger(0);
         sortedMap.values().forEach(total::addAndGet);
         if (sortedMap.values().isEmpty()) {
            ChatUtil.message(TextBuilder.of("There are ", GRAY)
                    .append("0", AQUA)
                    .append(" guilds with ", GRAY)
                    .append(total.get(), AQUA)
                    .append(" territor" + (total.get() == 1 ? "y" : "ies") + " total.", GRAY));
            return;
         } else if (sortedMap.values().size() == 1) {
            ChatUtil.message(TextBuilder.of("There is ", GRAY)
                    .append("1", AQUA)
                    .append(" guild with ", GRAY)
                    .append(total.get(), AQUA)
                    .append(" territor" + (total.get() == 1 ? "y" : "ies") + " total:", GRAY));
         } else {
            ChatUtil.message(TextBuilder.of("There are ", GRAY)
                    .append(sortedMap.values().size(), AQUA)
                    .append(" guilds with ", GRAY)
                    .append(total.get(), AQUA)
                    .append(" territor" + (total.get() == 1 ? "y" : "ies") + " total:", GRAY));
         }
         sortedMap.forEach(((owner, integer) -> {
            TextBuilder builder = TextBuilder.of("| ", YELLOW)
                    .append(place.incrementAndGet(), AQUA)
                    .append(". ", DARK_AQUA)
                    .append(owner.name(), AQUA)
                    .append(" [", DARK_AQUA)
                    .append(owner.prefix(), AQUA)
                    .append("] ", DARK_AQUA)
                    .append("has ", GRAY)
                    .append(integer, AQUA)
                    .append(" territor" + (integer == 1 ? "y " : "ies "), GRAY)
                    .append("(", DARK_AQUA)
                    .append(((float) Math.round((float) integer / total.get() * 100 * 100) / 100) + "%", AQUA)
                    .append(")", DARK_AQUA)
                    .line();
            ChatUtil.send(builder);
         }));
      } catch (NullPointerException e) {
         ChatUtil.message("Could not find territories", RED);
      } catch (Exception e) {
         ChatUtil.message("An error occurred", RED);
      }
   }

   @Alias({"dr", "tr", "totemreset"})
   @Subcommand("dailyreset")
   private static void dailyReset(
           CommandContext<FabricClientCommandSource> context
   ) { // TO-DO: Is this time right for daily objective reset? (11 pm my time)
      Date date = new Date();
      Date resetDate = new Date();
      resetDate.setHours(4 - resetDate.getTimezoneOffset() / 60);
      resetDate.setMinutes(0);
      resetDate.setSeconds(0);
      if (resetDate.before(date)) {
         resetDate.setDate(resetDate.getDate() + 1);
      }
      Duration timeBetween = Duration.of(date, resetDate);
      ChatUtil.message(TextBuilder.of("The next totem/daily objective reset is in ", GRAY)
              .append(timeBetween.toString(COMPACT, SECONDS), AQUA)
              .append(".", GRAY));
   }

   @Alias("wr")
   @Subcommand("weeklyreset")
   private static void weeklyReset(
           CommandContext<FabricClientCommandSource> context
   ) {
      Date date = new Date();
      Date resetDate = new Date();
      resetDate.setHours(3);
      resetDate.setMinutes(59);
      resetDate.setSeconds(30);
      while (resetDate.getDay() != 1 || resetDate.before(date)) {
         resetDate.setDate(resetDate.getDate() + 1);
      }
      resetDate.setHours(resetDate.getHours() - resetDate.getTimezoneOffset() / 60);

      Duration timeBetween = Duration.of(date, resetDate);
      ChatUtil.message(TextBuilder.of("The next weekly objective reset is in ", GRAY)
              .append(timeBetween.toString(COMPACT, SECONDS), AQUA)
              .append(".", GRAY));
   }

   @Alias("up")
   @Subcommand("uptime")
   private static void getServerUptime(
           CommandContext<FabricClientCommandSource> context,
           @Argument("Server") String server
   ) {
      server = server.toUpperCase();
      if (!server.startsWith("WC")) {
         server = "WC" + server;
      }
      String finalServer = server;
      new ServerList.Request().thenApply(Optional::orElseThrow).thenAccept(servers -> {

         TextBuilder builder = TextBuilder.of("World ", GRAY)
                 .append(finalServer, AQUA)
                 .append(" has been online for ", GRAY)
                 .append(servers.get(finalServer).getUptime().toString(COMPACT, SECONDS), AQUA)
                 .append(".", GRAY);

         ChatUtil.message(builder);
      });
   }

   @Alias("upr")
   @Subcommand("uptimerecent")
   private static void getServerUptimeRecent(
           CommandContext<FabricClientCommandSource> context
   ) {
      new ServerList.Request().thenApply(Optional::orElseThrow).thenAccept(servers -> {
         TextBuilder builder = TextBuilder.of("Most recently started servers: ", GRAY).line();

         TextBuilder builder2 = TextBuilder.empty();
         builder2.append(Iter.of(
                 servers.stream()
                         .filter(world -> !world.getWorld().isBlank())
                         .map(world -> Pair.of(
                                 world,
                                 world.getUptime()
                         )).sorted(Comparator.comparing(Pair::two))
                         .limit(9)
                         .iterator()
         ), pair -> builder2.append("| ", YELLOW)
                 .append(pair.one().getWorld(), AQUA)
                 .append(" started ", GRAY)
                 .append(pair.two().toString(COMPACT, SECONDS), AQUA)
                 .append(" ago", GRAY)
                 .onHover(b -> b
                         .append("Click to switch to ", GRAY)
                         .append(pair.one().getWorld(), WHITE)
                         .line()
                         .append("(Requires ", DARK_PURPLE)
                         .append("HERO", LIGHT_PURPLE)
                         .append(" rank)", DARK_PURPLE))
                 .onClick(ClickEvent.Action.RUN_COMMAND, "/switch " + pair.one().getWorld()))
                 .line();
         Managers.TickScheduler.scheduleNextTick(() -> {
            ChatUtil.message(builder);
            Managers.TickScheduler.scheduleNextTick(() -> ChatUtil.send(builder2));
         });
      });
   }

   @Subcommand("update")
   private static void onUpdate(CommandContext<?> context) {
      ChatUtil.message("Attempting update...", YELLOW);

      AutoUpdateFeature.update(false).thenAccept(result -> ChatUtil.message(result.getMessage()));
   }

   @Subcommand("wars")
   private static void getWars(
           CommandContext<FabricClientCommandSource> context,
           @Argument("Since") @StringType(GREEDY_PHRASE) Duration range
   ) {
      long wars = WeeklyWarCountOverlay.getWars()
              .stream()
              .filter(war -> Duration.since(war).lessThanOrEqual(range))
              .count();

      ChatUtil.message(
              TextBuilder.of("You have entered ", GRAY)
                      .append(wars, AQUA)
                      .append(" war", GRAY).appendIf(() -> wars != 1, "s", GRAY)
                      .append(" in the past ", GRAY)
                      .append(range.toString(COMPACT), AQUA).append(".", GRAY)
      );
   }

   private static final Date seasonStartDate = new Date(2024 - 1900, Calendar.APRIL, 26, 19, 0);
   private static final Date seasonEndDate = new Date(2024 - 1900, Calendar.JUNE, 18, 0, 0);
   private static final int season = 18;
   @Alias("swars")
   @Subcommand("seasonwars")
   private static void getSeasonWars(
           CommandContext<FabricClientCommandSource> context
   ) {
      Date date = new Date();
      Date startDate = new Date(seasonStartDate.toString());
      Date endDate = new Date(seasonEndDate.toString());
      startDate.setHours(startDate.getHours() - date.getTimezoneOffset() / 60);
      endDate.setHours(endDate.getHours() - date.getTimezoneOffset() / 60);
      if (startDate.after(date)) {
         ChatUtil.message("Season " + season + " hasn't started yet. If this doesn't seem right, message anAlarmingAlarm.", GRAY);
         return;
      }
      if (endDate.before(date)) {
         ChatUtil.message("Season " + season + " has ended. If there is a new season and you aren't on the latest version, please update by typing /bs update. If you are on the latest version, message anAlarmingAlarm.", GRAY);
         return;
      }
      Duration start = Duration.of(startDate, date);
      Duration end = Duration.of(date, endDate);
      long wars = WeeklyWarCountOverlay.getWars()
              .stream()
              .filter(war -> Duration.since(war).lessThanOrEqual(start))
              .count();

      ChatUtil.message(
              TextBuilder.of("You have entered ", GRAY)
                      .append(wars, AQUA)
                      .append(" war", GRAY).appendIf(() -> wars != 1, "s", GRAY)
                      .append(" in the past ", GRAY)
                      .append(start.toString(COMPACT, SECONDS), AQUA)
                      .append(". The season ends in ", GRAY)
                      .append(end.toString(COMPACT, SECONDS), AQUA)
                      .append(".", GRAY)
      );
   }

   private static void getPlayer(String string, Consumer<Player> consumer) {
      ChatUtil.message("Finding player %s...".formatted(string), GREEN);

      new Player.Request(string).thenAccept(optional -> optional.ifPresentOrElse(consumer, () ->
              ChatUtil.message("Could not find player %s".formatted(string), RED)));
   }
}
