/*
 * Copyright © Wynntils 2023.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package com.wynntils.models.lootrun.type;

public enum LootrunningState {
    NOT_RUNNING,
    CHOOSING_BEACON,
    IN_TASK;

    public boolean isRunning() {
        return this != NOT_RUNNING;
    }
}
