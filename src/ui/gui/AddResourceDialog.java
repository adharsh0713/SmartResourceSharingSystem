package ui.gui;

import service.ResourceService;
import javax.swing.*;

import model.User;

import java.awt.*;

public class AddResourceDialog extends JDialog {
    public AddResourceDialog(JFrame owner, ResourceService rs, User currentUser) {
        super(owner, "Add Resource", true);
        setSize(350,200);
        setLocationRelativeTo(owner);
        JPanel p = new JPanel(new GridLayout(4,2,5,5));
        JTextField id = new JTextField();
        JTextField name = new JTextField();
        JTextField qty = new JTextField();
        JButton add = new JButton("Add");
        p.add(new JLabel("Id:")); p.add(id);
        p.add(new JLabel("Name:")); p.add(name);
        p.add(new JLabel("Qty:")); p.add(qty);
        p.add(add);
        add(p);
        add.addActionListener(e -> {
            try {
                rs.addItem(
                    id.getText().trim(),
                    name.getText().trim(),
                    Integer.parseInt(qty.getText().trim()),
                    currentUser.getId(),
                    currentUser.getName()
                );

                JOptionPane.showMessageDialog(this, "Added");
                this.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
    }
}
