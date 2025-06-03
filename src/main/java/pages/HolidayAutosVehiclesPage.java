package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

public class HolidayAutosVehiclesPage {

    private final Page page;

    public HolidayAutosVehiclesPage(Page page) {
        this.page = page;
    }

    /**
     * Waits until the car results page is fully loaded. The navigation arrows for the results are present.
     */
    public void waitUntilVehiclesAreLoaded() {
        Locator loadingBar = page.locator(".ct-navigation-arrows");
        page.waitForCondition(loadingBar::isVisible, new Page.WaitForConditionOptions().setTimeout(30000));
    }
}