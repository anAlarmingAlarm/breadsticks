package com.breadsticksmod.client.features;

import com.breadsticksmod.client.util.ChatUtil;
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
@Feature.Definition(name = "Shorten Bomb Messages", description = "")
public class ShortenBombMessagesFeature extends Feature {
   private static final Pattern THROW_MESSAGE_PATTERN = Pattern.compile("^(?<thrower>.+) has thrown a (?<bomb>.+?)![\\s\\S]*$");
   private static final Pattern EXPIRE_MESSAGE_PATTERN = Pattern.compile("^(?<thrower>.+)'s bomb has expired. You can buy (?<bomb>.+)s at our website, wynncraft.com/store$");
   private static final Pattern EXPIRE_PARTY_MESSAGE_PATTERN = Pattern.compile("^(?<thrower>.+)'s Party Bomb has run out! Visit wynncraft\\.com/store to get your own!$");

   @SubscribeEvent
   public void onChatMessage(ChatMessageReceivedEvent event) {
      StyledText message = event.getStyledText();
      String thrower = ChatUtil.getNickname(event.getOriginalStyledText());
      Matcher matcher = message.getMatcher(THROW_MESSAGE_PATTERN, PartStyle.StyleType.NONE);
      if (matcher.matches()) {
         if (thrower.isEmpty()) thrower = matcher.group("thrower");
         event.setMessage(TextBuilder.of(thrower, ChatFormatting.AQUA)
                 .append(" has thrown a ", ChatFormatting.DARK_AQUA)
                 .append(ChatUtil.toTitleCase(matcher.group("bomb")), ChatFormatting.AQUA)
                 .append("!", ChatFormatting.DARK_AQUA)
                 .toComponent());
         return;
      }

      matcher = message.getMatcher(EXPIRE_MESSAGE_PATTERN, PartStyle.StyleType.NONE);
      if (matcher.matches()) {
         if (thrower.isEmpty()) thrower = matcher.group("thrower");
         event.setMessage(TextBuilder.of(thrower, ChatFormatting.AQUA)
                 .append("'s ", ChatFormatting.DARK_AQUA)
                 .append(ChatUtil.toTitleCase(matcher.group("bomb")), ChatFormatting.AQUA)
                 .append(" has expired.", ChatFormatting.DARK_AQUA)
                 .toComponent());
         return;
      }

      matcher = message.getMatcher(EXPIRE_PARTY_MESSAGE_PATTERN, PartStyle.StyleType.NONE);
      if (matcher.matches()) {
         if (thrower.isEmpty()) thrower = matcher.group("thrower");
         event.setMessage(TextBuilder.of(thrower, ChatFormatting.AQUA)
                 .append("'s ", ChatFormatting.DARK_AQUA)
                 .append("Party Bomb", ChatFormatting.AQUA)
                 .append(" has expired.", ChatFormatting.DARK_AQUA)
                 .toComponent());
      }
   }
}
