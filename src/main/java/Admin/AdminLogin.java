
package Admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;

public class AdminLogin extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public AdminLogin() {
        super("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("Login", SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        content.add(title, BorderLayout.NORTH);

        // --- Form (username/password) ---
        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.add(new JLabel("Username:", SwingConstants.RIGHT));
        usernameField = new JTextField();
        form.add(usernameField);

        form.add(new JLabel("Password:", SwingConstants.RIGHT));
        passwordField = new JPasswordField();
        form.add(passwordField);

        // --- THIS IS WHERE YOU ADD THE SNIPPET ---
        // Create Register button
        JButton registerBtn = new JButton("Register");
        registerBtn.addActionListener(ev ->
                SwingUtilities.invokeLater(() -> new RegisterFrame().setVisible(true))
        );

        // Two login buttons (Admin + Client) in one row
        form.add(new JLabel()); // spacer
        JPanel actionRow = new JPanel(new GridLayout(1, 3, 8, 0));
        JButton adminLoginBtn = new JButton("Login as Admin");
        JButton clientLoginBtn = new JButton("Login as Client");
        actionRow.add(adminLoginBtn);
        actionRow.add(clientLoginBtn);
        actionRow.add(registerBtn);
        form.add(actionRow);

        // Add form to the window
        content.add(form, BorderLayout.CENTER);
        setContentPane(content);
        getRootPane().setDefaultButton(adminLoginBtn);
        pack();

        // Wire actions
        adminLoginBtn.addActionListener(this::handleAdminLogin);
        clientLoginBtn.addActionListener(this::handleClientLogin);
    }

    /** Admin login: uses legacy admin_users table (SHA-256 hash). */
    private void handleAdminLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars);
        Arrays.fill(passwordChars, '\0');

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.");
            return;
        }

        try (Connection conn = Db.getConnection()) {
            String sql = "SELECT password FROM admin_users WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String storedHash = rs.getString("password");
                        String inputHash = hashPassword(password);
                        if (storedHash.equalsIgnoreCase(inputHash)) {
                            // Open dashboard as ADMIN (you can pass a role string if your dashboard supports it)
                            SwingUtilities.invokeLater(() -> new ParkingDashboard("ADMIN").setVisible(true));
                            dispose();
                            return;
                        }
                    }
                }
            }
            JOptionPane.showMessageDialog(this, "Invalid admin credentials.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    /** Client login: checks unified users table (role='CLIENT', SHA-256). */
    private void handleClientLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        char[] passwordChars = passwordField.getPassword();
        String password = new String(passwordChars);
        Arrays.fill(passwordChars, '\0');

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.");
            return;
        }

        try (Connection conn = Db.getConnection()) {
            String sql = "SELECT password FROM users WHERE username = ? AND role = 'CLIENT'";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String storedHash = rs.getString("password");
                        String inputHash = hashPassword(password);
                        if (storedHash.equalsIgnoreCase(inputHash)) {
                            // Open dashboard as CLIENT (reserve-only: hide Park/Remove in the dashboard)
                            SwingUtilities.invokeLater(() -> new ParkingDashboard("CLIENT").setVisible(true));
                            dispose();
                            return;
                        }
                    }
                }
            }
            JOptionPane.showMessageDialog(this, "Invalid client credentials.");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private String hashPassword(String password) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hash) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
