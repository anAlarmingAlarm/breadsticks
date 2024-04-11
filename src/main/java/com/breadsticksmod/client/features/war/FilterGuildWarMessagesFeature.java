package com.breadsticksmod.client.features.war;

import com.breadsticksmod.core.Default;
import com.breadsticksmod.core.Feature;
import com.breadsticksmod.core.State;
import com.breadsticksmod.core.config.Config;
import com.wynntils.core.text.PartStyle;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Default(State.DISABLED)
@Config.Category("War")
@Feature.Definition(name = "Filter Guild War Messages")
public class FilterGuildWarMessagesFeature extends Feature {
   private static final Pattern GUILD_CHAT_PATTERN = Pattern.compile("^\\[.+] .+ defense is (Very Low|Low|Medium|High|Very High)$");
   private static final Pattern WAR_PATTERN = Pattern.compile("^\\[WAR] The war for .+ will start in .+");

   @SubscribeEvent(priority = EventPriority.LOW)
   public void onChatMessage(ChatMessageReceivedEvent event) {
      Matcher matcher1 = event.getStyledText().getMatcher(GUILD_CHAT_PATTERN, PartStyle.StyleType.NONE);
      Matcher matcher2 = event.getStyledText().getMatcher(WAR_PATTERN, PartStyle.StyleType.NONE);
      if (matcher1.matches() || matcher2.matches()) event.setCanceled(true);
   }
}
