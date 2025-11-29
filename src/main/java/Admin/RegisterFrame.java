
package Admin;

import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {

    private final JTextField usernameField = new JTextField();
    private final JTextField fullNameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JPasswordField confirmField = new JPasswordField();
    private final JComboBox<String> roleCombo = new JComboBox<>(new String[]{"CLIENT", "ADMIN"});

    public RegisterFrame() {
        super("Register Account");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setSize(420, 300);

        JPanel p = new JPanel(new GridLayout(6, 2, 8, 8));
        p.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));

        p.add(new JLabel("Username:", SwingConstants.RIGHT));      p.add(usernameField);
        p.add(new JLabel("Full Name:", SwingConstants.RIGHT));     p.add(fullNameField);
        p.add(new JLabel("Password:", SwingConstants.RIGHT));      p.add(passwordField);
        p.add(new JLabel("Confirm Password:", SwingConstants.RIGHT)); p.add(confirmField);
        p.add(new JLabel("Role:", SwingConstants.RIGHT));          p.add(roleCombo);

        JButton registerBtn = new JButton("Register");
        p.add(new JLabel());
        p.add(registerBtn);

        setContentPane(p);

        registerBtn.addActionListener(e -> onRegister());
    }

    private void onRegister() {
        String username = usernameField.getText().trim();
        String fullName = fullNameField.getText().trim();
        String pass = new String(passwordField.getPassword());
        String confirm = new String(confirmField.getPassword());
        String role = (String) roleCombo.getSelectedItem();

        if (username.isEmpty() || fullName.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields."); return;
        }
        if (!pass.equals(confirm)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match."); return;
        }
        if (pass.length() < 4) { // minimal rule; adjust as you wish
            JOptionPane.showMessageDialog(this, "Password must be at least 4 characters."); return;
        }

        try {
            boolean ok = AuthStore.registerUser(username, fullName, pass, role);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Registration successful. You can now log in.");
                dispose(); // close register window
            } else {
                JOptionPane.showMessageDialog(this, "Username already exists.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
