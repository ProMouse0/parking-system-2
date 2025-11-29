
package Admin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthStore {

    /** Returns true if username is free and registration succeeded. */
    public static boolean registerUser(String username, String fullName, String passwordPlain, String role) throws Exception {
        if (username == null || username.isBlank()) throw new IllegalArgumentException("Username is required.");
        if (fullName == null || fullName.isBlank()) throw new IllegalArgumentException("Full name is required.");
        if (passwordPlain == null || passwordPlain.isBlank()) throw new IllegalArgumentException("Password is required.");
        if (!"ADMIN".equalsIgnoreCase(role) && !"CLIENT".equalsIgnoreCase(role)) {
            throw new IllegalArgumentException("Role must be ADMIN or CLIENT.");
        }

        // Check if username exists
        try (Connection conn = Db.getConnection()) {
            String checkSql = "SELECT 1 FROM users WHERE username=?";
            try (PreparedStatement check = conn.prepareStatement(checkSql)) {
                check.setString(1, username);
                try (ResultSet rs = check.executeQuery()) {
                    if (rs.next()) {
                        return false; // username taken
                    }
                }
            }

            // Insert user with SHA-256 hex password
            String insertSql = "INSERT INTO users (username, password, full_name, role) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ins = conn.prepareStatement(insertSql)) {
                ins.setString(1, username.trim());
                ins.setString(2, sha256Hex(passwordPlain));  // reuse your hashing
                ins.setString(3, fullName.trim());
                ins.setString(4, role.toUpperCase());
                int rows = ins.executeUpdate();
                return rows == 1;
            }
        }
    }

    private static String sha256Hex(String s) throws Exception {
        var md = java.security.MessageDigest.getInstance("SHA-256");
        byte[] h = md.digest(s.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        var sb = new StringBuilder();
        for (byte b : h) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}
