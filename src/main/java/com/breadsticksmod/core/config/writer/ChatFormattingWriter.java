package com.breadsticksmod.core.config.writer;

import com.breadsticksmod.core.config.Config;
import com.breadsticksmod.core.config.Writer;
import net.minecraft.ChatFormatting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

@Config.Writer(ChatFormatting.class)
public class ChatFormattingWriter extends Writer<ChatFormatting, String> {
   @Override
   public @Nullable String write(ChatFormatting value, Class<?> type, Type... typeArgs) throws Exception {
      return value.toString();
   }

   @Override
   public @Nullable ChatFormatting read(@NotNull String value, Class<?> type, Type... typeArgs) throws Exception {
      return ChatFormatting.getByCode(value.toCharArray()[1]);
   }

   @Override
   public ChatFormatting fromString(String string, Class<?> type, Type... typeArgs) throws Exception {
      return ChatFormatting.getByCode(string.toCharArray()[1]);
   }

   @Override
   public String toString(ChatFormatting value, Class<?> type, Type... typeArgs) throws Exception {
      return value.toString();
   }
}
