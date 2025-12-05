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
        setSize(400, 200);
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
            String id = idField.getText().trim();
            String pw = new String(pwField.getPassword());

            // HARD-CODED ADMIN LOGIN
            if (id.equals("admin") && pw.equals("admin@123")) {
                User admin = new User("admin", "System Administrator", "admin@example.com", "admin@123");
                JOptionPane.showMessageDialog(this, "Welcome System Administrator");
                AdminDashboardFrame ad = new AdminDashboardFrame(userService, resourceService, requestService);
                ad.setVisible(true);
                this.dispose();
                return;
            }

            // NORMAL USER LOGIN
            User u = userService.login(id, pw);
            JOptionPane.showMessageDialog(this, "Welcome " + u.getName());
            UserDashboardFrame df = new UserDashboardFrame(userService, resourceService, requestService, u);
            df.setVisible(true);
            this.dispose();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Login failed: " + ex.getMessage());
        }
    });



        regBtn.addActionListener(e -> {
            RegisterFrame rf = new RegisterFrame(userService);
            rf.setVisible(true);
        });
    }
}
