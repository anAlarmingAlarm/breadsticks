package com.breadsticksmod.client.features.war;

import com.breadsticksmod.client.models.territory.TerritoryModel;
import com.breadsticksmod.client.models.war.timer.Timer;
import com.breadsticksmod.client.util.ChatUtil;
import com.breadsticksmod.core.Default;
import com.breadsticksmod.core.Feature;
import com.breadsticksmod.core.State;
import com.breadsticksmod.core.config.Config;
import com.breadsticksmod.core.heartbeat.annotations.Schedule;
import com.breadsticksmod.core.http.api.guild.Guild;
import com.breadsticksmod.core.http.api.guild.GuildType;
import com.breadsticksmod.core.http.api.player.Player;
import com.breadsticksmod.core.text.TextBuilder;
import com.breadsticksmod.core.time.Duration;
import com.wynntils.mc.event.TickEvent;
import com.wynntils.models.worlds.event.WorldStateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.function.Consumer;

import static com.breadsticksmod.core.time.ChronoUnit.*;
import static com.breadsticksmod.core.time.FormatFlag.COMPACT;
import static com.wynntils.utils.mc.McUtils.mc;
import static net.minecraft.ChatFormatting.*;

@Default(State.ENABLED)
@Config.Category("War")
@Feature.Definition(name = "Warn when Attempting to Join Wars while on Cooldown")
public class WarCooldownCheckFeature extends Feature {
   private static boolean sendWarning = false;
   private static double minsUntilOffCd = 0;

   @SubscribeEvent
   public void onTick(TickEvent event) {
      if (sendWarning) {
         if (inTerr()) {
            ChatUtil.send(TextBuilder.of("You are on war cooldown. You will be off cooldown in ", GRAY)
                    .append(Duration.of(minsUntilOffCd, MINUTES).toString(COMPACT, MINUTES), AQUA)
                    .append(".", GRAY));
            sendWarning = false;
         }
      }
   }

   @SubscribeEvent
   public void onJoinWorld(WorldStateEvent event) {
      if (mc().player == null) return;
      getPlayer(mc().player.getName().getString(), player -> {
         if (player.guild().isPresent()) {
            player.guild().map(GuildType::name).map(Guild.Request::new).orElseThrow().thenAccept(optional -> {
               optional.ifPresent(guild -> {
                  if (guild.contains(player)) {
                     Guild.Member member = guild.get(player);
                     minsUntilOffCd = Duration.of(3, DAYS).minus(Duration.since(member.joinedAt()).toDays(), DAYS).toMinutes();
                     if (minsUntilOffCd > 0) {
                        sendWarning = true;
                     }
                  }
               });
            });
         }
      });
   }

   private static boolean inTerr() {
      for (Timer timer : AttackTimerOverlayFeature.ACTIVE_TIMERS) {
         if (TerritoryModel.getCurrentTerritory().isPresent() && TerritoryModel.getCurrentTerritory().get().getName().equals(timer.getTerritory())) {
            return true;
         }
      }
      return false;
   }

   private static void getPlayer(String string, Consumer<Player> consumer) {
      new Player.Request(string).thenAccept(optional -> optional.ifPresentOrElse(consumer, () -> {}));
   }

   @Schedule(rate = 1, unit = MINUTES)
   private void checkForCooldown() {
      if (sendWarning) {
         if (--minsUntilOffCd <= 0) {
            ChatUtil.send(TextBuilder.of("You are no longer on war cooldown!", GRAY));
            sendWarning = false;
         }
      }
   }
}
