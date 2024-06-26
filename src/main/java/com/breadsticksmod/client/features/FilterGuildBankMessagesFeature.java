package com.breadsticksmod.client.features;

import com.breadsticksmod.core.Default;
import com.breadsticksmod.core.Feature;
import com.breadsticksmod.core.State;
import com.wynntils.core.text.PartStyle;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Default(State.DISABLED)
@Feature.Definition(name = "Filter Guild Bank Messages")
public class FilterGuildBankMessagesFeature extends Feature {
   private static final Pattern GUILD_BANK_MESSAGE_PATTERN = Pattern.compile("^\\[INFO] (?<player>.+) (?<action>(withdrew)|(deposited)) (?<item>.+) (from|to) the Guild Bank \\((?<bank>.+)\\)");

   @SubscribeEvent
   public void onChatMessage(ChatMessageReceivedEvent event) {
      Matcher matcher = event.getStyledText().getMatcher(GUILD_BANK_MESSAGE_PATTERN, PartStyle.StyleType.NONE);
      if (matcher.matches()) event.setCanceled(true);
   }
}
