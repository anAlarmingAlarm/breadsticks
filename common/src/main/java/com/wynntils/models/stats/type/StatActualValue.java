/*
 * Copyright © Wynntils 2022-2023.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package com.wynntils.models.stats.type;

import com.wynntils.utils.type.RangedValue;

public record StatActualValue(StatType statType, int value, int stars, RangedValue internalRoll) {}
