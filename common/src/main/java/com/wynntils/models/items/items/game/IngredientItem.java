/*
 * Copyright © Wynntils 2022-2024.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package com.wynntils.models.items.items.game;

import com.wynntils.models.ingredients.type.IngredientInfo;
import com.wynntils.models.items.properties.LeveledItemProperty;
import com.wynntils.models.items.properties.NamedItemProperty;
import com.wynntils.models.items.properties.ProfessionItemProperty;
import com.wynntils.models.items.properties.QualityTierItemProperty;
import com.wynntils.models.profession.type.ProfessionType;
import java.util.List;

public class IngredientItem extends GameItem
        implements QualityTierItemProperty, LeveledItemProperty, NamedItemProperty, ProfessionItemProperty {
    private final IngredientInfo ingredientInfo;

    public IngredientItem(IngredientInfo ingredientInfo) {
        this.ingredientInfo = ingredientInfo;
    }

    public IngredientInfo getIngredientInfo() {
        return ingredientInfo;
    }

    @Override
    public int getQualityTier() {
        return ingredientInfo.tier();
    }

    @Override
    public int getLevel() {
        return ingredientInfo.level();
    }

    @Override
    public String getName() {
        return ingredientInfo.name();
    }

    @Override
    public String toString() {
        return "IngredientItem{" + "ingredientInfo=" + ingredientInfo + '}';
    }

    @Override
    public List<ProfessionType> getProfessionTypes() {
        return ingredientInfo.professions();
    }
}
