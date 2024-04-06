package com.breadsticksmod.client.models.war.events;

import com.breadsticksmod.client.models.war.War;
import com.breadsticksmod.core.events.BaseEvent;

public abstract class WarEvent extends BaseEvent {
   private final War war;

   public WarEvent(War war) {
      this.war = war;
   }

   public War getWar() {
      return war;
   }

   public WarEvent() {
      this(null);
   }
}
