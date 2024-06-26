package com.breadsticksmod.core.artemis;

import com.breadsticksmod.client.events.mc.screen.ScreenClickedEvent;
import com.breadsticksmod.core.render.overlay.Hud;
import com.google.common.base.CaseFormat;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.wynntils.core.components.Managers;
import com.wynntils.core.consumers.features.Feature;
import com.wynntils.core.consumers.overlays.OverlayPosition;
import com.wynntils.core.consumers.screens.WynntilsScreen;
import com.wynntils.core.persisted.config.Category;
import com.wynntils.core.persisted.config.Config;
import com.wynntils.core.persisted.config.ConfigCategory;
import com.wynntils.utils.render.type.HorizontalAlignment;
import com.wynntils.utils.render.type.VerticalAlignment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.Nullable;

@ConfigCategory(Category.UI)
public class BreadsticksFeature extends Feature {
   public static BreadsticksFeature INSTANCE;

   public BreadsticksFeature() {

   }

   @Override
   public String getTranslatedName() {
      return "breadsticks";
   }

   @Override
   public String getTranslation(String keySuffix) {
      return "breadsticks";
   }

   private boolean justSet = false;

   @Override
   protected void onConfigUpdate(Config<?> config) {
      if (config.getFieldName().equals("userEnabled") && !(Boolean) config.get()) {
         Managers.Feature.enableFeature(this);
      }
   }

   public static class Overlay extends com.wynntils.core.consumers.overlays.Overlay {
      private final String name;
      private final Hud.Element parent;

      public Overlay(
              String name,
              Hud.Element parent,
              float width,
              float height,
              float verticalOffset,
              float horizontalOffset,
              VerticalAlignment verticalAlignment,
              HorizontalAlignment horizontalAlignment,
              OverlayPosition.AnchorSection anchorSection
      ) {
         super(
                 new OverlayPosition(
                         verticalOffset,
                         horizontalOffset,
                         verticalAlignment,
                         horizontalAlignment,
                         anchorSection
                 ), width, height
         );

         this.name = name;
         this.parent = parent;
      }

      public boolean isEnabled() {
         return Managers.Overlay.isEnabled(this);
      }

      @Override
      public @Nullable Boolean isUserEnabled() {
         return userEnabled.get();
      }

      @Override
      public String getShortName() {
         return parent.getClass().getSimpleName();
      }

      @Override
      public String getTranslatedName() {
         return name;
      }

      @Override
      public String getTranslation(String keySuffix) {
         return name;
      }

//      @Override
//      protected String getNameCamelCase() {
//         return CaseFormat.UPPER_CAMEL.to(
//                 CaseFormat.LOWER_CAMEL, parent.getClass().getSimpleName().replace("Overlay", ""));
//      }
//

      @Override
      public String getJsonName() {
         String name = parent.getClass().getSimpleName();
         return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, name);
      }

      @Override
      public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, float v, Window window) {
         parent.render(poseStack, multiBufferSource, v, window);
      }

      @Override
      public void renderPreview(PoseStack poseStack, MultiBufferSource bufferSource, float partialTicks, Window window) {
         parent.renderPreview(poseStack, bufferSource, partialTicks, window);
      }

      @Override
      protected void onConfigUpdate(Config<?> config) {

      }

      @SubscribeEvent
      public void onScreenClick(ScreenClickedEvent event) {
         if (event.getScreen() instanceof WynntilsScreen && !parent.getFeature().isEnabled() && this.isEnabled()) {
            Managers.Overlay.disableOverlay(this);
         }
      }
   }
}
