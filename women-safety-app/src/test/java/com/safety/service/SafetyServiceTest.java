package com.safety.service;

import com.safety.model.Contact;
import com.safety.model.IncidentRecord;
import com.safety.model.SafeZone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SafetyServiceTest {

    private SafetyService safetyService;

    @BeforeEach
    void setUp() {
        safetyService = new SafetyService();
    }

    @Test
    @DisplayName("SOS Alert - Trigger dispatches location message and records incident log")
    void testSosAlertTrigger_DispatchesLocationAndLogsIncident() {
        String alertMsg = safetyService.triggerSosAlert();

        assertTrue(safetyService.isSosActive());
        assertNotNull(alertMsg);
        assertTrue(alertMsg.contains("EMERGENCY SOS ALERT"));
        assertTrue(alertMsg.contains("https://maps.google.com/?q="));

        List<IncidentRecord> logs = safetyService.getIncidentLogs();
        assertFalse(logs.isEmpty());
        assertEquals(IncidentRecord.Type.SOS_DISPATCH, logs.get(logs.size() - 1).getType());
    }

    @Test
    @DisplayName("SOS Alert - Cancel deactivates active alert")
    void testCancelSosAlert_DeactivatesAlert() {
        safetyService.triggerSosAlert();
        assertTrue(safetyService.isSosActive());

        safetyService.cancelSosAlert();
        assertFalse(safetyService.isSosActive());
    }

    @Test
    @DisplayName("Contacts Vault - Add and remove trusted contact")
    void testAddAndRemoveContact() {
        int initialCount = safetyService.getContacts().size();

        Contact newContact = new Contact("Mom", "+91-9988776655", "Family", true);
        safetyService.addContact(newContact);

        assertEquals(initialCount + 1, safetyService.getContacts().size());

        safetyService.removeContact(newContact.getId());
        assertEquals(initialCount, safetyService.getContacts().size());
    }

    @Test
    @DisplayName("Contacts Vault - Throws exception on invalid contact input")
    void testAddContact_InvalidInputThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                safetyService.addContact(new Contact("", "", "Unknown", false)));
    }

    @Test
    @DisplayName("Safe Zone Geofencing - Evaluates safe vs high-risk coordinates")
    void testSafeZoneGeofence_EvaluatesCoordinates() {
        // Move to Delhi University (Safe Zone)
        safetyService.updateGpsLocation(28.6139, 77.2090, 0.0);
        SafeZone zone1 = safetyService.getCurrentSafeZoneStatus();
        assertEquals(SafeZone.SecurityLevel.SAFE, zone1.getLevel());

        // Move to Dark Zone (High Risk)
        safetyService.updateGpsLocation(28.6500, 77.2500, 5.0);
        SafeZone zone2 = safetyService.getCurrentSafeZoneStatus();
        assertEquals(SafeZone.SecurityLevel.HIGH_RISK, zone2.getLevel());
    }

    @Test
    @DisplayName("Fake Call Escape - Generates incoming call and logs incident")
    void testFakeCallTrigger_CreatesIncidentLog() {
        int initialLogs = safetyService.getIncidentLogs().size();

        String caller = safetyService.triggerFakeCall("Dad");

        assertEquals("Dad", caller);
        assertEquals(initialLogs + 1, safetyService.getIncidentLogs().size());
        assertEquals(IncidentRecord.Type.FAKE_CALL_TRIGGER,
                safetyService.getIncidentLogs().get(safetyService.getIncidentLogs().size() - 1).getType());
    }
}
