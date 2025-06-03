package pages;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import com.microsoft.playwright.options.WaitUntilState;

import java.util.ArrayList;
import java.util.List;

public class HolidayAutosCarsPage {

    private final Page page;

    public HolidayAutosCarsPage(Page page) {
        this.page = page;
    }

    /**
     * Waits until the car results page is fully loaded: The navigation arrows for the results are present.
     */
    public void waitUntilCarsAreLoaded() {
        Locator loadingBar = page.locator(".ct-navigation-arrows");
        page.waitForCondition(loadingBar::isVisible, new Page.WaitForConditionOptions().setTimeout(30000));
    }

    /**
     * Clicks the 'Price (low to high)' sort button and waits for price elements to reload.
     */
    public void sortByPriceLowToHigh() {
        Locator priceSortBtn = page.locator(".ctc-button-group--collapsed >> button")
                .filter(new Locator.FilterOptions().setHasText("price (low to high)"));
        // Another button is found at the first position which is not doing the sort
        priceSortBtn.nth(1).click();

        // Wait for the prices to reappear (simple re-wait strategy)
        Locator priceLowToHighButton = page.locator(".ct-total-price");
        page.waitForCondition(priceLowToHighButton.first()::isVisible, new Page.WaitForConditionOptions().setTimeout(5000));
    }

    /**
     * Extracts all visible car prices from the results list using the `.ct-total-price` element.
     *
     * @return List of prices as doubles
     */
    public List<Double> getAllTotalCarPrices() {
        List<String> rawPrices = page.locator(".ct-total-price").allTextContents();
        List<Double> prices = new ArrayList<>();

        for (String price : rawPrices) {
            try {
                // Strip currency symbol and convert commas
                String cleaned = price.replaceAll("[^\\d.,]", "").replace(",", ".");
                prices.add(Double.parseDouble(cleaned));
            } catch (NumberFormatException ignored) {
                // Skip any malformed entries and log (in future iterations) ...
            }
        }

        return prices;
    }

    /**
     * Clicks the "Select" button on the first car and returns the booking page that opens in a new tab.
     *
     * @return a new HolidayAutosCarBookingPage representing the booking tab
     */
    public HolidayAutosCarBookingPage selectFirstCar() {
        // Wait for a new page (tab) to open ho
        Page newPage = page.waitForPopup(() -> {
            page.locator("button.ct-select-btn.ct-xlarge").first().click();
        });

        newPage.waitForLoadState(LoadState.DOMCONTENTLOADED);
        return new HolidayAutosCarBookingPage(newPage);
    }
}