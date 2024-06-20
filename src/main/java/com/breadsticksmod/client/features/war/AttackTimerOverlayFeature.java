package com.breadsticksmod.client.features.war;

import com.breadsticksmod.client.config.providers.text.ColorProvider;
import com.breadsticksmod.client.models.war.Defense;
import com.breadsticksmod.client.models.territory.TerritoryModel;
import com.breadsticksmod.client.models.war.timer.Timer;
import com.breadsticksmod.client.models.war.timer.TimerModel;
import com.breadsticksmod.core.Default;
import com.breadsticksmod.core.Feature;
import com.breadsticksmod.core.State;
import com.breadsticksmod.core.config.Config;
import com.breadsticksmod.core.render.overlay.Hud;
import com.breadsticksmod.core.time.Duration;
import com.breadsticksmod.core.time.ChronoUnit;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wynntils.core.consumers.overlays.OverlayPosition;
import com.wynntils.handlers.scoreboard.event.ScoreboardSegmentAdditionEvent;
import com.wynntils.mc.event.TickEvent;
import com.wynntils.models.territories.GuildAttackScoreboardPart;
import com.wynntils.utils.render.type.HorizontalAlignment;
import com.wynntils.utils.render.type.TextShadow;
import com.wynntils.utils.render.type.VerticalAlignment;
import me.shedaniel.math.Color;
import net.minecraft.ChatFormatting;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.List;

import static com.wynntils.utils.mc.McUtils.mc;
import static net.minecraft.ChatFormatting.*;

@Default(State.ENABLED)
@Config.Category("War")
@Feature.Definition(name = "Attack Timer Overlay", description = "Shows all active timers")
public class AttackTimerOverlayFeature extends Feature {
   @Hud
   private static TimerOverlay HUD;

   @Value("Hide timers on Scoreboard")
   @Tooltip("Hides the regular timers on the scoreboard")
   private static boolean hideTimers = true;

   @Value("Show queuer names when holding Tab")
   @Tooltip("Shows the username of the person who queued a territory beside its timer while holding Tab")
   private static boolean showNames = false;

   @Value("Text Style")
   private static TextShadow style = TextShadow.OUTLINE;

   @Dropdown(title = "Text Color", options = ColorProvider.class)
   private static ChatFormatting textColor = GRAY;

   @Dropdown(title = "Timer Color", options = ColorProvider.class)
   private static ChatFormatting timerColor = GRAY;

   @Dropdown(title = "Timer Color Below 30s", options = ColorProvider.class)
   private static ChatFormatting timerColor30s = RED;

   @Dropdown(title = "Timer Color while in Territory", options = ColorProvider.class)
   private static ChatFormatting timerColorTerr = LIGHT_PURPLE;

   @Dropdown(title = "Timer Length Color", options = ColorProvider.class)
   private static ChatFormatting timerLengthColor = AQUA;

   @Dropdown(title = "Queuer Name Color", options = ColorProvider.class)
   private static ChatFormatting queuerColor = WHITE;

   @Alpha
   @Value("Background Color")
   private static Color background_color = Color.ofRGBA(0, 0, 0, 127);

   public static List<Timer> ACTIVE_TIMERS = List.of();

   @SubscribeEvent()
   public void onScoreboardSegmentChange(ScoreboardSegmentAdditionEvent event) {
      if (hideTimers && event.getSegment().getScoreboardPart() instanceof GuildAttackScoreboardPart) event.setCanceled(true);
   }

   @Hud.Name("Attack Timer Overlay")
   @Hud.Offset(x = 0F, y = 60F)
   @Hud.Size(width = 270F, height = 25.926636F)
   @Hud.Anchor(OverlayPosition.AnchorSection.TOP_RIGHT)
   @Hud.Align(vertical = VerticalAlignment.TOP, horizontal = HorizontalAlignment.RIGHT)
   private static class TimerOverlay extends Hud.Element {
      @Override
      protected void onRender(float x, float y, float width, float height, PoseStack poseStack, float partialTicks, Window window) {
         render(ACTIVE_TIMERS, x, y);
      }

      @Override
      protected void onRenderPreview(float x, float y, float width, float height, PoseStack poseStack, float partialTicks, Window window) {
         render(List.of(
                 new Timer("Light Forest Canyon", Duration.of(10, ChronoUnit.MINUTES).add(12, ChronoUnit.SECONDS), "Preview", Defense.VERY_LOW),
                 new Timer("Mine Base Plains", Duration.of(5, ChronoUnit.MINUTES).add(40, ChronoUnit.SECONDS), "Preview", Defense.VERY_HIGH),
                 new Timer("Almuj City", Duration.of(5, ChronoUnit.MINUTES).add(13, ChronoUnit.SECONDS), "Preview", Defense.HIGH),
                 new Timer("Detlas", Duration.of(3, ChronoUnit.MINUTES).add(25, ChronoUnit.SECONDS), "Preview", Defense.HIGH),
                 new Timer("Detlas Savannah Transition", Duration.of(4, ChronoUnit.MINUTES).add(34, ChronoUnit.SECONDS), "Preview", Defense.LOW),
                 new Timer("Plains", Duration.of(0, ChronoUnit.MINUTES).add(11, ChronoUnit.SECONDS), "Preview", Defense.MEDIUM)
         ), x, y);
      }

      private void render(List<Timer> timers, float x, float y) {
         if (timers.isEmpty()) return;

         if (showNames && mc().options.keyPlayerList.isDown) {
            new TextBox(builder -> builder.append(timers, timer -> builder
                    .append(((timer.queuer.isEmpty()) ? "Unknown" : timer.queuer), ((timer.queuer.isEmpty()) ? new ChatFormatting[] {queuerColor, ITALIC} : new ChatFormatting[]{queuerColor}))
                    .append(" - ", RESET, textColor)
                    .append(timer.getTerritory(), getColor(timer))
                    .append(" (", RESET, textColor)
                    .append(timer.getDefense().toText(timer.isConfident()))
                    .append("): ", RESET, textColor)
                    .append(format(timer.getRemaining()), RESET, timerLengthColor))
                    , x, y).setTextStyle(style)
                    .setFill(background_color)
                    .with(this)
                    .setPadding(5, 5, 5, 5)
                    .dynamic()
                    .build();
         } else {
            new TextBox(builder -> builder.append(timers, timer -> builder
                    .append(timer.getTerritory(), getColor(timer))
                    .append(" (", RESET, textColor)
                    .append(timer.getDefense().toText(timer.isConfident()))
                    .append("): ", RESET, textColor)
                    .append(format(timer.getRemaining()), RESET, timerLengthColor))
                    , x, y).setTextStyle(style)
                    .setFill(background_color)
                    .with(this)
                    .setPadding(5, 5, 5, 5)
                    .dynamic()
                    .build();
         }
      }

      private static ChatFormatting[] getColor(Timer timer) {
         if (TerritoryModel.getCurrentTerritory().isPresent() && TerritoryModel.getCurrentTerritory().get().getName().equals(timer.getTerritory())) {
            return new ChatFormatting[] {timerColorTerr, BOLD};
         } else if (timer.getRemaining().toSeconds() <= 30) {
            return new ChatFormatting[] {timerColor30s};
         } else {
            return new ChatFormatting[] {timerColor};
         }
      }

      private static String format(Duration duration) {
         int minutes = (int) duration.getPart(ChronoUnit.MINUTES);
         int seconds = (int) duration.getPart(ChronoUnit.SECONDS);

         return ((minutes < 10) ? "0" + minutes : Integer.toString(minutes)) + ":" + ((seconds < 10) ? "0" + seconds : Integer.toString(seconds));
      }
   }

   @SubscribeEvent
   private static void onTick(TickEvent event) {
      ACTIVE_TIMERS = TimerModel.getTimers()
              .stream()
              .sorted()
              .toList();
   }
}
