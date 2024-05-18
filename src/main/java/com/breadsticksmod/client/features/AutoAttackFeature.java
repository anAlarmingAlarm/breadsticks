package com.breadsticksmod.client.features;

import com.breadsticksmod.client.keybinds.AutoAttackKeybind;
import com.breadsticksmod.core.Default;
import com.breadsticksmod.core.Feature;
import com.breadsticksmod.core.State;
import com.breadsticksmod.core.heartbeat.annotations.Schedule;
import com.breadsticksmod.core.time.ChronoUnit;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import java.util.List;

import static com.wynntils.utils.mc.McUtils.mc;

@Default(State.DISABLED)
@Feature.Definition(name = "Auto Attack", description = "Automatically attack when holding the mouse button")
public class AutoAttackFeature extends Feature {
   @Value("Toggle")
   @Tooltip("Makes the keybind a toggle instead of requiring being pressed continuously to activate")
   private boolean toggle = false;
   @Schedule(rate = 50, unit = ChronoUnit.MILLISECONDS)
   private void onUpdate() {
      if (toggle) {
          if (AutoAttackKeybind.getClickNum() % 2 == 1) {
              attack();
          }
      } else {
          if (AutoAttackKeybind.isKeyDown()) {
              attack();
          }
      }
   }

   private void attack() {
       if (mc().player == null) return;

       boolean bowHeld = false;
       ItemStack heldItem = mc().player.getMainHandItem();
       if (heldItem.toString().contains("bow")) {
           bowHeld = true;
       } else if (heldItem.toString().contains("diamond_shovel")) {
           List<Component> lines = heldItem.getTooltipLines(mc().player, TooltipFlag.NORMAL);
           for (Component line : lines) {
               if (line.getString().contains("Class Req:")) {
                   bowHeld = line.getString().contains("Class Req: Archer/Hunter");
                   break;
               }
           }
       }

       KeyMapping.click(bowHeld ? mc().options.keyUse.key : mc().options.keyAttack.key);
   }
}
