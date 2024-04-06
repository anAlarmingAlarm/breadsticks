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
/*import com.breadsticksmod.client.util.PacketUtil;
import com.breadsticksmod.core.Feature;
import com.breadsticksmod.core.heartbeat.Heartbeat;
import com.breadsticksmod.core.time.ChronoUnit;
import com.wynntils.core.text.PartStyle;
import com.wynntils.handlers.chat.event.ChatMessageReceivedEvent;
import com.wynntils.handlers.chat.event.NpcDialogEvent;
import com.wynntils.handlers.chat.type.NpcDialogueType;
import com.wynntils.handlers.chat.type.RecipientType;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket.Action;

@Feature.Definition(name = "Autoskip Dialogue", description = "Automatically skips dialogue")
public class AutoSkipDialogueFeature extends Feature {
    private static final String PRESS_TO_CONTINUE_TEXT = "Press SHIFT to continue";

    @SubscribeEvent
    public void NpcDialogEvent(NpcDialogEvent event) {
        if (event.getType() != NpcDialogueType.NORMAL) return;

        PacketUtil.Action(Action.PRESS_SHIFT_KEY);

        Heartbeat.schedule(() -> PacketUtil.Action(Action.RELEASE_SHIFT_KEY), 50, ChronoUnit.MILLISECONDS);
    }

    @SubscribeEvent
    public void ChatReceivedEvent(ChatMessageReceivedEvent event){
        if (event.getRecipientType() != RecipientType.INFO || !event.getStyledText().trim().contains(PRESS_TO_CONTINUE_TEXT, PartStyle.StyleType.NONE)) return;
        
        PacketUtil.Action(Action.PRESS_SHIFT_KEY);

        Heartbeat.schedule(() -> PacketUtil.Action(Action.RELEASE_SHIFT_KEY), 50, ChronoUnit.MILLISECONDS);
    }
}*/
