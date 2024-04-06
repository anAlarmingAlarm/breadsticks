package com.breadsticksmod.core.json.codecs;

import com.breadsticksmod.core.UnexpectedException;
import com.breadsticksmod.core.collector.EnumMapCollector;
import com.breadsticksmod.core.collector.LinkedMapCollector;
import com.breadsticksmod.core.json.AbstractCodec;
import com.breadsticksmod.core.json.Annotations;
import com.breadsticksmod.core.json.BaseModel;
import com.breadsticksmod.core.json.Json;
import com.breadsticksmod.core.util.Priority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collector;

@AbstractCodec.Definition(value = Map.class, priority = Priority.LOWEST)
@SuppressWarnings({"rawtypes", "unchecked"})
public class MapCodec extends AbstractCodec<Map, Json> {
   @Override
   public @Nullable Json write(Map value, Class<?> type, Annotations annotations, Type... typeArgs) throws Exception {
      Class<?> keyType = getClass(typeArgs[0]);
      Class<?> valueType = getClass(typeArgs[1]);

      AbstractCodec keyCodec = get(keyType);
      Type[] keyArgs = getTypeArgs(typeArgs[0]);

      AbstractCodec valueCodec = get(valueType);
      Type[] valueArgs = getTypeArgs(typeArgs[1]);

      return (Json) value.entrySet()
              .stream()
              .collect(new Json.Collector<Map.Entry>(
                      entry -> {
                         try {
                            return keyCodec.toString(entry.getKey(), keyType, keyArgs);
                         } catch (Exception e) {
                            throw UnexpectedException.propagate(e);
                         }
                      },
                      entry -> {
                         try {
                            return valueCodec.write(entry.getValue(), valueType, Annotations.empty(), valueArgs);
                         } catch (Exception e) {
                            throw UnexpectedException.propagate(e);
                         }
                      }
              ));
   }

   @Override
   public @Nullable Map read(@NotNull Json value, Class<?> type, Annotations annotations, Type... typeArgs) throws Exception {
      Class<?> keyType = getClass(typeArgs[0]);
      Class<?> valueType = getClass(typeArgs[1]);

      AbstractCodec keyCodec = get(keyType);
      Type[] keyArgs = getTypeArgs(typeArgs[0]);

      AbstractCodec valueCodec = get(valueType);
      Type[] valueArgs = getTypeArgs(typeArgs[1]);

      Function<Map.Entry<String, Object>, Object> keyMapper = entry -> {
         try {
            return keyCodec.fromString(entry.getKey(), keyType, keyArgs);
         } catch (Exception e) {
            throw UnexpectedException.propagate(e);
         }
      };

      Function<Map.Entry<String, Object>, Object> valueMapper = entry -> {
         try {
            return valueCodec.read(entry.getValue(), valueType, Annotations.empty(), valueArgs);
         } catch (Exception e) {
            throw UnexpectedException.propagate(e);
         }
      };

      Collector<Map.Entry<String, Object>, Map, Map> collector = keyCodec instanceof EnumCodec ?
              new EnumMapCollector(keyType, keyMapper, valueMapper) :
              new LinkedMapCollector(keyMapper, valueMapper);

      Map finished = value.entrySet()
              .stream()
              .collect(collector);

      if (!annotations.contains(BaseModel.Null.class))
         finished.values().removeIf(Objects::isNull);

      if (annotations.contains(BaseModel.Final.class))
         return Map.copyOf(finished);
      else
         return finished;
   }

   @Override
   public Map fromString(String string, Class<?> type, Type... typeArgs) throws Exception {
      throw new UnsupportedOperationException();
   }

   @Override
   public String toString(Map value, Class<?> type, Type... typeArgs) throws Exception {
      throw new UnsupportedOperationException();
   }
}
