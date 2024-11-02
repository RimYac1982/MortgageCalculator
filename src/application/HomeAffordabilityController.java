package application;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.NumberFormat;

/**
 * Controller class for managing the home affordability calculator view.
 * This class calculates an affordable home price based on income, expenses, and debt.
 * It provides methods to validate inputs, calculate affordability, and manage the UI components.
 */
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
    private TextField dtiRatioField;

    @FXML
    private BarChart<String, Number> affordabilityChart;

    /**
     * Initializes the controller. Sets up input restrictions and formatting for text fields.
     */
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

    /**
     * Restricts input in a text field to integers only.
     *
     * @param textField the text field to apply the restriction to
     */
    private void setIntegerInputRestrictions(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textField.setText(oldValue);
            }
        });
    }

    /**
     * Restricts input in the interest rate text field to a valid percentage format.
     * Limits input to two decimal places and a maximum value of 20%.
     *
     * @param textField the text field to apply the restriction to
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
                // Ignore invalid format temporarily
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
     * Calculates the maximum affordable home price based on income, expenses, and debt.
     * Updates the UI with the calculated values and displays them in a bar chart.
     */
    @FXML
    public void calculateAffordability() {
        try {
            double annualIncome = Double.parseDouble(annualIncomeField.getText().replaceAll("[^\\d.]", ""));
            double monthlyExpenses = Double.parseDouble(monthlyExpensesField.getText().replaceAll("[^\\d.]", ""));
            double creditCardDebt = Double.parseDouble(debtField.getText().replaceAll("[^\\d.]", ""));
            double annualDebt = (monthlyExpenses * 12) + creditCardDebt;

            double dtiRatio = (annualDebt / annualIncome) * 100;
            double affordableIncome = annualIncome - annualDebt;
            double maxAffordablePrice = affordableIncome * 5;

            maxAffordablePriceField.setText(String.format("$%.2f", maxAffordablePrice));
            dtiRatioField.setText(String.format("%.2f%%", dtiRatio));

            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Affordability Breakdown");
            series.getData().add(new XYChart.Data<>("Income", annualIncome));
            series.getData().add(new XYChart.Data<>("Expenses", monthlyExpenses * 12));
            series.getData().add(new XYChart.Data<>("Credit Card Debt", creditCardDebt));
            series.getData().add(new XYChart.Data<>("Max Affordable Price", maxAffordablePrice));

            affordabilityChart.setCategoryGap(20);  
            affordabilityChart.setBarGap(5);        
            affordabilityChart.getData().clear();   
            affordabilityChart.getData().add(series);

            for (XYChart.Data<String, Number> data : series.getData()) {
                if (data.getXValue().equals("Income")) {
                    data.getNode().setStyle("-fx-bar-fill: green;");
                } else if (data.getXValue().equals("Expenses")) {
                    data.getNode().setStyle("-fx-bar-fill: red;");
                } else if (data.getXValue().equals("Credit Card Debt")) {
                    data.getNode().setStyle("-fx-bar-fill: orange;");
                } else if (data.getXValue().equals("Max Affordable Price")) {
                    data.getNode().setStyle("-fx-bar-fill: blue;");
                }
            }

            NumberAxis yAxis = (NumberAxis) affordabilityChart.getYAxis();
            yAxis.setAutoRanging(false);
            yAxis.setLowerBound(0);
            yAxis.setUpperBound(500000);
            yAxis.setTickUnit(50000);

            Platform.runLater(() -> affordabilityChart.layout());

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

    /**
     * Placeholder method for calculating affordability, currently only prints a message.
     *
     * @param event the action event triggered by button click
     */
    @FXML
    void CalculateAffortabilityPrice(ActionEvent event) {
        System.out.println("Calculating recommended house price...");
    }

    /**
     * Clears all input and output fields in the form.
     *
     * @param event the action event triggered by button click
     */
    @FXML
    void ClearForm(ActionEvent event) {
        if (annualIncomeField != null) annualIncomeField.clear();
        if (monthlyExpensesField != null) monthlyExpensesField.clear();
        if (debtField != null) debtField.clear();
        if (downPaymentField != null) downPaymentField.clear();
        if (interestRateField != null) interestRateField.clear();
        if (maxAffordablePriceField != null) maxAffordablePriceField.clear();
        if (dtiRatioField != null) dtiRatioField.clear();
    }

    /**
     * Prompts the user to confirm exit before closing the application.
     *
     * @param event the action event triggered by button click
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
     * Navigates back to the home view.
     *
     * @param event the action event triggered by button click
     */
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
