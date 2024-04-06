package com.breadsticksmod.client.features;

import com.breadsticksmod.client.events.mc.chat.MessageAddEvent;
import com.breadsticksmod.core.Default;
import com.breadsticksmod.core.Feature;
import com.breadsticksmod.core.State;
import com.breadsticksmod.core.text.TextBuilder;
import com.wynntils.core.text.StyledText;
import net.minecraft.ChatFormatting;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;

@Default(State.ENABLED)
@Feature.Definition(name = "Stack Duplicate Messages")
public class StackDuplicateMessagesFeature extends Feature {
   private static final Map<Integer, Entry> LATEST_MESSAGES = new HashMap<>();

   @SubscribeEvent
   public void onMessageAdd(MessageAddEvent event) {
      int hash = System.identityHashCode(event.getChat());
      Entry entry = LATEST_MESSAGES.get(hash);

      if (entry != null && entry.original.equals(event.getMessage())) {
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
      } else LATEST_MESSAGES.put(hash, new Entry(event.getMessage()));
   }

   private static class Entry {
      private final StyledText original;
      private int count = 0;

      public Entry(StyledText original) {
         this.original = original;
      }
   }
}
