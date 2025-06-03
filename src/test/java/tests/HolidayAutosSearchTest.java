package tests;

import com.microsoft.playwright.*;
import helpers.TestDataCsvUtil;
import org.junit.jupiter.api.*;
import pages.HolidayAutosCarBookingPage;
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
    void shouldOrderCarsByPriceAndFirstCarIsCheapest() {
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
        // Read existing latest test data written in Test 1
        Map<String, String> data = TestDataCsvUtil.readTestData();
        String pickupDateStr = data.get("pickupDate");
        String returnDateStr = data.get("returnDate");

        // Store test data: Write all latest Test 1 execution data dates + updated the cheapest price from Test 2
        TestDataCsvUtil.writeTestData(pickupDateStr, returnDateStr, firstPrice);
    }

    @Test
    @DisplayName("Test 3 - Select the cheapest car and validate details")
    void testSelectCheapestCarAndVerifyDetails() {
        // load test data from CSV & Arrange
        Map<String, String> testData = TestDataCsvUtil.readTestData();

        String pickupDateFromTest1 = testData.get("pickupDate");
        String returnDateFromTest1 = testData.get("returnDate");
        String priceFromTest2 = testData.get("cheapestPrice");

        assertAll(
                () -> assertNotNull(pickupDateFromTest1, "Pickup date is missing in test data"),
                () -> assertNotNull(returnDateFromTest1, "Return date is missing in test data"),
                () -> assertNotNull(priceFromTest2, "Cheapest price is missing in test data")
        );

        HolidayAutosCarsPage holidayAutosCarsPage = searchCars("Barcelona", LocalDate.now().plusDays(1), LocalDate.now().plusDays(7));
        holidayAutosCarsPage.waitUntilCarsAreLoaded();
        holidayAutosCarsPage.sortByPriceLowToHigh();

        // Act
        HolidayAutosCarBookingPage holidayAutosCarBookingPage = holidayAutosCarsPage.selectFirstCar();

        // Assert
        double expectedPrice = Double.parseDouble(priceFromTest2);
        double actualPrice = holidayAutosCarBookingPage.getTotalPrice();

        assertAll("Booking page verification",
                () -> assertEquals(expectedPrice, actualPrice, 5.00, "Booking Price mismatch (Allowed 5EUR difference because the uncontrolled fees)"),
                () -> assertTrue(holidayAutosCarBookingPage.getPickupDate().contains(pickupDateFromTest1), "Booking Pickup date mismatch"),
                () -> assertTrue(holidayAutosCarBookingPage.getReturnDate().contains(returnDateFromTest1), "Booking Return date mismatch")
        );
    }

    private HolidayAutosCarsPage searchCars(String pickUpLocation, LocalDate pickUpDate, LocalDate returnDate) {
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