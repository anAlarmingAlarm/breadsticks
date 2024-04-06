package com.breadsticksmod.core;

public interface BreadsticksExtension {
   String getPackage();

   //This is mainly for SoundProvider
   default String[] getSounds() {
      return new String[0];
   }
}
