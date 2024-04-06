package com.breadsticksmod.client.features.war;

import com.breadsticksmod.client.models.war.War;
import com.breadsticksmod.client.models.war.events.WarCompleteEvent;
import com.breadsticksmod.client.models.war.events.WarEvent;
import com.breadsticksmod.client.models.war.events.WarLeaveEvent;
import com.breadsticksmod.client.util.ChatUtil;
import com.breadsticksmod.core.Default;
import com.breadsticksmod.core.Feature;
import com.breadsticksmod.core.State;
import com.breadsticksmod.core.config.Config;
import com.breadsticksmod.core.text.TextBuilder;
import com.breadsticksmod.core.time.Duration;
import com.breadsticksmod.core.util.NumUtil;
import com.wynntils.core.text.StyledText;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static net.minecraft.ChatFormatting.AQUA;
import static net.minecraft.ChatFormatting.LIGHT_PURPLE;

@Default(State.ENABLED)
@Config.Category("War")
@Feature.Definition(name = "Tower Stats Feature", override = "Display tower stats after war")
public class TowerStatsFeature extends Feature {
   @SubscribeEvent
   public void onWarComplete(WarCompleteEvent event) {
      onWarOver(event);
   }

   @SubscribeEvent
   public void onWarLeave(WarLeaveEvent event) {
      onWarOver(event);
   }

   private static void onWarOver(WarEvent event) {
      War war = event.getWar();

      String IN_WAR = WarCommon.format(war.getDuration());
      String DPS = NumUtil.format(war.getDPS(Duration.FOREVER));
      StyledText INITIAL_STATS = war.getTower().getInitialStats().toText();

      boolean DID_DAMAGE = !war.getTower().getInitialStats().equals(war.getTower().getStats());

      TextBuilder builder = TextBuilder.of("Time in War: ", LIGHT_PURPLE).next()
              .append(IN_WAR, AQUA)
              .onClick(ClickEvent.Action.COPY_TO_CLIPBOARD, IN_WAR)
              .onHover(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to copy war duration"))
              .line()
              .append("Average DPS: ", LIGHT_PURPLE).next()
              .append(DPS, AQUA)
              .onClick(ClickEvent.Action.COPY_TO_CLIPBOARD, DPS)
              .onHover(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to copy DPS"))
              .line()
              .append((DID_DAMAGE ? "Initial " : "") + "Tower Stats: ", LIGHT_PURPLE).next()
              .append(INITIAL_STATS)
              .onClick(ClickEvent.Action.COPY_TO_CLIPBOARD, INITIAL_STATS.getStringWithoutFormatting())
              .onHover(HoverEvent.Action.SHOW_TEXT, Component.literal( "Click to copy " + (DID_DAMAGE ? "initial " : "") + "tower stats"))
              .line();

      if (DID_DAMAGE) {
         StyledText FINAL_STATS = war.getTower().getStats().toText();

         builder.append("Final Tower Stats: ", LIGHT_PURPLE).next()
                 .append(FINAL_STATS)
                 .onClick(ClickEvent.Action.COPY_TO_CLIPBOARD, FINAL_STATS.getStringWithoutFormatting())
                 .onHover(HoverEvent.Action.SHOW_TEXT, Component.literal("Click to copy final tower stats"));
      }

      ChatUtil.send(builder);
   }
}
