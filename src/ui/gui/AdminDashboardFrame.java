package ui.gui;

import service.*;
import model.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboardFrame extends JFrame {

    private final UserService us;
    private final ResourceService rs;
    private final RequestService rqs;

    // Hardcoded admin user object
    private final User adminUser = new User("admin", "System Administrator", "admin@example.com", "admin@123");

    public AdminDashboardFrame(UserService us, ResourceService rs, RequestService rqs) {
        super("Admin Dashboard");
        this.us = us;
        this.rs = rs;
        this.rqs = rqs;

        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        init();
    }

    private void init() {

        // ---------------- TOP PANEL ----------------
        JPanel top = UIStyle.createToolbar();
        JTextField searchField = new JTextField(20);
        JButton searchBtn = UIStyle.styledButton("Search");
        JButton browseBtn = UIStyle.styledButton("Browse All");
        JButton pendingBtn = UIStyle.styledButton("Pending Requests");
        JButton statusBtn = UIStyle.styledButton("Borrow Status");


        top.add(new JLabel("Admin Dashboard     "));
        top.add(browseBtn);
        top.add(new JLabel("Search:"));
        top.add(searchField);
        top.add(searchBtn);
        top.add(pendingBtn);
        top.add(statusBtn);

        add(top, BorderLayout.NORTH);

        // --------------- CENTER PANEL ----------------
        JPanel center = new JPanel(new BorderLayout());
        add(center, BorderLayout.CENTER);

        // DEFAULT: Show all resources
        center.add(new BrowseResourcesPanel(rs, adminUser, rqs), BorderLayout.CENTER);

        // ------------------- SEARCH BUTTON ---------------------
        searchBtn.addActionListener(e -> {
            String pat = searchField.getText().trim();
            List<Item> filtered = ResourceService.filterByNameWildcard(rs.listAll(), pat);

            center.removeAll();
            center.add(new BrowseResourcesPanel(rs, filtered, adminUser, rqs), BorderLayout.CENTER);
            center.revalidate();
            center.repaint();
        });

        // ------------------- BROWSE BUTTON --------------------
        browseBtn.addActionListener(e -> {
            center.removeAll();
            center.add(new BrowseResourcesPanel(rs, adminUser, rqs), BorderLayout.CENTER);
            center.revalidate();
            center.repaint();
        });

        // ------------------- PENDING REQUESTS ---------------------
        pendingBtn.addActionListener(e -> {
            center.removeAll();
            center.add(buildPendingPanel(), BorderLayout.CENTER);
            center.revalidate();
            center.repaint();
        });

        // ------------------- BORROW STATUS -----------------------
        statusBtn.addActionListener(e -> {
            center.removeAll();
            center.add(buildBorrowStatusPanel(), BorderLayout.CENTER);
            center.revalidate();
            center.repaint();
        });
    }

    // ============================================================
    // PENDING REQUEST PANEL
    // ============================================================

    private JPanel buildPendingPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        List<Request> pending = rqs.listRequests().stream()
                .filter(r -> !r.isApproved())
                .toList();

        String[] cols = {"ReqID", "User", "Item", "Borrow", "Due"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        for (Request r : pending) {
            model.addRow(new Object[]{
                    r.getId(), r.getUserId(), r.getItemId(),
                    r.getBorrowDate(), r.getDueDate()
            });
        }

        JTable table = new JTable(model);
        UIStyle.styleTable(table);
        JButton approveBtn = new JButton("Approve Selected");

        approveBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a request first.");
                return;
            }
            String rid = (String) table.getValueAt(row, 0);
            try {
                rqs.approveRequest(rid);
                JOptionPane.showMessageDialog(this, "Approved!");

                // Refresh table
                model.removeRow(row);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(approveBtn, BorderLayout.SOUTH);

        return panel;
    }

    // ============================================================
    // BORROW STATUS PANEL
    // ============================================================

    private JPanel buildBorrowStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        List<Request> all = rqs.listRequests();

        String[] cols = {"ReqID", "User", "Item", "Borrow", "Due", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        for (Request r : all) {

            String status;
            if (!r.isApproved()) status = "Pending Approval";
            else if (r.isReturned()) status = "Returned";
            else if (r.getDueDate().isBefore(java.time.LocalDate.now()))
                status = "OVERDUE";
            else status = "Borrowed (Active)";

            model.addRow(new Object[]{
                    r.getId(), r.getUserId(), r.getItemId(),
                    r.getBorrowDate(), r.getDueDate(), status
            });
        }

        JTable table = new JTable(model);
        UIStyle.styleTable(table);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }
}
