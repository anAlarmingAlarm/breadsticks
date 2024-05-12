package com.breadsticksmod.client.features.war;

import com.breadsticksmod.client.config.providers.sound.SoundProvider;
import com.breadsticksmod.client.models.territory.TerritoryModel;
import com.breadsticksmod.client.models.territory.events.TerritoryCapturedEvent;
import com.breadsticksmod.client.util.ChatUtil;
import com.breadsticksmod.client.util.SoundUtil;
import com.breadsticksmod.client.util.Sounds;
import com.breadsticksmod.core.Default;
import com.breadsticksmod.core.Feature;
import com.breadsticksmod.core.State;
import com.breadsticksmod.core.config.Config;
import com.breadsticksmod.core.heartbeat.annotations.Schedule;
import com.breadsticksmod.core.http.requests.mapstate.Territory;
import com.breadsticksmod.core.text.TextBuilder;
import com.breadsticksmod.core.time.ChronoUnit;
import com.wynntils.core.components.Models;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static net.minecraft.ChatFormatting.RED;

@Default(State.DISABLED)
@Config.Category("War")
@Feature.Definition(name = "Alert when Territory Captured")
public class AlertWhenTerrTakenFeature extends Feature {
   @Value("Cooldown")
   @Tooltip("The time in seconds that no territories must be captured for before another alert can happen (set to 0 or lower to make it always trigger an alert)")
   private static int cooldown = 600;

   @Dropdown(title = "Selected sound", options = SoundProvider.class)
   private static SoundEvent sound = Sounds.ALERT;

   @Value("Sound volume")
   private static Float volume = 1.0F;

   @Value("Sound pitch")
   private static Float pitch = 1.0F;

   @Value("Only alert for specific terrs")
   private static boolean alertForSpecificTerrs = false;

   @Array("Alerted territories")
   @Tooltip("If the above option is enabled, only these territories will trigger an alert when taken")
   private static List<String> alertTerrs = List.of();

   @Value("Use terrcheck file")
   @Tooltip("If 'Only alert for specific terrs' is enabled and this isn't blank, this tcf file will override the above option (run '/bs tc help' for more info, for this only '-' prefix is supported)")
   private static String terrcheckFile = "";

   private static int timeSince = 2147483647;

   @Schedule(rate = 1, unit = ChronoUnit.SECONDS)
   private void timer() {
      if (timeSince < cooldown) timeSince++;
   }

   @SubscribeEvent
   public void onTerritoryCapture(TerritoryCapturedEvent event) {
      boolean tcMatch = false;
      if (!terrcheckFile.isEmpty() && alertForSpecificTerrs) {
         try {
            File tcFile = FabricLoader.getInstance().getConfigDir().resolve("breadsticks\\terrcheckfiles\\" + terrcheckFile + ".txt").toFile();
            Scanner scanner = new Scanner(tcFile);
            scanner.nextLine();
            String code = scanner.nextLine();
            scanner.close();
            tcMatch = terrCheck(event, code);
         } catch (FileNotFoundException e) {
            ChatUtil.message(TextBuilder.of("Could not find file", RED));
            return;
         } catch (Exception e) {
            ChatUtil.message(TextBuilder.of("Failed to open file", RED));
            ChatUtil.message(e.getMessage());
            return;
         }
      }
      if (!alertForSpecificTerrs || tcMatch || (terrcheckFile.isEmpty() && alertTerrs.contains(event.getTerritory()))) {
         if (timeSince >= cooldown) {
            SoundUtil.playAmbient(sound, pitch, volume);
         }
         timeSince = 0;
      }
   }

   private static boolean terrCheck(TerritoryCapturedEvent event, String code) {
      String name = event.getTerritory();
      String playerGuild = Models.Guild.getGuildName();

      List<Territory> territories = new ArrayList<>(TerritoryModel.getTerritoryList().stream().toList());
      territories.sort(Comparator.comparing(territory -> territory.getName().toLowerCase()));
      int i = 0;
      while (!territories.get(i).getName().equals(name)) i++;
      if (code.length() < i / 2) return false;

      if (i % 2 == 0) {
         i /= 2;
         int filter = Integer.parseInt(String.valueOf(code.charAt(i)));
         if (filter == 0) return false;
         if (filter % 3 == 1) return !event.getGuild().equals(playerGuild);
         if (filter % 3 == 2) return event.getGuild().equals(playerGuild);
      } else {
         i /= 2;
         int filter = Integer.parseInt(String.valueOf(code.charAt(i)));
         if (filter == 0) return false;
         if (filter / 3 == 1) return !event.getGuild().equals(playerGuild);
         if (filter / 3 == 2) return event.getGuild().equals(playerGuild);
      }
      return false;
   }
}
