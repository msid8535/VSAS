import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Tests the functionality of <code>User</code> class
 */
public class UserTest {

    private static int numUsersBeforeTests = 0;

    @BeforeAll
    public static void setUpBeforeClass() {
        if (!DatabaseHelper.isDatabaseCreated()) {
            DatabaseHelper.createTables();
        }

        numUsersBeforeTests = User.getAllUsers().size();

        User user1 = new User("test1", "testUser1", User.encryptPassword("testpassword1"), "testemail1@gmail.com", "Test User 1", "0123456789", UserType.REGISTERED_USER);
        User user2 = new User("test2", "testUser2", User.encryptPassword("testpassword2"), "testemail2@gmail.com", "Test User 2", "0123456789", UserType.REGISTERED_USER);

        user1.saveToDatabase();
        user2.saveToDatabase();
    }

    /**
     * Tests getting all users.
     * Checks that the new users created in the test setup have been written to the database.
     */
    @Test
    @Order(1)
    public void testGetAllUsers() {
        assertEquals(numUsersBeforeTests + 2, User.getAllUsers().size());
    }

    /**
     * Tests authenticating a user.
     * Checks that authenticating a user with the correct username and password returns the User object.
     */
    @Test
    public void testAuthenticateUser() {
        assertNotNull(User.authenticateUser("testUser1", "testpassword1"));
    }

    /**
     * Tests authenticating user with an incorrect password.
     * Checks that authenticating a user with an incorrect password returns null.
     */
    @Test
    public void testWrongPassword() {
        assertNull(User.authenticateUser("testUser2", "wrongpassword"));
    }

    /**
     * Tests authenticating a user that does not exist.
     * Checks that authenticating a non-existent user returns null.
     */
    @Test
    public void testUserDoesNotExist() {
        assertNull(User.authenticateUser("testUser3", "testpassword3"));
    }

    /**
     * Tests getting a user by username.
     * Checks that new user can be retrieved from the database by name.
     */
    @Test
    public void testGetUserByName() {
        User user = User.getUserByName("testUser1");
        assert user.getIdKey().equals("test1");
    }

    /**
     * Tests getting a user by ID.
     * Checks that new user can be retrieved from the database by user ID.
     */
    @Test
    public void testGetUserById() {
        User user = User.getUserById("test1");
        assert user.getUsername().equals("testUser1");
    }

    /**
     * Tests <code>isUserAdmin()</code>.
     * Checks that new user created in the test setup does not have admin privileges, in comparison to the admin user.
     */
    @Test
    public void testIsAdmin() {
        User user = User.getUserById("test1");
        User admin = User.getUserById("0");
        assertNotEquals(user.isUserAdmin(), admin.isUserAdmin());
    }

    /**
     * Tests the second constructor of class <code>User</code>, which doesn't have a parameter for the user ID.
     * Checks that a random String of length 13 is generated for the key.
     */
    @Test
    public void testCreateUserWithoutId() {
        User user = new User("testUser3", "testpassword3", "testemail3@gmail.com", "Test User 3", "0123456789", UserType.REGISTERED_USER);
        int idKeyLength = user.getIdKey().length();
        User.removeUserFromDatabase("testUser3");
        assertEquals(13, idKeyLength);
    }

    /**
     * Tests updating password.
     * Checks that after updating the password of the user, the user can be authenticated with their username and new password.
     */
    @Test
    public void testUpdatePassword() {
        User user = User.getUserByName("testUser1");
        user.setPassword("newPassword");
        assertNotNull(User.authenticateUser("testUser1", "newPassword"));
    }

    /**
     * Tests removing users from the database.
     * Checks that the size of the database remains the same after a user is added then removed.
     */
    @Test
    public void testRemoveUsers() {
        User user = new User("test4", "testUser4", "testpassword4", "testemail3@gmail.com", "Test User 4", "0123456789", UserType.REGISTERED_USER);
        user.saveToDatabase();
        User.removeUserFromDatabase("testUser4");
        assertEquals(User.getAllUsers().size(), numUsersBeforeTests + 2);
    }

    /**
     * Tests removing non-existing users from the database.
     * Checks that calling <code>User.removeUserFromDatabase()</code> on a non-existing user returns <code>False</code>.
     */
    @Test
    public void testRemoveNonExistingUser() {
        assertFalse(User.removeUserFromDatabase("NonExistingUser"));
    }

    @AfterAll
    public static void afterTests() {
        // Remove users
        User.removeUserFromDatabase("testUser1");
        User.removeUserFromDatabase("testUser2");
    }
}
