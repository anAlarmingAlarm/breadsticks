package com.breadsticksmod.core.json.codecs;

import com.breadsticksmod.core.UnexpectedException;
import com.breadsticksmod.core.json.AbstractCodec;
import com.breadsticksmod.core.json.Annotations;
import com.breadsticksmod.core.util.EnumUtil;
import com.breadsticksmod.core.util.Reflection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

@SuppressWarnings({"rawtypes"})
@AbstractCodec.Definition(Enum.class)
public class EnumCodec extends AbstractCodec<Enum, String> {
   @Override
   public @Nullable String write(Enum value, Class<?> type, Annotations annotations, Type... typeArgs) {
      return toString(value, type, typeArgs);
   }

   @Override
   public @Nullable Enum read(@NotNull String value, Class<?> type, Annotations annotations, Type... typeArgs) {
      return fromString(value, type, typeArgs);
   }

   @Override
   @SuppressWarnings("unchecked")
   public Enum fromString(String string, Class<?> type, Type... typeArgs) {
      Object obj = EnumUtil.valueOf(string, (Class<Enum>) type).orElse(null);

      if (obj == null)
         throw new UnexpectedException("Couldn't find %s that matches %s", Reflection.toSimpleString(type), string);

      return (Enum) obj;
   }

   @Override
   public String toString(Enum value, Class<?> type, Type... typeArgs) {
      return value.toString();
   }
}
