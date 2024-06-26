package com.breadsticksmod.client.config.providers.sound;

import com.breadsticksmod.core.config.Config;
import com.breadsticksmod.core.config.Writer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

@Config.Writer(SoundEvent.class)
public class SoundWriter extends Writer<SoundEvent, String> {
   @Override
   public @Nullable String write(SoundEvent value, Class<?> type, Type... typeArgs) throws Exception {
      return toString(value, type, typeArgs);
   }

   @Override
   public @Nullable SoundEvent read(@NotNull String value, Class<?> type, Type... typeArgs) throws Exception {
      return fromString(value, type, typeArgs);
   }

   @Override
   public SoundEvent fromString(String string, Class<?> type, Type... typeArgs) throws Exception {
      return SoundEvent.createVariableRangeEvent(new ResourceLocation(string));
   }

   @Override
   public String toString(SoundEvent value, Class<?> type, Type... typeArgs) throws Exception {
      return value.getLocation().toString();
   }
}
