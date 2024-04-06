package com.breadsticksmod.core.config.entry;

import com.breadsticksmod.core.config.Buildable;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;

public interface ConfigConstructor {
   ConfigEntry<?> create(Component title, Object ref, Field field, Buildable<?, ?> parent);
}
