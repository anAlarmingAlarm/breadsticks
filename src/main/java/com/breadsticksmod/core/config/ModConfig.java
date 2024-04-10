package com.breadsticksmod.core.config;

import com.breadsticksmod.client.events.mc.MinecraftStopEvent;
import com.breadsticksmod.client.util.ChatUtil;
import com.breadsticksmod.core.Feature;
import com.breadsticksmod.core.Model;
import com.breadsticksmod.core.config.entry.ConfigEntry;
import com.breadsticksmod.core.render.overlay.Hud;
import com.breadsticksmod.core.toml.Toml;
import com.breadsticksmod.core.util.ClassOrdering;
import com.breadsticksmod.core.util.Reflection;
import com.wynntils.core.components.Managers;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.breadsticksmod.client.BreadsticksMain.CLASS_SCANNER;
import static com.breadsticksmod.client.BreadsticksMain.CONFIG;
import static com.wynntils.utils.mc.McUtils.mc;

public class ModConfig implements Buildable<Screen, ConfigBuilder> {
   private final Map<Class<? extends Config>, Config> sections = new LinkedHashMap<>();
   private final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("breadsticks.toml").toFile();
   private final File FUY_CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("fuy_gg.toml").toFile();

   @SuppressWarnings({"rawtypes", "unchecked"})
   public ModConfig() {
      CONFIG = this;

      CLASS_SCANNER.getSubTypesOf(Config.class).stream()
              .sorted(new ClassOrdering(
                      Comparator.comparing(Class::getSimpleName),
                      Model.class,
                      Hud.Element.class,
                      Feature.class,
                      Config.class
              )).forEach(clazz -> {
                 try {
                    if (Reflection.isAbstract(clazz)) return;

                    Constructor<Config> constructor = (Constructor<Config>) clazz.getDeclaredConstructor();
                    constructor.setAccessible(true);

                    Config config = constructor.newInstance();

                    config.getEntries().forEach(entry -> ((ConfigEntry) entry).setDefault(entry.get()));

                    sections.put(config.getClass(), config);
                 } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                          NoSuchMethodException e) {
                    throw new RuntimeException(e);
                 }
              });

      sections.forEach((clazz, config) -> config.setInstances());

      load();
   }

   @Override
   public String getKey() {
      return null;
   }

   public File getConfigFile() {
      return CONFIG_FILE;
   }

   public File getFuyConfigFile() {
      return FUY_CONFIG_FILE;
   }

   @SuppressWarnings("unchecked")
   public <T extends Config> T getConfig(Class<?> clazz) {
      return (T) sections.get(clazz);
   }

   public Collection<Config> getConfigs() {
      return sections.values();
   }

   public void save() {
      Toml toml = Toml.empty();

      sections.values().forEach(section -> section.getEntries().forEach(entry -> entry.save(toml)));

      try {
         toml.write(CONFIG_FILE);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   public void load() {
      final Toml config;
      if (!getConfigFile().exists()) {
         if (!getFuyConfigFile().exists()) return;
         config = Toml.read(getFuyConfigFile());
      } else {
         config = Toml.read(getConfigFile());
      }

      sections.values().forEach(section -> section.getEntries().forEach(entry -> entry.load(config)));

      try {
         if (!Files.isDirectory(Paths.get(FabricLoader.getInstance().getConfigDir() + "\\breadsticks\\terrcheckfiles"))) {
            if (!Files.isDirectory(Paths.get(FabricLoader.getInstance().getConfigDir() + "\\breadsticks")))
               Files.createDirectory(Paths.get(FabricLoader.getInstance().getConfigDir() + "\\breadsticks"));
            Files.createDirectory(Paths.get(FabricLoader.getInstance().getConfigDir() + "\\breadsticks\\terrcheckfiles"));
            File tcFile = new File(FabricLoader.getInstance().getConfigDir() + "\\breadsticks\\terrcheckfiles\\FFA.txt");
            if (tcFile.createNewFile()) {
               List<String> lines = Arrays.asList(
                       "-",
                       "00000304000030000300000000000300000000000000000034000000000000000000030000000130000310000440000004100040000000001000000000000000000001000000303000100001",
                       "",
                       "This is an example terrcheck file. Terrcheck files essentially act as shortcuts for the terrcheck command, allowing users to run \"/bs tcf [filename]\" instead of \"/bs tc [prefix] [code that's probably at least 100 numbers long]\".",
                       "Line 1 is for the guild. It is case-sensitive and must be entered without brackets. Alternatively, you can use a - to use the player's current guild.",
                       "Line 2 is for the territory code. This a series of numbers representing a list of expected states (any (ignore), owned, or unowned). You can generate one using this spreadsheet: https://docs.google.com/spreadsheets/d/1zZhAEYI247FDhfx7j72muBX1xRWodsZGpIDG196mHnM/edit?usp=sharing",
                       "Lines 3 and beyond are ignored and can be safely used for descriptions, notes, credits, etc.",
                       "Any .txt files in the config\\breadsticks\\terrcheckfiles folder can be used for the terrcheckfile command via their name (e.g. to run this file, type /bs tcf FFA)."
               );
               Files.write(tcFile.toPath(), lines);
            }
         }
      } catch (Exception e) {
         ChatUtil.message("Failed to create example terrcheck file: " + e, ChatFormatting.RED);
      }
   }

   @Override
   public ConfigBuilder build(Screen screen) {
      ConfigBuilder builder = ConfigBuilder.create()
              .setTitle(Component.literal("breadsticks | Configuration"))
              .setSavingRunnable(this::save)
              .setShouldListSmoothScroll(true)
              .setShouldTabsSmoothScroll(true)
              .setTransparentBackground(true);

      if (screen != null) builder.setParentScreen(screen);

      Map<String, ModCategory> categories = new LinkedHashMap<>();
      categories.put("General", new ModCategory("General"));

      sections.values().stream()
              .sorted(Comparator.comparing(config -> config.getClass().getSimpleName()))
              .flatMap(config -> config.getEntries().stream())
              .forEach(entry -> {
                 if (!categories.containsKey(entry.getCategory())) categories.put(entry.getCategory(), new ModCategory(entry.getCategory()));

                 categories.get(entry.getCategory()).add(entry);
              });

      categories.values().forEach(category -> category.build(builder));

      return builder;
   }

   public void open() {
      Managers.TickScheduler.scheduleNextTick(() -> mc().setScreen(CONFIG.build(mc().screen).build()));
   }

   @SubscribeEvent
   private static void onMinecraftStop(MinecraftStopEvent event) {
      CONFIG.save();
   }
}
