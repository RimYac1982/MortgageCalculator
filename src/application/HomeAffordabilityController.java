package application;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class HomeAffordabilityController {

    @FXML
    private TextField monthlyIncomeField;

    @FXML
    private TextField monthlyExpensesField;

    @FXML
    private TextField DTIRatioField;

    @FXML
    private TextField affordabilityInterestRateField;

    @FXML
    private Slider affordabilityLoanTermSlider;

    @FXML
    private Label affordableHousePriceLabel;

    @FXML
    void calculateAffordability() {
        try {
            double monthlyIncome = Double.parseDouble(monthlyIncomeField.getText());
            double monthlyExpenses = Double.parseDouble(monthlyExpensesField.getText());
            double dtiRatio = Double.parseDouble(DTIRatioField.getText()) / 100;
            double interestRate = Double.parseDouble(affordabilityInterestRateField.getText()) / 100;
            double loanTermYears = affordabilityLoanTermSlider.getValue();

            // Simple calculation for affordable house price
            double affordableMonthlyPayment = (monthlyIncome - monthlyExpenses) * dtiRatio;
            double monthlyInterestRate = interestRate / 12;
            double loanTermMonths = loanTermYears * 12;
            double affordableLoanAmount = affordableMonthlyPayment / (monthlyInterestRate * Math.pow(1 + monthlyInterestRate, loanTermMonths) / (Math.pow(1 + monthlyInterestRate, loanTermMonths) - 1));

            affordableHousePriceLabel.setText(String.format("%.2f", affordableLoanAmount));

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Input Error");
            alert.setHeaderText(null);
            alert.setContentText("Please enter valid numeric values.");
            alert.showAndWait();
        }
    }
}
