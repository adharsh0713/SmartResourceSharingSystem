package ui.gui;

import service.ResourceService;
import model.Item;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;

public class BrowseResourcesPanel extends JPanel {
    public BrowseResourcesPanel(ResourceService rs) {
        setLayout(new BorderLayout());
        List<Item> items = rs.sortedByName();
        String[] cols = {"ID","Name","Qty"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (Item it : items) {
            model.addRow(new Object[]{ it.getId(), it.getName(), it.getQuantity() });
        }
        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }
}
