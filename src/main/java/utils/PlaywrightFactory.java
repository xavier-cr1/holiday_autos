package utils;

import com.microsoft.playwright.*;

public class PlaywrightFactory {

    private static Playwright playwright;
    private static Browser browser;

    /**
     * Initializes the Playwright engine and launches the browser.
     *
     * @return a new isolated BrowserContext instance
     */
    public static BrowserContext initBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false)
        );

        return browser.newContext();
    }

    /**
     * Closes Playwright and the browser.
     */
    public static void close() {
        if (playwright != null) {
            playwright.close();
        }
    }
}