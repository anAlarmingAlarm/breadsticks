package com.breadsticksmod.client.features.war;

import com.breadsticksmod.client.config.providers.sound.SoundProvider;
import com.breadsticksmod.client.models.war.events.WarStartEvent;
import com.breadsticksmod.client.models.war.timer.events.TimerStartEvent;
import com.breadsticksmod.client.util.SoundUtil;
import com.breadsticksmod.client.util.Sounds;
import com.breadsticksmod.core.Default;
import com.breadsticksmod.core.Feature;
import com.breadsticksmod.core.State;
import com.breadsticksmod.core.config.Config;
import com.wynntils.core.components.Managers;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Date;

@Default(State.ENABLED)
@Config.Category("War")
@Feature.Definition(name = "War Enter Sound", description = "Plays a sound when you enter a war")
public class WarEnterHornFeature extends Feature {
   @Dropdown(title = "Selected Sound", options = SoundProvider.class)
   private SoundEvent selected = Sounds.HOME_DEPOT;

   @Value("Sound volume")
   @Tooltip("The volume of the sound")
   private static Float volume = 1.0F;

   @Value("Pitch")
   @Tooltip("The pitch of the sound")
   private static Float pitch = 1.0F;

   @SubscribeEvent
   private void onWarStart(WarStartEvent event) {
      SoundUtil.playAmbient(selected, pitch, volume);
   }
}