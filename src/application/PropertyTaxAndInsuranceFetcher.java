package application;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Utility class for fetching property tax and home insurance rates based on ZIP code.
 */
public class PropertyTaxAndInsuranceFetcher {

    /**
     * Fetches the property tax based on ZIP code.
     *
     * @param zipCode the ZIP code for tax calculation
     * @return the property tax value
     */
    public double fetchPropertyTax(String zipCode) {
        String apiUrl = "https://real-api-url.com/property-tax?zip=" + zipCode;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
            conn.disconnect();

            JSONObject jsonResponse = new JSONObject(content.toString());
            return jsonResponse.getDouble("property_tax_rate");
        } catch (Exception e) {
            e.printStackTrace();
            return 280;  // Default value in case of an error
        }
    }

    /**
     * Fetches the home insurance rate based on ZIP code.
     *
     * @param zipCode the ZIP code for insurance calculation
     * @return the home insurance rate
     */
    public double fetchHomeInsurance(String zipCode) {
        String apiUrl = "https://real-api-url.com/home-insurance?zip=" + zipCode;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-Type", "application/json");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
            conn.disconnect();

            JSONObject jsonResponse = new JSONObject(content.toString());
            return jsonResponse.getDouble("home_insurance_rate");
        } catch (Exception e) {
            e.printStackTrace();
            return 66;  // Default value in case of an error
        }
    }
}
