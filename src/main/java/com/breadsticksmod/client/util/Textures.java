package com.breadsticksmod.client.util;

import com.breadsticksmod.core.render.TextureInfo;

public interface Textures {
   interface TerritoryMenu {
      TextureInfo BACKGROUND = new TextureInfo("breadsticks", "textures/gui/territory_menu/background.png", 255, 190);
      TextureInfo FOREGROUND = new TextureInfo("breadsticks", "textures/gui/territory_menu/foreground.png", 226, 160);
      TextureInfo MASK = new TextureInfo("breadsticks", "textures/gui/territory_menu/mask.png", 226, 160);
      TextureInfo SCROLLBAR = new TextureInfo("breadsticks", "textures/gui/territory_menu/scrollbar.png", 9, 30);

      interface Production {
         int SIZE = 8;
         int BUFFER = 2;

         int TEX_SIZE = SIZE + BUFFER;

         TextureInfo EMERALD = new TextureInfo("breadsticks", "textures/gui/territory_menu/production/emerald.png", TEX_SIZE, TEX_SIZE);
         TextureInfo ORE = new TextureInfo("breadsticks", "textures/gui/territory_menu/production/ore.png", TEX_SIZE, TEX_SIZE);
         TextureInfo WOOD = new TextureInfo("breadsticks", "textures/gui/territory_menu/production/wood.png", TEX_SIZE, TEX_SIZE);
         TextureInfo FISH = new TextureInfo("breadsticks", "textures/gui/territory_menu/production/fish.png", TEX_SIZE, TEX_SIZE);
         TextureInfo CROP = new TextureInfo("breadsticks", "textures/gui/territory_menu/production/crop.png", TEX_SIZE, TEX_SIZE);
      }
   }
}
