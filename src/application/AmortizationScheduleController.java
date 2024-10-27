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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;

public class AmortizationScheduleController {

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
    private LineChart<Number, Number> paymentChart; // LineChart for payments

    private static final DecimalFormat df = new DecimalFormat("#.00");

    @FXML
    public void initialize() {
        paymentNumberColumn.setCellValueFactory(new PropertyValueFactory<>("paymentNumber"));
        paymentDateColumn.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        paymentAmountColumn.setCellValueFactory(new PropertyValueFactory<>("paymentAmountFormatted"));
        principalPaidColumn.setCellValueFactory(new PropertyValueFactory<>("principalPaidFormatted"));
        interestPaidColumn.setCellValueFactory(new PropertyValueFactory<>("interestPaidFormatted"));
    }

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

     // After adding the series to the chart
        paymentChart.getData().add(principalSeries);
        paymentChart.getData().add(interestSeries);

        // Apply CSS classes to series nodes
        principalSeries.getNode().getStyleClass().add("principal-series");
        interestSeries.getNode().getStyleClass().add("interest-series");

    }


    private double calculateMonthlyPayment(double loanAmount, double monthlyInterestRate, int loanTerm) {
        double numerator = monthlyInterestRate * Math.pow(1 + monthlyInterestRate, loanTerm * 12);
        double denominator = Math.pow(1 + monthlyInterestRate, loanTerm * 12) - 1;
        return loanAmount * (numerator / denominator);
    }

    @FXML
    public void goBack(ActionEvent event) {
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
