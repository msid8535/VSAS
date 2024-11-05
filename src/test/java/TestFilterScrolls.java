import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Tests the functionality of <code>ScrollFilter</code> class
 */
public class TestFilterScrolls {

    @BeforeAll
    public static void setUpBeforeClass() {
        if (!DatabaseHelper.isDatabaseCreated()) {
            DatabaseHelper.createTables();
        }

        Scroll scroll1 = new Scroll("test1", "helloScroll1", "0", new byte[3], "01-01-2020", "testextension", 0, 0);
        Scroll scroll2 = new Scroll("test2", "helloScroll2", "0", new byte[3], "01-01-2020", "testextension", 1000, 0);
        Scroll scroll3 = new Scroll("test3", "scroll3", "0", new byte[3], "01-01-2021", "testextension", 1001, 0);
        Scroll scroll4 = new Scroll("test4", "scroll4", "0", new byte[3], "02-01-2021", "testextension1", 0, 0);

        scroll1.saveToDatabase();
        scroll2.saveToDatabase();
        scroll3.saveToDatabase();
        scroll4.saveToDatabase();

    }

    /**
     * Tests filtering by file type.
     * Checks that filtering for "testextension" file type returns only three scrolls.
     */
    @Test
    public void testFilterByFileType() {
        ScrollFilter filter = new ScrollFilter();
        filter.setFileType("testextension");

        List<Scroll> scrolls = Scroll.filterScrolls(filter);
        assertEquals(3, scrolls.size());
    }

    /**
     * Tests filtering by min downloads.
     * Checks that filtering for 1000 downloads minimum returns only two scrolls.
     */
    @Test
    public void testFilterByMinDownloads() {
        ScrollFilter filter = new ScrollFilter();
        filter.setMinDownloads(1000);

        List<Scroll> scrolls = Scroll.filterScrolls(filter);
        assertEquals(2, scrolls.size());
    }

    /**
     * Tests filtering by name.
     * Checks that filtering for the file name "hello" returns only two scrolls.
     * <b><i>Note: this test may fail if any other files that contain "hello" already exist in the database</i></b>
     */
    @Test
    public void testFilterByName() {
        ScrollFilter filter = new ScrollFilter();
        filter.setName("hello");

        List<Scroll> scrolls = Scroll.filterScrolls(filter);
        assertEquals(2, scrolls.size());
    }

    @AfterAll
    public static void tearDownAfterClass() {
        Scroll.removeFromDatabase("test1");
        Scroll.removeFromDatabase("test2");
        Scroll.removeFromDatabase("test3");
        Scroll.removeFromDatabase("test4");
    }
}
