package com.breadsticksmod.core.http.models.wynncraft.player;

import com.breadsticksmod.core.http.api.player.Profession;
import com.breadsticksmod.core.json.BaseModel;

public class ProfessionsModel extends BaseModel implements Profession {
   @Key int level;
   @Key int xpPercent;

   @Override
   public int level() {
      return level;
   }

   @Override
   public int xpPercent() {
      return xpPercent;
   }
}
