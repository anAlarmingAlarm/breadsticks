package com.breadsticksmod.core.config.writer.primitives;

import com.breadsticksmod.core.config.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

@Config.Writer(Long.class)
public class LongWriter extends NumberWriter<Long> {
   @Override
   public @Nullable Long read(@NotNull Number value, Class<?> type, Type... typeArgs) throws Exception {
      return value.longValue();
   }

   @Override
   public Long fromString(String string, Class<?> type, Type... typeArgs) throws Exception {
      return Long.valueOf(string);
   }
}
