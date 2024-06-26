package com.breadsticksmod.core.http.api.player;

import java.util.Optional;
import java.util.UUID;

public interface PlayerType {
   String username();

   UUID uuid();

   Optional<String> world();
}
