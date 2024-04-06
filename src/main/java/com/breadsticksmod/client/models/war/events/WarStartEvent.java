package com.breadsticksmod.client.models.war.events;

import com.breadsticksmod.client.models.war.War;

public class WarStartEvent extends WarEvent{
   public WarStartEvent(War war) {
      super(war);
   }

   public WarStartEvent() {
      this(null);
   }
}