package ru.gnev.conciergebot.bean.entity;

/**
 * Координаты квартиры в рамках дома
 */
public enum FlatCoordinate {
    ENTRANCE("entrance", "подъезд"),
    FLOOR("floor", "этаж"),
    SECTION("section", "план"),
    FLAT("flat", "квартира");

    private String value;
    private String meaning;

    FlatCoordinate(String value, String meaning) {
        this.value = value;
        this.meaning = meaning;
    }

    public String getValue() {
        return value;
    }

    public String getMeaning() {
        return meaning;
    }
}
