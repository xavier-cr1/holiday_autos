package tests;

import com.microsoft.playwright.*;
import helpers.TestDataCsvUtil;
import org.junit.jupiter.api.*;
import pages.HolidayAutosHomePage;
import pages.HolidayAutosCarsPage;
import utils.PlaywrightFactory;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HolidayAutos – Car Search Flow")
public class HolidayAutosSearchTest {

    private BrowserContext context;
    private Page page;
    private HolidayAutosHomePage homePage;

    @BeforeEach
    void launchBrowserAndOpenHomePage() {
        context = PlaywrightFactory.initBrowser();
        page = context.newPage();
        homePage = new HolidayAutosHomePage(page);
        homePage.navigate();
    }

    @Test
    @DisplayName("Test 1: Search for cars using pickup location and date range")
    void shouldSearchForCarsWithPickupLocationAndDateRange() {
        // Arrange & Act
        String pickUpLocation = "Barcelona";
        LocalDate pickUpDate = LocalDate.now().plusDays(1);
        LocalDate returnDate = LocalDate.now().plusDays(7);
        HolidayAutosCarsPage holidayAutosCarsPage = searchCars(pickUpLocation, pickUpDate, returnDate);

        // Assert
        // Verify user is redirected to the cars results page
        assertDoesNotThrow(holidayAutosCarsPage::waitUntilCarsAreLoaded, "Expected vehicle page results to be loaded, but it wasn't.");
        // Store test data: pick up and return dates from Test 1
        TestDataCsvUtil.writeTestData(pickUpDate, returnDate, null);
    }

    @Test
    @DisplayName("Test 2: Verify first car is the cheapest after sorting by price")
    void testFirstCarIsCheapest() {
        // Arrange & Act
        HolidayAutosCarsPage holidayAutosCarsPage = searchCars("Barcelona", LocalDate.now().plusDays(1), LocalDate.now().plusDays(7));
        holidayAutosCarsPage.waitUntilCarsAreLoaded();
        holidayAutosCarsPage.sortByPriceLowToHigh();
        List<Double> prices = holidayAutosCarsPage.getAllTotalCarPrices();

        // Assert
        // Verify the first price after setting price low to high is the cheapest
        double firstPrice = prices.get(0);
        for (int i = 1; i < prices.size(); i++) {
            double other = prices.get(i);
            assertTrue(
                    firstPrice <= other,
                    String.format("First price €%.2f (the cheapest) is higher than another €%.2f at index %d", firstPrice, other, i)
            );
        }
        // Read existing data written in Test 1
        Map<String, String> data = TestDataCsvUtil.readTestData();
        String pickupDateStr = data.get("pickupDate");
        String returnDateStr = data.get("returnDate");

        // Store test data: Write all latest Test 1 execution data dates + updated the cheapest price from Test 2 to CSV
        TestDataCsvUtil.writeTestData(pickupDateStr, returnDateStr, firstPrice);
    }

    private HolidayAutosCarsPage searchCars(String pickUpLocation, LocalDate pickUpDate, LocalDate returnDate) {

        // Perform actions: fill search and submit
        homePage.enterPickupLocation(pickUpLocation);
        homePage.selectPickupAndReturnDates(pickUpDate, returnDate);
        return homePage.clickSearch();
    }

    @AfterEach
    void closeBrowser() {
        context.close();
        PlaywrightFactory.close();
    }
}