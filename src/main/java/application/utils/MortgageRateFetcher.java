package application.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Class responsible for fetching the latest mortgage rates from an external API.
 * Utilizes the HttpURLConnection to make a GET request and retrieve data in JSON format.
 */
public class MortgageRateFetcher {

    private final String apiKey = "d9a9426d2bmsh32adb70019dca87p16bbe4jsncd8e3ee56140";
    private final String apiUrl = "https://interest-rate-by-api-ninjas.p.rapidapi.com/v1/interestrate";

    /**
     * Fetches mortgage rates from the external API.
     * Connects to the API endpoint, retrieves the response, and returns it as a JSON string.
     *
     * @return a JSON string containing the mortgage rates data, or an error message if the fetch fails
     */
    public String fetchMortgageRates() {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("x-rapidapi-host", "interest-rate-by-api-ninjas.p.rapidapi.com");
            conn.setRequestProperty("x-rapidapi-key", apiKey);
            conn.setConnectTimeout(5000); 
            conn.setReadTimeout(5000);    

            int responseCode = conn.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                return response.toString();
            } else {
                System.err.println("Failed to fetch rates. Response code: " + responseCode);
                return "Error: Failed to fetch mortgage rates. Response Code: " + responseCode;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Exception occurred while fetching mortgage rates - " + e.getMessage();
        }
    }
}
