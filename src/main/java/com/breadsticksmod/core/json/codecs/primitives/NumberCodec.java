package com.breadsticksmod.core.json.codecs.primitives;

import com.breadsticksmod.core.json.AbstractCodec;
import com.breadsticksmod.core.json.Annotations;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

abstract class NumberCodec<T extends Number> extends AbstractCodec<T, Number> {
   @Override
   public @Nullable Number write(T value, Class<?> type, Annotations annotations, Type... typeArgs) throws Exception {
      return value;
   }

   @Override
   public String toString(T value, Class<?> type, Type... typeArgs) throws Exception {
      return value.toString();
   }
}
