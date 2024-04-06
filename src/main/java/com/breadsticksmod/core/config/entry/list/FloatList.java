package com.breadsticksmod.core.config.entry.list;

import com.breadsticksmod.core.annotated.Annotated;
import com.breadsticksmod.core.config.Buildable;
import com.breadsticksmod.core.config.entry.ConfigEntry;
import com.breadsticksmod.core.config.entry.value.FloatValue;
import com.breadsticksmod.core.toml.Toml;
import com.essentuan.acf.core.command.arguments.builtin.primitaves.Float.FloatMax;
import com.essentuan.acf.core.command.arguments.builtin.primitaves.Float.FloatMin;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.List;

public class FloatList extends ConfigEntry<List<Float>> {
   public FloatList(Component title, Object ref, Field field, Buildable<?, ?> parent) {
      super(title, ref, field, parent,
              Annotated.Optional(new FloatValue.Max()),
              Annotated.Optional(new FloatValue.Min())
      );
   }

   @Override
   protected AbstractFieldBuilder<List<Float>, ?, ?> create(ConfigEntryBuilder builder) {
      return create(builder::startFloatList)
              .setMax(getAnnotation(FloatMax.class, FloatMax::value))
              .setMin(getAnnotation(FloatMin.class, FloatMin::value));
   }

   @Override
   public void save(Toml writer) {
      writer.put(getKey(), get());
   }

   @Override
   protected @Nullable List<Float> from(Toml object) {
      return object.getList(getKey(), Number::floatValue);
   }
}
