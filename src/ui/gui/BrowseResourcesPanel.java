package ui.gui;

import service.ResourceService;
import service.RequestService;
import model.Item;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * BrowseResourcesPanel shows items in a JTable and allows deletion (if permitted).
 * It accepts the currentUser so delete permission can be enforced, and requestService
 * to check active borrows before allowing deletion.
 */
public class BrowseResourcesPanel extends JPanel {
    private final ResourceService resourceService;
    private final RequestService requestService;
    private final User currentUser;
    private JTable table;
    private DefaultTableModel model;

    // Constructor: show all items
    public BrowseResourcesPanel(ResourceService rs, User currentUser, RequestService rqs) {
        this(rs, rs.sortedByName(), currentUser, rqs);
    }

    // Constructor: show a filtered list
    public BrowseResourcesPanel(ResourceService rs, List<Item> items, User currentUser, RequestService rqs) {
        super(new BorderLayout());
        this.resourceService = rs;
        this.requestService = rqs;
        this.currentUser = currentUser;

        String[] cols = {"ID", "Name", "Qty", "Added By", "User ID"};
        model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        UIStyle.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // populate rows
        for (Item it : items) {
            model.addRow(new Object[]{
                it.getId(),
                it.getName(),
                it.getQuantity(),
                it.getAddedByUserName(),
                it.getAddedByUserId()
            });
        }

        add(new JScrollPane(table), BorderLayout.CENTER);

        // Delete button (visible to admin and item owner)
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton deleteBtn = new JButton("Delete Selected");
        bottom.add(deleteBtn);
        add(bottom, BorderLayout.SOUTH);

        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a resource to delete.");
                return;
            }

            String itemId = String.valueOf(model.getValueAt(row, 0));

            try {
                // Let the service handle all permission + null checks
                resourceService.deleteItem(itemId, currentUser, requestService);

                JOptionPane.showMessageDialog(this, "Resource deleted successfully!");

                // Remove from the table
                model.removeRow(row);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        this,
                        "Delete failed: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}
