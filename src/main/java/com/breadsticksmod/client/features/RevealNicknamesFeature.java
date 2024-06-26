package com.breadsticksmod.client.features;

import com.breadsticksmod.client.util.ChatUtil;
import com.breadsticksmod.core.Default;
import com.breadsticksmod.core.Feature;
import com.breadsticksmod.core.State;
import com.wynntils.core.components.Models;
import com.wynntils.core.text.PartStyle;
import com.wynntils.core.text.StyledText;
import com.wynntils.core.text.StyledTextPart;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import com.wynntils.mc.event.ClientsideMessageEvent;
import com.wynntils.utils.type.IterationDecision;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.Style;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Default(State.ENABLED)
@Feature.Definition(name = "Reveal Nicknames")
public class RevealNicknamesFeature extends Feature {
   @Value("Replace nicknames")
   private static boolean REPLACE_NICKNAMES = false;

   @SubscribeEvent
   public void onChatReceived(ChatMessageReceivedEvent e) {
      if (!Models.WorldState.onWorld()) return;

      e.setMessage(revealNicknames(e.getStyledText()).getComponent());
   }

   @SubscribeEvent
   public void onClientsideMessage(ClientsideMessageEvent e) {
      if (!Models.WorldState.onWorld()) return;

      e.setMessage(revealNicknames(e.getStyledText()).getComponent());
   }

   private StyledText revealNicknames(StyledText text) {
      return text.iterate((next, changes) -> {
         var hover = next.getPartStyle().getStyle().getHoverEvent();

         if (hover != null && hover.getAction() == HoverEvent.Action.SHOW_TEXT && next.getPartStyle().isItalic()) {
            for (StyledText component : StyledText.fromComponent(hover.getValue(HoverEvent.Action.SHOW_TEXT)).split("\n")) {
               Matcher matcher = component.getMatcher(ChatUtil.NICK_REGEX, PartStyle.StyleType.NONE);
               if (!matcher.matches()) continue;

               String username = matcher.group("username");
               if (REPLACE_NICKNAMES) changes.clear();

               changes.add(new StyledTextPart(
                       REPLACE_NICKNAMES ? username : " (" + username + ")",
                       (REPLACE_NICKNAMES ? next.getPartStyle().getStyle() : Style.EMPTY.withColor(ChatFormatting.RED)).withItalic(false),
                       null,
                       next.getPartStyle().getStyle()
               ));

               return IterationDecision.CONTINUE;
            }
         }

         return IterationDecision.CONTINUE;
      });
   }
}
