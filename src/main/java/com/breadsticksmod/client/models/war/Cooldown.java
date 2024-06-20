package com.breadsticksmod.client.models.war;

import com.breadsticksmod.core.time.Duration;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public final class Cooldown implements Comparable<Cooldown> {
   private final String territory;
   private final Date start;
   private final Duration length;
   private final String owner;

   public Cooldown(String territory, Duration length, String owner) {
      this.territory = territory;
      this.start = new Date();
      this.length = length;
      this.owner = owner;
   }

   public String getTerritory() {
      return territory;
   }

   public Date getStart() {
      return start;
   }

   public Duration getLength() {
      return length;
   }

   public String getOwner() {
      return owner;
   }

   public Duration getRemaining() {
      return length.minus(Duration.since(start));
   }

   @Override
   public String toString() {
      return "Cooldown{" +
              "territory='" + territory + '\'' +
              ", start=" + start +
              ", length=" + length +
              '}';
   }

   @Override
   public int compareTo(@NotNull Cooldown o) {
      return compare(this, o);
   }

   public static int compare(Cooldown timer1, Cooldown timer2) {
      int result = Duration.compare(timer1.getRemaining(), timer2.getRemaining());
      if (result == 0) return timer1.getTerritory().compareTo(timer2.getTerritory());

      return result;
   }
}
