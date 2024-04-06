package com.breadsticksmod.client.features.war;

import com.breadsticksmod.client.models.territory.TerritoryModel;
import com.breadsticksmod.client.models.war.timer.Timer;
import com.breadsticksmod.client.models.war.timer.TimerModel;
import com.breadsticksmod.core.Default;
import com.breadsticksmod.core.Feature;
import com.breadsticksmod.core.State;
import com.breadsticksmod.core.http.requests.mapstate.Territory;
import com.breadsticksmod.core.config.Config;
import com.breadsticksmod.core.render.Beacon;
import com.breadsticksmod.core.render.Renderer;
import com.wynntils.core.components.Models;
import com.wynntils.core.text.StyledText;
import com.wynntils.mc.event.TickEvent;
import com.wynntils.models.marker.type.StaticLocationSupplier;
import com.wynntils.services.map.pois.MarkerPoi;
import com.wynntils.utils.colors.CommonColors;
import com.wynntils.utils.colors.CustomColor;
import com.wynntils.utils.mc.type.Location;
import com.wynntils.utils.render.Texture;
import com.wynntils.utils.render.type.TextShadow;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

@Default(State.ENABLED)
@Config.Category("War")
@Feature.Definition(name = "Beacon on Timers", override = "Show beacon at timers")
public class BeaconOnTimersFeature extends Feature implements Beacon.Provider {
   @Value("Only show soonest timer")
   @Tooltip("If enabled, only the beacon for the war that will happen the soonest will show")
   private static boolean soonestOnly = false;
   @Value("Also show timers with a minute of soonest")
   @Tooltip("If enabled, also shows wars within a minute of the soonest timer (does nothing if the above setting is disabled)")
   private static boolean soonestMinute = false;

   private final List<Beacon> BEACONS = new ArrayList<>();
   private final List<MarkerPoi> POIS = new ArrayList<>();

   public BeaconOnTimersFeature() {
      Renderer.beacon(this);
   }

   @SubscribeEvent
   public void onTick(TickEvent event) {
      Territory.List territories = TerritoryModel.getTerritoryList();
      AtomicBoolean isFirst = new AtomicBoolean(true);

      BEACONS.clear();
      POIS.clear();

      double first;
      try {
         first = TimerModel.getTimers().stream().sorted().findFirst().get().getRemaining().toSeconds();
      } catch (NoSuchElementException e) {
         return;
      }

      TimerModel.getTimers()
              .stream()
              .filter(timer -> territories.contains(timer.getTerritory()))
              .filter(timer -> {
                 if (soonestOnly) {
                    if (soonestMinute) {
                       return timer.getRemaining().toSeconds() <= first + 60;
                    } else {
                       return timer.getRemaining().toSeconds() == first;
                    }
                 }
                 return true;
              })
              .sorted()
              .map(timer -> new Beacon(
                      timer.getTerritory(),
                      StyledText.fromString(timer.getTerritory()),
                      TextShadow.OUTLINE,
                      new StaticLocationSupplier(
                              Location.containing(territories.get(timer.getTerritory()).getLocation().getCenter())
                      ),
                      getTexture(timer, isFirst.get()),
                      getColor(timer, isFirst.getAndSet(false)),
                      CommonColors.WHITE,
                      CommonColors.WHITE
              )).peek(beacon -> POIS.add(beacon.toPoi()))
              .forEach(BEACONS::add);
   }

   @Override
   public Stream<Beacon> getBeacons() {
      return BEACONS.stream();
   }

   @Override
   public Stream<MarkerPoi> getPois() {
      return POIS.stream();
   }

   @Override
   public boolean isEnabled() {
      return super.isEnabled() && Models.WorldState.onWorld();
   }

   private static CustomColor getColor(Timer timer, boolean isFirst) {
      if (isFirst) return CommonColors.RED;
      else if (timer.isPersonal()) return new CustomColor(78, 245, 217);
      else return new CustomColor(23, 255, 144);
   }

   private static Texture getTexture(Timer timer, boolean isFirst) {
      if (isFirst) return Texture.DEFEND;
      else if (timer.isPersonal()) return Texture.DIAMOND;
      else return Texture.SLAY;
   }
}
