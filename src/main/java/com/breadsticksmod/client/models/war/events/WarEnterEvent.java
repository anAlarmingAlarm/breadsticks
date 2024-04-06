package com.breadsticksmod.client.models.war.events;

import com.breadsticksmod.client.models.war.War;

public class WarEnterEvent extends WarEvent {
   public WarEnterEvent(War war) {
      super(war);
   }

   public WarEnterEvent() {
      this(null);
   }
}