package com.breadsticksmod.client.features.war;

import com.breadsticksmod.client.screen.territories.ManageTerritoriesScreen;
import com.breadsticksmod.client.screen.territories.SelectTerritoriesScreen;
import com.breadsticksmod.client.util.ChatUtil;
import com.breadsticksmod.client.util.ContainerHelper;
import com.breadsticksmod.core.Default;
import com.breadsticksmod.core.Feature;
import com.breadsticksmod.core.State;
import com.breadsticksmod.core.config.Config;
import com.breadsticksmod.core.heartbeat.Heartbeat;
import com.breadsticksmod.core.time.ChronoUnit;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wynntils.core.consumers.overlays.OverlayPosition;
import com.wynntils.core.text.StyledText;
import com.wynntils.mc.event.ContainerClickEvent;
import com.wynntils.mc.event.ContainerSetContentEvent;
import com.wynntils.mc.event.MenuEvent;
import com.wynntils.mc.event.SlotRenderEvent;
import com.wynntils.core.consumers.overlays.TextOverlay;
import com.wynntils.models.worlds.event.WorldStateEvent;
import com.wynntils.utils.render.FontRenderer;
import com.wynntils.utils.render.TextRenderSetting;
import com.wynntils.utils.render.TextRenderTask;
import com.wynntils.utils.render.type.HorizontalAlignment;
import com.wynntils.utils.render.type.TextShadow;
import com.wynntils.utils.render.type.VerticalAlignment;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static com.breadsticksmod.client.models.territory.eco.Patterns.GUILD_MANAGE_MENU;
import static com.breadsticksmod.client.screen.territories.ManageTerritoriesScreen.TERRITORY_MENU_PATTERN;
import static com.breadsticksmod.client.screen.territories.SelectTerritoriesScreen.SELECT_TERRITORIES_MENU;
import static com.wynntils.utils.mc.McUtils.mc;

@Config.Category("War")
@Default(State.ENABLED)
@Feature.Definition(name = "Territory Helper Menu")
public class TerritoryHelperMenuFeature extends Feature {
   @Value("Display production")
   static boolean production = true;

   @Value("Display usage percents")
   static boolean percents = false;

   @Value("Replace loadouts menu")
   static boolean replaceLoadouts = true;

   @Value("Show 1s next to Lv. 1 upgrades/bonuses")
   @Tooltip("When enabled, Lv. 1 territory upgrades and bonuses will show a 1 next to them similar to the number shown for Lv. 2 and above upgrades and bonuses")
   private static boolean showOnesInLevels = true;

   @Value("Hide ignored territories")
   @Tooltip({
           "Will hide ignored territories",
           "",
           "Does not apply to the loadouts menu"
   })
   private static boolean hideIgnoredTerritories = false;

   @Value("Ignore cut off resources")
   private static boolean ignoreCutOffResources = true;

   @Value("Ignore resources from blacklist")
   @Tooltip("When enabled, will ignore resources from territories on the blacklist")
   private static boolean ignoreBlacklistedResources = true;

   @Array("Blacklist")
   private static List<String> blacklist = List.of(
           "Light Forest West Upper",
           "Light Forest West Mid",
           "Light Forest East Lower",
           "Light Forest East Mid",
           "Light Forest Canyon",
           "Aldorei Valley South Entrance",
           "Aldorei's North Exit",
           "Cinfras County Lower",
           "Path To The Arch",
           "Ghostly Path",
           "Aldorei's Arch",
           "Burning Farm",
           "Heavenly Ingress",
           "Primal Fen",
           "Luminous Plateau",
           "Field of Life",
           "Path to Light",
           "Otherwordly Monolith",
           "Azure Frontier",
           "Nexus of Light",
           "Jungle Lake",
           "Herb Cave",
           "Great Bridge Jungle",
           "Jungle Lower",
           "Jungle Mid",
           "Jungle Upper",
           "Dernel Jungle Mid",
           "Dernel Jungle Lower",
           "Dernel Jungle Upper"
   );

   public static final Pattern TERRITORY_UPGRADE_PATTERN = Pattern.compile("^.*: Guild Tower$");

   public static final Pattern TERRITORY_BONUSES_PATTERN = Pattern.compile("^.*: Bonus$");

   public static final Pattern LV1_PATTERN = Pattern.compile(".*\\[Lv\\. 1]");

   public static boolean hideIgnoredTerritories() {
      return hideIgnoredTerritories;
   }

   public static boolean ignoreCutOffResources() {
      return ignoreCutOffResources;
   }

   public static boolean ignoreBlacklistedTerritories() {
      return ignoreBlacklistedResources;
   }

   public static Set<String> blacklist() {
      return new HashSet<>(blacklist);
   }

   @SubscribeEvent(priority = EventPriority.LOWEST)
   public void onMenuOpen(MenuEvent.MenuOpenedEvent event) {
      StyledText text = StyledText.fromComponent(event.getTitle());

      if (text.matches(TERRITORY_MENU_PATTERN)) {
         var screen = new ManageTerritoriesScreen(event.getContainerId(), production, percents);

         mc().setScreen(screen);

         if (!NO_RESET)
            screen.reset();

         NO_RESET = false;

         event.setCanceled(true);
      } else if (replaceLoadouts && text.matches(SELECT_TERRITORIES_MENU)) {
         mc().setScreen(new SelectTerritoriesScreen(event.getContainerId(), production, percents));
         event.setCanceled(true);
      } else if (text.matches(GUILD_MANAGE_MENU) && OPEN_TERRITORY_MENU) event.setCanceled(true);

      TOWER_MENU = text.matches(TERRITORY_UPGRADE_PATTERN) || text.matches(TERRITORY_BONUSES_PATTERN);
   }

   public static boolean OPEN_TERRITORY_MENU = false;
   private static boolean NO_RESET = false;
   private static boolean TOWER_MENU = false;

   @SubscribeEvent(priority = EventPriority.HIGH)
   public void onMenuSetContents(ContainerSetContentEvent.Pre event) {
      if (OPEN_TERRITORY_MENU && ContainerHelper.Click(14, 0, GUILD_MANAGE_MENU)) {
         OPEN_TERRITORY_MENU = false;
      }
   }

   @SubscribeEvent
   public void onClick(ContainerClickEvent event) {
      if (event.getSlotNum() != 9) return;

      ItemStack stack = event.getContainerMenu().slots.get(11).getItem();
      OPEN_TERRITORY_MENU = stack.getItem() == Items.DISPENSER && ChatUtil.strip(stack.getDisplayName()).equals("[Guild Tower]");

      if (OPEN_TERRITORY_MENU) {
         NO_RESET = true;

         Heartbeat.schedule(() -> {
            if (OPEN_TERRITORY_MENU) OPEN_TERRITORY_MENU = false;
         }, 500, ChronoUnit.MILLISECONDS);
      }
   }

   @SubscribeEvent
   public void onWorldSwap(WorldStateEvent event) {
      OPEN_TERRITORY_MENU = false;
      TOWER_MENU = false;
   }

   @SubscribeEvent
   public void onRenderSlot(SlotRenderEvent.Post e) {
      if (showOnesInLevels && TOWER_MENU) drawTextOverlay(e.getPoseStack(), e.getSlot().getItem(), e.getSlot().x, e.getSlot().y);
   }

   private void drawTextOverlay(PoseStack poseStack, ItemStack itemStack, int slotX, int slotY) {
      if (LV1_PATTERN.matcher(ChatUtil.strip(itemStack.getDisplayName())).find()) {
         TextOverlay textOverlay = UpgradeOverlay.getTextOverlay();
         if (textOverlay == null) return;

         poseStack.pushPose();
         poseStack.translate(0, 0, 300); // items are drawn at z300, so text has to be as well
         poseStack.scale(1, 1, 1);
         float x = slotX + 11;
         float y = slotY + 9;
         FontRenderer.getInstance().renderText(poseStack, x, y, new TextRenderTask("1", TextRenderSetting.DEFAULT.withTextShadow(TextShadow.NORMAL)));
         poseStack.popPose();
      }
   }

   public static class UpgradeOverlay extends TextOverlay {
      public UpgradeOverlay() {
         super(
            new OverlayPosition(
               5,
               0,
               VerticalAlignment.TOP,
               HorizontalAlignment.LEFT,
               OverlayPosition.AnchorSection.BOTTOM_RIGHT
            ),
            200,
            100
         );
      }

      public static TextOverlay getTextOverlay() {
         return new TextOverlay(new OverlayPosition(
                 5,
                 0,
                 VerticalAlignment.TOP,
                 HorizontalAlignment.LEFT,
                 OverlayPosition.AnchorSection.BOTTOM_RIGHT
         ),
                 200,
                 100) {
            @Override
            protected String getTemplate() {
               return null;
            }

            @Override
            protected String getPreviewTemplate() {
               return null;
            }
         };
      }

      @Override
      protected String getTemplate() {
         return null;
      }

      @Override
      protected String getPreviewTemplate() {
         return null;
      }


   }
}
