package com.breadsticksmod.core.json.codecs;

import com.breadsticksmod.core.json.AbstractCodec;
import com.breadsticksmod.core.json.Annotations;
import com.breadsticksmod.core.util.UUIDUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.UUID;

@AbstractCodec.Definition(UUID.class)
public class UUIDCodec extends AbstractCodec<UUID, String> {
   @Override
   public @Nullable String write(UUID value, Class<?> type, Annotations annotations, Type... typeArgs) throws Exception {
      return toString(value, type, typeArgs);
   }

   @Override
   public @Nullable UUID read(@NotNull String value, Class<?> type, Annotations annotations, Type... typeArgs) throws Exception {
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
