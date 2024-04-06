package com.breadsticksmod.core.config.entry.list;

import com.breadsticksmod.core.config.Buildable;
import com.breadsticksmod.core.config.entry.ConfigEntry;
import com.breadsticksmod.core.toml.Toml;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.List;

public class StringList extends ConfigEntry<List<String>> {
   public StringList(Component title, Object ref, Field field, Buildable<?, ?> parent) {
      super(title, ref, field, parent);
   }

   @Override
   protected AbstractFieldBuilder<List<String>, ?, ?> create(ConfigEntryBuilder builder) {
      return create(builder::startStrList);
   }

   @Override
   public void save(Toml writer) {
      writer.put(getKey(), get());
   }

   @Override
   protected @Nullable List<String> from(Toml object) {
      return object.getList(getKey(), String.class);
   }
}
