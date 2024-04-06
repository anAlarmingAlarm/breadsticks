package com.breadsticksmod.core;

import com.breadsticksmod.client.events.mc.MinecraftStartupEvent;
import com.breadsticksmod.core.config.Config;
import com.breadsticksmod.core.events.EventListener;
import com.breadsticksmod.core.heartbeat.Scheduler;
import com.breadsticksmod.core.heartbeat.Task;
import com.wynntils.core.components.Models;
import com.wynntils.models.worlds.type.WorldState;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static com.breadsticksmod.client.BreadsticksMain.CONFIG;

public abstract class Model extends Config implements EventListener, Scheduler {

   public Model() {
      REGISTER_TASKS();
   }

   private static boolean HAS_STARTED = false;

   @Override
   public boolean SHOULD_EXECUTE(Task task) {
      return HAS_STARTED && Models.WorldState.getCurrentState() == WorldState.WORLD;
   }

   @SubscribeEvent
   private static void onGameStart(MinecraftStartupEvent event) {
      HAS_STARTED = true;

      CONFIG.getConfigs().forEach(config -> {
         if (config instanceof Model model) {
            model.REGISTER_EVENTS();
         }
      });
   }
}
