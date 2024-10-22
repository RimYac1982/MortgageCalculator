package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.NumberFormat;

public class HomeAffordabilityController {
	
	private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
	
	@FXML
    private Slider LoanDurationSelected;

    @FXML
    private TextField annualIncomeField;

    @FXML
    private TextField interestRateField;

    @FXML
    private TextField debtField;

    @FXML
    private TextField monthlyExpensesField;

    @FXML
    private TextField downPaymentField;

    @FXML
    private TextField maxAffordablePriceField;

    @FXML
    private Button calculateAffordabilityBtn;

    @FXML
    private Button Exit;

    @FXML
    private Button Clear;

    @FXML
    private Button GoHome;
    
    @FXML
    public void initialize() {
        
        setIntegerInputRestrictions(annualIncomeField);
        setInterestRateInputRestrictions(interestRateField);
        setIntegerInputRestrictions(debtField);
        setIntegerInputRestrictions(monthlyExpensesField);
        setIntegerInputRestrictions(downPaymentField);
        
        addCurrencyFormattingListener(annualIncomeField);
        addCurrencyFormattingListener(debtField);
        addCurrencyFormattingListener(monthlyExpensesField);
        addCurrencyFormattingListener(downPaymentField);
    }

    private void setIntegerInputRestrictions(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
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
    public void calculateAffordability() {
        try {
            double annualIncome = Double.parseDouble(annualIncomeField.getText().replaceAll("[^\\d.]", ""));
            double monthlyExpenses = Double.parseDouble(monthlyExpensesField.getText().replaceAll("[^\\d.]", ""));
            double creditCardDebt = Double.parseDouble(debtField.getText().replaceAll("[^\\d.]", ""));
            double annualDebt = (monthlyExpenses * 12) + creditCardDebt;
            double affordableIncome = annualIncome - annualDebt;
            double maxAffordablePrice = affordableIncome * 5;

            maxAffordablePriceField.setText(String.format("%.2f", maxAffordablePrice));

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setHeaderText(null);
            alert.setContentText("All fields are required and must contain valid numeric values.");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("An unexpected error occurred. Please check the input fields.");
            alert.showAndWait();
        }
    }
    
    @FXML
    void CalculateAffortabilityPrice(ActionEvent event) {
        System.out.println("Calculating recommended house price...");
    }

    @FXML
    void ClearForm(ActionEvent event) {
        if (annualIncomeField != null) annualIncomeField.clear();
        if (monthlyExpensesField != null) monthlyExpensesField.clear();
        if (debtField != null) debtField.clear();
        if (downPaymentField != null) downPaymentField.clear();
        if (interestRateField != null) interestRateField.clear();
        if (maxAffordablePriceField != null) maxAffordablePriceField.clear();
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
    void goHome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("HomeView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Mortgage Calculator");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
