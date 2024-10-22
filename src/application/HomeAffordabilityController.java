package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeAffordabilityController {

    @FXML
    private TextField annualIncomeField;

    @FXML
    private TextField interestRateField;
    
    @FXML
    private TextField DebtField;

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
    private Button Home;

    public void calculateAffordability() {
        double annualIncome = Double.parseDouble(annualIncomeField.getText());
        double monthlyExpenses = Double.parseDouble(monthlyExpensesField.getText());
        double interestRate = Double.parseDouble(interestRateField.getText());
        double downPayment = Double.parseDouble(downPaymentField.getText());

        double maxAffordable = (annualIncome - (monthlyExpenses * 12)) * 5;
        maxAffordablePriceField.setText(String.format("%.2f", maxAffordable));
    }

    @FXML
    void ClearForm(ActionEvent event) {
    	annualIncomeField.clear();
        downPaymentField.clear();
        interestRateField.clear();
        monthlyExpensesField.clear();
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
            // Load the home view FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("HomeView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) Home.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Home View");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
