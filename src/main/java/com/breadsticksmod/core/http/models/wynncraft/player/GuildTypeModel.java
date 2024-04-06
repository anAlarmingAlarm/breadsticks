package com.breadsticksmod.core.http.models.wynncraft.player;

import com.breadsticksmod.core.http.api.guild.Guild;
import com.breadsticksmod.core.http.api.player.Player;
import com.breadsticksmod.core.json.BaseModel;

public class GuildTypeModel extends BaseModel implements Player.Guild {
   @Key private String name;
   @Key private String prefix;
   @Key private Guild.Rank rank;

   @Override
   public String name() {
      return name;
   }

   @Override
   public String prefix() {
      return prefix;
   }

   @Override
   public Guild.Rank rank() {
      return rank;
   }
}
