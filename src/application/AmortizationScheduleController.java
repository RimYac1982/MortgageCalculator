package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDate;

public class AmortizationScheduleController {

    @FXML
    private TableView<Payment> scheduleTable;
    
    @FXML
    private Button Exit;
    
    @FXML
    private Button Back;

    @FXML
    private TableColumn<Payment, Integer> paymentNumberColumn;

    @FXML
    private TableColumn<Payment, LocalDate> paymentDateColumn;

    @FXML
    private TableColumn<Payment, Double> paymentAmountColumn;

    @FXML
    private TableColumn<Payment, Double> principalPaidColumn;

    @FXML
    private TableColumn<Payment, Double> interestPaidColumn;

    private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

    public void initialize() {
 
        paymentNumberColumn.setCellValueFactory(new PropertyValueFactory<>("paymentNumber"));
        paymentDateColumn.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        paymentAmountColumn.setCellValueFactory(new PropertyValueFactory<>("paymentAmount"));
        principalPaidColumn.setCellValueFactory(new PropertyValueFactory<>("principalPaid"));
        interestPaidColumn.setCellValueFactory(new PropertyValueFactory<>("interestPaid"));
    }

    public void populateSchedule(double loanAmount, double interestRate, int loanTerm) {
        ObservableList<Payment> payments = FXCollections.observableArrayList();
        double monthlyInterestRate = (interestRate / 100) / 12;
        double monthlyPayment = calculateMonthlyPayment(loanAmount, monthlyInterestRate, loanTerm);

        LocalDate paymentDate = LocalDate.now(); 

        for (int i = 1; i <= loanTerm * 12; i++) {
            double interestPaid = loanAmount * monthlyInterestRate;
            double principalPaid = monthlyPayment - interestPaid;

            payments.add(new Payment(i, paymentDate, monthlyPayment, principalPaid, interestPaid));

            loanAmount -= principalPaid;

            paymentDate = paymentDate.plusMonths(1);
        }

        System.out.println("Monthly Payment: " + currencyFormat.format(monthlyPayment));

        scheduleTable.setItems(payments);
    }


    private double calculateMonthlyPayment(double loanAmount, double monthlyInterestRate, int loanTerm) {
        double numerator = monthlyInterestRate * Math.pow(1 + monthlyInterestRate, loanTerm * 12);
        double denominator = Math.pow(1 + monthlyInterestRate, loanTerm * 12) - 1;
        return loanAmount * (numerator / denominator);
    }

    public static class Payment {
        private final int paymentNumber;
        private final LocalDate paymentDate;
        private final double paymentAmount;
        private final double principalPaid;
        private final double interestPaid;

        public Payment(int paymentNumber, LocalDate paymentDate, double paymentAmount, double principalPaid, double interestPaid) {
            this.paymentNumber = paymentNumber;
            this.paymentDate = paymentDate;
            this.paymentAmount = paymentAmount;
            this.principalPaid = principalPaid;
            this.interestPaid = interestPaid;
        }
        
        @FXML
        public void goBack(ActionEvent event) {
            try {
                // Load the HomeView.fxml
                FXMLLoader loader = new FXMLLoader(getClass().getResource("HomeView.fxml"));
                Parent root = loader.load();

                // Get the current stage from the event
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                
                // Set the new scene
                stage.setScene(new Scene(root));
                stage.setTitle("Mortgage Calculator");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        @FXML
        void ExitApp(ActionEvent event) {
            System.exit(0);
        }

        public int getPaymentNumber() {
            return paymentNumber;
        }
        

        public LocalDate getPaymentDate() {
            return paymentDate;
        }

        public double getPaymentAmount() {
            return paymentAmount;
        }

        public double getPrincipalPaid() {
            return principalPaid;
        }

        public double getInterestPaid() {
            return interestPaid;
        }
    }
}
