/*
 * Copyright © Wynntils 2023.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package com.wynntils.core.consumers.overlays;

public enum RenderState {
    PRE,
    POST,
    REPLACE // This is Pre, but the event is cancelled
}
