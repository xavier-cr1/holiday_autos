package tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import utils.PlaywrightFactory;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class HolidayAutosSearchTest {

    private BrowserContext context;
    private Page page;

    @BeforeEach
    void setUp() {
        context = PlaywrightFactory.initBrowser();
        page = context.newPage();
    }

    @Test
    void openHolidayAutosHomePage() {
        // Step 1: Navigate to the homepage
        page.navigate("https://www.holidayautos.com/home");

        // Step 2: Get and print the page title
        String title = page.title();
        System.out.println("Page title: " + title);

        // Step 3: Basic validation
        assertTrue(title.toLowerCase().contains("holiday"), "Title should contain 'holiday'");
    }

    @AfterEach
    void tearDown() {
        context.close();
        PlaywrightFactory.close();
    }
}