package application.utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Utility class for fetching property tax and home insurance rates based on ZIP code.
 */
public class PropertyTaxFetcher {

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
}
