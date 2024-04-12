package com.breadsticksmod.client.features;

import com.breadsticksmod.core.Default;
import com.breadsticksmod.core.Feature;
import com.breadsticksmod.core.State;
import com.breadsticksmod.core.text.TextBuilder;
import com.wynntils.core.text.PartStyle;
import com.wynntils.core.text.StyledText;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import net.minecraft.ChatFormatting;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Default(State.DISABLED)
@Feature.Definition(name = "Shorten Guild Reward Messages", description = "")
public class ShortenGuildRewardMessagesFeature extends Feature {
   private static final Pattern TOME_FOUND_PATTERN = Pattern.compile("^\\[INFO] A Guild Tome has been found and added to the Guild Rewards. Owner and Chiefs can gift it to members.$");
   private static final Pattern TOME_REWARDED_PATTERN = Pattern.compile("^\\[INFO] (?<rewarder>.+) rewarded a Tome to (?<recipient>.+)\\.[\\s\\S]*$");
   private static final Pattern EMERALDS_REWARDED_PATTERN = Pattern.compile("^\\[INFO] (?<rewarder>.+) rewarded 1024 Emeralds to (?<recipient>.+)\\.[\\s\\S]*$");

   @SubscribeEvent
   public void onChatMessage(ChatMessageReceivedEvent event) {
      StyledText message = event.getStyledText();
      Matcher matcher = message.getMatcher(TOME_FOUND_PATTERN, PartStyle.StyleType.NONE);
      if (matcher.matches()) {
         event.setMessage(TextBuilder.of("[INFO] ", ChatFormatting.DARK_AQUA)
                 .append("A ", ChatFormatting.AQUA)
                 .append("Guild Tome ", ChatFormatting.DARK_AQUA)
                 .append("has been found and added to the Guild Rewards.", ChatFormatting.AQUA)
                 .toComponent());
         return;
      }

      matcher = message.getMatcher(TOME_REWARDED_PATTERN, PartStyle.StyleType.NONE);
      if (matcher.matches()) {
         event.setMessage(TextBuilder.of("[INFO] ", ChatFormatting.DARK_AQUA)
                 .append(matcher.group("rewarder") + " rewarded a ", ChatFormatting.AQUA)
                 .append("Tome ", ChatFormatting.DARK_AQUA)
                 .append("to " + matcher.group("recipient") + ".", ChatFormatting.AQUA)
                 .toComponent());
         return;
      }

      matcher = message.getMatcher(EMERALDS_REWARDED_PATTERN, PartStyle.StyleType.NONE);
      if (matcher.matches()) {
         event.setMessage(TextBuilder.of("[INFO] ", ChatFormatting.DARK_AQUA)
                 .append(matcher.group("rewarder") + " rewarded ", ChatFormatting.AQUA)
                 .append("1024 Emeralds ", ChatFormatting.DARK_AQUA)
                 .append("to " + matcher.group("recipient") + ".", ChatFormatting.AQUA)
                 .toComponent());
      }
   }
}
