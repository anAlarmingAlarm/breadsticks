package com.breadsticksmod.core.json.codecs;

import com.breadsticksmod.core.json.AbstractCodec;
import com.breadsticksmod.core.json.Annotations;
import com.breadsticksmod.core.json.BaseModel;
import com.breadsticksmod.core.time.ChronoUnit;
import com.breadsticksmod.core.time.Duration;
import com.breadsticksmod.core.util.Priority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@AbstractCodec.Definition(value = Duration.class, priority = Priority.LOWEST)
public class DurationCodec extends AbstractCodec<Duration, Number> {
   private static final BaseModel.Unit DEFAULT_UNIT = new BaseModel.Unit() {
      @Override
      public ChronoUnit value() {
         return ChronoUnit.SECONDS;
      }

      @Override
      public Class<? extends Annotation> annotationType() {
         return BaseModel.Unit.class;
      }
   };

   @Override
   public @Nullable Double write(Duration value, Class<?> type, Annotations annotations, Type... typeArgs) {
      return value.to(annotations.get(BaseModel.Unit.class, DEFAULT_UNIT).value());
   }

   @Override
   public @Nullable Duration read(@NotNull Number value, Class<?> type, Annotations annotations, Type... typeArgs) {
      return Duration.of(value.doubleValue(), annotations.get(BaseModel.Unit.class, DEFAULT_UNIT).value());
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
