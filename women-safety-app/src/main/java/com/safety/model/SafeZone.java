package com.safety.model;

public class SafeZone {

    public enum SecurityLevel {
        SAFE,
        CAUTION,
        HIGH_RISK
    }

    private final String name;
    private final double centerLat;
    private final double centerLng;
    private final double radiusMeters;
    private final SecurityLevel level;
    private final String description;

    public SafeZone(String name, double centerLat, double centerLng, double radiusMeters, SecurityLevel level, String description) {
        this.name = name;
        this.centerLat = centerLat;
        this.centerLng = centerLng;
        this.radiusMeters = radiusMeters;
        this.level = level;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public double getCenterLat() {
        return centerLat;
    }

    public double getCenterLng() {
        return centerLng;
    }

    public double getRadiusMeters() {
        return radiusMeters;
    }

    public SecurityLevel getLevel() {
        return level;
    }

    public String getDescription() {
        return description;
    }

    public boolean contains(double lat, double lng) {
        // Haversine formula distance calculation in meters
        final int R = 6371000;
        double latDistance = Math.toRadians(lat - centerLat);
        double lonDistance = Math.toRadians(lng - centerLng);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(centerLat)) * Math.cos(Math.toRadians(lat))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;

        return distance <= radiusMeters;
    }
}
