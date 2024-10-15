/*
 * Copyright © Wynntils 2023.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package com.wynntils.utils.type;

public record NamedValue(String name, int value) {
    public static final NamedValue EMPTY = new NamedValue("", 0);

    @Override
    public String toString() {
        return name + ": " + value;
    }
}
