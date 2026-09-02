package com.safety.service;

import com.safety.model.Contact;
import com.safety.model.IncidentRecord;
import com.safety.model.SafeZone;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class SafetyService {

    private final List<Contact> contacts = new CopyOnWriteArrayList<>();
    private final List<IncidentRecord> incidentLogs = new CopyOnWriteArrayList<>();
    private final List<SafeZone> safeZones = new CopyOnWriteArrayList<>();
    private final AudioSirenService sirenService;

    // Simulated Live GPS State
    private double currentLatitude = 28.6139;  // Delhi City Center
    private double currentLongitude = 77.2090;
    private double currentSpeedKmh = 0.0;
    private boolean isSosActive = false;

    public SafetyService() {
        this.sirenService = new AudioSirenService();
        seedDefaultData();
    }

    private void seedDefaultData() {
        // Pre-seeded Official Helplines & Demo Emergency Contacts
        contacts.add(new Contact("National Emergency Helpline", "112", "Government Helpline", true));
        contacts.add(new Contact("Women Distress Helpline", "1091", "Government Helpline", true));
        contacts.add(new Contact("Domestic Abuse Line", "181", "Support Helpline", false));
        contacts.add(new Contact("Cyber Crime Cell", "1930", "Police Helpline", false));
        contacts.add(new Contact("Dad (Primary Contact)", "+91-9876543210", "Family", true));

        // Pre-seeded Safe Zones & High Risk Zones
        safeZones.add(new SafeZone("University Campus & Police Precinct", 28.6139, 77.2090, 1500.0,
                SafeZone.SecurityLevel.SAFE, "High Police Patrol & CCTV Coverage Area"));
        safeZones.add(new SafeZone("Central Metro Station", 28.6289, 77.2190, 800.0,
                SafeZone.SecurityLevel.SAFE, "24/7 Security Guards & Emergency Call Boxes"));
        safeZones.add(new SafeZone("Low Light Industrial Park", 28.6500, 77.2500, 2000.0,
                SafeZone.SecurityLevel.HIGH_RISK, "Low Visibility & Poor Street Lighting Area"));
    }

    // --- Emergency SOS Dispatch Engine ---
    public synchronized String triggerSosAlert() {
        isSosActive = true;
        sirenService.startSiren();

        String locationUrl = getFormattedLocationUrl();
        String message = String.format("EMERGENCY SOS ALERT! I need immediate help. My current GPS location: %s (Speed: %.1f km/h)",
                locationUrl, currentSpeedKmh);

        IncidentRecord record = new IncidentRecord(
                IncidentRecord.Type.SOS_DISPATCH,
                getFormattedCoordinates(),
                "SOS Emergency Alert dispatched to " + contacts.size() + " contacts. Message: " + message
        );
        incidentLogs.add(record);

        return message;
    }

    public synchronized void cancelSosAlert() {
        isSosActive = false;
        sirenService.stopSiren();
        incidentLogs.add(new IncidentRecord(
                IncidentRecord.Type.MANUAL_CHECKIN,
                getFormattedCoordinates(),
                "SOS Emergency Alert canceled by user safety PIN."
        ));
    }

    public boolean isSosActive() {
        return isSosActive;
    }

    // --- Fake Call Escape Generator ---
    public String triggerFakeCall(String callerName) {
        return triggerFakeCall(callerName, "+91-9876543210");
    }

    public String triggerFakeCall(String callerName, String phoneNumber) {
        String name = (callerName == null || callerName.trim().isEmpty()) ? "Dad" : callerName.trim();
        String phone = (phoneNumber == null || phoneNumber.trim().isEmpty()) ? "+91-9876543210" : phoneNumber.trim();

        incidentLogs.add(new IncidentRecord(
                IncidentRecord.Type.FAKE_CALL_TRIGGER,
                getFormattedCoordinates(),
                String.format("Fake escape incoming call simulated from: %s (%s)", name, phone)
        ));
        return name;
    }

    // --- Siren & Strobe Alarm Control ---
    public void toggleSirenAlarm() {
        if (sirenService.isSirenActive()) {
            sirenService.stopSiren();
        } else {
            sirenService.startSiren();
            incidentLogs.add(new IncidentRecord(
                    IncidentRecord.Type.SIREN_ALARM,
                    getFormattedCoordinates(),
                    "Manual high-decibel Siren Alarm activated."
            ));
        }
    }

    public boolean isSirenActive() {
        return sirenService.isSirenActive();
    }

    // --- Location & Geofencing ---
    public void updateGpsLocation(double lat, double lng, double speedKmh) {
        this.currentLatitude = lat;
        this.currentLongitude = lng;
        this.currentSpeedKmh = speedKmh;
    }

    public double getCurrentLatitude() {
        return currentLatitude;
    }

    public double getCurrentLongitude() {
        return currentLongitude;
    }

    public double getCurrentSpeedKmh() {
        return currentSpeedKmh;
    }

    public String getFormattedCoordinates() {
        return String.format(Locale.US, "%.4f, %.4f", currentLatitude, currentLongitude);
    }

    public String getFormattedLocationUrl() {
        return String.format(Locale.US, "https://maps.google.com/?q=%.6f,%.6f", currentLatitude, currentLongitude);
    }

    public SafeZone getCurrentSafeZoneStatus() {
        for (SafeZone zone : safeZones) {
            if (zone.contains(currentLatitude, currentLongitude)) {
                return zone;
            }
        }
        return new SafeZone("Standard City Area", currentLatitude, currentLongitude, 5000.0,
                SafeZone.SecurityLevel.CAUTION, "Regular urban zone. Stay vigilant.");
    }

    // --- Contacts Vault Management ---
    public void addContact(Contact contact) {
        if (contact == null || contact.getName().trim().isEmpty() || contact.getPhoneNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Contact name and phone number are required.");
        }
        contacts.add(contact);
    }

    public void updateContact(String id, String newName, String newPhone, String newRelation, boolean isPrimary) {
        for (Contact c : contacts) {
            if (c.getId().equalsIgnoreCase(id)) {
                c.setName(newName);
                c.setPhoneNumber(newPhone);
                c.setRelation(newRelation);
                c.setPrimary(isPrimary);
                return;
            }
        }
    }

    public void removeContact(String id) {
        contacts.removeIf(c -> c.getId().equalsIgnoreCase(id));
    }

    public List<Contact> getContacts() {
        return Collections.unmodifiableList(contacts);
    }

    public List<IncidentRecord> getIncidentLogs() {
        return Collections.unmodifiableList(incidentLogs);
    }

    public List<SafeZone> getSafeZones() {
        return Collections.unmodifiableList(safeZones);
    }

    public AudioSirenService getSirenService() {
        return sirenService;
    }
}
