package ui.gui;

import service.*;
import model.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class UserDashboardFrame extends JFrame {

    private final UserService us;
    private final ResourceService rs;
    private final RequestService rqs;
    private final User user;

    public UserDashboardFrame(UserService us, ResourceService rs, RequestService rqs, User user) {
        super("Dashboard - " + user.getName());
        this.us = us;
        this.rs = rs;
        this.rqs = rqs;
        this.user = user;

        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        init();
    }

    private void init() {

        // ---------- TOP TOOLBAR ----------
        JPanel top = UIStyle.createToolbar();

        JLabel helloLabel = UIStyle.titleLabel("Hello, " + user.getName());
        JButton addBtn = UIStyle.styledButton("Add Resource");
        JButton browseBtn = UIStyle.styledButton("Browse Resources");
        JButton myBorrowsBtn = UIStyle.styledButton("My Borrows");
        JTextField searchField = new JTextField(15);
        searchField.setFont(UIStyle.LABEL_FONT);
        JButton searchBtn = UIStyle.styledButton("Search");

        top.add(helloLabel);
        top.add(addBtn);
        top.add(browseBtn);
        top.add(myBorrowsBtn);
        top.add(new JLabel("Search:"));
        top.add(searchField);
        top.add(searchBtn);

        add(top, BorderLayout.NORTH);

        // ---------- CENTER PANEL ----------
        JPanel center = new JPanel(new BorderLayout());
        add(center, BorderLayout.CENTER);

        // Default view
        center.add(new BrowseResourcesPanel(rs, user, rqs), BorderLayout.CENTER);

        // Browse button
        browseBtn.addActionListener(e -> {
            center.removeAll();
            center.add(new BrowseResourcesPanel(rs, user, rqs), BorderLayout.CENTER);
            center.revalidate();
            center.repaint();
        });

        // Add button
        addBtn.addActionListener(e -> {
            AddResourceDialog d = new AddResourceDialog(this, rs, user);
            d.setVisible(true);

            center.removeAll();
            center.add(new BrowseResourcesPanel(rs, user, rqs), BorderLayout.CENTER);
            center.revalidate();
            center.repaint();
        });

        // Search
        searchBtn.addActionListener(e -> {
            String pattern = searchField.getText().trim();
            List<Item> filtered = ResourceService.filterByNameWildcard(rs.listAll(), pattern);

            center.removeAll();
            center.add(new BrowseResourcesPanel(rs, filtered, user, rqs), BorderLayout.CENTER);
            center.revalidate();
            center.repaint();
        });

        // My borrows
        myBorrowsBtn.addActionListener(e -> {
            List<Request> list = rqs.outstandingRequestsForUser(user.getId());
            StringBuilder sb = new StringBuilder();
            for (Request r : list) sb.append(r).append("\n");
            if (list.isEmpty()) sb.append("No active borrows");
            JOptionPane.showMessageDialog(this, sb.toString());
        });

        // Background threads
        OverdueCheckThread ot = new OverdueCheckThread(rqs, true, (lst) ->
                JOptionPane.showMessageDialog(this, "Overdue items detected: " + lst.size()));
        ot.start();

        AutoSaveThread ast = new AutoSaveThread(us, rs, rqs);
        ast.start();
    }

}
