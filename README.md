# Holiday Autos QA Automation Project

This repository contains a minimal UI automation framework written in Java using JUnit 5 and Playwright for testing the Holiday Autos car rental platform.

## How to Run the Tests

### 1. Prerequisites

- Java 17 or later
- Maven 3.8 or later
- IDE IntelliJ IDEA Community Edition

### 2. Run All Tests Using Terminal

Open a terminal (e.g. GitBash) from the project root, run:

```
mvn clean test
```

Example output:
```
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Running com.holidayautos.tests.HolidayAutosSearchTest

Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 32.19 s -- in tests.HolidayAutosSearchTest

Results:

Tests run: 3, Failures: 0, Errors: 0, Skipped: 0

-------------------------------------------------------
BUILD SUCCESS
-------------------------------------------------------
```

JUnit reports are automatically saved in:
```
target/surefire-reports/
├── TEST-HolidayAutosSearchTest.xml
├── HolidayAutosSearchTest.txt
```
Test report sample in:
```
test/resources/test_report_samples
├── surefire_reports.zip
├── ...
```

### 3. Run Tests Individually Using the IDE
- Open the HolidayAutosSearchTest.java file
- Click the green play button next to each test method
- You can run them one by one:

    - Test 1: Run a search with location and date range
    - Test 2: Identify the cheapest car
    - Test 3: Select the car and verify booking matches expectations

### 4. Assumptions
- Default pickup date is set to today + 1 day, and return date to today + 7 days
- Care Search result page is considered loaded when the navigation arrows are visible: Search Results > Options > Details > Confirmation
- Cheapest price may include dynamic fees added at the booking step; a reasonable delta is tolerated
- Cars sorting by "Price (low to high)" is assumed to correctly order cars by cost
- Clicking "Book" opens the car booking in a new tab, not a popup
- Pickup and return date verification are gathered from the Trip Summary panel displayed on the booking page. Price with fee from the Car Details placeholder.

### 5. Tools Used
- Java 17
- Maven
- JUnit 5
- Playwright for Java
- Maven Surefire Plugin (for test execution and reporting)

### 6. Future improvements
- **Externalize Test Data & Restructure Test Execution for Isolation**  
  Currently, test data like pickup and return dates are hardcoded in Test 1 and Test 2. This should be preloaded from the CSV to improve consistency and reduce coupling between tests and test data setup.
  In addition, the test flow depends on method execution order using `@TestMethodOrder`, where:

  Test 1 performs the vehicle search
  Test 2 identifies and stores the cheapest price
  Test 3 verifies booking details based on previous data

  While this works, it introduces tight coupling between tests. In a more maintainable setup:

  - **Test 1 & 2** should be moved into a dedicated test class (e.g., `SearchCarTestDataPreparationTest`) responsible for preparing and writing test data.
  - **Test 3** should reside in its own class (e.g., `CarBookingVerificationTest`) and depend only on the data present in the CSV, not previous method execution.

  This makes test execution easier and more modular, especially in CI pipelines:

  ```
  # Prepare test data
  mvn test -Dtest=SearchCarTestDataPreparationTest
  
  # Then run only the booking validation
  mvn test -Dtest=CarBookingVerificationTest
  ```

- **Test Class Inheritance**  
  The `@BeforeEach` / `@AfterEach` setup logic should be abstracted into a parent test class, allowing shared browser and context initialization across multiple test classes.

- **Private Method Refinement**  
  Some private methods could be further simplified or renamed to enhance readability and maintainability.

- **Gherkin Avoidance by Design**  
  Gherkin syntax and BDD tools (like Cucumber) were intentionally not used, as they add unnecessary abstraction when there is no business-facing need for natural language test cases.

- **Environment Configuration Files**  
  Use `.env` or external configuration files to define base URLs, execution modes, and other environment-specific parameters.

- **Centralized Playwright Config**  
  Move browser configuration (headless mode, viewport size, browser type) to a dedicated Playwright config file under `src/test/resources` to avoid hardcoded settings.

- **CI Integration**  
  Add continuous integration via GitHub Actions or GitLab CI to automatically execute tests on pull requests, with logging and test artifact storage.

- **Logging Framework**  
  Integrate a structured logging solution like Log4j2 to enable cleaner logging across framework actions and page events.

- **Visual Test Artifacts**  
  Enable screenshot and video recording support in Playwright to assist in debugging failing test runs. Alternatively, integrate a reporting framework like Allure for richer results visualization.

- **Code Formatting Enforcement**  
  Use tools like Spotless with Google Java Format to automatically enforce consistent code style, integrated into PR validation for team-wide alignment.