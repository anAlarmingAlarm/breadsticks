package com.breadsticksmod.client.models.territory;

import com.breadsticksmod.client.events.mc.MinecraftStartupEvent;
import com.breadsticksmod.client.models.territory.events.MapUpdateEvent;
import com.breadsticksmod.client.models.territory.events.TerritoryCapturedEvent;
import com.breadsticksmod.core.Model;
import com.breadsticksmod.core.http.requests.mapstate.MapState;
import com.breadsticksmod.core.http.requests.mapstate.Territory;
import com.breadsticksmod.core.heartbeat.Heartbeat;
import com.breadsticksmod.core.json.Json;
import com.breadsticksmod.core.time.ChronoUnit;
import com.wynntils.core.text.PartStyle;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import com.wynntils.mc.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.wynntils.utils.mc.McUtils.player;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class TerritoryModel extends Model {
   private final static Pattern TERRITORY_CAPTURED_PATTERN = Pattern.compile("^\\[WAR] \\[(?<guild>.+)] captured the territory (?<territory>.+)\\.");
   private final static Pattern TERRITORY_CONTROL_PATTERN = Pattern.compile("^\\[INFO] \\[(?<guild>.+)] has taken control of (?<territory>.+)!");

   @SuppressWarnings("unused")
   private Socket ACTIVE_SOCKET;

   private MapState LATEST_TERRITORIES;

   @Instance
   private static TerritoryModel THIS;

   private static Optional<Territory> CURRENT_TERRITORY = Optional.empty();

   @SubscribeEvent
   public void onMessage(ChatMessageReceivedEvent event) {
      Matcher matcher = event.getOriginalStyledText().getMatcher(TERRITORY_CAPTURED_PATTERN, PartStyle.StyleType.NONE);
      if (matcher.matches()) {
         new TerritoryCapturedEvent(matcher.group("territory"), matcher.group("guild")).post();
         return;
      }

      matcher = event.getOriginalStyledText().getMatcher(TERRITORY_CONTROL_PATTERN, PartStyle.StyleType.NONE);
      if (matcher.matches())
         new TerritoryCapturedEvent(matcher.group("territory"), matcher.group("guild")).post();
   }

   // Todo: adding this to try to stop random mass errors on game start; does it even work?
   @SubscribeEvent
   public static void onGameStart(MinecraftStartupEvent event) {
      onGameStartHelper(1);
   }

   public static void onGameStartHelper(int tries) {
      if (tries > 6) return;
      try {
         new Territory.Request().thenAccept(optional -> {
            new MapUpdateEvent(optional.orElse(MapState.empty())).post();

            THIS.ACTIVE_SOCKET = THIS.new Socket();
         });
      } catch (Exception e) {
         LOGGER.warn("Failed to get map state, retrying in 10 seconds");
         Heartbeat.schedule(() -> onGameStartHelper(tries + 1), 10, ChronoUnit.SECONDS);
      }
   }

   @SubscribeEvent(priority = EventPriority.LOWEST)
   public void onMapUpdate(MapUpdateEvent event) {
      if (LATEST_TERRITORIES != null)
         event.getState().forEach(territory -> {
            if (LATEST_TERRITORIES.contains(territory) && !LATEST_TERRITORIES.get(territory).getOwner().equals(territory.getOwner()))
               new TerritoryCapturedEvent(territory.getName(), territory.getOwner().prefix());
         });

      LATEST_TERRITORIES = event.getState();
   }

   @SubscribeEvent
   public void onTick(TickEvent event) {
      if (LATEST_TERRITORIES != null) {
         CURRENT_TERRITORY = LATEST_TERRITORIES.stream()
                 .filter(territory -> territory.getLocation().isInside(player().position()))
                 .findFirst();
      }
   }

   public static MapState getTerritoryList() {
      return THIS.LATEST_TERRITORIES;
   }

   public static Optional<Territory> getCurrentTerritory() {
      return CURRENT_TERRITORY;
   }

   protected class Socket extends WebSocketClient {
      private static final URI SOCKET_URI = URI.create("wss://thesimpleones.net/war");

      public Socket() {
         super(SOCKET_URI);

         connect();
      }

      @Override
      public void onOpen(ServerHandshake handshake) {

      }

      @Override
      public void onMessage(String message) {
         Json.tryParse(message).ifPresent(json -> {
            if (json.getString("type").equals("MapUpdate")) {
               new MapUpdateEvent(json.getJson("map").wrap(MapState::new)).post();
            }
         });
      }

      @Override
      public void onClose(int code, String reason, boolean remote) {
         LOGGER.warn("Territory socket has disconnected with code {} (reason={}, remote={})", code, reason, reason);

         Heartbeat.schedule(() -> ACTIVE_SOCKET = new Socket(), 10, ChronoUnit.SECONDS);
      }

      @Override
      public void onError(Exception ex) {
         LOGGER.error("Error in socket", ex);
      }
   }

   public static List<String> TERRITORIES = List.of("Abandoned Farm",
           "Abandoned Manor",
           "Abandoned Pass",
           "Active Volcano",
           "Ahmsord",
           "Ahmsord Outskirts",
           "Air Temple Lower",
           "Air Temple Upper",
           "Aldorei Lowlands",
           "Aldorei Valley Lower",
           "Aldorei Valley Mid",
           "Aldorei Valley South Entrance",
           "Aldorei Valley Upper",
           "Aldorei Valley West Entrance",
           "Aldorei's Arch",
           "Aldorei's North Exit",
           "Aldorei's River",
           "Aldorei's Waterfall",
           "Almuj City",
           "Ancient Nemract",
           "Angel Refuge",
           "Arachnid Route",
           "Astraulus' Tower",
           "Avos Temple",
           "Avos Workshop",
           "Azure Frontier",
           "Bandit Camp Exit",
           "Bandit Cave Lower",
           "Bandit Cave Upper",
           "Bandits Toll",
           "Bizarre Passage",
           "Bloody Beach",
           "Bob's Tomb",
           "Bremminglar",
           "Burning Airship",
           "Burning Farm",
           "Canyon Dropoff",
           "Canyon Entrance Waterfall",
           "Canyon Fortress",
           "Canyon High Path",
           "Canyon Lower South East",
           "Canyon Mountain East",
           "Canyon Mountain South",
           "Canyon Of The Lost",
           "Canyon Path North Mid",
           "Canyon Path North West",
           "Canyon Path South East",
           "Canyon Path South West",
           "Canyon Survivor",
           "Canyon Upper North West",
           "Canyon Valley South",
           "Canyon Walk Way",
           "Canyon Waterfall Mid North",
           "Canyon Waterfall North",
           "Cathedral Harbour",
           "Central Islands",
           "Chained House",
           "Cherry Blossom Forest",
           "Cinfras",
           "Cinfras County Lower",
           "Cinfras County Mid-Lower",
           "Cinfras County Mid-Upper",
           "Cinfras County Upper",
           "Cinfras Entrance",
           "Cinfras Outskirts",
           "Cinfras Thanos Transition",
           "Cinfras's Small Farm",
           "City of Troms",
           "Cliff Side of the Lost",
           "Cliffside Lake",
           "Cliffside Passage",
           "Cliffside Passage North",
           "Cliffside Valley",
           "Cliffside Waterfall",
           "Coastal Trail",
           "Corkus Castle",
           "Corkus City",
           "Corkus City Mine",
           "Corkus City South",
           "Corkus Countryside",
           "Corkus Docks",
           "Corkus Forest North",
           "Corkus Forest South",
           "Corkus Mountain",
           "Corkus Outskirts",
           "Corkus Sea Cove",
           "Corkus Sea Port",
           "Corkus Statue",
           "Corrupted Road",
           "Crater Descent",
           "Dark Forest Cinfras Transition",
           "Dark Forest Village",
           "Dead Island North East",
           "Dead Island North West",
           "Dead Island South East",
           "Dead Island South West",
           "Decayed Basin",
           "Dernel Jungle Lower",
           "Dernel Jungle Mid",
           "Dernel Jungle Upper",
           "Desert East Lower",
           "Desert East Mid",
           "Desert East Upper",
           "Desert Lower",
           "Desert Mid-Lower",
           "Desert Mid-Upper",
           "Desert Upper",
           "Desert West Lower",
           "Desert West Upper",
           "Desolate Valley",
           "Detlas",
           "Detlas Close Suburbs",
           "Detlas Far Suburbs",
           "Detlas Savannah Transition",
           "Detlas Suburbs",
           "Detlas Trail East Plains",
           "Detlas Trail West Plains",
           "Dragonling Nests",
           "Dujgon Nation",
           "Durum Isles Center",
           "Durum Isles East",
           "Durum Isles Lower",
           "Durum Isles Upper",
           "Efilim East Plains",
           "Efilim South East Plains",
           "Efilim South Plains",
           "Efilim Village",
           "Elkurn",
           "Elkurn Fields",
           "Eltom",
           "Emerald Trail",
           "Entrance to Kander",
           "Entrance to Olux",
           "Entrance to Rodoroc",
           "Entrance to Thesead North",
           "Entrance to Thesead South",
           "Factory Entrance",
           "Fallen Factory",
           "Fallen Village",
           "Field of Life",
           "Final Step",
           "Fleris Trail",
           "Forest of Eyes",
           "Forgotten Path",
           "Forgotten Town",
           "Fortress North",
           "Fortress South",
           "Frozen Fort",
           "Fungal Grove",
           "Gateway to Nothing",
           "Gelibord",
           "Gelibord Castle",
           "Gelibord Corrupted Farm",
           "Gert Camp",
           "Ghostly Path",
           "Goblin Plains East",
           "Goblin Plains West",
           "Great Bridge Jungle",
           "Great Bridge Nesaak",
           "Grey Ruins",
           "Guild Hall",
           "Gylia Lake North East",
           "Gylia Lake North West",
           "Gylia Lake South East",
           "Gylia Lake South West",
           "Half Moon Island",
           "Heart of Decay",
           "Heavenly Ingress",
           "Herb Cave",
           "Hive",
           "Hive South",
           "Hobbit River",
           "Icy Descent",
           "Icy Island",
           "Iron Road",
           "Jitak's Farm",
           "Jofash Docks",
           "Jofash Tunnel",
           "Jungle Lake",
           "Jungle Lower",
           "Jungle Mid",
           "Jungle Upper",
           "Kander Mines",
           "Kandon Farm",
           "Kandon Ridge",
           "Kandon-Beda",
           "Katoa Ranch",
           "Krolton's Cave",
           "Lava Lake",
           "Lava Lake Bridge",
           "Leadin Fortress",
           "Legendary Island",
           "Lexdale",
           "Lexdales Prison",
           "Light Forest Canyon",
           "Light Forest East Lower",
           "Light Forest East Mid",
           "Light Forest East Upper",
           "Light Forest Entrance",
           "Light Forest North Entrance",
           "Light Forest North Exit",
           "Light Forest South Entrance",
           "Light Forest South Exit",
           "Light Forest West Lower",
           "Light Forest West Mid",
           "Light Forest West Upper",
           "Light Peninsula",
           "Lighthouse Plateau",
           "Lion Lair",
           "Little Wood",
           "Llevigar",
           "Llevigar Entrance",
           "Llevigar Farm",
           "Llevigar Farm Plains East",
           "Llevigar Farm Plains West",
           "Llevigar Gate East",
           "Llevigar Gate West",
           "Llevigar Plains East Lower",
           "Llevigar Plains East Upper",
           "Llevigar Plains West Lower",
           "Llevigar Plains West Upper",
           "Loamsprout Camp",
           "Lone Farmstead",
           "Lost Atoll",
           "Luminous Plateau",
           "Lusuco",
           "Lutho",
           "Mage Island",
           "Maltic",
           "Maltic Coast",
           "Maltic Plains",
           "Mansion of Insanity",
           "Mantis Nest",
           "Maro Peaks",
           "Mesquis Tower",
           "Meteor Crater",
           "Military Base",
           "Military Base Lower",
           "Military Base Upper",
           "Mine Base Plains",
           "Mining Base Lower",
           "Mining Base Upper",
           "Molten Heights Portal",
           "Molten Reach",
           "Mountain Edge",
           "Mountain Path",
           "Mummy's Tomb",
           "Mushroom Hill",
           "Nemract Cathedral",
           "Nemract Plains East",
           "Nemract Plains West",
           "Nemract Quarry",
           "Nemract Road",
           "Nemract Town",
           "Nesaak Bridge Transition",
           "Nesaak Plains Lower North West",
           "Nesaak Plains Mid North West",
           "Nesaak Plains North East",
           "Nesaak Plains South East",
           "Nesaak Plains South West",
           "Nesaak Plains Upper North West",
           "Nesaak Transition",
           "Nesaak Village",
           "Nether Gate",
           "Nether Plains Lower",
           "Nether Plains Upper",
           "Nexus of Light",
           "Nivla Woods",
           "Nivla Woods Edge",
           "Nivla Woods Entrance",
           "Nivla Woods Exit",
           "Nodguj Nation",
           "North Farmers Valley",
           "North Nivla Woods",
           "Old Coal Mine",
           "Old Crossroads North",
           "Old Crossroads South",
           "Olux",
           "Orc Battlegrounds",
           "Orc Lake",
           "Orc Road",
           "Otherwordly Monolith",
           "Path to Ahmsord Lower",
           "Path to Ahmsord Upper",
           "Path to Cinfras",
           "Path to Light",
           "Path To Military Base",
           "Path To Ozoth's Spire Lower",
           "Path To Ozoth's Spire Mid",
           "Path To Ozoth's Spire Upper",
           "Path to Talor",
           "Path To Thanos",
           "Path To The Arch",
           "Paths of Sludge",
           "Phinas Farm",
           "Pigmen Ravines",
           "Pigmen Ravines Entrance",
           "Pirate Town",
           "Plains",
           "Plains Coast",
           "Plains Lake",
           "Pre-Light Forest Transition",
           "Primal Fen",
           "Quartz Mines North East",
           "Quartz Mines North West",
           "Quartz Mines South East",
           "Quartz Mines South West",
           "Ragni",
           "Ragni East Suburbs",
           "Ragni Main Entrance",
           "Ragni North Entrance",
           "Ragni North Suburbs",
           "Ragni Plains",
           "Raider's Base Lower",
           "Raider's Base Upper",
           "Ranol's Farm",
           "Regular Island",
           "Road to Elkurn",
           "Road To Light Forest",
           "Road To Mine",
           "Road to Time Valley",
           "Rodoroc",
           "Rooster Island",
           "Royal Gate",
           "Ruined Houses",
           "Rymek East Lower",
           "Rymek East Mid",
           "Rymek East Upper",
           "Rymek West Lower",
           "Rymek West Mid",
           "Rymek West Upper",
           "Sablestone Camp",
           "Sanctuary Bridge",
           "Santa's Hideout",
           "Savannah East Lower",
           "Savannah East Upper",
           "Savannah West Lower",
           "Savannah West Upper",
           "Selchar",
           "Sinister Forest",
           "Skiens Island",
           "Sky Castle",
           "Sky Falls",
           "Sky Island Ascent",
           "Snail Island",
           "South Farmers Valley",
           "South Nivla Woods",
           "South Pigmen Ravines",
           "Southern Outpost",
           "Sunspark Camp",
           "Swamp Dark Forest Transition Lower",
           "Swamp Dark Forest Transition Mid",
           "Swamp Dark Forest Transition Upper",
           "Swamp East Lower",
           "Swamp East Mid",
           "Swamp East Mid-Upper",
           "Swamp East Upper",
           "Swamp Island",
           "Swamp Lower",
           "Swamp Mountain Base",
           "Swamp Mountain Transition Lower",
           "Swamp Mountain Transition Mid",
           "Swamp Mountain Transition Mid-Upper",
           "Swamp Mountain Transition Upper",
           "Swamp Plains Basin",
           "Swamp West Lower",
           "Swamp West Mid",
           "Swamp West Mid-Upper",
           "Swamp West Upper",
           "Taproot Descent",
           "Temple Island",
           "Temple of Legends",
           "Temple of the Lost East",
           "Ternaves",
           "Ternaves Plains Lower",
           "Ternaves Plains Upper",
           "Thanos",
           "Thanos Exit",
           "Thanos Exit Upper",
           "Thanos Valley West",
           "The Bear Zoo",
           "The Broken Road",
           "The Gate",
           "The Silent Road",
           "Thesead",
           "Thesead Suburbs",
           "Time Valley",
           "Tower of Ascension",
           "Toxic Caves",
           "Toxic Drip",
           "Tree Island",
           "Twain Lake",
           "Twain Mansion",
           "Twisted Housing",
           "Twisted Ridge",
           "Valley of the Lost",
           "Viscera Pits East",
           "Viscera Pits West",
           "Void Valley",
           "Volcanic Slope",
           "Volcano Lower",
           "Volcano Upper",
           "Wizard Tower North",
           "Worm Tunnel",
           "Wybel Island",
           "Zhight Island");
}
