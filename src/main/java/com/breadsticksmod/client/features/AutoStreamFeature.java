package com.breadsticksmod.client.features;

import com.breadsticksmod.client.features.keybinds.Keybind;
import com.breadsticksmod.client.util.BossbarUtil;
import com.breadsticksmod.core.Default;
import com.breadsticksmod.core.Feature;
import com.breadsticksmod.core.State;
import com.breadsticksmod.core.time.Duration;
import com.breadsticksmod.core.time.ChronoUnit;
import com.mojang.blaze3d.platform.InputConstants;
import com.wynntils.core.components.Models;
import com.wynntils.mc.event.TickEvent;
import com.wynntils.models.worlds.type.WorldState;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Date;

import static com.wynntils.utils.mc.McUtils.mc;

@Default(State.DISABLED)
@Feature.Definition(name = "Auto Stream", description = "Puts player in streamer mode.")
public class AutoStreamFeature extends Feature {
   @Instance
   private static AutoStreamFeature THIS;

   private static boolean wasEnabled = false;

   private static Date lastCommand = new Date(0);

   @Value("Use Keybind")
   @Tooltip("Use the keybind (RShift + RCtrl + Tab)?")
   private boolean keybind = true;

   private void tryToggle() {
      if (((isEnabled() && !BossbarUtil.contains("Streamer mode enabled"))
              || (wasEnabled && !isEnabled() && BossbarUtil.contains("Streamer mode enabled")))
              && mc().getConnection() != null
              && Models.WorldState.getCurrentState() == WorldState.WORLD
              && !inHuntedMode()) {
         mc().getConnection().sendCommand("stream");
         lastCommand = new Date();
      }
      wasEnabled = isEnabled();
   }

   private static boolean lastInput = false;

   @SubscribeEvent
   private static void onTick(TickEvent event) {
      if ((THIS.isEnabled() || wasEnabled) && Duration.since(lastCommand).greaterThanOrEqual(400, ChronoUnit.MILLISECONDS)) {
         THIS.tryToggle();
      }

      if (get().keybind && Keybind.isKeyDown(InputConstants.KEY_RSHIFT) && Keybind.isKeyDown(InputConstants.KEY_RCONTROL)) {
         boolean isTabPressed = Keybind.isKeyDown(InputConstants.KEY_TAB);

         if (isTabPressed && !lastInput) {
            get().toggle();
         }

         lastInput = isTabPressed;
      }
   }

   private static boolean inHuntedMode() {
      if (mc().player != null) {
         String itemName = mc().player.getInventory().getItem(9).toString();
         return itemName.contains("snow");
      }
      return false;
   }

   public static AutoStreamFeature get() {
      return THIS;
   }
}
