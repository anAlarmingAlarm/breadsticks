package com.breadsticksmod.core.config;

public interface Buildable<T, R> {
   String getKey();

   R build(T obj);
}
