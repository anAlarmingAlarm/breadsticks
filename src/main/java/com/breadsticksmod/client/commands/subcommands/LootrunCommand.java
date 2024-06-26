package com.breadsticksmod.client.commands.subcommands;

import com.breadsticksmod.client.features.lootrun.LootrunDryStreakFeature;
import com.breadsticksmod.client.util.ChatUtil;
import com.breadsticksmod.core.text.TextBuilder;
import com.essentuan.acf.core.annotations.Argument;
import com.essentuan.acf.core.annotations.Command;
import com.essentuan.acf.core.annotations.Subcommand;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;

import java.util.List;

import static com.breadsticksmod.client.util.ChatUtil.prefixLength;

@Command("lootrun")
public class LootrunCommand {
   private static final int ITEMS_PER_PAGE = 5;

   @Subcommand("drystreak")
   private static void onGetDryStreak(CommandContext<?> context) {
      ChatUtil.message(TextBuilder.of("You've gone ", ChatFormatting.GRAY)
              .append(LootrunDryStreakFeature.dry(), ChatFormatting.AQUA)
              .append(" pulls without finding a ", ChatFormatting.GRAY)
              .append("Mythic", ChatFormatting.DARK_PURPLE)
              .append(".", ChatFormatting.GRAY));
   }

   @Subcommand("drystreak average")
   private static void onGetDryStreakAverage(CommandContext<?> context) {
      ChatUtil.message(
              TextBuilder.of("You average ", ChatFormatting.GRAY)
                      .append(
                              (int) LootrunDryStreakFeature.pulls()
                                      .stream()
                                      .mapToInt(LootrunDryStreakFeature.Pull::pulls)
                                      .average()
                                      .orElse(0),
                              ChatFormatting.AQUA
                      )
                      .append(" pulls between ", ChatFormatting.GRAY)
                      .append("Mythics", ChatFormatting.DARK_PURPLE)
                      .append(".", ChatFormatting.GRAY)
      );
   }

   @Subcommand("drystreak history")
   private static void getDryStreakHistory(CommandContext<?> context) {
      getDryStreakPage(context, 0);
   }

   @Subcommand("drystreak history page")
   private static void getDryStreakPage(
           CommandContext<?> context,
           @Argument("Page") int page
   ) {
      List<LootrunDryStreakFeature.Pull> pulls = LootrunDryStreakFeature.pulls();

      TextBuilder builder = TextBuilder.of("\n\n");

      if (pulls.isEmpty())
         builder.append("There is nothing to display", ChatFormatting.WHITE)
                 .underline()
                 .center(prefixLength())
                 .line();
      else
         pulls.stream()
                 .skip(page * ITEMS_PER_PAGE)
                 .limit(ITEMS_PER_PAGE)
                 .forEach(pull -> builder.append("")
                         .reset()
                         .append(pull.item())
                         .underline()
                         .center(prefixLength())
                         .line()
                         .line()
                         .append("After ", ChatFormatting.GRAY)
                         .append(pull.pulls(), ChatFormatting.AQUA)
                         .append(" pulls.", ChatFormatting.GRAY)
                         .center(prefixLength())
                         .line().line());

      int maxPages = (int) Math.ceil(pulls.size() / (double) ITEMS_PER_PAGE);

      boolean hasNext = page < maxPages - 1;
      boolean hasPrevious = page > 0;

      builder.append("⋘",
              hasPrevious ? ChatFormatting.WHITE : ChatFormatting.DARK_GRAY);

      if (hasPrevious)
         builder
                 .onPartClick(ClickEvent.Action.RUN_COMMAND, "/breadsticks lootrun drystreak history page " + (page - 1));
      else
         builder.strikethrough();

      builder
              .append("   ")
              .reset()
              .append(page + 1, ChatFormatting.WHITE)
              .append("/", ChatFormatting.GRAY)
              .append(maxPages, ChatFormatting.WHITE)
              .append("   ")
              .append("⋙", hasNext ? ChatFormatting.WHITE : ChatFormatting.DARK_GRAY);

      if (hasNext)
         builder
                 .onPartClick(ClickEvent.Action.RUN_COMMAND, "/breadsticks lootrun drystreak history page " + (page + 1));
      else
         builder.strikethrough();

      builder.center(prefixLength());

      ChatUtil.message(builder);
   }
}
