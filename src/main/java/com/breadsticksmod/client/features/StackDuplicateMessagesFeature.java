package com.breadsticksmod.client.features;

import com.breadsticksmod.client.events.mc.chat.MessageAddEvent;
import com.breadsticksmod.client.util.PlayerUtil;
import com.breadsticksmod.core.Default;
import com.breadsticksmod.core.Feature;
import com.breadsticksmod.core.State;
import com.breadsticksmod.core.heartbeat.annotations.Schedule;
import com.breadsticksmod.core.text.TextBuilder;
import com.breadsticksmod.core.time.ChronoUnit;
import com.wynntils.core.text.PartStyle;
import com.wynntils.core.text.StyledText;
import com.wynntils.utils.type.IterationDecision;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Default(State.ENABLED)
@Feature.Definition(name = "Stack Duplicate Messages")
public class StackDuplicateMessagesFeature extends Feature {
   @Value("Ignore Timestamps")
   @Default(State.DISABLED)
   @Tooltip("If enabled, timestamps from Wynntils will be ignored when checking for duplicate messages")
   private static boolean ignoreTimestamps = false;

   private static final String timestampPattern = "^(�8)?\\[[^A-z]*] ";

   private static Entry latestMessage = null;

   private static boolean duplicatedCall = false;

   @SubscribeEvent
   public static void onMessageAdd(MessageAddEvent event) {
      List<ItemStack> items = new java.util.ArrayList<>(List.of());
      String newMsg = event.getMessage().getStringWithoutFormatting();

      event.getMessage().iterate((next, changes) -> {
         try {
            var hover = next.getPartStyle().getStyle().getHoverEvent();
            if (hover != null && hover.getAction() == HoverEvent.Action.SHOW_ITEM) {
               items.add(hover.getValue(HoverEvent.Action.SHOW_ITEM).getItemStack());
            }
         } catch (Exception ignored) {}
         return IterationDecision.CONTINUE;
      });

      boolean itemsMatch = true;
      if (latestMessage == null) {
         itemsMatch = false;
      } else  {
         if (items.size() != latestMessage.items.size()) {
            itemsMatch = false;
         } else {
            for (int i = 0; i < items.size(); i++) {
               if (!items.get(i).getTooltipLines(null, TooltipFlag.NORMAL).equals(latestMessage.items.get(i).getTooltipLines(null, TooltipFlag.NORMAL))) {
                  itemsMatch = false;
                  break;
               }
            }
         }
      }

      newMsg = ignoreTimestamps ? newMsg.replaceAll(timestampPattern, "") : newMsg;
      if (latestMessage != null && newMsg.equals(latestMessage.original.getStringWithoutFormatting()) && itemsMatch) {
         if (!duplicatedCall) {
            latestMessage.count++;
         }
         duplicatedCall = !duplicatedCall;

         if (!event.getAllMessages().isEmpty()) {
            event.getAllMessages().remove(0);
         }

         if (!event.getTrimmedMessages().isEmpty()) {
            event.getTrimmedMessages().remove(0);
         }

         for (var iter = event.getTrimmedMessages().iterator(); iter.hasNext(); ) {
            if (iter.next().endOfEntry()) break;
            else iter.remove();
         }

         event.setMessage(
                 TextBuilder.of(event.getMessage())
                         .append(event.getMessage().endsWith(" ") ? "" : " ")
                         .append("(" + latestMessage.count + ")", ChatFormatting.GRAY)
         );
      } else {
         if (ignoreTimestamps) {
            Matcher matcher = event.getMessage().getMatcher(Pattern.compile(timestampPattern), PartStyle.StyleType.NONE);
            latestMessage = new Entry(StyledText.fromString(matcher.replaceFirst("")), items);
         } else {
            latestMessage = new Entry(event.getMessage(), items);
         }
         duplicatedCall = true;
      }
   }

   private static class Entry {
      private final StyledText original;
      private int count = 1;
      public List<ItemStack> items;

      public Entry(StyledText original, List<ItemStack> items) {
         this.original = original;
         this.items = items;
      }
   }
}
