package com.breadsticksmod.client.features;

import com.breadsticksmod.client.util.PacketUtil;
import com.breadsticksmod.core.Feature;
import com.breadsticksmod.core.heartbeat.Heartbeat;
import com.breadsticksmod.core.time.ChronoUnit;
import com.wynntils.models.npcdialogue.event.NpcDialogueProcessingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;

@Feature.Definition(name = "Autoskip Dialogue", description = "Automatically skips dialogue")
public class AutoSkipDialogueFeature extends Feature {
    private static final String PRESS_TO_CONTINUE_TEXT = "Press SHIFT to continue";

    @SubscribeEvent
    public void NpcDialogEvent(NpcDialogueProcessingEvent.Post event) {
        PacketUtil.Action(Action.PRESS_SHIFT_KEY);

        Heartbeat.schedule(() -> PacketUtil.Action(Action.RELEASE_SHIFT_KEY), 50, ChronoUnit.MILLISECONDS);
    }
}
