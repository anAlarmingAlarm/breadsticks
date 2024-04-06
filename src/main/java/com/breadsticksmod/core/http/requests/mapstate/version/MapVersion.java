package com.breadsticksmod.core.http.requests.mapstate.version;

import com.breadsticksmod.core.Promise;
import com.breadsticksmod.core.http.AbstractRequest;
import com.breadsticksmod.core.http.GetRequest;
import com.breadsticksmod.core.http.RateLimit;
import com.breadsticksmod.core.http.requests.mapstate.version.template.MapTemplate;
import com.breadsticksmod.core.json.BaseModel;
import com.breadsticksmod.core.json.Json;

import java.util.UUID;

public class MapVersion extends BaseModel {
   @Key
   private int revision;
   @Key
   private String tileSet;
   @Key("template")
   private UUID templateUUID;

   private final Promise.Getter<Tiles> tiles = new Promise.Getter<>(
           () -> new Tiles.Request(tileSet).thenApply(optional -> optional.orElse(null))
   );
   private final Promise.Getter<MapTemplate> template = new Promise.Getter<>(
           () -> new MapTemplate.Request(templateUUID).thenApply(optional -> optional.orElse(null))
   );

   public Promise<Tiles> getTileSet() {
      return tiles.get();
   }

   public Promise<MapTemplate> getTemplate() {
      return template.get();
   }

   public static class Tiles extends BaseModel {
      @Key("tileHash")
      String hash;
      @Key("offsets.x")
      long offsetX;
      @Key("offsets.y")
      long offsetY;
      @Key
      int nativeZoom;

      public String getHash() {
         return hash;
      }

      public long getOffsetX() {
         return offsetX;
      }

      public long getOffsetY() {
         return offsetY;
      }

      public int getNativeZoom() {
         return nativeZoom;
      }

      @AbstractRequest.Definition(route = "https://thesimpleones.net/api/mapTiles/%s", ratelimit = RateLimit.NONE, cache_length = 15)
      public static class Request extends GetRequest<Tiles> {
         public Request(String hash) {
            super(hash);
         }

         @org.jetbrains.annotations.Nullable
         @Override
         protected Tiles get(Json json) {
            return json.wrap(Tiles::new);
         }
      }
   }


   @AbstractRequest.Definition(route = "https://thesimpleones.net/api/mapVersions/version%s", ratelimit = RateLimit.NONE, cache_length = 15)
   public static class Request extends GetRequest<MapVersion> {
      public Request(int version) {
         super(Integer.toString(version));
      }

      @org.jetbrains.annotations.Nullable
      @Override
      protected MapVersion get(Json json) {
         var version = json.wrap(MapVersion::new);
         if (version != null) {
            version.getTileSet();
            version.getTemplate();
         }

         return version;
      }
   }
}
