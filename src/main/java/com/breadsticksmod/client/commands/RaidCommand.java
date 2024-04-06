package com.breadsticksmod.client.commands;

import com.breadsticksmod.client.models.raids.Raid;
import com.breadsticksmod.client.models.raids.RaidModel;
import com.breadsticksmod.client.models.raids.RaidType;
import com.breadsticksmod.client.util.ChatUtil;
import com.essentuan.acf.core.annotations.Argument;
import com.essentuan.acf.core.annotations.Command;
import com.essentuan.acf.core.annotations.Subcommand;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;

@Command("raids")
public class RaidCommand {
   @Subcommand("pb")
   public static void onPb(
           CommandContext<?> context,
           @Argument("Raid") RaidType type
   ) {
      RaidModel.getPB(type).ifPresentOrElse(raid ->
              ChatUtil.send(Raid.format(raid)), () ->
              ChatUtil.message("You have not completed %s", type.title(), ChatFormatting.RED));
   }
}
