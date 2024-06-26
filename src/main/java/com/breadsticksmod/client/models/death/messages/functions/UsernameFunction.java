package com.breadsticksmod.client.models.death.messages.functions;

import com.breadsticksmod.client.models.death.messages.DeathMessageModel;
import com.breadsticksmod.client.models.death.messages.Target;
import com.breadsticksmod.core.artemis.functions.Function;
import com.wynntils.core.consumers.functions.arguments.FunctionArguments;
import net.minecraft.ChatFormatting;

@Function.With("username")
public class UsernameFunction extends Function<String> {
   @Override
   protected String call(FunctionArguments args) {
      Target target = DeathMessageModel.target();

      if (target == null)
         return "";

      return ChatFormatting.GOLD + target.username();
   }
}
