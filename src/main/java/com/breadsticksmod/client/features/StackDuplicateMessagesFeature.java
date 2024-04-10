package com.breadsticksmod.client.features;

import com.breadsticksmod.client.events.mc.chat.MessageAddEvent;
import com.breadsticksmod.core.Default;
import com.breadsticksmod.core.Feature;
import com.breadsticksmod.core.State;
import com.breadsticksmod.core.text.TextBuilder;
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

@Default(State.ENABLED)
@Feature.Definition(name = "Stack Duplicate Messages")
public class StackDuplicateMessagesFeature extends Feature {
   @Value("Ignore Timestamps")
   @Default(State.DISABLED)
   @Tooltip("If enabled, timestamps from Wynntils will be ignored when checking for duplicate messages")
   private static boolean ignoreTimestamps = false;

   @Value("Use Old System")
   @Default(State.DISABLED)
   @Tooltip("Enable this to use the pre-1.0.7 version of this feature\nThis may cause a memory leak; only use this if messages aren't being stacked correctly")
   private static boolean oldSystem = false;

   private static String timestampPattern = "^\\[[^A-z]*] ";

   private static Entry latestMessage = null; // for new system

   private static final Map<Integer, Entry> LATEST_MESSAGES = new HashMap<>(); // for old system

   @SubscribeEvent
   public void onMessageAdd(MessageAddEvent event) {
      if (oldSystem)
         handleStackingOld(event);
      else
         handleStacking(event);
   }

   private void handleStacking(MessageAddEvent event) {
      List<ItemStack> items = new java.util.ArrayList<>(List.of());
      String prevMsg = null;
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
         prevMsg = latestMessage.original.getStringWithoutFormatting();
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

      if (latestMessage != null) {
         newMsg = ignoreTimestamps ? newMsg.replaceAll(timestampPattern, "") : newMsg;
      }
      if (latestMessage != null && latestMessage.original.getStringWithoutFormatting().equals(newMsg) && itemsMatch) {
         latestMessage.count++;

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
                 TextBuilder.of(latestMessage.original)
                         .append(latestMessage.original.endsWith(" ") ? "" : " ")
                         .append("(" + latestMessage.count + ")", ChatFormatting.GRAY)
         );
      } else {
         latestMessage = new Entry(StyledText.fromString(newMsg), items);
      }
   }

   private void handleStackingOld(MessageAddEvent event) {
      int hash = System.identityHashCode(event.getChat());
      List<ItemStack> items = new java.util.ArrayList<>(List.of());
      Entry entry = LATEST_MESSAGES.get(hash);

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
      if (entry == null) {
         itemsMatch = false;
      } else if (items.size() != entry.items.size()) {
         itemsMatch = false;
      } else {
         for (int i = 0; i < items.size(); i++) {
            if (!items.get(i).getTooltipLines(null, TooltipFlag.NORMAL).equals(entry.items.get(i).getTooltipLines(null, TooltipFlag.NORMAL))) {
               itemsMatch = false;
               break;
            }
         }
      }

      StyledText msg = ignoreTimestamps ? event.getMessage().replaceAll(timestampPattern, "") : event.getMessage();
      if (entry != null && entry.original.equals(msg) && itemsMatch) {
         entry.count++;

         event.getAllMessages().remove(0);
         event.getTrimmedMessages().remove(0);

         for (var iter = event.getTrimmedMessages().iterator(); iter.hasNext(); ) {
            if (iter.next().endOfEntry()) break;
            else iter.remove();
         }

         event.setMessage(
                 TextBuilder.of(entry.original)
                         .append(entry.original.endsWith(" ") ? "" : " ")
                         .append("(" + (entry.count + 1) + ")", ChatFormatting.GRAY)
         );
      } else {
         LATEST_MESSAGES.put(hash, new Entry(msg, items));
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
