package com.breadsticksmod.core.http.models.wynncraft.guild.banner;

import com.breadsticksmod.core.http.api.guild.Guild;
import com.breadsticksmod.core.json.BaseModel;
import com.breadsticksmod.core.json.Json;

public class BaseLayer extends BaseModel implements Guild.Banner.Layer {
   @Key("colour") private Guild.Banner.Color color;
   @Key private Guild.Banner.Pattern pattern;

   @Override
   protected void onPreLoad(Json json) {
      if (json.has("color"))
         json.set("colour", json.getString("color"));
   }

   @Override
   public Guild.Banner.Color color() {
      return color;
   }

   @Override
   public Guild.Banner.Pattern pattern() {
      return pattern;
   }
}
