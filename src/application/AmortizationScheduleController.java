package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;

/**
 * Controller class for managing the amortization schedule view.
 * This class is responsible for generating the loan amortization schedule
 * and displaying it in a table and chart format. It also provides
 * functionalities to navigate between views and exit the application.
 */
public class AmortizationScheduleController {
	
	private ToolbarController toolbarController = new ToolbarController();

    @FXML
    private TableView<Payment> scheduleTable;

    @FXML
    private TableColumn<Payment, Integer> paymentNumberColumn;

    @FXML
    private TableColumn<Payment, LocalDate> paymentDateColumn;

    @FXML
    private TableColumn<Payment, String> paymentAmountColumn;

    @FXML
    private TableColumn<Payment, String> principalPaidColumn;

    @FXML
    private TableColumn<Payment, String> interestPaidColumn;

    @FXML
    private Button backButton;

    @FXML
    private Button Exit;

    @FXML
    private LineChart<Number, Number> paymentChart;

    private static final DecimalFormat df = new DecimalFormat("#.00");

    /**
     * Initializes the controller class. This method is called after the FXML file
     * has been loaded. It sets up the table columns with the correct data properties.
     */
    @FXML
    public void initialize() {
        paymentNumberColumn.setCellValueFactory(new PropertyValueFactory<>("paymentNumber"));
        paymentDateColumn.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        paymentAmountColumn.setCellValueFactory(new PropertyValueFactory<>("paymentAmountFormatted"));
        principalPaidColumn.setCellValueFactory(new PropertyValueFactory<>("principalPaidFormatted"));
        interestPaidColumn.setCellValueFactory(new PropertyValueFactory<>("interestPaidFormatted"));
    }

    /**
     * Populates the amortization schedule table and payment chart based on the loan parameters.
     *
     * @param loanAmount  the initial loan amount
     * @param interestRate the annual interest rate (in percentage)
     * @param loanTerm    the loan term in years
     */
    public void populateSchedule(double loanAmount, double interestRate, int loanTerm) {
        ObservableList<Payment> payments = FXCollections.observableArrayList();
        double monthlyInterestRate = (interestRate / 100) / 12;
        double monthlyPayment = calculateMonthlyPayment(loanAmount, monthlyInterestRate, loanTerm);

        LocalDate paymentDate = LocalDate.now();

        XYChart.Series<Number, Number> principalSeries = new XYChart.Series<>();
        XYChart.Series<Number, Number> interestSeries = new XYChart.Series<>();

        principalSeries.setName("Principal Paid");
        interestSeries.setName("Interest Paid");

        for (int i = 1; i <= loanTerm * 12; i++) {
            double interestPaid = loanAmount * monthlyInterestRate;
            double principalPaid = monthlyPayment - interestPaid;

            principalSeries.getData().add(new XYChart.Data<>(i, principalPaid));
            interestSeries.getData().add(new XYChart.Data<>(i, interestPaid));

            payments.add(new Payment(i, paymentDate, monthlyPayment, principalPaid, interestPaid));

            loanAmount -= principalPaid;
            paymentDate = paymentDate.plusMonths(1);
        }

        scheduleTable.setItems(payments);
        paymentChart.getData().add(principalSeries);
        paymentChart.getData().add(interestSeries);

        principalSeries.getNode().getStyleClass().add("principal-series");
        interestSeries.getNode().getStyleClass().add("interest-series");
    }

    /**
     * Calculates the monthly payment for the loan based on the loan parameters.
     *
     * @param loanAmount         the initial loan amount
     * @param monthlyInterestRate the monthly interest rate
     * @param loanTerm           the loan term in years
     * @return the calculated monthly payment
     */
    private double calculateMonthlyPayment(double loanAmount, double monthlyInterestRate, int loanTerm) {
        double numerator = monthlyInterestRate * Math.pow(1 + monthlyInterestRate, loanTerm * 12);
        double denominator = Math.pow(1 + monthlyInterestRate, loanTerm * 12) - 1;
        return loanAmount * (numerator / denominator);
    }

    /**
     * Navigates back to the home view by delegating to ToolbarController's goHome method.
     *
     * @param event the ActionEvent triggered by clicking the Back button
     */
    @FXML
    void goBack(ActionEvent event) {
        toolbarController.goHome(event);
    }

    /**
     * Exits the app by delegating to ToolbarController's ExitApp method.
     *
     * @param event the ActionEvent triggered by clicking the Exit button
     */
    @FXML
    void ExitApp(ActionEvent event) {
        toolbarController.ExitApp(event);
    }

    /**
     * Opens the Loan Comparison view for comparing different loan options.
     *
     * @param event the action event triggered by pressing the button
     */
    @FXML
    public void openLoanComparison(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("LoanComparisonView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Loan Comparison Tool");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Inner class representing a single loan payment in the amortization schedule.
     * It contains details about the payment amount, principal paid, and interest paid.
     */
    public static class Payment {
        private final int paymentNumber;
        private final LocalDate paymentDate;
        private final double paymentAmount;
        private final double principalPaid;
        private final double interestPaid;

        /**
         * Constructs a Payment instance with details of a single loan payment.
         *
         * @param paymentNumber the payment number in the schedule
         * @param paymentDate   the date of the payment
         * @param paymentAmount the total amount of the payment
         * @param principalPaid the principal portion of the payment
         * @param interestPaid  the interest portion of the payment
         */
        public Payment(int paymentNumber, LocalDate paymentDate, double paymentAmount, double principalPaid, double interestPaid) {
            this.paymentNumber = paymentNumber;
            this.paymentDate = paymentDate;
            this.paymentAmount = paymentAmount;
            this.principalPaid = principalPaid;
            this.interestPaid = interestPaid;
        }

        public int getPaymentNumber() {
            return paymentNumber;
        }

        public LocalDate getPaymentDate() {
            return paymentDate;
        }

        public String getPaymentAmountFormatted() {
            return df.format(paymentAmount);
        }

        public String getPrincipalPaidFormatted() {
            return df.format(principalPaid);
        }

        public String getInterestPaidFormatted() {
            return df.format(interestPaid);
        }
    }
}
