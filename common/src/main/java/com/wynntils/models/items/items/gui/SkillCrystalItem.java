/*
 * Copyright © Wynntils 2022-2023.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package com.wynntils.models.items.items.gui;

import com.wynntils.models.items.properties.CountedItemProperty;

public class SkillCrystalItem extends GuiItem implements CountedItemProperty {
    private final int count;

    public SkillCrystalItem(int count) {
        this.count = count;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public boolean hasCount() {
        return count != 0;
    }

    @Override
    public String toString() {
        return "SkillCrystalItem{" + "count=" + count + '}';
    }
}
