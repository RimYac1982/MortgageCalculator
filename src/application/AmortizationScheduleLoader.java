package application;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Utility class for opening the Amortization Schedule view with populated loan data.
 */
public class AmortizationScheduleLoader {

    /**
     * Opens the Amortization Schedule view with populated loan data.
     *
     * @param loanAmount   the loan amount for amortization calculation
     * @param interestRate the interest rate for the loan
     * @param loanTerm     the loan term in years
     */
    public void openAmortizationSchedule(double loanAmount, double interestRate, int loanTerm) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AmortizationSchedule.fxml"));
            Parent root = loader.load();

            AmortizationScheduleController controller = loader.getController();
            controller.populateSchedule(loanAmount, interestRate, loanTerm);

            Stage stage = new Stage();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.setTitle("Amortization Schedule");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("All Fields are required for calculations.");
            alert.showAndWait();
        }
    }
}
