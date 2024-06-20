package com.breadsticksmod.client.models.war.timer;

import com.breadsticksmod.client.models.war.Defense;
import com.breadsticksmod.core.time.Duration;
import com.breadsticksmod.core.time.ChronoUnit;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public final class Timer implements Comparable<Timer> {
   private final String territory;
   private final Date start;
   private final Duration timer;
   private final String owner;

   public String queuer = "";

   boolean confident = false;

   boolean personal = false;

   Defense defense = Defense.UNKNOWN;

   Timer(String territory, Duration timer, String owner) {
      this.territory = territory;
      this.start = new Date();
      this.timer = timer;
      this.owner = owner;
   }

   public Timer(String territory, Duration timer, String owner, Defense defense) {
      this.territory = territory;
      this.start = new Date();
      this.timer = timer.add(499, ChronoUnit.MILLISECONDS);
      this.owner = owner;
      this.defense = defense;
      this.confident = true;
   }

   public Timer(String territory, Date start, Duration timer, String owner, Defense defense, boolean confident, boolean personal, String queuer) {
      this.territory = territory;
      this.start = start;
      this.timer = timer;
      this.owner = owner;
      this.defense = defense;
      this.confident = confident;
      this.personal = personal;
      this.queuer = queuer;
   }

   public String getTerritory() {
      return territory;
   }

   public Date getStart() {
      return start;
   }

   public Duration getDuration() {
      return timer;
   }

   public String getOwner() {
      return owner;
   }

   public Defense getDefense() {
      return defense;
   }

   public boolean isConfident() {
      return confident;
   }

   public boolean isPersonal() {
      return personal;
   }

   public Duration getRemaining() {
      return timer.minus(Duration.since(start));
   }

   @Override
   public String toString() {
      return "Timer{" +
              "territory='" + territory + '\'' +
              ", start=" + start +
              ", timer=" + timer +
              ", confident=" + confident +
              ", defense=" + defense +
              '}';
   }

   @Override
   public int compareTo(@NotNull Timer o) {
      return compare(this, o);
   }

   public static int compare(Timer timer1, Timer timer2) {
      int result = Duration.compare(timer1.getRemaining(), timer2.getRemaining());
      if (result == 0) return timer1.getTerritory().compareTo(timer2.getTerritory());

      return result;
   }
}
