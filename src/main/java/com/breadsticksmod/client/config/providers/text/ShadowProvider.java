package com.breadsticksmod.client.config.providers.text;

import com.breadsticksmod.core.config.Config;
import com.wynntils.utils.render.type.TextShadow;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShadowProvider implements Config.Dropdown.Provider<TextShadow> {
    @Override
    public Iterable<TextShadow> getOptions() {
        return List.of(TextShadow.values());
    }
    @Override
    public @Nullable TextShadow get(String string) throws Throwable {
        return TextShadow.valueOf(string);
    }
}
