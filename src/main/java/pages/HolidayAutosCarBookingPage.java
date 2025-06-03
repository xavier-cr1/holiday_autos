package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.List;
import java.util.Locale;

public class HolidayAutosCarBookingPage {
    private final Page page;

    public HolidayAutosCarBookingPage(Page page) {
        this.page = page;
    }

    /**
     * Returns the total car hire price from the booking page.
     * @return price as double
     */
    public double getTotalPrice() {
        Locator priceLocator = page.locator("div.ct-total-price.ct-font-weight-bold");
        String priceText = priceLocator.innerText().replaceAll("[^\\d.,]", "")
                .replace(",", ".") // support commas as decimal points
                .trim();
        return Double.parseDouble(priceText);
    }

    /**
     * Returns the pick-up date shown in the trip summary panel.
     * @return pickup date string
     */
    public String getPickupDate() {
        // Trip summary panel index 0 is pickUp date located
        return extractDateFromPanel(0);
    }

    /**
     * Returns the return date shown in the trip summary panel.
     * @return return date string
     */
    public String getReturnDate() {
        // Trip summary index 1 is return date located
        return extractDateFromPanel(1);
    }

    private String extractDateFromPanel(int index) {
        List<String> contents = page.locator(".ct-panel-trip-summary--item")
                .nth(index)
                .locator("span")
                .allTextContents();
        return normalizeDate(contents.get(0));
    }

    private String normalizeDate(String text) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("E, d MMM", Locale.ENGLISH);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Parse without year (returns a MonthDay, not a full LocalDate)
        TemporalAccessor parsed = inputFormatter.parse(text.trim());

        int day = parsed.get(ChronoField.DAY_OF_MONTH);
        Month month = Month.of(parsed.get(ChronoField.MONTH_OF_YEAR));
        int year = LocalDate.now().getYear();

        LocalDate fullDate = LocalDate.of(year, month, day);
        return fullDate.format(outputFormatter);
    }
}