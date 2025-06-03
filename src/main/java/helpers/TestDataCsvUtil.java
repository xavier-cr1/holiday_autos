package helpers;

import java.io.*;
import java.nio.file.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Utility class to persist and retrieve test data using a CSV file.
 * Used to store pickup date, return date, and cheapest price between tests.
 */
public class TestDataCsvUtil {
    private static final String FILE_PATH = "src/test/resources/test-data.csv";
    private static final String[] HEADERS = {"pickupDate", "returnDate", "cheapestPrice"};

    /**
     * Writes the given test data to the CSV file.
     * Empty fields are written as blank strings.
     *
     * @param pickupDate    the pickup date or null
     * @param returnDate    the return date or null
     * @param cheapestPrice the cheapest price or null
     */
    public static void writeTestData(String pickupDate, String returnDate, Double cheapestPrice) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(FILE_PATH))) {
            writer.write(String.join(",", HEADERS));
            writer.newLine();
            writer.write(String.format("%s,%s,%s",
                    pickupDate != null ? pickupDate : "",
                    returnDate != null ? returnDate : "",
                    // Local US to do not break the CSV
                    cheapestPrice != null ? NumberFormat.getInstance(Locale.US).format(cheapestPrice) : ""
            ));
        } catch (IOException e) {
            throw new RuntimeException("❌ Failed to write test data to CSV", e);
        }
    }

    /**
     * Method overload
     *
     * @param pickupDate    LocalDate pickup date or null
     * @param returnDate    LocalDate return date or null
     * @param cheapestPrice LocalDate cheapest price or null
     */
    public static void writeTestData(LocalDate pickupDate, LocalDate returnDate, Double cheapestPrice) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String pickupStr = pickupDate != null ? pickupDate.format(formatter) : "";
        String returnStr = returnDate != null ? returnDate.format(formatter) : "";
        writeTestData(pickupStr, returnStr, cheapestPrice);
    }


    /**
     * Reads the test data from the CSV file.
     * Returns a map with keys: pickupDate, returnDate, cheapestPrice.
     *
     * @return Map containing the CSV data as string values
     */
    public static Map<String, String> readTestData() {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(FILE_PATH))) {
            String[] headers = reader.readLine().split(",");
            String[] values = reader.readLine().split(",");

            Map<String, String> data = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                data.put(headers[i], i < values.length ? values[i] : "");
            }
            return data;
        } catch (IOException | NullPointerException e) {
            throw new RuntimeException("Failed to read test data from CSV. Did you run Test 1 & 2?", e);
        }
    }

    /**
     * Validates that all expected fields are present and non-empty in the CSV file.
     * Use this at the start of Test 3 to ensure tests 1 and 2 ran beforehand.
     */
    public static void validateTestDataExists() {
        Map<String, String> data = readTestData();
        for (String key : HEADERS) {
            if (!data.containsKey(key) || data.get(key).trim().isEmpty()) {
                throw new IllegalStateException("Missing test data field: " + key);
            }
        }
    }
}
