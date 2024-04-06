package com.breadsticksmod.client.features;

import com.breadsticksmod.client.util.PlayerUtil;
import com.breadsticksmod.core.Default;
import com.breadsticksmod.core.Feature;
import com.breadsticksmod.core.State;
import com.breadsticksmod.core.text.TextBuilder;
import com.wynntils.core.text.PartStyle;
import com.wynntils.core.text.StyledText;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.minecraft.ChatFormatting.AQUA;
import static net.minecraft.ChatFormatting.UNDERLINE;

@Default(State.ENABLED)
@Feature.Definition(name = "Click To Congratulate", description = "Click to send a congratulations message!")
public class ClickToCongratulateFeature extends Feature {
   private static final Pattern REGULAR_PATTERN = Pattern.compile("^\\[!] Congratulations to (?<player>.+) for reaching (?<type>.+) level (?<level>.+)!");
   private static final Pattern PROF_PATTERN = Pattern.compile("\\[!] Congratulations to (?<player>.+) for reaching level (?<level>.+) in (. )?(?<type>.+)!");

   @Value("Congratulations message")
   @Tooltip("What message to use to congratulate people")
   private static String message = "Congratulations!";

   @Value("Make message clickable")
   @Tooltip("If enabled, the message itself will be clickable")
   private static boolean clickable = false;

   @SubscribeEvent
   public void onChatMessageReceivedEvent(ChatMessageReceivedEvent event) {
      StyledText text = event.getOriginalStyledText();

      Matcher matcher = text.getMatcher(REGULAR_PATTERN, PartStyle.StyleType.NONE);
      if (!matcher.matches() && !(matcher = text.getMatcher(PROF_PATTERN, PartStyle.StyleType.NONE)).matches())
         return;

      String player = matcher.group("player");
      if (!PlayerUtil.isPlayer(player)) return;

      if (clickable) {
         event.setMessage(TextBuilder.of(event.getOriginalMessage())
            .onHover(
               HoverEvent.Action.SHOW_TEXT,
               Component.literal("Click to congratulate " + player + "!")
            ).onClick(
               ClickEvent.Action.RUN_COMMAND,
               "/msg %s %s".formatted(player, message)
            ).toComponent());
      } else {
         event.setMessage(TextBuilder.of(event.getStyledText())
            .line()
            .append("Click to congratulate ", AQUA, UNDERLINE)
            .append(player, AQUA, UNDERLINE)
            .append("!", AQUA, UNDERLINE)
            .onClick(
               ClickEvent.Action.RUN_COMMAND,
               "/msg %s %s".formatted(player, message)
            ).toComponent());
      }
   }
}
