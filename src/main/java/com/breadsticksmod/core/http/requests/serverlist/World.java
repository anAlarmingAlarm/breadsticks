package com.breadsticksmod.core.http.requests.serverlist;

import com.breadsticksmod.core.json.BaseModel;
import com.breadsticksmod.core.time.Duration;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class World extends BaseModel implements Collection<String> {

    @Key @Null protected String world;

    @Key private Set<String> players;
    @Key private Date firstSeen;

    public String getWorld() {
        return world;
    }

    public Date getFirstSeen() {
        return firstSeen;
    }

    public Duration getUptime() {
        return Duration.since(getFirstSeen());
    }

    @Override
    public int size() {
        return players.size();
    }

    @Override
    public boolean isEmpty() {
        return players.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return players.contains(o);
    }

    @NotNull
    @Override
    public Iterator<String> iterator() {
        return players.iterator();
    }

    @NotNull
    @Override
    public Object @NotNull [] toArray() {
        return players.toArray();
    }

    @NotNull
    @Override
    public <T> T @NotNull [] toArray(@NotNull T @NotNull [] a) {
        return players.toArray(a);
    }

    @Override
    public boolean add(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return players.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends String> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return world;
    }
}
