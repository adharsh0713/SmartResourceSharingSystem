package ui.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class UIStyle {

    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public static JPanel createToolbar() {
        JPanel p = new JPanel();
        p.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));
        p.setBackground(new Color(235, 235, 235));
        p.setBorder(new EmptyBorder(10, 10, 10, 10));
        return p;
    }

    public static JButton styledButton(String text) {
        JButton b = new JButton(text);
        b.setFont(BUTTON_FONT);
        b.setFocusPainted(false);
        b.setBackground(new Color(230, 230, 250));
        b.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    public static JLabel titleLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(TITLE_FONT);
        return l;
    }

    public static void styleTable(JTable table) {
        table.setRowHeight(25);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(220, 220, 220));
        table.setGridColor(Color.LIGHT_GRAY);
    }
}
