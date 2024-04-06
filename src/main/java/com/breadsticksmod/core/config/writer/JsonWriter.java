package com.breadsticksmod.core.config.writer;

import com.breadsticksmod.core.config.Config;
import com.breadsticksmod.core.config.Writer;
import com.breadsticksmod.core.json.Json;
import com.breadsticksmod.core.toml.Toml;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

@Config.Writer(Json.class)
public class JsonWriter extends Writer<Json, Toml> {
   @Override
   public @Nullable Toml write(Json value, Class<?> type, Type... typeArgs) {
      return Toml.of(value);
   }

   @Override
   public @Nullable Json read(@NotNull Toml value, Class<?> type, Type... typeArgs) {
      return Json.of(value);
   }

   @Override
   public Json fromString(String string, Class<?> type, Type... typeArgs) {
      return Json.parse(string);
   }

   @Override
   public String toString(Json value, Class<?> type, Type... typeArgs) {
      return value.toString();
   }
}
