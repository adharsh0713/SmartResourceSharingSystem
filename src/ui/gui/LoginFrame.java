package ui.gui;

import service.*;
import model.User;
import exceptions.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {
    private final UserService userService;
    private final ResourceService resourceService;
    private final RequestService requestService;

    public LoginFrame(UserService us, ResourceService rs, RequestService rqs) {
        super("Smart Resource - Login");
        this.userService = us;
        this.resourceService = rs;
        this.requestService = rqs;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400,200);
        setLocationRelativeTo(null);
        init();
    }

    private void init() {
        JPanel p = new JPanel(new GridLayout(4,2,5,5));
        JTextField idField = new JTextField();
        JPasswordField pwField = new JPasswordField();
        JButton loginBtn = new JButton("Login");
        JButton regBtn = new JButton("Register");

        p.add(new JLabel("User ID:")); p.add(idField);
        p.add(new JLabel("Password:")); p.add(pwField);
        p.add(loginBtn); p.add(regBtn);

        add(p);

        loginBtn.addActionListener(e -> {
            try {
                User u = userService.login(idField.getText().trim(), new String(pwField.getPassword()));
                JOptionPane.showMessageDialog(this, "Welcome " + u.getName());
                DashboardFrame df = new DashboardFrame(userService, resourceService, requestService, u);
                df.setVisible(true);
                this.dispose();
            } catch (UserNotFoundException ex) {
                JOptionPane.showMessageDialog(this, "Login failed: " + ex.getMessage());
            }
        });

        regBtn.addActionListener(e -> {
            RegisterFrame rf = new RegisterFrame(userService);
            rf.setVisible(true);
        });
    }
}
