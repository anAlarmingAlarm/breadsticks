package com.breadsticksmod.client;

import com.breadsticksmod.core.BreadsticksExtension;
import com.breadsticksmod.core.config.ModConfig;
import com.breadsticksmod.core.events.BaseEvent;
import com.breadsticksmod.core.heartbeat.Heartbeat;
import com.wynntils.core.WynntilsMod;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.minecraftforge.eventbus.api.IEventBus;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.reflect.Field;

public class BreadsticksMain implements ClientModInitializer, BreadsticksExtension {
   public static Reflections CLASS_SCANNER;

   public static final Logger LOGGER = LoggerFactory.getLogger("breadsticks");

   public static ModConfig CONFIG;

   private static Field EVENT_BUS;

   private static ModContainer CONTAINER;

   @Override
   public void onInitializeClient() {
      ConfigurationBuilder builder = new ConfigurationBuilder();

      CONTAINER = FabricLoader.getInstance().getModContainer("breadsticks").orElseThrow(
              () -> new RuntimeException("no breadsticks...")
      );

      FabricLoader.getInstance()
              .getEntrypointContainers("breadsticks", BreadsticksExtension.class)
              .forEach(container -> builder.forPackage(container.getEntrypoint().getPackage()));

      builder.addScanners(Scanners.SubTypes, Scanners.TypesAnnotated, Scanners.MethodsAnnotated);

      CLASS_SCANNER = new Reflections(builder);

      for (Field field : WynntilsMod.class.getDeclaredFields()) {
         if (field.getName().equals("eventBus")) {
            EVENT_BUS = field;

            EVENT_BUS.setAccessible(true);

            break;
         }
      }

      if (EVENT_BUS == null) {
         throw new RuntimeException("Could not find the event bus");
      }

      BaseEvent.validate();
      Heartbeat.create();

      new ModConfig();
   }

   public static Version getVersion() {
      return CONTAINER.getMetadata().getVersion();
   }

   public static File getJar() {
      return CONTAINER.getOrigin().getPaths().get(0).toFile();
   }

   public static IEventBus getEventBus() {
      try {
         return (IEventBus) EVENT_BUS.get(null);
      } catch (IllegalAccessException e) {
         throw new RuntimeException(e);
      }
   }

   @Override
   public String getPackage() {
      return "com.breadsticksmod";
   } //

   @Override
   public String[] getSounds() {
      return new String[]{
              "breadsticks:war.horn",
              "breadsticks:cheep.cheep",
              "breadsticks:alert",
              "breadsticks:alert.extended",
              "breadsticks:home.depot",
              "breadsticks:home.depot.extended",
              "breadsticks:flute",
              "breadsticks:high.beep",
              "breadsticks:low.beep"
      };
   }
}
