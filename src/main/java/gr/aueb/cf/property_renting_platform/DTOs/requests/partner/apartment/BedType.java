package gr.aueb.cf.property_renting_platform.DTOs.requests.partner.apartment;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public enum BedType {
    SINGLE("single", 1),
    DOUBLE("double", 2),
    KING_SIZE("king_size", 2),
    SINGLE_SOFA("single_sofa", 1),
    DOUBLE_SOFA("double_sofa", 2);

    private final String wire;
    @Getter
    private final int capacity;

    BedType(String wire, int capacity) {
        this.wire = wire;
        this.capacity = capacity;
    }

    @JsonValue
    public String getWire() {
        return wire;
    }

    @JsonCreator
    public static BedType fromWire(String value) {
        if (value == null) return null;
        String v = value.trim().toLowerCase();

        for (BedType t : values()) {
            if (t.wire.equals(v)) return t;
        }
        throw new IllegalArgumentException("Unknown BedType: " + value);
    }
}