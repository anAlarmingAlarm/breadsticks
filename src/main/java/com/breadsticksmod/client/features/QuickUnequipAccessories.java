package com.breadsticksmod.client.features;

import com.breadsticksmod.client.features.keybinds.Keybind;
import com.breadsticksmod.core.Default;
import com.breadsticksmod.core.Feature;
import com.breadsticksmod.core.State;
import com.breadsticksmod.core.heartbeat.Heartbeat;
import com.breadsticksmod.core.time.ChronoUnit;
import com.mojang.blaze3d.platform.InputConstants;
import com.wynntils.mc.event.ContainerClickEvent;
import com.wynntils.utils.wynn.ContainerUtils;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.wynntils.utils.mc.McUtils.mc;

@Default(State.DISABLED)
@Feature.Definition(name = "Shift-Click to Unequip Accessories")
public class QuickUnequipAccessories extends Feature {
   @SubscribeEvent
   public void onClick(ContainerClickEvent event) {
      int offset;
      if (event.getContainerMenu().slots.size() == 46)
         offset = 0;
      else if (event.getContainerMenu().slots.size() == 63)
         offset = 18;
      else if (event.getContainerMenu().slots.size() == 90)
         offset = 45;
      else
         return;

      if (event.getSlotNum() >= (9 + offset) && event.getSlotNum() <= (12 + offset)
              && event.getMouseButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT
              && Keybind.isKeyDown(InputConstants.KEY_LSHIFT)
              && mc().player != null) {
         List<ItemStack> items = mc().player.inventoryMenu.getItems();

         AtomicInteger index = new AtomicInteger(-1);
         for (int i = 14 + offset; i <= 41 + offset; i++) {
            if (items.get(i).isEmpty()) {
               index.set(i);
               break;
            }
         }
         if (index.get() == -1) return;

         event.setCanceled(true);
         ContainerUtils.clickOnSlot(event.getSlotNum(), mc().player.inventoryMenu.containerId, GLFW.GLFW_MOUSE_BUTTON_LEFT, items);
         mc().player.inventoryMenu.clicked(event.getSlotNum(), GLFW.GLFW_MOUSE_BUTTON_LEFT, ClickType.PICKUP, mc().player);
         Heartbeat.schedule(() -> {
            mc().player.inventoryMenu.clicked(index.get(), GLFW.GLFW_MOUSE_BUTTON_LEFT, ClickType.PICKUP, mc().player);
            ContainerUtils.clickOnSlot(index.get(), mc().player.inventoryMenu.containerId, GLFW.GLFW_MOUSE_BUTTON_LEFT, mc().player.inventoryMenu.getItems());
         }, 120, ChronoUnit.MILLISECONDS);
      }
   }
}
