package com.breadsticksmod.client.models.territory.eco;

import com.breadsticksmod.client.models.territory.eco.types.UpgradeType;

public record Upgrade(UpgradeType type, int level) {
   public long cost() {
      return type.getLevel(level).cost();
   }

   public double bonus() {
      return type.getLevel(level).bonus();
   }
}
