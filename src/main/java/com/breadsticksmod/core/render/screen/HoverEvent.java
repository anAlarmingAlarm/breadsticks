package com.breadsticksmod.core.render.screen;

public enum HoverEvent {
   PRE,
   POST;

   public interface Handler<T> {
      void accept(int mouseX, int mouseY, T widget);
   }
}
