package com.breadsticksmod.client.features.war;

import com.breadsticksmod.client.config.providers.sound.SoundProvider;
import com.breadsticksmod.client.models.war.timer.events.TimerStartEvent;
import com.breadsticksmod.client.util.SoundUtil;
import com.breadsticksmod.client.util.Sounds;
import com.breadsticksmod.core.Default;
import com.breadsticksmod.core.Feature;
import com.breadsticksmod.core.State;
import com.breadsticksmod.core.config.Config;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Default(State.ENABLED)
@Config.Category("War")
@Feature.Definition(name = "War Horn", description = "Plays a sound when a war starts")
public class WarHornFeature extends Feature {
   @Dropdown(title = "Selected Sound", options = SoundProvider.class)
   private SoundEvent selected = Sounds.WAR_HORN;

   @Value("Sound volume")
   @Tooltip("The volume of the sound")
   private static Float volume = 1.0F;

   @Value("Pitch")
   @Tooltip("The pitch of the sound")
   private static Float pitch = 1.0F;

   @SubscribeEvent
   public void onTimerStart(TimerStartEvent event) {
      if (!event.isFromScoreboard()) SoundUtil.playAmbient(selected, pitch, volume);
   }
}