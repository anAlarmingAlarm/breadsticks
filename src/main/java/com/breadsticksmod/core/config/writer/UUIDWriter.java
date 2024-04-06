package com.breadsticksmod.core.config.writer;

import com.breadsticksmod.core.config.Config;
import com.breadsticksmod.core.config.Writer;
import com.breadsticksmod.core.util.UUIDUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.UUID;

@Config.Writer(UUID.class)
public class UUIDWriter extends Writer<UUID, String> {
   @Override
   public @Nullable String write(UUID value, Class<?> type, Type... typeArgs) throws Exception {
      return toString(value, type, typeArgs);
   }

   @Override
   public @Nullable UUID read(@NotNull String value, Class<?> type, Type... typeArgs) throws Exception {
      return fromString(value, type, typeArgs);
   }

   @Override
   public UUID fromString(String string, Class<?> type, Type... typeArgs) throws Exception {
      return UUIDUtil.parseUUID(string);
   }

   @Override
   public String toString(UUID value, Class<?> type, Type... typeArgs) throws Exception {
      return value.toString();
   }
}
