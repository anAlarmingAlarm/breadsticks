package com.breadsticksmod.core.toml;

import com.breadsticksmod.core.collector.SimpleCollector;
import com.breadsticksmod.core.json.Json;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface Toml extends Map<String, Object> {
   Toml set(final String key, final Object value);

   Toml remove(final String key);

   default Toml removeAll(final String... keys) {
      for (String key : keys) {
         remove(key);
      }

      return this;
   }

   default Toml removeAll(final Collection<String> c) {
      c.forEach(this::remove);

      return this;
   }

   default <T> Toml removeAll(final Collection<T> c, Function<T, String> mapper) {
      c.forEach(obj -> remove(mapper.apply(obj)));

      return this;
   }


   <T> T get(final String key, final Class<T> clazz);

   <T> T get(final String key, final T defaultValue);

   <T> T get(final String key, final Class<T> clazz, final T defaultValue);


   default boolean has(final String key) {
      return containsKey(key);
   }

   Number getNumber(final String key);

   default <T extends Number> T getNumber(final String key, Function<Number, T> getter) {
      Number number = getNumber(key);
      if (number == null) return null;

      return getter.apply(number);
   }

   Number getNumber(final String key, Number defaultValue);

   default <T extends Number> T getNumber(final String key, Number defaultValue, Function<Number, T> getter) {
      return getter.apply(getNumber(key, defaultValue));
   }

   Integer getInteger(final String key);

   int getInteger(final String key, final int defaultValue);

   Long getLong(final String key);
   long getLong(final String key, final long defaultValue);

   Float getFloat(final String key);

   float getFloat(final String key, final float defaultValue);

   Double getDouble(final String key);

   double getDouble(final String key, final double defaultValue);

   String getString(final String key);

   String getString(final String key, final String defaultValue);;

   Boolean getBoolean(final String key);

   boolean getBoolean(final String key, final boolean defaultValue);

   Date getDate(final String key);

   Date getDate(final String key, final Date defaultValue);

   default Date getDate(final String key, final long defaultValue) {
      return getDate(key, new Date(defaultValue));
   }
   
   List<Object> getList(final String key);

   List<Object> getList(final String key, final List<Object> defaultValue);

   <T> List<T> getList(final String key, final Class<T> clazz);

   <T> List<T> getList(final String key, final Class<T> clazz, final List<T> defaultValue);

   default <T extends Number> List<T> getList(String key, Function<Number, T> number) {
      List<Number> list = getList(key, Number.class);

      return list == null ? null : getList(key, Number.class).stream().map(number).collect(Collectors.toList());
   }
   default <T extends Number> List<T> getList(String key, List<T> defaultValue, Function<Number, T> number) {
      List<T> list = getList(key, number);

      return list == null ? defaultValue : list;
   }


   Toml getTable(final String key);

   Toml getTable(final String key, final Toml defaultValue);

   default @Nullable Class<?> getType(final String key) {
      return containsKey(key) ? get(key).getClass() : null;
   }

   default boolean isType(final String key, final Class<?> clazz) {
      Class<?> type = getType(key);

      return type != null && clazz.isAssignableFrom(type);
   }

   default boolean isPrimitive(final String key) {
      Class<?> type = getType(key);

      return type != null && type.isPrimitive();
   }

   default boolean isInteger(final String key) {
      return isType(key, Integer.class) || isType(key, int.class);
   }

   default boolean isLong(final String key) {
      return isType(key, Long.class) || isType(key, long.class);
   }

   default boolean isDouble(final String key) {
      return isType(key, Double.class) || isType(key, double.class);
   }

   default boolean isBoolean(final String key) {
      return isType(key, Boolean.class) || isType(key, boolean.class);
   }

   default boolean isString(final String key) {
      return isType(key, String.class);
   }

   default boolean isDate(final String key) {
      return isType(key, Date.class);
   }

   default boolean isList(final String key) {
      return isType(key, List.class);
   }

   default boolean isTable(final String key) {
      return isType(key, Toml.class);
   }

   String write();

   void write(File target) throws IOException;

   void write(OutputStream target) throws IOException;
   void write(Writer target) throws IOException;

   String toString();

   static Toml empty() {
      return new TomlObject();
   }

   static Toml of(String key, Object object) {
      return new TomlObject(key, object);
   }

   static Toml of(Json json) {
      var toml = empty();

      json.forEach((key, value) ->
              toml.set(key, value instanceof Json j ? of(j) : value));

      return toml;
   }

   static Toml parse(String string) {
      return TomlObject.from(string, me.shedaniel.cloth.clothconfig.shadowed.com.moandjiezana.toml.Toml::read);
   }


   static Toml read(File file) {
      return TomlObject.from(file, me.shedaniel.cloth.clothconfig.shadowed.com.moandjiezana.toml.Toml::read);
   }

   class Collector<T> extends SimpleCollector<T, Toml, Toml> {
      private final Function<T, String> keyMapper;
      private final Function<T, ?> valueMapper;

      @SuppressWarnings("unchecked")
      public Collector(Function<T, String> keyMapper) {
         this(keyMapper, value -> value);
      }

      @SuppressWarnings("unchecked")
      public Collector(Function<T, String> keyMapper, Function<T, ?> valueMapper) {
         this.keyMapper = keyMapper;
         this.valueMapper = valueMapper;
      }

      @Override
      protected Toml supply() {
         return Toml.empty();
      }

      @Override
      protected void accumulate(Toml container, T value) {
         container.set(keyMapper.apply(value), valueMapper.apply(value));
      }

      @Override
      protected Toml combine(Toml left, Toml right) {
         left.putAll(right);

         return left;
      }

      @Override
      protected Toml finish(Toml container) {
         return container;
      }
   }
}