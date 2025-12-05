package ui.gui;

import service.*;
import model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class DashboardFrame extends JFrame {
    private final UserService us;
    private final ResourceService rs;
    private final RequestService rqs;
    private final User user;

    public DashboardFrame(UserService us, ResourceService rs, RequestService rqs, User user) {
        super("Dashboard - " + user.getName());
        this.us = us; this.rs = rs; this.rqs = rqs; this.user = user;
        setSize(800,600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        init();
    }

    private void init() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addBtn = new JButton("Add Resource");
        JButton browseBtn = new JButton("Browse Resources");
        JButton myBorrowsBtn = new JButton("My Borrows");
        top.add(new JLabel("Hello, " + user.getName()));
        top.add(addBtn); top.add(browseBtn); top.add(myBorrowsBtn);

        add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout());
        add(center, BorderLayout.CENTER);

        browseBtn.addActionListener(e -> {
            BrowseResourcesPanel brp = new BrowseResourcesPanel(rs);
            center.removeAll();
            center.add(brp, BorderLayout.CENTER);
            center.revalidate();
            center.repaint();
        });

        addBtn.addActionListener(e -> {
            AddResourceDialog d = new AddResourceDialog(this, rs);
            d.setVisible(true);
        });

        myBorrowsBtn.addActionListener(e -> {
            List<Request> list = rqs.outstandingRequestsForUser(user.getId());
            StringBuilder sb = new StringBuilder();
            for (Request r : list) sb.append(r).append("\n");
            if (list.isEmpty()) sb.append("No active borrows");
            JOptionPane.showMessageDialog(this, sb.toString());
        });

        // start overdue thread with GUI notifications
        OverdueCheckThread ot = new OverdueCheckThread(rqs, true, (lst) -> {
            String msg = "Overdue items detected: " + lst.size();
            JOptionPane.showMessageDialog(this, msg);
        });
        ot.start();

        // autosave
        AutoSaveThread ast = new AutoSaveThread(us, rs, rqs);
        ast.start();
    }
}
