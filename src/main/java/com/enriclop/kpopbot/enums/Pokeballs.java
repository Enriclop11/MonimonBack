package com.enriclop.kpopbot.enums;

public enum Pokeballs {
    POKEBALL(100),
    SUPERBALL(60),
    ULTRABALL(30),
    MASTERBALL(0);

    public final int catchRate;

    private Pokeballs(int catchRate) {
        this.catchRate = catchRate;
    }
}
