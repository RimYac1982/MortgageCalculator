package application.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Utility class for fetching property tax and home insurance rates based on ZIP code.
 */
public class PropertyTaxAndInsuranceFetcher {

    private final String homeInsuranceApiUrl = "https://real-api-url.com/home-insurance"; // Replace with a valid Home Insurance API URL

    /**
     * Fetches the property tax based on ZIP code.
     *
     * @param zipCode       the ZIP code for tax calculation
     * @param purchasePrice the purchase price of the property
     * @return the monthly property tax value or 0 if an error occurs
     */
    public double fetchPropertyTax(String zipCode, double purchasePrice) {
        final String apiKey = "3RqIl//dQVODgVjl2hQEyQ==uQObBmjKIlklP506";
        final String apiUrl = "https://api.api-ninjas.com/v1/propertytax";

        try {
            String urlWithParams = apiUrl + "?zip=" + zipCode;

            URL url = new URL(urlWithParams);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-Api-Key", apiKey);
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                JSONArray jsonResponse = new JSONArray(response.toString());
                if (jsonResponse.length() > 0) {
                    JSONObject firstResult = jsonResponse.getJSONObject(0);
                    if (firstResult.has("property_tax_50th_percentile")) {
                        double taxRate = firstResult.getDouble("property_tax_50th_percentile");
                        double annualPropertyTax = purchasePrice * taxRate;
                        return annualPropertyTax / 12.0; 
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0.0;
    }


    /**
     * Fetches the home insurance rate based on ZIP code.
     *
     * @param zipCode the ZIP code for insurance calculation
     * @return the home insurance rate or a default value if an error occurs
     */
    public double fetchHomeInsurance(String zipCode) {
        try {
            // Build the API URL with the ZIP code parameter
            String urlWithParams = homeInsuranceApiUrl + "?zip=" + zipCode;

            // Create a connection to the API
            URL url = new URL(urlWithParams);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setConnectTimeout(5000); // Connection timeout
            conn.setReadTimeout(5000);   // Read timeout

            // Get the response code
            int responseCode = conn.getResponseCode();
            System.out.println("Home Insurance API Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Read the response
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();

                // Parse the JSON response
                JSONObject jsonResponse = new JSONObject(response.toString());
                return jsonResponse.getDouble("home_insurance_rate"); // Replace with actual field name from API response
            } else {
                System.err.println("Error fetching home insurance: Response code " + responseCode);
                return scrapeHomeInsurance(zipCode); // Fallback to scraping if API fails
            }
        } catch (Exception e) {
            e.printStackTrace();
            return scrapeHomeInsurance(zipCode); // Fallback to scraping in case of an exception
        }
    }

    /**
     * Scrapes the home insurance rate from a webpage.
     *
     * @param zipCode the ZIP code for insurance calculation
     * @return the home insurance rate or a default value if scraping fails
     */
    private double scrapeHomeInsurance(String zipCode) {
        try {
            // URL of the webpage with insurance data
            String scrapingUrl = "https://www.policygenius.com/homeowners-insurance/home-insurance-rates-by-zip-code/";
            Document doc = Jsoup.connect(scrapingUrl).get();

            // Find the rate based on ZIP code
            Element table = doc.selectFirst("table"); // Assuming the data is in a table
            if (table != null) {
                for (Element row : table.select("tr")) {
                    Elements cells = row.select("td");
                    if (cells.size() > 1 && cells.get(0).text().equals(zipCode)) {
                        // Assuming the rate is in the second cell
                        double annualRate = Double.parseDouble(cells.get(1).text().replace("$", "").replace(",", ""));
                        System.out.println("Scraped Home Insurance Annual Rate: " + annualRate);
                        return annualRate / 12.0; // Convert annual rate to monthly
                    }
                }
            }

            System.err.println("No insurance rate found for ZIP code: " + zipCode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Return a default value if scraping fails
        return 66.0;
    }
}
