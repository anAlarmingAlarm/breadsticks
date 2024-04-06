package com.breadsticksmod.core.config.entry.value;

import com.breadsticksmod.core.config.Buildable;
import com.breadsticksmod.core.config.entry.ConfigEntry;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;

public class BooleanValue extends ConfigEntry<Boolean> {
   public BooleanValue(Component title, Object ref, Field field, Buildable<?, ?> parent) {
      super(title, ref, field, parent);
   }

   @Override
   protected AbstractFieldBuilder<Boolean, ?, ?> create(ConfigEntryBuilder builder) {
      return create(builder::startBooleanToggle);
   }
}
