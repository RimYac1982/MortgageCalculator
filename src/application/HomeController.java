package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.NumberFormat;

public class HomeController {

    private NumberFormat Currency = NumberFormat.getCurrencyInstance();

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
    private Label RecommendedHousePrice; 
    
    @FXML
    private Button viewAmortizationScheduleButton;

    @FXML
    void CalculateButtonPressed(ActionEvent event) {
                double PurchasePriceAmount = Double.parseDouble(PurchasePriceField.getText());
                double DownPaymentPriceAmount = Double.parseDouble(DownPaymentField.getText());
                double InterestRateAmount = Double.parseDouble(InterestRateField.getText());
                double Loan = LoanDurationSelected.getValue();         
                double Principle = PurchasePriceAmount - DownPaymentPriceAmount;
                InterestRateAmount = InterestRateAmount / 100;
                double InterestRateMonth = InterestRateAmount / 12;
                double LoanMonth = Loan * 12;
                double top = (InterestRateMonth * Math.pow(1 + InterestRateMonth, LoanMonth));
                double bottom = (Math.pow(1 + InterestRateMonth, LoanMonth) - 1);
                double Total = Principle * (top / bottom);
                TotalPayment.setText(Currency.format(Total));           
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
        System.exit(0);
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

            double loanAmount = Double.parseDouble(PurchasePriceField.getText()) - Double.parseDouble(DownPaymentField.getText());
            double interestRate = Double.parseDouble(InterestRateField.getText());
            int loanTerm = (int) LoanDurationSelected.getValue(); // get value from the slider
  
            AmortizationScheduleController controller = loader.getController();

            controller.populateSchedule(loanAmount, interestRate, loanTerm);
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Amortization Schedule");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
