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
import me.shedaniel.math.Color;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import static com.wynntils.utils.mc.McUtils.mc;

@Default(State.DISABLED)
@Feature.Definition(name = "Mark Non-Boxed Unidentified Items")
public class MarkUnidentifiedItemsFeature extends Feature {
   @Value("Show text in bottom-right")
   @Tooltip("Show the ? text in the bottom-right corner instead of the center of the item")
   private static boolean cornerText = false;

   @Value("Text Style")
   private static TextShadow style = TextShadow.OUTLINE;

   @Value("Text Color")
   private static Color textColor = ChatUtil.colorOf(ChatFormatting.LIGHT_PURPLE);

   private static final Pattern UNID_PATTERN = Pattern.compile("^\\[Unidentified .+]");

   @SubscribeEvent
   public void onRenderSlot(SlotRenderEvent.Post e) {
      drawTextOverlay(e.getPoseStack(), e.getSlot().getItem(), e.getSlot().x, e.getSlot().y);
   }

   @SubscribeEvent
   public void onRenderHotbarSlot(HotbarSlotRenderEvent.Pre e) {
      drawTextOverlay(e.getPoseStack(), e.getItemStack(), e.getX(), e.getY());
   }

   public void drawTextOverlay(PoseStack poseStack, ItemStack itemStack, int slotX, int slotY) {
      // Check if item is unidentified
      if (!UNID_PATTERN.matcher(ChatUtil.strip(itemStack.getDisplayName())).find()) return;

      // Check if item is boxed (boxes use the Stone Shovel item)
      if (itemStack.getItem().getDescription().getString().equals("Stone Shovel")) {
         // Reliks use the Stone Shovel item too, check to make sure it's not a revealed Relik
         AtomicBoolean quit = new AtomicBoolean(true);
         itemStack.getTooltipLines(mc().player, TooltipFlag.NORMAL).forEach(component -> {
            if (quit.get() && component.getString().contains("Attack Speed")) {
               quit.set(false);
            }
         });
         if (quit.get()) return;
      }

      poseStack.pushPose();
      poseStack.translate(0, 0, 300); // items are drawn at z300, so text has to be as well
      poseStack.scale(1, 1, 1);
      float x = slotX + (cornerText ? 11 : 5);
      float y = slotY + (cornerText ? 9 : 5);
      FontRenderer.getInstance().renderText(poseStack, x, y, new TextRenderTask(
              "?",
              TextRenderSetting.DEFAULT
                      .withTextShadow(style)
                      .withCustomColor(CustomColor.fromInt(textColor.getColor()))));
      poseStack.popPose();
   }
}
