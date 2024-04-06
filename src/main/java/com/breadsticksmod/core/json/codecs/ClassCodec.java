package com.breadsticksmod.core.json.codecs;

import com.breadsticksmod.core.json.AbstractCodec;
import com.breadsticksmod.core.json.Annotations;
import com.breadsticksmod.core.util.Base64;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

@SuppressWarnings("ALL")
@AbstractCodec.Definition(Class.class)
public class ClassCodec extends AbstractCodec<Class, String> {
   @Override
   public @Nullable String write(Class value, Class<?> type, Annotations annotations, Type... typeArgs) throws Exception {
      return toString(value, type, typeArgs);
   }

   @Override
   public @Nullable Class read(@NotNull String value, Class<?> type, Annotations annotations, Type... typeArgs) throws Exception {
      return fromString(value, type, typeArgs);
   }

   @Override
   public Class fromString(String string, Class<?> type, Type... typeArgs) throws Exception {
      return Class.forName(Base64.decode(string));
   }

   @Override
   public String toString(Class value, Class<?> type, Type... typeArgs) throws Exception {
      return Base64.encode(value.getName());
   }
}
