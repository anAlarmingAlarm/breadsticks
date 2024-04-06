package com.breadsticksmod.core.config.writer;

import com.breadsticksmod.core.config.Config;
import com.breadsticksmod.core.config.Writer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

@SuppressWarnings({"rawtypes", "unchecked"})
@Config.Writer(Enum.class)
public class EnumWriter extends Writer<Enum, String> {
   @Override
   public @Nullable String write(Enum value, Class<?> type, Type... typeArgs) {
      return toString(value, type, typeArgs);
   }

   @Override
   public @Nullable Enum read(@NotNull String value, Class<?> type, Type... typeArgs) {
      return fromString(value, type, typeArgs);
   }

   @Override
   public Enum fromString(String string, Class<?> type, Type... typeArgs) {
      return Enum.valueOf((Class<? extends Enum>) type, string);
   }

   @Override
   public String toString(Enum value, Class<?> type, Type... typeArgs) {
      return value.toString();
   }
}
