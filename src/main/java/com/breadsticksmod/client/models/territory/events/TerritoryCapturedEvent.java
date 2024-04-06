package com.breadsticksmod.client.models.territory.events;

import com.breadsticksmod.core.events.BaseEvent;

public class TerritoryCapturedEvent extends BaseEvent {
   private final String territory;
   private final String guild;

   public TerritoryCapturedEvent(String territory, String guild) {
      this.territory = territory;
      this.guild = guild;
   }

   public String getTerritory() {
      return territory;
   }

   public String getGuild() {
      return guild;
   }

   public TerritoryCapturedEvent() {
      this(null, null);
   }
}
