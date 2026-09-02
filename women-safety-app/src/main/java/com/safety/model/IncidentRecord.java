package com.safety.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class IncidentRecord {

    public enum Type {
        SOS_DISPATCH,
        FAKE_CALL_TRIGGER,
        SIREN_ALARM,
        SAFE_ZONE_ALERT,
        MANUAL_CHECKIN
    }

    private final String id;
    private final LocalDateTime timestamp;
    private final Type type;
    private final String locationCoords;
    private final String details;

    public IncidentRecord(Type type, String locationCoords, String details) {
        this.id = "INC-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        this.timestamp = LocalDateTime.now();
        this.type = type;
        this.locationCoords = locationCoords;
        this.details = details;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getFormattedTimestamp() {
        return timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public Type getType() {
        return type;
    }

    public String getLocationCoords() {
        return locationCoords;
    }

    public String getDetails() {
        return details;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Type: %s | Location: %s | %s",
                id, getFormattedTimestamp(), type, locationCoords, details);
    }
}
