package com.safety;

import com.safety.service.SafetyService;
import com.safety.ui.SafetyFrame;

import javax.swing.*;

public class WomenSafetyApplication {

    public static void main(String[] args) {
        // Enable anti-aliasing and system look-and-feel adjustments
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            SafetyService safetyService = new SafetyService();
            SafetyFrame frame = new SafetyFrame(safetyService);
            frame.setVisible(true);
        });
    }
}
