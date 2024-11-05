// User.java
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;

public class User {

    private String idKey;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private String phoneNumber;
    private UserType userType;

    // Constructor with idKey
    public User(String idKey, String username, String password, String email, String fullName, String phoneNumber,
            UserType userType) {
        this.idKey = idKey;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.userType = userType;
    }

    // Constructor with no idKey
    public User(String username, String password, String email, String fullName, String phoneNumber,
                UserType userType) {

        // Generates idKey until unique key is created
        String idKey;
        while (true) {
            idKey = generateUserKey();
            if (getUserById(idKey) == null) {
                break;
            }
        }

        this.idKey = idKey;
        this.username = username;
        this.password = encryptPassword(password);
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.userType = userType;
    }

    public boolean isUserAdmin() {
        return getUserType() == UserType.ADMIN;
    }

    // Method to save a user to the database
    public void saveToDatabase() {
        String sql = "INSERT INTO users(idKey, username, password, email, fullName, phoneNumber, userType) VALUES(?,?,?,?,?,?,?)";

        try (Connection conn = DatabaseHelper.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, idKey);
            pstmt.setString(2, username);
            pstmt.setString(3, password);
            pstmt.setString(4, email);
            pstmt.setString(5, fullName);
            pstmt.setString(6, phoneNumber);
            pstmt.setString(7, userType.toString());

            pstmt.executeUpdate();
            //System.out.println("User added to the database.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Method to update user profile
    public void updateUserInDatabase() {
        String sql = "UPDATE users SET username = ?, password = ?, email = ?, fullName = ?, phoneNumber = ? WHERE idKey = ?";

        try (Connection conn = DatabaseHelper.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setString(3, email);
            pstmt.setString(4, fullName);
            pstmt.setString(5, phoneNumber);
            pstmt.setString(6, idKey);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static User getGuestUser() {
        return new User("1", "Guest", "guest", "guest", "guest", "guest", UserType.GUEST);
    }

    // Method to retrieve a user from the database by userId
    public static User getUserById(String userId) {
        String sql = "SELECT * FROM users WHERE idKey = ?";
        User user = null;

        try (Connection conn = DatabaseHelper.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                user = new User(
                        rs.getString("idKey"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("fullName"),
                        rs.getString("phoneNumber"),
                        UserType.valueOf(rs.getString("userType")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return user;
    }

    // Method to retrieve a user from the database by username
    public static User getUserByName(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        User user = null;

        try (Connection conn = DatabaseHelper.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                user = new User(
                        rs.getString("idKey"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("fullName"),
                        rs.getString("phoneNumber"),
                        UserType.valueOf(rs.getString("userType")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return user;
    }

    public static List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();

        try (Connection conn = DatabaseHelper.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                User user = new User(
                        rs.getString("idKey"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("fullName"),
                        rs.getString("phoneNumber"),
                        UserType.valueOf(rs.getString("userType")));
                users.add(user);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return users;
    }

    public static User authenticateUser(String username, String password) {
        User user = getUserByName(username);

        // If user doesn't exist, return null
        if (user == null) {
            return null;
        }

        if (!validatePassword(password, user.getPassword())) {
            return null;
        }

        return user;
    }

    public static String encryptPassword(String password) {
        int iterations = 10000;
        int keyLength = 512;
        byte[] salt = getSalt();

        char[] chars = password.toCharArray();
        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, keyLength);
        SecretKeyFactory skf = null;
        try {
            skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            byte[] hash = skf.generateSecret(spec).getEncoded();

            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hash);

            return iterations + ":" + saltBase64 + ":" + hashBase64;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] getSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        return salt;
    }

    public static boolean validatePassword(String originalPassword, String storedPassword) {
        String[] parts = storedPassword.split(":");
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = Base64.getDecoder().decode(parts[1]);
        byte[] hash = Base64.getDecoder().decode(parts[2]);

        PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);
        SecretKeyFactory skf = null;
        try {
            skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            byte[] testHash = skf.generateSecret(spec).getEncoded();

            int diff = hash.length ^ testHash.length;
            for(int i = 0; i < hash.length && i < testHash.length; i++)
                diff |= hash[i] ^ testHash[i];
            return diff == 0;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUsername() {
        return username;
    }

    public String getIdKey() {
        return idKey;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = encryptPassword(password);
        updateUserInDatabase();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        updateUserInDatabase();
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
        updateUserInDatabase();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        updateUserInDatabase();
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUsername(String username) {
        this.username = username;
        updateUserInDatabase();
    }

    // Method to remove a user from the database based on their username
    public static boolean removeUserFromDatabase(String username) {
        String sql = "DELETE FROM users WHERE username = ?";

        try (Connection conn = DatabaseHelper.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * Generates a random user key for storage in the database. User keys are 13 characters in length, and start with 'user-'
     * @return the random user key as a String.
     */
    private static String generateUserKey() {
        String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder key = new StringBuilder();
        Random rnd = new Random();

        key.append("user-");

        while (key.length() < 13) { // length of the random string.
            int index = (int) (rnd.nextFloat() * CHARS.length());
            key.append(CHARS.charAt(index));
        }
        return key.toString();
    }
}
