package backend.enumeration;


public enum EntityType {
    ORDER("O-"),
    WAREHOUSE("W-");

    private final String prefix;

    EntityType(String prefix) {
        this.prefix = prefix;
    }

    public String getPrefix() {
        return prefix;
    }
}


