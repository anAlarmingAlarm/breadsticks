package com.breadsticksmod.client.features.raids;


import com.breadsticksmod.client.models.raids.Raid;
import com.breadsticksmod.client.models.raids.events.RaidEvent;
import com.breadsticksmod.client.util.ChatUtil;
import com.breadsticksmod.core.Default;
import com.breadsticksmod.core.Feature;
import com.breadsticksmod.core.State;
import com.breadsticksmod.core.config.Config;
import com.breadsticksmod.core.text.TextBuilder;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static net.minecraft.ChatFormatting.*;

@Default(State.ENABLED)
@Config.Category("Raids")
@Feature.Definition(name = "Print completion times")
public class PrintTimesFeature extends Feature {
   @SubscribeEvent
   public void onRaidComplete(RaidEvent.Complete event) {
      TextBuilder builder = Raid.format(event.getRaid());
      builder.appendIf(event::isPB, "\n\nNEW PERSONAL BEST!", YELLOW, UNDERLINE, BOLD);

      ChatUtil.send(builder);
   }
}
