package com.breadsticksmod.core.config.entry.value;

import com.breadsticksmod.core.annotated.Annotated;
import com.breadsticksmod.core.config.Buildable;
import com.breadsticksmod.core.config.entry.ConfigEntry;
import com.essentuan.acf.core.command.arguments.builtin.primitaves.Float.FloatMax;
import com.essentuan.acf.core.command.arguments.builtin.primitaves.Float.FloatMin;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import net.minecraft.network.chat.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class FloatValue extends ConfigEntry<Float> {
   public record Min() implements FloatMin {
      @Override
      public float value() {
         return Float.MIN_VALUE;
      }

      @Override
      public Class<? extends Annotation> annotationType() {
         return FloatMin.class;
      }
   }

   public record Max() implements FloatMax {
      @Override
      public float value() {
         return Long.MAX_VALUE;
      }

      @Override
      public Class<? extends Annotation> annotationType() {
         return FloatMax.class;
      }
   }

   public FloatValue(Component title, Object ref, Field field, Buildable<?, ?> parent) {
      super(title, ref, field, parent,
              Annotated.Optional(new Max()),
              Annotated.Optional(new Min())
      );
   }


   @Override
   protected AbstractFieldBuilder<Float, ?, ?> create(ConfigEntryBuilder builder) {
      return create(builder::startFloatField)
              .setMax(getAnnotation(FloatMax.class, FloatMax::value))
              .setMin(getAnnotation(FloatMin.class, FloatMin::value));
   }
}
