package com.breadsticksmod.client.keybinds;

import com.breadsticksmod.client.features.keybinds.Keybind;
import com.mojang.blaze3d.platform.InputConstants;

@Keybind.Definition(name = "Auto Attack", defaultKey = InputConstants.KEY_MINUS)
public class AutoAttackKeybind extends Keybind {
    private static AutoAttackKeybind THIS;

    public AutoAttackKeybind() {
        super(AutoAttackKeybind.class);
        THIS = this;
    }
    public static boolean isKeyDown() {
        return THIS.isDown();
    }

    public static int getClickNum() {
        return THIS.getClickCount();
    }
}