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
   private static final Map<Integer, Entry> LATEST_MESSAGES = new HashMap<>();

   @SubscribeEvent
   public void onMessageAdd(MessageAddEvent event) {
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

      if (entry != null && entry.original.equals(event.getMessage()) && itemsMatch) {
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
         LATEST_MESSAGES.put(hash, new Entry(event.getMessage(), items));
      }
   }

   private static class Entry {
      private final StyledText original;
      private int count = 0;
      public List<ItemStack> items;

      public Entry(StyledText original, List<ItemStack> items) {
         this.original = original;
         this.items = items;
      }
   }
}
