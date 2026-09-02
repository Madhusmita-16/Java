package com.safety.ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SafetyButton extends JButton {

    private final Color topColor;
    private final Color bottomColor;
    private final Color hoverTop;
    private final Color hoverBottom;
    private final int cornerRadius;
    private boolean isHovered = false;
    private boolean isPressed = false;

    public SafetyButton(String text, Color baseColor, Color textColor, int cornerRadius) {
        super(text);
        this.cornerRadius = cornerRadius;

        this.topColor = baseColor.brighter();
        this.bottomColor = baseColor.darker();
        this.hoverTop = baseColor.brighter().brighter();
        this.hoverBottom = baseColor;

        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setForeground(textColor);
        setFont(new Font("Segoe UI", Font.BOLD, 13));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { isHovered = true; repaint(); }
            public void mouseExited(MouseEvent e) { isHovered = false; isPressed = false; repaint(); }
            public void mousePressed(MouseEvent e) { isPressed = true; repaint(); }
            public void mouseReleased(MouseEvent e) { isPressed = false; repaint(); }
        });
    }

    public SafetyButton(String text, Color baseColor) {
        this(text, baseColor, Color.WHITE, 12);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Draw Drop Shadow
        if (!isPressed) {
            g2.setColor(new Color(0, 0, 0, 70));
            g2.fillRoundRect(2, 4, w - 4, h - 4, cornerRadius, cornerRadius);
        }

        // Select Colors
        Color t = isPressed ? bottomColor : (isHovered ? hoverTop : topColor);
        Color b = isPressed ? topColor : (isHovered ? hoverBottom : bottomColor);

        int offsetY = isPressed ? 2 : 0;
        GradientPaint gp = new GradientPaint(0, offsetY, t, 0, h - 2 + offsetY, b);
        g2.setPaint(gp);
        g2.fillRoundRect(0, offsetY, w - 2, h - 2 - offsetY, cornerRadius, cornerRadius);

        // Inner Highlight Bezel
        g2.setColor(new Color(255, 255, 255, isHovered ? 110 : 50));
        g2.drawRoundRect(1, 1 + offsetY, w - 4, h - 4 - offsetY, cornerRadius - 2, cornerRadius - 2);

        // Outer Border Ring
        g2.setColor(isHovered ? t.brighter() : new Color(0, 0, 0, 90));
        g2.drawRoundRect(0, offsetY, w - 2, h - 2 - offsetY, cornerRadius, cornerRadius);

        g2.dispose();
        super.paintComponent(g);
    }
}
