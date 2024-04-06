package com.breadsticksmod.client.features.keybinds;

import com.breadsticksmod.client.events.mc.MinecraftStartupEvent;
import com.breadsticksmod.core.events.EventListener;
import com.breadsticksmod.core.Feature;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import static com.breadsticksmod.client.BreadsticksMain.CLASS_SCANNER;

@Feature.Definition(name = "Keybind Loader", description = "Loads and creates keybinds", required = true)
public class KeybindFeature extends Feature {
   private static final Map<Class<?extends Keybind>, Keybind> keybindMap = new HashMap<>();

   @Instance
   private static KeybindFeature THIS;

   protected void onInit() {
      CLASS_SCANNER.getSubTypesOf(Keybind.class).forEach(clazz -> {
         try {
            if (clazz == Keybind.class) return;

            Keybind keybind = clazz.getConstructor().newInstance();

            keybindMap.put(clazz, keybind);

            KeyBindingHelper.registerKeyBinding(keybind);
         } catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException("Failed to register keybind (%s)".formatted(clazz.getName()), e);
         }
      });
   }

   @SubscribeEvent
   private static void onGameStart(MinecraftStartupEvent event) {
      keybindMap.values().forEach(EventListener::REGISTER_EVENTS);
   }

                              @SuppressWarnings("unchecked")
   public <T> T getKey(Class<? extends Keybind> T) {
      return (T) keybindMap.get(T);
   }
   public static <T> T getKeybind(Class<? extends Keybind> T) {
      return THIS.getKey(T);
   }
}
