package com.breadsticksmod.client.models.war.events;

import com.breadsticksmod.client.models.war.War;

public class WarCompleteEvent extends WarEvent {
   public WarCompleteEvent(War war) {
      super(war);
   }

   public WarCompleteEvent() {
      this(null);
   }
}
