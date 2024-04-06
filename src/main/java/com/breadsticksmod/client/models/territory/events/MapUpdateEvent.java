package com.breadsticksmod.client.models.territory.events;

import com.breadsticksmod.core.http.requests.mapstate.MapState;
import com.breadsticksmod.core.events.BaseEvent;

public class MapUpdateEvent extends BaseEvent {
   private final MapState list;

   public MapUpdateEvent(MapState list) {
      this.list = list;
   }

   public MapState getState() {
      return list;
   }

   public MapUpdateEvent() {
      this.list = MapState.empty();
   }
}
