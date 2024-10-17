package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Tooltip;
import java.text.NumberFormat;

public class HomeController {
    private NumberFormat Currency = NumberFormat.getCurrencyInstance();
    ObservableList<String> LoanDurationList = FXCollections.observableArrayList("Custom", "Selected");

    @FXML
    private Slider LoanDurationSelected;

    @FXML
    private TextField LoanDurationCustom;

    @FXML
    private ChoiceBox<String> LoanDurationSelector;

    @FXML
    private TextField DownPaymentPrice;

    @FXML
    private TextField InterestRate;

    @FXML
    private TextField PurchasePrice;

    @FXML
    private TextField TotalPayment;

    @FXML
    private void initialize() {
        LoanDurationSelector.setItems(LoanDurationList);
        LoanDurationSelector.setTooltip(new Tooltip("Custom or Selected?"));
    }

    @FXML
    void CalculateButtonPressed(ActionEvent event) {
        double PurchasePriceAmount = Double.parseDouble(PurchasePrice.getText());
        double DownPaymentPriceAmount = Double.parseDouble(DownPaymentPrice.getText());
        double InterestRateAmount = Double.parseDouble(InterestRate.getText());
        double Loan;
        if (LoanDurationSelector.getValue().equals("Custom")) {
            Loan = Double.parseDouble(LoanDurationCustom.getText());
        } else {
            Loan = LoanDurationSelected.getValue();
        }
        double Principle = PurchasePriceAmount - DownPaymentPriceAmount;
        InterestRateAmount = InterestRateAmount / 100;
        double InterestRateMonth = InterestRateAmount / 12;
        double LoanMonth = Loan * 12;
        double top = (InterestRateMonth * Math.pow(1 + InterestRateMonth, LoanMonth));
        double bottom = (Math.pow(1 + InterestRateMonth, LoanMonth) - 1);
        double Total = Principle * (top / bottom);
        TotalPayment.setText(Currency.format(Total));
    }

    // Method to clear all input fields
    @FXML
    void ClearForm(ActionEvent event) {
        PurchasePrice.clear();
        DownPaymentPrice.clear();
        InterestRate.clear();
        LoanDurationCustom.clear();
        TotalPayment.clear();
        LoanDurationSelector.setValue(null); // Reset ChoiceBox
        LoanDurationSelected.setValue(20); // Reset Slider to default value (20)
    }

    @FXML
    void ExitApp(ActionEvent event) {
        System.exit(0);
    }

    // New method to calculate the recommended house price
    @FXML
    void CalculateRecommendedPrice(ActionEvent event) {
        // Implement your logic for calculating the recommended house price here
        // For example, it might be based on income, expenses, and loan term
        System.out.println("Calculating recommended house price...");
    }

    // New method to view the amortization schedule
    @FXML
    void ViewAmortizationSchedule(ActionEvent event) {
        // Implement your logic for showing the amortization schedule here
        // For example, open a new window or display the amortization breakdown
        System.out.println("Viewing amortization schedule...");
    }
}
