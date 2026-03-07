package gr.aueb.cf.property_renting_platform.models.property;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ParkingPolicy {
    NONE("no"),
    FREE("free"),
    PAID("paid");

    private final String wire;

    ParkingPolicy(String wire) {
        this.wire = wire;
    }

    @JsonValue
    public String getWire() {
        return wire;
    }

    @JsonCreator
    public static ParkingPolicy fromWire(String value) {
        if (value == null) return null;
        String v = value.trim().toLowerCase();
        for (ParkingPolicy p : values()) {
            if (p.wire.equals(v)) return p;
        }
        throw new IllegalArgumentException("Unknown ParkingPolicy: " + value);
    }
}
