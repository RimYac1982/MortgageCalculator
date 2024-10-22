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
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.NumberFormat;

import org.json.JSONObject;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;

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
    public void initialize() {
    	
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
                
    }
    
    public void populatePieChart(double monthlyPayment, double propertyTax, double homeInsurance) {
        PieChart.Data principalAndInterestData = new PieChart.Data("Principal & Interest", monthlyPayment);
        PieChart.Data propertyTaxData = new PieChart.Data("Property Tax", propertyTax);
        PieChart.Data homeInsuranceData = new PieChart.Data("Homeowner's Insurance", homeInsurance);
        paymentBreakdownChart.getData().clear();
        paymentBreakdownChart.getData().addAll(principalAndInterestData, propertyTaxData, homeInsuranceData);

        principalAndInterestData.getNode().setStyle("-fx-pie-color: blue;");
        propertyTaxData.getNode().setStyle("-fx-pie-color: yellow;");
        homeInsuranceData.getNode().setStyle("-fx-pie-color: red;");
    }



    private void setNumericInputRestrictions(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                textField.setText(oldValue);
            }
        });
    }
    
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

    @FXML
    void CalculateButtonPressed(ActionEvent event) {
        try {
            double PurchasePriceAmount = Double.parseDouble(PurchasePriceField.getText().replaceAll("[^\\d.]", ""));
            double DownPaymentPriceAmount = Double.parseDouble(DownPaymentField.getText().replaceAll("[^\\d.]", ""));
            double InterestRateAmount = Double.parseDouble(InterestRateField.getText().replaceAll("[^\\d.]", ""));
            double Loan = LoanDurationSelected.getValue();
            double Principle = PurchasePriceAmount - DownPaymentPriceAmount;
            InterestRateAmount = InterestRateAmount / 100;
            double InterestRateMonth = InterestRateAmount / 12;
            double LoanMonth = Loan * 12;
            double monthlyPayment = Principle * ((InterestRateMonth * Math.pow(1 + InterestRateMonth, LoanMonth)) / (Math.pow(1 + InterestRateMonth, LoanMonth) - 1));

            String zipCode = zipCodeField.getText();

            double propertyTax = fetchPropertyTax(zipCode);
            double homeInsurance = fetchHomeInsurance(zipCode);

            populatePieChart(monthlyPayment, propertyTax, homeInsurance);

            TotalPayment.setText(currencyFormat.format(monthlyPayment));
            Tax.setText(currencyFormat.format(propertyTax));
            HomeInsurance.setText(currencyFormat.format(homeInsurance));

            TotalPayment.setText(currencyFormat.format(monthlyPayment + propertyTax + homeInsurance));

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("All Fields are required for calculations.");
            alert.showAndWait();
        }
    }

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
    
    @FXML
    void goToHomeAffordabilityCalculator(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/HomeAffordabilityCalculator.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Home Affordability Calculator");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();           
        }
    }

    @FXML
    void CalculateRecommendedPrice(ActionEvent event) {
        System.out.println("Calculating recommended house price...");
    }

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
            stage.setScene(new Scene(root));
            stage.setTitle("Amortization Schedule");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (NumberFormatException e) {
        	Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("All Fields are required for calculations.");
            alert.showAndWait();
        }
    }
}
