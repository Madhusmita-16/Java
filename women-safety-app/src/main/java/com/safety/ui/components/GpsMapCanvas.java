package com.safety.ui.components;

import com.safety.model.SafeZone;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.List;

public class GpsMapCanvas extends JPanel {

    private double currentLat = 28.6139;
    private double currentLng = 77.2090;
    private double currentSpeed = 0.0;
    private List<SafeZone> safeZones;
    private float pulseRadius = 0;
    private Timer pulseTimer;

    public GpsMapCanvas() {
        setBackground(new Color(2, 6, 23)); // Dark Radar Canvas
        setBorder(BorderFactory.createLineBorder(new Color(2, 132, 199), 2, true));
        setPreferredSize(new Dimension(0, 260));

        // Pulsating radar animation timer
        pulseTimer = new Timer(50, e -> {
            pulseRadius += 0.8f;
            if (pulseRadius > 24) pulseRadius = 0;
            repaint();
        });
        pulseTimer.start();
    }

    public void updateGps(double lat, double lng, double speed, List<SafeZone> zones) {
        this.currentLat = lat;
        this.currentLng = lng;
        this.currentSpeed = speed;
        this.safeZones = zones;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();
        int centerX = w / 2;
        int centerY = h / 2;

        // Draw Radar Grid Lines & Concentric Distance Circles
        g2.setColor(new Color(15, 23, 42));
        g2.fillRect(0, 0, w, h);

        g2.setColor(new Color(30, 41, 59, 150));
        for (int r = 40; r < Math.max(w, h); r += 45) {
            g2.drawOval(centerX - r, centerY - r, r * 2, r * 2);
        }

        // Draw Crosshair Grid
        g2.setColor(new Color(51, 65, 85, 120));
        g2.drawLine(centerX, 0, centerX, h);
        g2.drawLine(0, centerY, w, centerY);

        // Map Scale Factor: 1 pixel ~ 15 meters
        double mapScale = 0.00015;

        // Draw Safe Zones as Geofence Circles
        if (safeZones != null) {
            for (SafeZone zone : safeZones) {
                double dx = (zone.getCenterLng() - currentLng) / mapScale;
                double dy = -(zone.getCenterLat() - currentLat) / mapScale;

                int zx = (int) (centerX + dx);
                int zy = (int) (centerY + dy);
                int radiusPx = (int) Math.max(30, zone.getRadiusMeters() / 15.0);

                Color zoneColor;
                if (zone.getLevel() == SafeZone.SecurityLevel.SAFE) {
                    zoneColor = new Color(16, 185, 129, 60);
                } else if (zone.getLevel() == SafeZone.SecurityLevel.CAUTION) {
                    zoneColor = new Color(245, 158, 11, 60);
                } else {
                    zoneColor = new Color(239, 68, 68, 60);
                }

                // Fill Geofence Circle
                g2.setColor(zoneColor);
                g2.fillOval(zx - radiusPx, zy - radiusPx, radiusPx * 2, radiusPx * 2);

                // Draw Geofence Outer Outline
                g2.setColor(zoneColor.brighter());
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10f, new float[]{6f, 6f}, 0f));
                g2.drawOval(zx - radiusPx, zy - radiusPx, radiusPx * 2, radiusPx * 2);
                g2.setStroke(new BasicStroke(1f));

                // Label
                g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
                g2.setColor(Color.WHITE);
                g2.drawString(zone.getName() + " [" + zone.getLevel() + "]", zx - 40, zy - radiusPx - 4);
            }
        }

        // Draw Safe Route Vector Line to Nearest Safe Zone
        int nearestZx = centerX + 80;
        int nearestZy = centerY - 60;
        g2.setColor(new Color(56, 189, 248, 200));
        g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10f, new float[]{8f, 6f}, 0f));
        g2.drawLine(centerX, centerY, nearestZx, nearestZy);
        g2.setStroke(new BasicStroke(1f));

        g2.setFont(new Font("Segoe UI", Font.BOLD, 10));
        g2.setColor(new Color(56, 189, 248));
        g2.drawString("SAFE ROUTE PATH (350m)", (centerX + nearestZx) / 2 - 40, (centerY + nearestZy) / 2 - 5);

        // Draw Pulsating Current Position Pin
        g2.setColor(new Color(56, 189, 248, (int) Math.max(0, 200 - pulseRadius * 8)));
        g2.fill(new Ellipse2D.Float((float) centerX - pulseRadius, (float) centerY - pulseRadius, pulseRadius * 2, pulseRadius * 2));

        // Core Pin Point
        g2.setColor(new Color(239, 68, 68)); // Bright Red Core
        g2.fillOval(centerX - 8, centerY - 8, 16, 16);

        g2.setColor(Color.WHITE);
        g2.fillOval(centerX - 3, centerY - 3, 6, 6);

        // Location Info Badge Overlay
        g2.setColor(new Color(15, 23, 42, 220));
        g2.fillRoundRect(10, 10, 260, 45, 10, 10);
        g2.setColor(new Color(56, 189, 248));
        g2.drawRoundRect(10, 10, 260, 45, 10, 10);

        g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
        g2.setColor(Color.WHITE);
        g2.drawString(String.format("LIVE GPS: %.4f, %.4f", currentLat, currentLng), 18, 28);
        g2.setColor(new Color(74, 222, 128));
        g2.drawString(String.format("NAV SPEED: %.1f km/h | RADAR ACTIVE", currentSpeed), 18, 44);

        g2.dispose();
    }
}
