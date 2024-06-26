package com.breadsticksmod.client.models.raids.rooms;

public abstract class BaseBuilder implements Room.Builder {
   protected final String title;

   protected BaseBuilder(String title) {
      this.title = title;
   }
}
