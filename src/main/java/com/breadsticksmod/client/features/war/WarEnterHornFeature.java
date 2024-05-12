package com.breadsticksmod.client.features.war;

import com.breadsticksmod.client.config.providers.sound.SoundProvider;
import com.breadsticksmod.client.models.war.WarModel;
import com.breadsticksmod.client.models.war.events.*;
import com.breadsticksmod.client.util.SoundUtil;
import com.breadsticksmod.client.util.Sounds;
import com.breadsticksmod.core.Default;
import com.breadsticksmod.core.Feature;
import com.breadsticksmod.core.State;
import com.breadsticksmod.core.config.Config;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Default(State.DISABLED)
@Config.Category("War")
@Feature.Definition(name = "War Enter Sound", description = "Plays a sound when you enter a war")
public class WarEnterHornFeature extends Feature {
   @Dropdown(title = "Selected Sound", options = SoundProvider.class)
   private static SoundEvent selected = Sounds.FLUTE;

   @Value("Sound volume")
   @Tooltip("The volume of the sound")
   private static Float volume = 1.0F;

   @Value("Pitch")
   @Tooltip("The pitch of the sound")
   private static Float pitch = 1.0F;

   @Instance
   private static WarEnterHornFeature THIS;

   @SubscribeEvent
   private static void onWarEnter(WarEnterEvent event) {
      if (THIS.isEnabled()) SoundUtil.playAmbient(selected, pitch, volume);
   }
}