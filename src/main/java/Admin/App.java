
package Admin;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AdminLogin().setVisible(true));
    }
}
