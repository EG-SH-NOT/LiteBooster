package org.infernworld.litebooster.manager;

import lombok.Getter;

@Getter
public enum BoosterType {
    EXP("exp", "Опыта"),
    CULT("cult", "Культуры"),
    COIN("coin", "Монет"),
    ALL("all","Все бустеры");
    private final String id;
    private final String displayName;

    BoosterType(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }
}

