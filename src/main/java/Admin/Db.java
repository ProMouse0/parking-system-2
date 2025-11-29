package Admin;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Simple JDBC connection helper for MySQL.
 * Reads configuration from db.properties (inside resources folder).
 *
 * Required properties:
 *  jdbc.url=jdbc:mysql://localhost:3306/parkingdb?useSSL=false&serverTimezone=Asia/Manila
 *  jdbc.user=parking_user
 *  jdbc.password=StrongPassword123!
 */
public class Db {

    private static String url;
    private static String user;
    private static String password;
    private static volatile boolean initialized = false;

    private static void init() {
        if (initialized) return;

        synchronized (Db.class) {
            if (initialized) return;

            try (InputStream in = Db.class.getClassLoader().getResourceAsStream("db.properties")) {

                if (in == null) {
                    throw new IllegalStateException("ERROR: db.properties not found in /resources folder!");
                }

                Properties p = new Properties();
                p.load(in);

                url = p.getProperty("jdbc.url");
                user = p.getProperty("jdbc.user");
                password = p.getProperty("jdbc.password");

                if (url == null || user == null || password == null) {
                    throw new IllegalStateException("""
                        Missing database configuration!
                        Required:
                          jdbc.url
                          jdbc.user
                          jdbc.password
                        """);
                }

                // Ensure MySQL driver is loaded (safe for all Java versions)
                Class.forName("com.mysql.cj.jdbc.Driver");

                initialized = true;

            } catch (Exception e) {
                throw new RuntimeException("DB initialization failed!", e);
            }
        }
    }

    /**
     * Returns a NEW MySQL connection.
     * Always close it using try-with-resources.
     */
    public static Connection getConnection() throws SQLException {
        init();
        return DriverManager.getConnection(url, user, password);
    }
}
