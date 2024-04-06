package com.breadsticksmod.core.http.models.wynncraft.guild.banner;

import com.breadsticksmod.core.http.api.guild.Guild;
import com.breadsticksmod.core.json.AbstractCodec;
import com.breadsticksmod.core.json.Annotations;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

@AbstractCodec.Definition(Guild.Banner.Structure.class)
public class StructureCodec extends AbstractCodec<Guild.Banner.Structure, String> {
   @Override
   public @Nullable String write(Guild.Banner.Structure value, Class<?> type, Annotations annotations, Type... typeArgs) throws Exception {
      return value.toString();
   }

   @Override
   public @Nullable Guild.Banner.Structure read(@NotNull String value, Class<?> type, Annotations annotations, Type... typeArgs) throws Exception {
      return Guild.Banner.Structure.of(value);
   }

   @Override
   public Guild.Banner.Structure fromString(String string, Class<?> type, Type... typeArgs) throws Exception {
      return Guild.Banner.Structure.of(string);
   }

   @Override
   public String toString(Guild.Banner.Structure value, Class<?> type, Type... typeArgs) throws Exception {
      return value.toString();
   }
}
