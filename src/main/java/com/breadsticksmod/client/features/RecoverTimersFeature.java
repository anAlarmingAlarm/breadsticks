package com.breadsticksmod.client.features;

import com.breadsticksmod.client.models.war.timer.TimerModel;
import com.breadsticksmod.core.Default;
import com.breadsticksmod.core.Feature;
import com.breadsticksmod.core.State;
import com.breadsticksmod.core.config.Config;

@Default(State.ENABLED)
@Config.Category("War")
@Feature.Definition(name = "Recover timers after restart")
public class RecoverTimersFeature extends Feature {
    public static RecoverTimersFeature THIS;

    @Override
    protected void onInit() {
        THIS = this;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (this.isEnabled()) TimerModel.saveTimers();
        }));
    }

    public void loadTimers() {
        if (this.isEnabled()) TimerModel.loadTimers();
    }
}
