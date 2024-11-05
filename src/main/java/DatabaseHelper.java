import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHelper {

    private static final String DATABASE_URL = "jdbc:sqlite:localdb.db"; 

    // Method to connect to SQLite
    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DATABASE_URL);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    // Method to check if the database file exists
    public static boolean isDatabaseCreated() {
        File dbFile = new File("localdb.db");
        return dbFile.exists();
    }

    // Method to create tables if they don't exist
    public static void createTables() {
        String userTableSQL = "CREATE TABLE IF NOT EXISTS users ("
                + "idKey TEXT PRIMARY KEY,"
                + "username TEXT NOT NULL,"
                + "password TEXT NOT NULL,"
                + "email TEXT,"
                + "fullName TEXT,"
                + "phoneNumber TEXT,"
                + "userType TEXT"
                + ");";

        String scrollTableSQL = "CREATE TABLE IF NOT EXISTS scrolls ("
                + "scrollId TEXT PRIMARY KEY,"
                + "name TEXT NOT NULL,"
                + "uploaderId TEXT NOT NULL,"
                + "fileData BLOB NOT NULL,"
                + "uploadDate TEXT,"
                + "fileType TEXT NOT NULL,"
                + "downloadCount INTEGER NOT NULL,"
                + "editCount INTEGER NOT NULL,"
                + "FOREIGN KEY (uploaderId) REFERENCES users(idKey)"
                + ");";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            // Create users table
            stmt.execute(userTableSQL);
            //System.out.println("User table has been created or already exists.");

            // Create scrolls table
            stmt.execute(scrollTableSQL);
            //System.out.println("Scrolls table has been created or already exists.");

            // Add default admin and guest users
            User admin = new User("0", "admin", User.encryptPassword("admin"), "none", "admin", "none", UserType.ADMIN);
            User guestUser = new User("1", "Guest", "guest", "guest", "guest", "guest", UserType.GUEST);
            admin.saveToDatabase();
            guestUser.saveToDatabase();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
