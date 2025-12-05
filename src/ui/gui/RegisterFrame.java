package ui.gui;

import service.UserService;
import model.User;
import exceptions.InvalidInputException;

import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {
    public RegisterFrame(UserService userService) {
        super("Register");
        setSize(400,220);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel p = new JPanel(new GridLayout(5,2,5,5));
        JTextField id = new JTextField();
        JTextField name = new JTextField();
        JTextField email = new JTextField();
        JPasswordField pw = new JPasswordField();
        JButton submit = new JButton("Register");
        p.add(new JLabel("Id:")); p.add(id);
        p.add(new JLabel("Name:")); p.add(name);
        p.add(new JLabel("Email:")); p.add(email);
        p.add(new JLabel("Password:")); p.add(pw);
        p.add(submit);
        add(p);
        submit.addActionListener(e -> {
            try {
                User u = userService.register(id.getText().trim(), name.getText().trim(), email.getText().trim(), new String(pw.getPassword()));
                JOptionPane.showMessageDialog(this, "Registered " + u.getName());
                this.dispose();
            } catch (InvalidInputException ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
    }
}
