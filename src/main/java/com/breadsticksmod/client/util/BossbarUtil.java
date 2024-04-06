package com.breadsticksmod.client.util;

import com.breadsticksmod.client.mixin.BossHealthOverlayAccessor;
import net.minecraft.client.gui.components.LerpingBossEvent;

import java.util.Collection;

import static com.wynntils.utils.mc.McUtils.mc;

public class BossbarUtil {
   public static Collection<LerpingBossEvent> getBars() {
      return ((BossHealthOverlayAccessor) mc().gui.getBossOverlay()).getEvents().values();
   }

   public static boolean contains(String string) {
      for (LerpingBossEvent bossbar : getBars()) {
         if (bossbar.getName().getString().contains(string)) return true;
      }

      return false;
   }
}
