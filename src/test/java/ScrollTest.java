import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * Tests the functionality of <code>Scroll</code> class
 */
public class ScrollTest {

    private static int numScrollsBeforeTest = 0;

    @BeforeAll
    public static void setUpBeforeClass() {
        if (!DatabaseHelper.isDatabaseCreated()) {
            DatabaseHelper.createTables();
        }

        numScrollsBeforeTest = Scroll.getAllScrollsFromDatabase().size();

        Scroll scroll1 = new Scroll("testId1", "testScroll1", "0", new byte[3], "01-01-2024", "txt", 0, 0);
        Scroll scroll2 = new Scroll("testId2", "testScroll2", "0", new byte[3], "01-01-2024", "txt", 0, 0);

        scroll1.saveToDatabase();
        scroll2.saveToDatabase();

    }

    /**
     * Tests getting all scrolls from the database.
     * Checks that the scrolls added in the test set up have been written to the database.
     */
    @Test
    @Order(1)
    public void testGetAllScrolls() {
        assertTrue(numScrollsBeforeTest < Scroll.getAllScrollsFromDatabase().size());
    }

    /**
     * Tests getting scroll by ID.
     * Checks that new scroll can be retrieved from the database by ID.
     */
    @Test
    public void testGetScrollById() {
        Scroll scroll = Scroll.getScrollById("testId1");
        assertEquals("testScroll1", scroll.getName());
    }

    /**
     * Tests getting scroll by name.
     * Checks that new scroll can be retrieved from the database by name.
     */
    @Test
    public void testGetScrollByName() {
        Scroll scroll = Scroll.getScrollByName("testScroll1");
        assertEquals("testId1", scroll.getScrollId());
    }

    /**
     * Tests incrementing download count.
     * Checks that new scroll has download count updated in the database when <code>incrementDownloadCount()</code> is called.
     */
    @Test
    public void testIncrementDownloadCount() {
        Scroll scroll = Scroll.getScrollById("testId1");
        scroll.incrementDownloadCount();
        assertEquals(1, Scroll.getScrollById("testId1").getDownloadCount());
    }

    /**
     * Tests incrementing edit count.
     * Checks that new scroll has edit count updated in the database when <code>incrementEditCount()</code> is called.
     */
    @Test
    public void testIncrementEditCount() {
        Scroll scroll = Scroll.getScrollById("testId2");
        scroll.incrementEditCount();
        assertEquals(1, Scroll.getScrollById("testId2").getEditCount());
    }

    /**
     * Tests adding a new scroll
     * Checks that database contains one additional scroll after a new scroll is added.
     */
    @Test
    public void testAddScroll() {
        int numScrollsBeforeTest = Scroll.getAllScrollsFromDatabase().size();
        Scroll scroll3 = new Scroll("testId3", "testScroll3", "0", new byte[3], "01-01-2024", "txt", 0, 0);
        scroll3.saveToDatabase();
        assertEquals(numScrollsBeforeTest + 1, Scroll.getAllScrollsFromDatabase().size());
    }

    /**
     * Tests the second constructor of class <code>Scroll</code>, which doesn't have a parameter for the scroll ID.
     * Checks that a random String of length 16 is generated for the key.
     */
    @Test
    public void testAddScrollWithoutId() {
        Scroll scroll4 = new Scroll("testScroll4", "0", new byte[3], "01-01-2024", "txt", 0, 0);
        String scrollId = scroll4.getScrollId();
        Scroll.removeFromDatabase(scrollId);
        assertEquals(16, scrollId.length());
    }

    /**
     * Tests the removal of the scroll from a database.
     * Checks that the size of the database remains the same after a scroll is added then removed.
     */
    @Test
    public void testRemoveScroll() {
        int numScrollsBeforeTest = Scroll.getAllScrollsFromDatabase().size();
        Scroll scroll4 = new Scroll("testScroll4", "testScroll4", "0", new byte[3], "01-01-2024", "txt", 0, 0);
        scroll4.saveToDatabase();
        Scroll.removeFromDatabase("testScroll4");
        assertEquals(numScrollsBeforeTest, Scroll.getAllScrollsFromDatabase().size());
    }

    @AfterAll
    public static void tearDownAfterClass() {
        Scroll.removeFromDatabase("testId1");
        Scroll.removeFromDatabase("testId2");
        Scroll.removeFromDatabase("testId3");
    }
}
