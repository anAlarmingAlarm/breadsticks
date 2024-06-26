package com.breadsticksmod.core.json.codecs;

import com.breadsticksmod.core.json.AbstractCodec;
import com.breadsticksmod.core.json.Annotations;
import com.breadsticksmod.core.util.Priority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

@AbstractCodec.Definition(value = Object.class, priority = Priority.LOWEST)
public class ObjectCodec extends AbstractCodec<Object, Object> {
   @Nullable
   @Override
   public Object write(Object value, Class<?> type, Annotations annotations, Type... typeArgs) throws Exception {
      return value;
   }

   @Override
   public @NotNull Object read(@NotNull Object value, Class<?> type, Annotations annotations, Type... typeArgs) throws Exception {
      return value;
   }

   @Override
   public Object fromString(String string, Class<?> type, Type... typeArgs) throws Exception {
      throw new UnsupportedOperationException();
   }

   @Override
   public String toString(Object value, Class<?> type, Type... typeArgs) throws Exception {
      throw new UnsupportedOperationException();
   }
};
