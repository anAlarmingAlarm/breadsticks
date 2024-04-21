package com.breadsticksmod.client.features;

import com.breadsticksmod.client.util.ChatUtil;
import com.breadsticksmod.core.Default;
import com.breadsticksmod.core.Feature;
import com.breadsticksmod.core.State;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wynntils.mc.event.HotbarSlotRenderEvent;
import com.wynntils.mc.event.SlotRenderEvent;
import com.wynntils.utils.colors.CustomColor;
import com.wynntils.utils.render.FontRenderer;
import com.wynntils.utils.render.TextRenderSetting;
import com.wynntils.utils.render.TextRenderTask;
import com.wynntils.utils.render.type.TextShadow;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.regex.Pattern;

@Default(State.ENABLED)
@Feature.Definition(name = "Mark Non-Boxed Unidentified Items")
public class MarkUnidentifiedItemsFeature extends Feature {
   public static final Pattern UNID_PATTERN = Pattern.compile("^\\[Unidentified .+]");

   @SubscribeEvent
   public void onRenderSlot(SlotRenderEvent.Post e) {
      drawTextOverlay(e.getPoseStack(), e.getSlot().getItem(), e.getSlot().x, e.getSlot().y, false);
   }

   @SubscribeEvent
   public void onRenderHotbarSlot(HotbarSlotRenderEvent.Pre e) {
      drawTextOverlay(e.getPoseStack(), e.getItemStack(), e.getX(), e.getY(), true);
   }

   public void drawTextOverlay(PoseStack poseStack, ItemStack itemStack, int slotX, int slotY, boolean hotbar) {
      if (!itemStack.getItem().getDescription().getString().equals("Stone Shovel") && UNID_PATTERN.matcher(ChatUtil.strip(itemStack.getDisplayName())).find()) {
         poseStack.pushPose();
         poseStack.translate(0, 0, 300); // items are drawn at z300, so text has to be as well
         poseStack.scale(1, 1, 1);
         float x = slotX + 5;
         float y = slotY + 5;
         FontRenderer.getInstance().renderText(poseStack, x, y, new TextRenderTask("?", TextRenderSetting.DEFAULT.withTextShadow(TextShadow.OUTLINE).withCustomColor(CustomColor.fromChatFormatting(ChatFormatting.LIGHT_PURPLE))));
         poseStack.popPose();
      }
   }
}
