package com.breadsticksmod.core.json.codecs.primitives;

import com.breadsticksmod.core.json.AbstractCodec;
import com.breadsticksmod.core.json.Annotations;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

@AbstractCodec.Definition(Integer.class)
public class IntegerCodec extends NumberCodec<Integer> {

   @Override
   public @Nullable Integer read(@NotNull Number value, Class<?> type, Annotations annotations, Type... typeArgs) throws Exception {
      return value.intValue();
   }

   @Override
   public Integer fromString(String string, Class<?> type, Type... typeArgs) throws Exception {
      return Integer.valueOf(string);
   }
}
