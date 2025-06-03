package pages;

import com.microsoft.playwright.Page;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class HolidayAutosHomePage {

    private final Page page;

    public HolidayAutosHomePage(Page page) {
        this.page = page;
    }

    private final String URL = "https://www.holidayautos.com/";

    /**
     * Navigates to the Holiday Autos car hire search page.
     */
    public void navigate() {
        page.navigate(URL);
        // Wait for the page to load the car search input
        page.waitForURL("**/searchcars");
    }

    /**
     * Enters a pickup location into the location input field, presses Enter to trigger the suggestion and selects the first item.
     *
     * @param location the name of the pickup location, e.g., "Barcelona"
     */
    public void enterPickupLocation(String location) {
        String pickUpLocationSelector = "#pickupLocation";
        page.click(pickUpLocationSelector);
        page.fill(pickUpLocationSelector, location);
        page.keyboard().press("Enter");
        page.click("#item-0-0");
    }

    /**
     * Selects the pickup and return dates in the calendar widget.
     *
     * @param pickupDate Pickup date
     * @param returnDate Return date
     */
    public void selectPickupAndReturnDates(LocalDate pickupDate, LocalDate returnDate) {
        page.click("#pickupDate");
        selectDate(pickupDate);

        page.click("#returnDate");
        selectDate(returnDate);
    }

    /**
     * Submits the search form by clicking the search button.
     */
    public HolidayAutosVehiclesPage clickSearch() {
        page.click("#searchCarsFormBtn-searchcars");
        return new HolidayAutosVehiclesPage(page);
    }

    /**
     * Selects a specific calendar day in its DOM element using a predictable ID format.
     *
     * @param localDate LocalDate to format to select from the calendar
     */
    private void selectDate(LocalDate localDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String isoDate = localDate.format(formatter);
        String dateId = "day-" + isoDate.replace("-", "");
        page.click("#" + dateId);
    }
}