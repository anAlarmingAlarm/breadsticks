package com.breadsticksmod.client.models.war.timer;

import com.breadsticksmod.client.features.RevealNicknamesFeature;
import com.breadsticksmod.client.models.territory.TerritoryModel;
import com.breadsticksmod.client.models.territory.events.TerritoryCapturedEvent;
import com.breadsticksmod.client.models.war.Defense;
import com.breadsticksmod.client.models.war.timer.events.TimerStartEvent;
import com.breadsticksmod.client.util.ChatUtil;
import com.breadsticksmod.core.Model;
import com.breadsticksmod.core.http.requests.mapstate.Territory;
import com.breadsticksmod.core.heartbeat.annotations.Schedule;
import com.breadsticksmod.core.time.Duration;
import com.breadsticksmod.core.time.ChronoUnit;
import com.breadsticksmod.core.util.TempMap;
import com.breadsticksmod.core.util.TempSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.wynntils.core.components.Models;
import com.wynntils.core.text.PartStyle;
import com.wynntils.core.text.StyledText;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import com.wynntils.handlers.chat.type.RecipientType;
import com.wynntils.mc.event.InventoryMouseClickedEvent;
import com.wynntils.mc.event.TickEvent;
import com.wynntils.models.territories.TerritoryAttackTimer;
import com.wynntils.utils.mc.McUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimerModel extends Model {
   private final static Pattern ATTACK_PATTERN = Pattern.compile("^\\[WAR] The war for (?<territory>.+) will start in (?<timer>.+).$");
   private final static Pattern DEFENSE_PATTERN = Pattern.compile("^\\[★*(?<queuer>.+)] (?<territory>.+) defense is (?<defense>.+)?$");
   private final static Pattern STEAL_PATTERN = Pattern.compile("^\\[WAR] \\[(?<guild>.+)] captured the territory (?<territory>.+)\\.$");
   private static final Pattern ATTACK_SCREEN_TITLE = Pattern.compile("Attacking: (?<territory>.+)");

   private final Multimap<String, Timer> TIMERS = MultimapBuilder.hashKeys().arrayListValues().build();
   private final Map<String, Defense> KNOWN_DEFENSES = new TempMap<>(10, ChronoUnit.SECONDS);
   private final Map<String, String> KNOWN_QUEUERS = new TempMap<>(10, ChronoUnit.SECONDS);


   private final Set<String> PERSONALLY_QUEUED = new TempSet<>(10, ChronoUnit.SECONDS);


   @Instance
   private static TimerModel THIS;

   private void addTimer(String territory, Duration timeRemaining, boolean trust) {
      if (!trust && getTimer(territory, timeRemaining, String::startsWith).isPresent()) return;

      territory = findBestMatch(territory);
      if (territory == null) return;

      Timer timer = new Timer(territory, timeRemaining, TerritoryModel.getTerritoryList().get(territory).getOwner().toString());
      if (getTimer(timer.getTerritory(), timer.getRemaining()).isPresent()) return;

      if (KNOWN_DEFENSES.containsKey(timer.getTerritory())) {
         timer.defense = KNOWN_DEFENSES.get(timer.getTerritory());
         timer.confident = true;
      } else timer.defense = Defense.get(timer.getTerritory());

      if (KNOWN_QUEUERS.containsKey(timer.getTerritory())) {
         timer.queuer = KNOWN_QUEUERS.get(timer.getTerritory());
      }

      if (PERSONALLY_QUEUED.contains(timer.getTerritory())) timer.personal = true;

      if (new TimerStartEvent(timer, !trust).post()) return;

      TIMERS.put(timer.getTerritory(), timer);
   }

   @SubscribeEvent
   public void onMessage(ChatMessageReceivedEvent event) {
      if (event.getRecipientType() == RecipientType.GUILD) {
         onGuildMessage(event);

         Matcher matcher = event.getOriginalStyledText().getMatcher(ATTACK_PATTERN, PartStyle.StyleType.NONE);
         if (!matcher.matches()) return;

         Duration.parse(matcher.group("timer")).ifPresent(timer -> addTimer(matcher.group("territory"), timer, true));
      } else {
         Matcher matcher = event.getOriginalStyledText().getMatcher(STEAL_PATTERN, PartStyle.StyleType.NONE);
         if (!matcher.matches()) return;
         TIMERS.removeAll(matcher.group("territory"));
      }
   }

   private void onGuildMessage(ChatMessageReceivedEvent event) {
      Matcher matcher = event.getOriginalStyledText().getMatcher(DEFENSE_PATTERN, PartStyle.StyleType.NONE);
      if (!matcher.matches()) return;

      String territory = matcher.group("territory");
      Defense defense = Defense.from(matcher.group("defense"));

      KNOWN_DEFENSES.put(territory, defense);
      KNOWN_QUEUERS.put(territory, getNickname(event.getOriginalStyledText()));

      for (Timer timer : TIMERS.get(territory)) {
         if (Duration.since(timer.getStart()).lessThan(300, ChronoUnit.MILLISECONDS)) {
            if (!timer.confident) {
               timer.defense = defense;
               timer.confident = true;
            }
            if (timer.queuer.isEmpty()) {
               timer.queuer = getNickname(event.getOriginalStyledText());
            }
            break;
         }
      }
   }

   private String getNickname(StyledText msg) {
      Matcher matcher;
      List<StyledText> textList = List.of(msg.getPartsAsTextArray());
      for (StyledText text : textList) {
         var hover = text.getFirstPart().getPartStyle().getStyle().getHoverEvent();
         if (hover != null && hover.getAction() == HoverEvent.Action.SHOW_TEXT && text.getFirstPart().getPartStyle().isItalic()) {
            for (StyledText component : StyledText.fromComponent(hover.getValue(HoverEvent.Action.SHOW_TEXT)).split("\n")) {
               matcher = component.getMatcher(RevealNicknamesFeature.NICK_REGEX, PartStyle.StyleType.NONE);
               if (!matcher.matches()) continue;
               
               return matcher.group("username");
            }
         }
      }
      matcher = msg.getMatcher(DEFENSE_PATTERN, PartStyle.StyleType.NONE);
      if (!matcher.matches()) return "";
      return matcher.group("queuer");
   }

   @SubscribeEvent
   public void onTerritoryCapture(TerritoryCapturedEvent event) {
      TIMERS.values().removeIf(timer -> timer.getTerritory().equals(event.getTerritory()));
   }

   @SubscribeEvent
   public void onInventoryClick(InventoryMouseClickedEvent event) {
      if (event.getHoveredSlot() == null || McUtils.mc().screen == null) return;
      Matcher titleMatcher = ATTACK_SCREEN_TITLE.matcher(McUtils.mc().screen.getTitle().getString());
      if (!titleMatcher.matches()) return;

      PERSONALLY_QUEUED.add(titleMatcher.group("territory"));
   }

   @SubscribeEvent(priority = EventPriority.HIGHEST)
   @SuppressWarnings("ResultOfMethodCallIgnored")
   public void onClear(TickEvent event) {
      TIMERS.values().removeIf(timer -> timer.getRemaining().lessThanOrEqual(100, ChronoUnit.MILLISECONDS) ||
              !timer.getOwner().equals(TerritoryModel.getTerritoryList().get(timer.getTerritory()).getOwner().toString()));
      KNOWN_DEFENSES.size();
      KNOWN_QUEUERS.size();
   }

   private Set<Integer> SCOREBOARD = new HashSet<>();

   @Schedule(rate = 500, unit = ChronoUnit.MILLISECONDS)
   private void HandleScoreboard() {
      Set<Integer> previous = SCOREBOARD;
      SCOREBOARD = new HashSet<>();

      for (TerritoryAttackTimer timer : Models.GuildAttackTimer.getAttackTimers()) {
         SCOREBOARD.add(hash(timer)); if (previous.contains(hash(timer))) continue;
         addTimer(timer.territoryName(), Duration.of(timer.asSeconds(), ChronoUnit.SECONDS), false);
      }
   }

   private static int hash(TerritoryAttackTimer timer) {
      return Objects.hash(
              timer.territoryName(),
              timer.asSeconds()
      );
   }

   private static @Nullable String findBestMatch(String name) {
      Territory.List<?> list = TerritoryModel.getTerritoryList();
      if (list != null && !list.isEmpty()) {
         if (list.contains(name)) return name;

         for (Territory territory : list) {
            if (territory.getName().startsWith(name)) return territory.getName();
         }
      }

      return null;
   }

   public static Collection<Timer> getTimers() {
      return THIS.TIMERS.values();
   }

   private static Optional<Timer> getTimer(String territory, Duration timeRemaining, BiFunction<String, String, Boolean> comparison) {
      for (Timer timer : getTimers()) {
         if (comparison.apply(timer.getTerritory(), territory) && timer.getRemaining().subtract(timeRemaining).abs().lessThanOrEqual(10, ChronoUnit.SECONDS)) {
            return Optional.of(timer);
         }
      }

      return Optional.empty();
   }

   public static Optional<Timer> getTimer(String territory, Duration timeRemaining) {
      return getTimer(territory, timeRemaining, String::equalsIgnoreCase);
   }
}
