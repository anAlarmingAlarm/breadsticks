package com.breadsticksmod.client.models.raids.events;

import com.breadsticksmod.client.models.raids.Raid;
import com.breadsticksmod.client.models.raids.RaidModel;
import com.breadsticksmod.core.events.BaseEvent;

public abstract class RaidEvent extends BaseEvent {
   private final Raid raid;

   RaidEvent(Raid raid) {
      this.raid = raid;
   }

   public Raid getRaid() {
      return raid;
   }

   public RaidEvent() {
      this(null);
   }

   public static class Start extends RaidEvent {
      public Start(Raid raid) {
         super(raid);
      }

      @Deprecated
      public Start() {}
   }

   public static class Complete extends RaidEvent {
      private final boolean isPB;

      public Complete(Raid raid) {
         super(raid);

         isPB = RaidModel.getPB(raid.type())
                 .map(previous ->
                         raid.duration().orElseThrow().lessThanOrEqual(previous.duration().orElseThrow()))
                 .orElse(true);
      }

      @Deprecated
      public Complete() {
         isPB = false;
      }

      public boolean isPB() {
         return isPB;
      }
   }

   public static class Fail extends RaidEvent {
      public Fail(Raid raid) {
         super(raid);
      }

      @Deprecated
      public Fail() {}
   }
}
