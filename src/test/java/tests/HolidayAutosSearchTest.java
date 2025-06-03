package tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import pages.HolidayAutosHomePage;
import pages.HolidayAutosVehiclesPage;
import utils.PlaywrightFactory;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        HolidayAutosVehiclesPage holidayAutosVehiclesPage = searchVehicles();

        // Assert
        // Verify user is redirected to the vehicles results page
        assertDoesNotThrow(holidayAutosVehiclesPage::waitUntilVehiclesAreLoaded, "Expected vehicle page results to be loaded, but it wasn't.");
    }

    private HolidayAutosVehiclesPage searchVehicles() {
        // Define car search page input values
        String pickUpLocation = "Barcelona";
        LocalDate pickUpDate = LocalDate.now().plusDays(1);
        LocalDate returnDate = LocalDate.now().plusDays(7);

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