package com.breadsticksmod.client.features.war;

import com.breadsticksmod.client.config.providers.text.ColorProvider;
import com.breadsticksmod.client.models.territory.TerritoryModel;
import com.breadsticksmod.client.models.war.Cooldown;
import com.breadsticksmod.client.models.war.timer.TimerModel;
import com.breadsticksmod.core.Default;
import com.breadsticksmod.core.Feature;
import com.breadsticksmod.core.State;
import com.breadsticksmod.core.config.Config;
import com.breadsticksmod.core.heartbeat.annotations.Schedule;
import com.breadsticksmod.core.render.overlay.Hud;
import com.breadsticksmod.core.time.ChronoUnit;
import com.breadsticksmod.core.time.Duration;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wynntils.core.consumers.overlays.OverlayPosition;
import com.wynntils.core.text.PartStyle;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import com.wynntils.utils.render.type.HorizontalAlignment;
import com.wynntils.utils.render.type.TextShadow;
import com.wynntils.utils.render.type.VerticalAlignment;
import me.shedaniel.math.Color;
import net.minecraft.ChatFormatting;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.minecraft.ChatFormatting.*;

@Default(State.DISABLED)
@Config.Category("War")
@Feature.Definition(name = "Cooldown Tracker Overlay", description = "Tracks cooldowns of terrs you have attempted to queue")
public class TerrCooldownOverlayFeature extends Feature {
   @Hud
   private static CooldownOverlay HUD;

   @Value("Seconds to keep cooldowns that have ended")
   @Tooltip("""
           The number of seconds that cooldowns are kept for once they're over.
           Set to 0 to remove them immediately, or -1 or lower to not remove them after any time.
           Timers will be removed regardless of this value if they are queued or if any guild takes the corresponding territory
           """)
   private static int keepCooldowns = 10;

   @Dropdown(title = "Cooldown Color", options = ColorProvider.class)
   private static ChatFormatting timerColor = RED;

   @Dropdown(title = "Cooldown Color Below 30s", options = ColorProvider.class)
   private static ChatFormatting timerColor30s = YELLOW;

   @Dropdown(title = "Timer Color At 0s", options = ColorProvider.class)
   private static ChatFormatting timerColorExpired = GREEN;


   @Value("Text Style")
   private static TextShadow style = TextShadow.OUTLINE;

   @Alpha
   @Value("Background Color")
   private static Color background_color = Color.ofRGBA(0, 0, 0, 127);

   private final static Pattern PATTERN = Pattern.compile("^This territory is in cooldown! Please wait ((?<minutes>\\d+) minutes? )?(and )?((?<seconds>\\d+) seconds? )?before attacking.$");

   public static List<Cooldown> ACTIVE_COOLDOWNS = new ArrayList<>();

   @Schedule(rate = 3, unit = ChronoUnit.SECONDS)
   private void CooldownCheck() {
      ACTIVE_COOLDOWNS.removeIf(cooldown ->
              (keepCooldowns > -1 && cooldown.getRemaining().toSeconds() <= keepCooldowns * -1) ||
            !cooldown.getOwner().equals(TerritoryModel.getTerritoryList().get(cooldown.getTerritory()).getOwner().toString()) ||
            TimerModel.getTimers().stream().anyMatch(timer -> timer.getTerritory().equals(cooldown.getTerritory()))
      );
   }

   @SubscribeEvent
   public void onChatMessage(ChatMessageReceivedEvent event) {
      Matcher matcher = event.getStyledText().getMatcher(PATTERN, PartStyle.StyleType.NONE);
      if (matcher.matches()) {
         int minutes = (matcher.group("minutes") == null) ? 0 : Integer.parseInt(matcher.group("minutes"));
         int seconds = (matcher.group("seconds") == null) ? 0 : Integer.parseInt(matcher.group("seconds"));
         Duration duration = Duration.of(minutes * 60 + seconds, ChronoUnit.SECONDS);

         if (duration.equals(0, ChronoUnit.SECONDS) || TerritoryModel.getCurrentTerritory().isEmpty()) return;

         ACTIVE_COOLDOWNS.removeIf(cooldown -> cooldown.getTerritory().equals(TerritoryModel.getCurrentTerritory().get().getName()));

         ACTIVE_COOLDOWNS.add(new Cooldown(
                 TerritoryModel.getCurrentTerritory().get().getName(),
                 duration,
                 TerritoryModel.getCurrentTerritory().get().getOwner().toString()
               )
         );
         ACTIVE_COOLDOWNS.sort(Cooldown::compareTo);
      }
   }

   @Hud.Name("Attack Timer Overlay")
   @Hud.Offset(x = 0F, y = -20F)
   @Hud.Size(width = 270F, height = 25.926636F)
   @Hud.Anchor(OverlayPosition.AnchorSection.TOP_RIGHT)
   @Hud.Align(vertical = VerticalAlignment.TOP, horizontal = HorizontalAlignment.RIGHT)
   private static class CooldownOverlay extends Hud.Element {
      @Override
      protected void onRender(float x, float y, float width, float height, PoseStack poseStack, float partialTicks, Window window) {
         render(ACTIVE_COOLDOWNS, x, y);
      }

      @Override
      protected void onRenderPreview(float x, float y, float width, float height, PoseStack poseStack, float partialTicks, Window window) {
         render(List.of(
                 new Cooldown("Light Forest Canyon", Duration.of(2, ChronoUnit.MINUTES).add(12, ChronoUnit.SECONDS), "Preview"),
                 new Cooldown("Light Forest East Mid", Duration.of(7, ChronoUnit.MINUTES).add(3, ChronoUnit.SECONDS), "Preview"),
                 new Cooldown("Light Forest West Mid", Duration.of(5, ChronoUnit.MINUTES), "Preview"),
                 new Cooldown("Light Forest East Lower", Duration.of(36, ChronoUnit.SECONDS), "Preview"),
                 new Cooldown("Heavenly Ingress", Duration.of(3, ChronoUnit.MINUTES).add(13, ChronoUnit.SECONDS), "Preview")
         ), x, y);
      }

      private void render(List<Cooldown> cooldowns, float x, float y) {
         if (cooldowns.isEmpty()) return;

         new TextBox(builder -> builder.append(cooldowns, cooldown -> builder
                 .append((cooldown.getTerritory() + ": " + format(cooldown.getRemaining())), getColor(cooldown)))
                 , x, y).setTextStyle(style)
                 .setFill(background_color)
                 .with(this)
                 .setPadding(5, 5, 5, 5)
                 .dynamic()
                 .build();
      }

      private static ChatFormatting[] getColor(Cooldown cooldown) {
         ChatFormatting color;
         if (cooldown.getRemaining().toSeconds() <= 0) {
            color = timerColor;
         } else if (cooldown.getRemaining().toSeconds() <= 30) {
            color = timerColor30s;
         } else {
            color = timerColorExpired;
         }

         if (TerritoryModel.getCurrentTerritory().isPresent() && TerritoryModel.getCurrentTerritory().get().getName().equals(cooldown.getTerritory())) {
            return new ChatFormatting[] {color, BOLD};
         } else {
            return new ChatFormatting[] {color};
         }
      }

      private static String format(Duration duration) {
         int minutes = (int) Math.max(duration.getPart(ChronoUnit.MINUTES), 0);
         int seconds = (int) Math.max(duration.getPart(ChronoUnit.SECONDS), 0);

         return ((minutes < 10) ? "0" + minutes : Integer.toString(minutes)) + ":" + ((seconds < 10) ? "0" + seconds : Integer.toString(seconds));
      }
   }
}
