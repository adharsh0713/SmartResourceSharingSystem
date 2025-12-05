package ui.gui;

import data.DataStore;
import service.*;
import javax.swing.*;

public class GuiLauncher {
    public static void main(String[] args) {
        DataStore store = new DataStore();
        UserService us = new UserService(store);
        ResourceService rs = new ResourceService(store);
        RequestService rqs = new RequestService(store, rs, us);

        SwingUtilities.invokeLater(() -> {
            LoginFrame lf = new LoginFrame(us, rs, rqs);
            lf.setVisible(true);
        });
    }
}
