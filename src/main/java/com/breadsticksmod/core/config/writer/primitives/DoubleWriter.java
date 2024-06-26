package com.breadsticksmod.core.config.writer.primitives;

import com.breadsticksmod.core.config.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

@Config.Writer(Double.class)
public class DoubleWriter extends NumberWriter<Double> {
   @Override
   public @Nullable Double read(@NotNull Number value, Class<?> type, Type... typeArgs) throws Exception {
      return value.doubleValue();
   }

   @Override
   public Double fromString(String string, Class<?> type, Type... typeArgs) throws Exception {
      return Double.valueOf(string);
   }
}
