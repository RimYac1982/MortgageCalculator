package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.NumberFormat;

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
    public void initialize() {
    	
        setNumericInputRestrictions(monthlyIncomeField);
        setNumericInputRestrictions(monthlyExpensesField);
        setNumericInputRestrictions(PurchasePriceField);
        setNumericInputRestrictions(DownPaymentField);
        setInterestRateInputRestrictions(InterestRateField);
        setNumericInputRestrictions(InsuranceRateField);

        addCurrencyFormattingListener(monthlyIncomeField);
        addCurrencyFormattingListener(monthlyExpensesField);
        addCurrencyFormattingListener(PurchasePriceField);
        addCurrencyFormattingListener(DownPaymentField);
        addCurrencyFormattingListener(InsuranceRateField);
    }

    private void setNumericInputRestrictions(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                textField.setText(oldValue);
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
            double top = (InterestRateMonth * Math.pow(1 + InterestRateMonth, LoanMonth));
            double bottom = (Math.pow(1 + InterestRateMonth, LoanMonth) - 1);
            double Total = Principle * (top / bottom);
            TotalPayment.setText(currencyFormat.format(Total));
        } catch (NumberFormatException e) {
        	Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("All Fields are required for calculations.");
            alert.showAndWait();
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
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
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
