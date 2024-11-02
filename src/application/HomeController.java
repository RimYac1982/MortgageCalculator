package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.text.NumberFormat;

import org.json.JSONArray;
import org.json.JSONObject;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Controller class for managing the home view of the mortgage calculator application.
 * Provides functionalities for calculating mortgage payments, fetching mortgage rates, and displaying results.
 */
public class HomeController {

    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    @FXML
    private Slider LoanDurationSelected;

    @FXML
    private TextField monthlyIncomeField;

    @FXML
    private TextField monthlyExpensesField;

    @FXML
    private TextField InterestRateField;

    @FXML
    private TextField DownPaymentField;

    @FXML
    private TextField PurchasePriceField;

    @FXML
    private TextField InsuranceRateField;

    @FXML
    private TextField TotalPayment;

    @FXML
    private PieChart paymentBreakdownChart;

    @FXML
    private Button Exit;

    @FXML
    private Button Clear;

    @FXML
    private Button Calculate;

    @FXML
    private Button viewAmortizationScheduleButton;

    @FXML
    private Button homeAffordabilityButton;

    @FXML
    private TextField zipCodeField;

    @FXML
    private TextField Tax;

    @FXML
    private TextField HomeInsurance;

    @FXML
    private TextArea ratesTextArea;

    /**
     * Initializes the controller by setting input restrictions and fetching mortgage rates.
     */
    @FXML
    public void initialize() {
        fetchAndDisplayMortgageRates(); 

        setNumericInputRestrictions(monthlyIncomeField);
        setNumericInputRestrictions(monthlyExpensesField);
        setNumericInputRestrictions(PurchasePriceField);
        setNumericInputRestrictions(DownPaymentField);
        setInterestRateInputRestrictions(InterestRateField);
        setNumericInputRestrictions(InsuranceRateField);
        setZipCodeInputRestrictions(zipCodeField);

        addCurrencyFormattingListener(monthlyIncomeField);
        addCurrencyFormattingListener(monthlyExpensesField);
        addCurrencyFormattingListener(PurchasePriceField);
        addCurrencyFormattingListener(DownPaymentField);
        addCurrencyFormattingListener(InsuranceRateField);

        ratesTextArea.setStyle("-fx-text-fill: #9a8021; -fx-font-family: 'Courier New'; -fx-font-size: 13px; -fx-font-weight: bold;");
    }

    /**
     * Fetches and displays the latest mortgage rates in the ratesTextArea.
     */
    public void fetchAndDisplayMortgageRates() {
        MortgageRateFetcher fetcher = new MortgageRateFetcher();
        String rawRates = fetcher.fetchMortgageRates();

        if (rawRates != null && !rawRates.startsWith("Error")) {
            String formattedRates = formatInterestRates(rawRates);
            ratesTextArea.setText(formattedRates);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(rawRates);
            alert.showAndWait();
        }
    }

    /**
     * Formats the interest rates JSON response for display.
     *
     * @param rawJson the raw JSON response containing interest rates
     * @return a formatted string with interest rate information
     */
    public String formatInterestRates(String rawJson) {
        StringBuilder formattedRates = new StringBuilder();

        try {
            JSONObject jsonObj = new JSONObject(rawJson);

            formattedRates.append(String.format("%-29s %-18s %-10s %-12s\n", "Central Bank", "Country", "Rate(%)", "Last Updated"));
            
            JSONArray centralBankRates = jsonObj.getJSONArray("central_bank_rates");
            for (int i = 0; i < centralBankRates.length(); i++) {
                JSONObject rate = centralBankRates.getJSONObject(i);
                String bank = rate.getString("central_bank");
                String country = rate.getString("country");
                double ratePct = rate.getDouble("rate_pct");
                String lastUpdated = rate.getString("last_updated");

                formattedRates.append(String.format("%-29s %-18s %-10.2f %-12s\n", bank, country, ratePct, lastUpdated));
            }

            formattedRates.append("\n");

        } catch (Exception e) {
            formattedRates.append("Error parsing rates");
            e.printStackTrace();
        }

        return formattedRates.toString();
    }

    /**
     * Populates the pie chart with breakdown data including principal, interest, tax, and insurance.
     *
     * @param monthlyPayment the principal and interest payment
     * @param propertyTax the monthly property tax
     * @param homeInsurance the monthly homeowner's insurance
     */
    public void populatePieChart(double monthlyPayment, double propertyTax, double homeInsurance) {
        PieChart.Data principalAndInterestData = new PieChart.Data("Principal & Interest", monthlyPayment);
        PieChart.Data propertyTaxData = new PieChart.Data("Property Tax", propertyTax);
        PieChart.Data homeInsuranceData = new PieChart.Data("Homeowner's Insurance", homeInsurance);
        paymentBreakdownChart.getData().clear();
        paymentBreakdownChart.getData().addAll(principalAndInterestData, propertyTaxData, homeInsuranceData);

        principalAndInterestData.getNode().setStyle("-fx-pie-color: #0000ff;");
        propertyTaxData.getNode().setStyle("-fx-pie-color: #9a8021;");
        homeInsuranceData.getNode().setStyle("-fx-pie-color: fc0000;");
    }

    /**
     * Sets input restrictions for numeric values on a TextField.
     *
     * @param textField the TextField to apply restrictions to
     */
    private void setNumericInputRestrictions(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                textField.setText(oldValue);
            }
        });
    }

    /**
     * Sets input restrictions for ZIP code input on a TextField.
     *
     * @param textField the TextField to apply restrictions to
     */
    private void setZipCodeInputRestrictions(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*") || newValue.length() > 5) { 
                textField.setText(oldValue);
            }
        });

        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { 
                if (textField.getText().length() != 5) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Input Error");
                    alert.setHeaderText(null);
                    alert.setContentText("ZIP Code must be exactly 5 digits.");
                    alert.showAndWait();
                    textField.setText(""); 
                }
            }
        });
    }

    /**
     * Sets input restrictions and formatting for interest rate input on a TextField.
     *
     * @param textField the TextField to apply restrictions to
     */
    private void setInterestRateInputRestrictions(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?") || newValue.length() > 5) {
                textField.setText(oldValue);
            }
            try {
                double interestRate = Double.parseDouble(newValue);
                if (interestRate < 0 || interestRate > 20) {
                    textField.setText(oldValue);
                }
            } catch (NumberFormatException e) {
                // Ignore temporary invalid input
            }
        });

        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { 
                try {
                    double interestRate = Double.parseDouble(textField.getText());
                    textField.setText(String.format("%.2f%%", interestRate));
                } catch (NumberFormatException e) {
                    textField.setText(""); 
                }
            }
        });
    }

    /**
     * Adds currency formatting to a text field when focus is lost.
     *
     * @param textField the text field to apply formatting to
     */
    private void addCurrencyFormattingListener(TextField textField) {
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { 
                try {
                    double value = Double.parseDouble(textField.getText());
                    textField.setText(currencyFormat.format(value));
                } catch (NumberFormatException e) {
                    textField.setText("");
                }
            }
        });
    }

    /**
     * Calculates the monthly mortgage payment and updates the UI with calculated values.
     *
     * @param event the ActionEvent triggered by clicking the Calculate button
     */
    @FXML
    void CalculateButtonPressed(ActionEvent event) {
        try {
            double purchasePrice = Double.parseDouble(PurchasePriceField.getText().replaceAll("[^\\d.]", ""));
            double downPayment = Double.parseDouble(DownPaymentField.getText().replaceAll("[^\\d.]", ""));
            double interestRate = Double.parseDouble(InterestRateField.getText().replaceAll("[^\\d.]", ""));
            double loanTerm = LoanDurationSelected.getValue();
            double principal = purchasePrice - downPayment;
            interestRate /= 100;
            double monthlyInterestRate = interestRate / 12;
            double totalLoanMonths = loanTerm * 12;
            double monthlyPayment = principal * ((monthlyInterestRate * Math.pow(1 + monthlyInterestRate, totalLoanMonths)) / (Math.pow(1 + monthlyInterestRate, totalLoanMonths) - 1));

            String zipCode = zipCodeField.getText();

            double propertyTax = fetchPropertyTax(zipCode);
            double homeInsurance = fetchHomeInsurance(zipCode);

            populatePieChart(monthlyPayment, propertyTax, homeInsurance);

            TotalPayment.setText(currencyFormat.format(monthlyPayment + propertyTax + homeInsurance));
            Tax.setText(currencyFormat.format(propertyTax));
            HomeInsurance.setText(currencyFormat.format(homeInsurance));

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("All Fields are required for calculations.");
            alert.showAndWait();
        }
    }

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
            return 280;
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
            return 66;
        }
    }

    /**
     * Clears all input fields and resets the form.
     *
     * @param event the ActionEvent triggered by clicking the Clear button
     */
    @FXML
    void ClearForm(ActionEvent event) {
        PurchasePriceField.clear();
        DownPaymentField.clear();
        InterestRateField.clear();
        monthlyIncomeField.clear();
        monthlyExpensesField.clear();
        InsuranceRateField.clear();
        TotalPayment.setText("");
        LoanDurationSelected.setValue(20);
    }

    /**
     * Confirms exit before closing the application.
     *
     * @param event the ActionEvent triggered by clicking the Exit button
     */
    @FXML
    void ExitApp(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Application");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to exit the app?");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                System.exit(0);
            }
        });
    }

    /**
     * Navigates to the Home Affordability Calculator view.
     *
     * @param event the ActionEvent triggered by clicking the Home Affordability button
     */
    @FXML
    void goToHomeAffordabilityCalculator(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/HomeAffordabilityCalculator.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Home Affordability Calculator");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Calculates the recommended house price (placeholder function).
     *
     * @param event the ActionEvent triggered by clicking the Calculate Recommended Price button
     */
    @FXML
    void CalculateRecommendedPrice(ActionEvent event) {
        System.out.println("Calculating recommended house price...");
    }

    /**
     * Opens the Amortization Schedule view with populated loan data.
     *
     * @param event the ActionEvent triggered by clicking the View Amortization Schedule button
     */
    @FXML
    void ViewAmortizationSchedule(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AmortizationSchedule.fxml"));
            Parent root = loader.load();

            double loanAmount = Double.parseDouble(PurchasePriceField.getText().replaceAll("[^\\d.]", "")) -
                    Double.parseDouble(DownPaymentField.getText().replaceAll("[^\\d.]", ""));
            double interestRate = Double.parseDouble(InterestRateField.getText().replaceAll("[^\\d.]", ""));
            int loanTerm = (int) LoanDurationSelected.getValue();

            AmortizationScheduleController controller = loader.getController();
            controller.populateSchedule(loanAmount, interestRate, loanTerm);

            Stage stage = new Stage();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("Amortization Schedule");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("All Fields are required for calculations.");
            alert.showAndWait();
        }
    }
}
