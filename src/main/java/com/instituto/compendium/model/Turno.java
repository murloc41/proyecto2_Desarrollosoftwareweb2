package com.instituto.compendium.model;

public enum Turno {
    MANANA("Mañana"),
    TARDE("Tarde"),
    NOCHE("Noche");

    private final String displayName;

    Turno(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
