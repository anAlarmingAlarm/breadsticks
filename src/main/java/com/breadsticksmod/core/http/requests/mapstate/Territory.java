package com.breadsticksmod.core.http.requests.mapstate;

import com.breadsticksmod.core.Promise;
import com.breadsticksmod.core.collector.Characteristics;
import com.breadsticksmod.core.collector.SimpleCollector;
import com.breadsticksmod.core.http.AbstractRequest;
import com.breadsticksmod.core.http.GetRequest;
import com.breadsticksmod.core.http.RateLimit;
import com.breadsticksmod.core.http.api.guild.Guild;
import com.breadsticksmod.core.http.api.guild.GuildType;
import com.breadsticksmod.core.json.Json;
import com.breadsticksmod.core.time.Duration;
import com.breadsticksmod.core.tuples.Pair;
import com.breadsticksmod.core.util.Comparing;
import net.minecraft.core.Position;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.Range;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.stream.Collector.Characteristics.UNORDERED;

public interface Territory {
   record Impl(String name, Owner owner, Date acquired, Location location) implements Territory {

      @Override
      public String getName() {
         return name;
      }

      @Override
      public Owner getOwner() {
         return owner;
      }

      @Override
      public Date getAcquired() {
         return acquired;
      }

      @Override
      public Location getLocation() {
         return location;
      }
   }

   String getName();

   Owner getOwner();

   Date getAcquired();

   default Duration getHeldFor() {
      return Duration.since(getAcquired());
   }

   Location getLocation();

   interface Owner extends GuildType {
      final class Impl implements Owner {
         private final String guild;
         private final String prefix;
         private int owned = 0;

         public Impl(String guild, String prefix) {
            this.guild = guild;
            this.prefix = prefix;
         }

         @Override
         public String name() {
            return guild;
         }

         @Override
         public String prefix() {
            return prefix;
         }

         @Override
         public void setOwned(int owned) {
            this.owned = owned;
         }

         @Override
         public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (Impl) obj;
            return Objects.equals(this.guild, that.guild) &&
                    Objects.equals(this.prefix, that.prefix) &&
                    this.owned == that.owned;
         }

         @Override
         public int hashCode() {
            return Objects.hash(guild, prefix, owned);
         }

         @Override
         public String toString() {
            return "Impl[" +
                    "guild=" + guild + ", " +
                    "prefix=" + prefix + ", " +
                    "owned=" + owned + ']';
         }
      }

      void setOwned(int owned);

      default Promise<Optional<Guild>> asGuild() {
         return new Guild.Request(name());
      }
   }

   interface Location {
      record Impl(long startX, long startZ, long endX, long endZ) implements Location {
         @Override
         public long getStartX() {
            return startX;
         }

         @Override
         public long getStartZ() {
            return startZ;
         }

         @Override
         public long getEndX() {
            return endX;
         }

         @Override
         public long getEndZ() {
            return endZ;
         }
      }

      long getStartX();

      long getStartZ();

      long getEndX();

      long getEndZ();

      default boolean isInside(Position position) {
         return Range.between((double) getStartX(), (double) getEndX()).contains(position.x()) &&
                 Range.between((double) getStartZ(), (double) getEndZ()).contains(position.z());
      }

      default Position getCenter() {
         return Vec3.ZERO.add(
                 (getStartX() + getEndX()) / 2D,
                 0,
                 (getStartZ() + getEndZ()) / 2D
         );
      }
   }

   interface List<T extends Territory> extends Collection<T> {
      record Impl(Map<String, Territory> state, Date timestamp) implements List<Territory> {

         @Override
         public Territory get(String territory) {
            return state.get(territory);
         }

         @Override
         public boolean contains(String territory) {
            return state.containsKey(territory);
         }

         @Override
         public Date getTimestamp() {
            return timestamp;
         }

         @Override
         public int size() {
            return state.size();
         }

         @Override
         public boolean isEmpty() {
            return state.isEmpty();
         }

         @NotNull
         @Override
         public Iterator<Territory> iterator() {
            return state.values().iterator();
         }

         @NotNull
         @Override
         public Object @NotNull [] toArray() {
            return state.values().toArray();
         }

         @NotNull
         @Override
         public <T> T @NotNull [] toArray(@NotNull T @NotNull [] a) {
            return state.values().toArray(a);
         }
      }

      T get(String territory);

      default T get(Territory territory) {
         return get(territory.getName());
      }

      boolean contains(String territory);

      default boolean contains(Territory territory) {
         return contains(territory.getName());
      }


      default boolean contains(Object o) {
         if (o instanceof String string) return contains(string);
         else if (o instanceof Territory territory) return contains(territory.getName());

         return false;
      }

      Date getTimestamp();

      default java.util.List<T> getOwned(Owner owner) {
         return getOwned(owner::equals);
      }

      default java.util.List<T> getOwned(String guild) {
         return getOwned(owner -> owner.name().equals(guild));
      }

      default java.util.List<T> getOwnedByPrefix(String prefix) {
         return getOwned(owner -> owner.prefix().equals(prefix));
      }

      default java.util.List<T> getOwned(Predicate<Owner> predicate) {
         return stream()
                 .filter(territory -> predicate.test(territory.getOwner()))
                 .toList();
      }

      default java.util.List<Owner> getLeaderboard() {
         Map<String, Pair<Owner, AtomicInteger>> count = new HashMap<>();

         for (Territory territory : this) {
            if (!count.containsKey(territory.getOwner().name())) {
               count.put(territory.getOwner().name(), new Pair<>(territory.getOwner(), new AtomicInteger()));
            }

            count.get(territory.getOwner().name()).two().incrementAndGet();
         }

         return count.values()
                 .stream()
                 .sorted(Comparing.of(
                         (record1, record2) -> record2.two().get() - record1.two().get(),
                         Comparator.comparing(record -> record.one().name())
                 ))
                 .map(record -> {
                    record.one().setOwned(record.two().get());

                    return record.one();
                 })
                 .toList();
      }

      @Override
      default boolean add(T territory) {
         throw new UnsupportedOperationException();
      }

      @Override
      default boolean remove(Object o) {
         throw new UnsupportedOperationException();
      }

      @Override
      default boolean addAll(@NotNull Collection<? extends T> c) {
         throw new UnsupportedOperationException();
      }

      @Override
      default boolean removeAll(@NotNull Collection<?> c) {
         throw new UnsupportedOperationException();
      }

      @Override
      default boolean retainAll(@NotNull Collection<?> c) {
         throw new UnsupportedOperationException();
      }

      @Override
      default boolean containsAll(@NotNull Collection<?> c) {
         for (Object obj : c) {
            if (!contains(obj)) return false;
         }

         return true;
      }

      @Override
      default void clear() {
         throw new UnsupportedOperationException();
      }

      default java.util.List<Change> compare(List<?> previous) {
         return stream()
                 .filter(territory -> !territory.getOwner().equals(previous.get(territory).getOwner()))
                 .map(territory -> (Change) new Change.Impl(previous.get(territory), territory))
                 .toList();
      }

      static List<Territory> empty() {
         return new Territory.List.Impl(new HashMap<>(), new Date());
      }
   }

   interface Change {
      record Impl(Territory before, Territory after) implements Change {
         @Override
         public Territory getBefore() {
            return before;
         }

         @Override
         public Territory getAfter() {
            return after;
         }
      }

      default String getTerritory() {
         return getAfter().getName();
      }

      Territory getBefore();

      Territory getAfter();

      default Duration getHeldFor() {
         return Duration.of(getBefore().getAcquired(), getAfter().getAcquired());
      }
   }

   @AbstractRequest.Definition(route = "https://thesimpleones.net/api/territoryList", ratelimit = RateLimit.NONE, cache_length = 0)
   class Request extends GetRequest<MapState> {
      @Nullable
      @Override
      protected MapState get(Json json) {
         var map = json.wrap(MapState::new);
         if (map != null) map.getVersion();

         return map;
      }
   }

   @Characteristics({UNORDERED})
   class Collector extends SimpleCollector<Territory, Map<String, Territory>, List<Territory>> {
      private final Supplier<Date> timestamp;


      public Collector(Supplier<Date> timestamp) {
         this.timestamp = timestamp;
      }

      public Collector(Date timestamp) {
         this(() -> timestamp);
      }

      public Collector() {
         this(new Date());
      }


      @Override
      protected Map<String, Territory> supply() {
         return new HashMap<>();
      }

      @Override
      protected void accumulate(Map<String, Territory> container, Territory value) {
         container.put(value.getName(), value);
      }

      @Override
      protected Map<String, Territory> combine(Map<String, Territory> left, Map<String, Territory> right) {
         left.putAll(right);

         return left;
      }

      @Override
      protected List finish(Map<String, Territory> container) {
         return new List.Impl(container, timestamp.get());
      }
   }
}
