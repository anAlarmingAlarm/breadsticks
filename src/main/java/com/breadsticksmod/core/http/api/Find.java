
package com.breadsticksmod.core.http.api;

import com.breadsticksmod.core.http.AbstractRequest;
import com.breadsticksmod.core.http.GetRequest;
import com.breadsticksmod.core.http.RateLimit;
import com.breadsticksmod.core.json.BaseModel;
import com.breadsticksmod.core.json.Json;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public interface Find {
   class Result extends BaseModel {
      @Key private String username;
      @Key private UUID uuid;
      @Key @Null String world;

      public String getUsername() {
         return username;
      }

      public UUID getUuid() {
         return uuid;
      }

      public Optional<String> getWorld() {
         return Optional.ofNullable(world);
      }
   }

   @AbstractRequest.Definition(route = "https://thesimpleones.net/api/find?q=%s", ratelimit = RateLimit.NONE, cache_length = 0)
   class Request extends GetRequest<Result> {
      public Request(String string) {
         super(string);
      }

      @Nullable
      @Override
      protected Result get(Json json) {
         return json.wrap(Result::new);
      }
   }
}