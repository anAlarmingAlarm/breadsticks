package com.breadsticksmod.client.config.providers.text;

import com.breadsticksmod.client.util.ChatUtil;
import com.breadsticksmod.core.config.Config;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import static net.minecraft.ChatFormatting.*;

public class ColorProvider implements Config.Dropdown.Provider<ChatFormatting> {
   private static Map<String, ChatFormatting> COLORS = null;

   @Override
   public Iterable<ChatFormatting> getOptions() {
      return getColors().values();
   }

   @Override
   public @Nullable ChatFormatting get(String string) {
      return getColors().get(string);
   }

   @Override
   public Component getName(ChatFormatting value) {
      return Component.literal(ChatUtil.toTitleCase(value.getName().replaceAll("_", " ")));
   }

   private static Map<String, ChatFormatting> getColors() {
      if (COLORS == null) {
         COLORS = new HashMap<>();
         COLORS.put("Dark Red", DARK_RED);
         COLORS.put("Red", RED);
         COLORS.put("Gold", GOLD);
         COLORS.put("Yellow", YELLOW);
         COLORS.put("Green", GREEN);
         COLORS.put("Dark Green", DARK_GREEN);
         COLORS.put("Aqua", AQUA);
         COLORS.put("Dark Aqua", DARK_AQUA);
         COLORS.put("Blue", BLUE);
         COLORS.put("Dark Blue", DARK_BLUE);
         COLORS.put("Light Purple", LIGHT_PURPLE);
         COLORS.put("Dark Purple", DARK_PURPLE);
         COLORS.put("White", WHITE);
         COLORS.put("Gray", GRAY);
         COLORS.put("Dark Gray", DARK_GRAY);
         COLORS.put("Black", BLACK);
      }

      return COLORS;
   }
}
