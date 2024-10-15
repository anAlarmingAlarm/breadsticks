/*
 * Copyright © Wynntils 2022-2023.
 * This file is released under LGPLv3. See LICENSE for full license details.
 */
package com.wynntils.services.hades;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class HadesUserRegistry {
    private final Map<UUID, HadesUser> hadesUserMap = new ConcurrentHashMap<>();

    public Map<UUID, HadesUser> getHadesUserMap() {
        return hadesUserMap;
    }

    public Optional<HadesUser> getUser(UUID uuid) {
        return Optional.ofNullable(hadesUserMap.get(uuid));
    }

    public void putUser(UUID uuid, HadesUser hadesUser) {
        hadesUserMap.put(uuid, hadesUser);
    }

    public void removeUser(UUID uuid) {
        hadesUserMap.remove(uuid);
    }

    public void reset() {
        hadesUserMap.clear();
    }
}
