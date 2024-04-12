package com.breadsticksmod.client.features;

import com.breadsticksmod.client.BreadsticksMain;
import com.breadsticksmod.client.util.ChatUtil;
import com.breadsticksmod.core.Default;
import com.breadsticksmod.core.Feature;
import com.breadsticksmod.core.Promise;
import com.breadsticksmod.core.State;
import com.breadsticksmod.core.config.Config;
import com.breadsticksmod.core.http.requests.Update;
import com.breadsticksmod.core.time.ChronoUnit;
import com.wynntils.core.text.StyledText;
import com.wynntils.models.worlds.event.WorldStateEvent;
import com.wynntils.utils.FileUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Default(State.ENABLED)
@Config.Category("Updates")
@Feature.Definition(name = "Check for updates on startup")
public class AutoUpdateFeature extends Feature {

   @Value("Auto Update")
   @Default(State.DISABLED)
   @Tooltip("If enabled, the mod will update itself on startup if a new version is available. Does nothing unless the above option is enabled.")
   private static boolean autoUpdate = false;

   private static final Path TEMP_DIRECTORY = FabricLoader.getInstance().getGameDir().resolve("temp").resolve("breadsticks-update.jar");
   public static AutoUpdateFeature THIS;

   @Override
   protected void onInit() {
      THIS = this;
      Runtime.getRuntime().addShutdownHook(new Thread(() -> {
         File temp = getTempFile();
         if (!temp.exists()) return;
         else if (temp.isDirectory()) {
            temp.delete();
            return;
         }

         try {
            FileUtils.copyFile(temp, BreadsticksMain.getJar());
            temp.delete();
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      }));
   }

   public void checkForUpdate(WorldStateEvent event) {
      super.onJoinWorld(event);

      if (!event.isFirstJoinWorld() || !THIS.isEnabled() || FabricLoader.getInstance().isDevelopmentEnvironment()) return;

      update(true).thenAccept(result -> {
         if (result == Result.ON_LATEST) return;

         ChatUtil.message(result.getMessage());
      });
   }

   public static Promise<Result> update(boolean auto) {
      if (FabricLoader.getInstance().isDevelopmentEnvironment()) return Promise.of(Result.DEV_ENV);

      File temp = getTempFile();
      if (temp.exists()) FileUtils.deleteFile(temp);

      return new Update.Request()
              .<Result>thenApplyStage((promise, optional) -> {
                 Update update;

                 if (optional.isEmpty()) {
                    promise.complete(Result.ERROR);
                    return;
                 } else update = optional.get();

                 if (!update.greaterThan(BreadsticksMain.getVersion())) {
                    promise.complete(Result.ON_LATEST);
                    return;
                 }

                 if (!auto || autoUpdate) {
                    FileUtils.createNewFile(temp);

                    try {
                       InputStream download = update.download();

                       Files.copy(download, temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
                       promise.complete(Result.SUCCESSFUL);
                    } catch (IOException e) {
                       if (temp.exists()) FileUtils.deleteFile(temp);
                       LOGGER.error("Error while downloading update", e);
                       temp.delete();

                       promise.complete(Result.ERROR);
                    }
                 } else {
                    promise.complete(Result.AVAILABLE);
                 }

              }).completeOnTimeout(Result.ERROR, 1, ChronoUnit.MINUTES);
   }

   private static File getTempFile() {
      File file = TEMP_DIRECTORY.toFile();
      File tempDir = file.getParentFile();

      if (!tempDir.exists() && !tempDir.mkdirs())
         throw new RuntimeException("Could not create directories for %s".formatted(tempDir));

      return file;
   }

   public enum Result {
      SUCCESSFUL("Successfully downloaded update, it will be applied on shutdown", ChatFormatting.GREEN),
      AVAILABLE("Update available! Type '/bs update' to download it automatically", ChatFormatting.GREEN),
      ON_LATEST("Already on the latest version", ChatFormatting.YELLOW),
      DEV_ENV("Development environment detected, cancelling", ChatFormatting.RED),
      ERROR("Error occurred while checking for updates", ChatFormatting.RED);

      private final StyledText message;

      Result(String message, ChatFormatting... formattings) {
         this.message = StyledText.fromComponent(ChatUtil.component(message, formattings));
      }

      public StyledText getMessage() {
         return message;
      }
   }
}
