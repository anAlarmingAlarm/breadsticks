package com.breadsticksmod.core.http.api.player;

import com.breadsticksmod.core.http.api.Printable;

public enum StoreRank implements Printable {
    CHAMPION("Champion"),
    HERO("Hero"),
    VIP_PLUS("VIP+"),
    VIP("VIP"),
    REGULAR("Regular");

    private final String prettyString;

    StoreRank(String prettyString) {
        this.prettyString = prettyString;
    }

    public String prettyPrint() {
        return this.prettyString;
    }
}
