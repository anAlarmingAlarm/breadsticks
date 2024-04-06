package com.breadsticksmod.client.models.war.timer.events;

import com.breadsticksmod.client.models.war.timer.Timer;
import com.breadsticksmod.core.events.BaseEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class TimerStartEvent extends BaseEvent {
   private final Timer timer;
   private final boolean fromScoreboard;

   public TimerStartEvent(Timer timer, boolean fromScoreboard) {
      this.timer = timer;
      this.fromScoreboard = fromScoreboard;
   }

   public Timer getTimer() {
      return timer;
   }

   public boolean isFromScoreboard() {
      return fromScoreboard;
   }

   public TimerStartEvent() {
      this(null, false);
   }
}
