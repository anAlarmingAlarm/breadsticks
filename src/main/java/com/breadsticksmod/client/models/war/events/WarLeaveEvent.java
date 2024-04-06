package com.breadsticksmod.client.models.war.events;

import com.breadsticksmod.client.models.war.War;

public class WarLeaveEvent extends WarEvent{
   private final Cause cause;

   public WarLeaveEvent(War war, Cause cause) {
      super(war);

      this.cause = cause;
   }

   public Cause getCause() {
      return cause;
   }

   public enum Cause {
      DEATH,
      HUB,
      CAPTURED
   }

   public WarLeaveEvent() {
      this(null, null);
   }

}
