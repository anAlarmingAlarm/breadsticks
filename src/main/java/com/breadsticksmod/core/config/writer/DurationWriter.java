package com.breadsticksmod.core.config.writer;

import com.breadsticksmod.core.config.Config;
import com.breadsticksmod.core.config.Writer;
import com.breadsticksmod.core.time.Duration;
import com.breadsticksmod.core.time.ChronoUnit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

@Config.Writer(Duration.class)
public class DurationWriter extends Writer<Duration, Number> {
   @Override
   public @Nullable Double write(Duration value, Class<?> type, Type... typeArgs) {
      return value.toSeconds();
   }

   @Override
   public @Nullable Duration read(@NotNull Number value, Class<?> type, Type... typeArgs) {
      return Duration.of(value, ChronoUnit.SECONDS);
   }

   @Override
   public Duration fromString(String string, Class<?> type, Type... typeArgs)    {
      return Duration.of(Double.parseDouble(string), ChronoUnit.SECONDS);
   }

   @Override
   public String toString(Duration value, Class<?> type, Type... typeArgs) {
      return Double.toString(value.toSeconds());
   }
}
