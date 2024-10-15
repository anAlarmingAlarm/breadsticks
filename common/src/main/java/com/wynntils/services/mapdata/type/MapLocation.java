/*
 * Copyright © Wynntils 2023-2024.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package com.wynntils.services.mapdata.type;

import com.wynntils.utils.mc.type.Location;

public interface MapLocation extends MapFeature {
    Location getLocation();
}
