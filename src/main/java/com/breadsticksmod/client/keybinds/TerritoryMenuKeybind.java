package com.breadsticksmod.client.keybinds;

import com.breadsticksmod.client.features.keybinds.Keybind;
import com.breadsticksmod.client.features.war.TerritoryHelperMenuFeature;
import com.mojang.blaze3d.platform.InputConstants;

import static com.wynntils.utils.mc.McUtils.mc;

@Keybind.Definition(name = "Territory Menu", defaultKey = InputConstants.KEY_U)
public class TerritoryMenuKeybind extends Keybind {
   public TerritoryMenuKeybind() {
      super(TerritoryMenuKeybind.class);
   }

   @Override
   protected void onKeyDown() {
      TerritoryHelperMenuFeature.OPEN_TERRITORY_MENU = true;
      mc().getConnection().sendCommand("gu manage");
   }
}
