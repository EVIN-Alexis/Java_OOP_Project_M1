package brasserie.ui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.JTableHeader;
import java.awt.*;

public final class CraftTheme {

    public static final Color FOREST = new Color(39, 82, 68);
    public static final Color FOREST_LIGHT = new Color(48, 122, 98);
    public static final Color CREAM = new Color(243, 235, 221);
    public static final Color CARD = new Color(250, 247, 240);
    public static final Color COPPER = new Color(184, 117, 67);
    public static final Color TEXT = new Color(41, 45, 41);
    public static final Color MUTED = new Color(112, 108, 98);
    public static final Color WHITE = Color.WHITE;

    private CraftTheme() {
    }

    public static void install() {

        UIManager.put("Panel.background", CREAM);

        UIManager.put("TabbedPane.background", CREAM);
        UIManager.put("TabbedPane.foreground", TEXT);
        UIManager.put("TabbedPane.selected", CARD);
        UIManager.put("TabbedPane.contentAreaColor", CREAM);
        UIManager.put("TabbedPane.focus", COPPER);

        UIManager.put("ComboBox.background", CARD);
        UIManager.put("ComboBox.foreground", TEXT);

        UIManager.put("Spinner.background", CARD);
        UIManager.put("Spinner.foreground", TEXT);

        UIManager.put("ScrollPane.background", CREAM);
        UIManager.put("Viewport.background", CREAM);

        UIManager.put("Table.background", CARD);
        UIManager.put("Table.foreground", TEXT);
        UIManager.put("Table.selectionBackground", FOREST_LIGHT);
        UIManager.put("Table.selectionForeground", WHITE);

        UIManager.put("OptionPane.background", CREAM);
        UIManager.put("Label.foreground", TEXT);

        UIManager.put("Button.background", FOREST);
        UIManager.put("Button.foreground", WHITE);
    }

    public static void apply(Component component) {

        // Panneaux généraux : fond crème
        if (component instanceof JPanel panel) {
            panel.setBackground(CREAM);
            panel.setOpaque(true);
        }

        if (component instanceof JFrame frame) {
            frame.getContentPane().setBackground(CREAM);
        }

        // Champs de texte
        if (component instanceof JTextField field) {
            field.setBackground(CARD);
            field.setForeground(TEXT);
            field.setCaretColor(FOREST);
        }

        // Zones de texte
        if (component instanceof JTextArea area) {
            area.setBackground(CARD);
            area.setForeground(TEXT);
            area.setCaretColor(FOREST);
            area.setSelectionColor(FOREST_LIGHT);
            area.setSelectedTextColor(WHITE);
            area.setBorder(new EmptyBorder(10, 10, 10, 10));
        }

        // Cases à cocher
        if (component instanceof JCheckBox checkBox) {
            checkBox.setBackground(CREAM);
            checkBox.setForeground(TEXT);
            checkBox.setOpaque(true);
        }

        // Boutons radio
        if (component instanceof JRadioButton radio) {
            radio.setBackground(CREAM);
            radio.setForeground(TEXT);
            radio.setOpaque(true);
        }

        // Labels
        if (component instanceof JLabel label) {
            label.setFont(new Font(
                    "SansSerif",
                    label.getFont().getStyle(),
                    label.getFont().getSize()
            ));
        }

        // Boutons
        if (component instanceof JButton button) {
            button.setBackground(FOREST);
            button.setForeground(WHITE);
            button.setFont(new Font("SansSerif", Font.BOLD, 13));
            button.setFocusPainted(false);
            button.setOpaque(true);

            button.setBorder(new CompoundBorder(
                    new LineBorder(FOREST, 1, true),
                    new EmptyBorder(9, 16, 9, 16)
            ));

            button.setCursor(Cursor.getPredefinedCursor(
                    Cursor.HAND_CURSOR
            ));

            String name = button.getText();

            if (name != null && (
                    name.toLowerCase().contains("supprimer")
                            || name.toLowerCase().contains("bilan")
            )) {
                Color danger = new Color(145, 55, 48);

                button.setBackground(danger);
                button.setBorder(new CompoundBorder(
                        new LineBorder(danger, 1, true),
                        new EmptyBorder(9, 16, 9, 16)
                ));
            }
        }

        // Tableaux
        if (component instanceof JTable table) {
            table.setBackground(CARD);
            table.setForeground(TEXT);
            table.setSelectionBackground(FOREST_LIGHT);
            table.setSelectionForeground(WHITE);
            table.setGridColor(new Color(222, 211, 193));
            table.setRowHeight(28);
            table.setFont(new Font("SansSerif", Font.PLAIN, 13));
            table.setFillsViewportHeight(true);

            JTableHeader header = table.getTableHeader();
            header.setBackground(FOREST);
            header.setForeground(WHITE);
            header.setFont(new Font("SansSerif", Font.BOLD, 13));
            header.setPreferredSize(new Dimension(0, 34));
        }

        // Listes déroulantes
        if (component instanceof JComboBox<?> combo) {
            combo.setBackground(CARD);
            combo.setForeground(TEXT);
            combo.setFont(new Font("SansSerif", Font.PLAIN, 13));
        }

        // Sélecteurs numériques
        if (component instanceof JSpinner spinner) {
            spinner.setBackground(CARD);
            spinner.setForeground(TEXT);
            spinner.setFont(new Font("SansSerif", Font.PLAIN, 13));
        }

        // Listes
        if (component instanceof JList<?> list) {
            list.setBackground(CARD);
            list.setForeground(TEXT);
            list.setSelectionBackground(FOREST_LIGHT);
            list.setSelectionForeground(WHITE);
            list.setFont(new Font("SansSerif", Font.PLAIN, 14));
        }

        // Panneaux de défilement
        if (component instanceof JScrollPane scrollPane) {
            scrollPane.setBackground(CREAM);

            scrollPane.setBorder(new LineBorder(
                    new Color(222, 211, 193), 1, true
            ));

            scrollPane.getViewport().setBackground(CARD);
        }

        // Onglets
        if (component instanceof JTabbedPane tabbedPane) {
            tabbedPane.setBackground(CREAM);
            tabbedPane.setForeground(TEXT);
            tabbedPane.setFont(new Font("SansSerif", Font.BOLD, 13));
        }

        // Appliquer récursivement aux composants enfants
        if (component instanceof Container container) {
            for (Component child : container.getComponents()) {
                apply(child);
            }
        }
    }
}