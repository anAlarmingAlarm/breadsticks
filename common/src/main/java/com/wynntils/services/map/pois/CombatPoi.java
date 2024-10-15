/*
 * Copyright © Wynntils 2022-2023.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package com.wynntils.services.map.pois;

import com.wynntils.core.components.Managers;
import com.wynntils.features.map.MainMapFeature;
import com.wynntils.services.map.type.CombatKind;
import com.wynntils.services.map.type.DisplayPriority;
import com.wynntils.utils.mc.type.PoiLocation;
import com.wynntils.utils.render.Texture;

public class CombatPoi extends StaticIconPoi {
    private final String name;
    private final CombatKind kind;

    public CombatPoi(PoiLocation location, String name, CombatKind kind) {
        super(location);
        this.name = name;
        this.kind = kind;
    }

    @Override
    public Texture getIcon() {
        return kind.getIcon();
    }

    @Override
    public float getMinZoomForRender() {
        if (kind == CombatKind.CAVES) {
            return Managers.Feature.getFeatureInstance(MainMapFeature.class)
                    .cavePoiMinZoom
                    .get();
        }
        return Managers.Feature.getFeatureInstance(MainMapFeature.class)
                .combatPoiMinZoom
                .get();
    }

    @Override
    public String getName() {
        return name;
    }

    public CombatKind getKind() {
        return kind;
    }

    @Override
    public DisplayPriority getDisplayPriority() {
        return DisplayPriority.NORMAL;
    }
}
