package application.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.text.NumberFormat;

import org.json.JSONArray;
import org.json.JSONObject;

import application.loaders.AmortizationScheduleLoader;
import application.utils.MortgageRateFetcher;
import application.utils.PropertyTaxAndInsuranceFetcher;

/**
 * Controller class for managing the home view of the mortgage calculator
 * application. Provides functionalities for calculating mortgage payments,
 * fetching mortgage rates, and displaying results.
 */
public class HomeController {

	private AmortizationScheduleLoader amortizationScheduleLoader = new AmortizationScheduleLoader();

	private ToolbarController toolbarController = new ToolbarController();

	private PropertyTaxAndInsuranceFetcher fetcher = new PropertyTaxAndInsuranceFetcher();

	private NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

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
	private PieChart paymentBreakdownChart;

	@FXML
	private Button Exit;

	@FXML
	private Button Clear;

	@FXML
	private Button Calculate;

	@FXML
	private Button viewAmortizationScheduleButton;

	@FXML
	private Button homeAffordabilityButton;

	@FXML
	private TextField zipCodeField;

	public PropertyTaxAndInsuranceFetcher getFetcher() {
		return fetcher;
	}

	public void setFetcher(PropertyTaxAndInsuranceFetcher fetcher) {
		this.fetcher = fetcher;
	}

	@FXML
	private TextField Tax;

	@FXML
	private TextField HomeInsurance;

	@FXML
	private TextArea ratesTextArea;

	public void setFieldValues(double purchasePrice, double downPayment, double interestRate, int loanTerm) {
		PurchasePriceField.setText(String.valueOf(purchasePrice));
		DownPaymentField.setText(String.valueOf(downPayment));
		InterestRateField.setText(String.valueOf(interestRate));
		LoanDurationSelected.setValue(loanTerm);
	}

	/**
	 * Initializes the controller by setting input restrictions and fetching
	 * mortgage rates.
	 */
	@FXML
	public void initialize() {
		fetchAndDisplayMortgageRates();

		setNumericInputRestrictions(monthlyIncomeField);
		setNumericInputRestrictions(monthlyExpensesField);
		setNumericInputRestrictions(PurchasePriceField);
		setNumericInputRestrictions(DownPaymentField);
		setInterestRateInputRestrictions(InterestRateField);
		setNumericInputRestrictions(InsuranceRateField);
		setZipCodeInputRestrictions(zipCodeField);

		addCurrencyFormattingListener(monthlyIncomeField);
		addCurrencyFormattingListener(monthlyExpensesField);
		addCurrencyFormattingListener(PurchasePriceField);
		addCurrencyFormattingListener(DownPaymentField);
		addCurrencyFormattingListener(InsuranceRateField);

		ratesTextArea.setStyle(
				"-fx-text-fill: #9a8021; -fx-font-family: 'Courier New'; -fx-font-size: 13px; -fx-font-weight: bold;");
	}

	/**
	 * Fetches and displays the latest mortgage rates in the ratesTextArea.
	 */
	public void fetchAndDisplayMortgageRates() {
		MortgageRateFetcher fetcher = new MortgageRateFetcher();
		String rawRates = fetcher.fetchMortgageRates();

		if (rawRates != null && !rawRates.startsWith("Error")) {
			String formattedRates = formatInterestRates(rawRates);
			ratesTextArea.setText(formattedRates);
		} else {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText(null);
			alert.setContentText(rawRates);
			alert.showAndWait();
		}
	}

	/**
	 * Formats the interest rates JSON response for display.
	 *
	 * @param rawJson the raw JSON response containing interest rates
	 * @return a formatted string with interest rate information
	 */
	public String formatInterestRates(String rawJson) {
		StringBuilder formattedRates = new StringBuilder();

		try {
			JSONObject jsonObj = new JSONObject(rawJson);

			formattedRates.append(
					String.format("%-29s %-18s %-10s %-12s\n", "Central Bank", "Country", "Rate(%)", "Last Updated"));

			JSONArray centralBankRates = jsonObj.getJSONArray("central_bank_rates");
			for (int i = 0; i < centralBankRates.length(); i++) {
				JSONObject rate = centralBankRates.getJSONObject(i);
				String bank = rate.getString("central_bank");
				String country = rate.getString("country");
				double ratePct = rate.getDouble("rate_pct");
				String lastUpdated = rate.getString("last_updated");

				formattedRates
						.append(String.format("%-29s %-18s %-10.2f %-12s\n", bank, country, ratePct, lastUpdated));
			}

			formattedRates.append("\n");

		} catch (Exception e) {
			formattedRates.append("Error parsing rates");
			e.printStackTrace();
		}

		return formattedRates.toString();
	}

	/**
	 * Populates the pie chart with breakdown data including principal, interest,
	 * tax, and insurance.
	 *
	 * @param monthlyPayment the principal and interest payment
	 * @param propertyTax    the monthly property tax
	 * @param homeInsurance  the monthly homeowner's insurance
	 */
	public void populatePieChart(double monthlyPayment, double propertyTax, double homeInsurance) {
		PieChart.Data principalAndInterestData = new PieChart.Data("Principal & Interest", monthlyPayment);
		PieChart.Data propertyTaxData = new PieChart.Data("Property Tax", propertyTax);
		PieChart.Data homeInsuranceData = new PieChart.Data("Homeowner's Insurance", homeInsurance);
		paymentBreakdownChart.getData().clear();
		paymentBreakdownChart.getData().addAll(principalAndInterestData, propertyTaxData, homeInsuranceData);

		principalAndInterestData.getNode().setStyle("-fx-pie-color: #0000ff;");
		propertyTaxData.getNode().setStyle("-fx-pie-color: #9a8021;");
		homeInsuranceData.getNode().setStyle("-fx-pie-color: fc0000;");
	}

	/**
	 * Sets input restrictions for numeric values on a TextField.
	 *
	 * @param textField the TextField to apply restrictions to
	 */
	private void setNumericInputRestrictions(TextField textField) {
		textField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.matches("\\d*(\\.\\d*)?")) {
				textField.setText(oldValue);
			}
		});
	}

	/**
	 * Sets input restrictions for ZIP code input on a TextField.
	 *
	 * @param textField the TextField to apply restrictions to
	 */
	private void setZipCodeInputRestrictions(TextField textField) {
		textField.textProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue.matches("\\d*") || newValue.length() > 5) {
				textField.setText(oldValue);
			}
		});

		textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
			if (!newValue) {
				if (textField.getText().length() != 5) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle("Input Error");
					alert.setHeaderText(null);
					alert.setContentText("ZIP Code must be exactly 5 digits.");
					alert.showAndWait();
					textField.setText("");
				}
			}
		});
	}

	/**
	 * Sets input restrictions and formatting for interest rate input on a
	 * TextField.
	 *
	 * @param textField the TextField to apply restrictions to
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
	 * Calculates the total monthly payment, including the principal & interest
	 * payment, property tax, and home insurance.
	 *
	 * @param monthlyPayment the principal & interest portion of the mortgage
	 *                       payment
	 * @param propertyTax    the monthly property tax amount
	 * @param homeInsurance  the monthly home insurance amount
	 * @return the total monthly payment including mortgage, property tax, and home
	 *         insurance
	 */
	public double calculateTotalPayment(double monthlyPayment, double propertyTax, double homeInsurance) {
		return monthlyPayment + propertyTax + homeInsurance;
	}

	/**
	 * Calculates the monthly mortgage payment based on the purchase price, down
	 * payment, interest rate, and loan term.
	 *
	 * @param purchasePrice the total price of the property
	 * @param downPayment   the initial amount paid upfront
	 * @param interestRate  the annual interest rate (as a percentage, e.g., 6 for
	 *                      6%)
	 * @param loanTerm      the duration of the loan in years
	 * @return the monthly mortgage payment (principal & interest)
	 */
	public double calculateMonthlyPayment(double purchasePrice, double downPayment, double interestRate,
			double loanTerm) {
		double principal = purchasePrice - downPayment;
		double monthlyInterestRate = interestRate / 100 / 12;
		double totalLoanMonths = loanTerm * 12;

		if (monthlyInterestRate == 0) {
			return principal / totalLoanMonths;
		}

		return principal * ((monthlyInterestRate * Math.pow(1 + monthlyInterestRate, totalLoanMonths))
				/ (Math.pow(1 + monthlyInterestRate, totalLoanMonths) - 1));
	}

	/**
	 * Handles the action triggered when the Calculate button is clicked. This
	 * method retrieves user inputs for purchase price, down payment, interest rate,
	 * loan term, and ZIP code, then calculates the monthly mortgage payment along
	 * with property tax and home insurance.
	 * 
	 * It then calculates the total monthly payment and updates the UI with the
	 * calculated values for mortgage payment breakdown and total payment.
	 * 
	 * If any required input field contains invalid or missing data, an error alert
	 * is displayed to notify the user.
	 *
	 * @param event the ActionEvent triggered by clicking the Calculate button
	 */
	@FXML
	void CalculateButtonPressed(ActionEvent event) {
	    try {
	        double purchasePrice = Double.parseDouble(PurchasePriceField.getText().replaceAll("[^\\d.]", ""));
	        double downPayment = Double.parseDouble(DownPaymentField.getText().replaceAll("[^\\d.]", ""));
	        double interestRate = Double.parseDouble(InterestRateField.getText().replaceAll("[^\\d.]", ""));
	        double loanTerm = LoanDurationSelected.getValue();

	        double monthlyPayment = calculateMonthlyPayment(purchasePrice, downPayment, interestRate, loanTerm);

	        String zipCode = zipCodeField.getText();
	        double propertyTax = fetcher.fetchPropertyTax(zipCode, purchasePrice);
	        double homeInsurance = fetcher.fetchHomeInsurance(zipCode);

	        double totalPayment = calculateTotalPayment(monthlyPayment, propertyTax, homeInsurance);

	        populatePieChart(monthlyPayment, propertyTax, homeInsurance);
	        TotalPayment.setText(currencyFormat.format(totalPayment));
	        Tax.setText(currencyFormat.format(propertyTax));
	        HomeInsurance.setText(currencyFormat.format(homeInsurance));

	    } catch (NumberFormatException e) {
	        Alert alert = new Alert(Alert.AlertType.ERROR);
	        alert.setTitle("Error");
	        alert.setHeaderText(null);
	        alert.setContentText("All Fields are required for calculations.");
	        alert.showAndWait();
	    }
	}


	/**
	 * Clears all input fields and resets the form.
	 *
	 * @param event the ActionEvent triggered by clicking the Clear button
	 */
	@FXML
	void ClearForm(ActionEvent event) {
		if (PurchasePriceField != null)
			PurchasePriceField.clear();
		if (DownPaymentField != null)
			DownPaymentField.clear();
		if (InterestRateField != null)
			InterestRateField.clear();
		if (monthlyIncomeField != null)
			monthlyIncomeField.clear();
		if (monthlyExpensesField != null)
			monthlyExpensesField.clear();
		if (InsuranceRateField != null)
			InsuranceRateField.clear();
		if (TotalPayment != null)
			TotalPayment.setText("");
		if (LoanDurationSelected != null)
			LoanDurationSelected.setValue(20);
		if (Tax != null)
			Tax.setText("");
		if (HomeInsurance != null)
			HomeInsurance.setText("");
		if (zipCodeField != null)
			zipCodeField.setText("");
		if (paymentBreakdownChart != null)
			paymentBreakdownChart.getData().clear();
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
	 * Navigates to the Home Affordability Calculator view.
	 *
	 * @param event the ActionEvent triggered by clicking the Home Affordability
	 *              button
	 */
	@FXML
	void goToHomeAffordabilityCalculator(ActionEvent event) {
		try {
			FXMLLoader loader = new FXMLLoader(
					getClass().getResource("/application/fxml/HomeAffordabilityCalculator.fxml"));
			Parent root = loader.load();

			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("Mortgage Match");
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Calculates the recommended house price (placeholder function).
	 *
	 * @param event the ActionEvent triggered by clicking the Calculate Recommended
	 *              Price button
	 */
	@FXML
	void CalculateRecommendedPrice(ActionEvent event) {
		System.out.println("Calculating recommended house price...");
	}

	/**
	 * Opens the Amortization Schedule view by calling AmortizationScheduleLoader.
	 *
	 * @param event the ActionEvent triggered by clicking the View Amortization
	 *              Schedule button
	 */
	@FXML
	void ViewAmortizationSchedule(ActionEvent event) {
		try {
			double loanAmount = Double.parseDouble(PurchasePriceField.getText().replaceAll("[^\\d.]", ""))
					- Double.parseDouble(DownPaymentField.getText().replaceAll("[^\\d.]", ""));
			double interestRate = Double.parseDouble(InterestRateField.getText().replaceAll("[^\\d.]", ""));
			int loanTerm = (int) LoanDurationSelected.getValue();

			amortizationScheduleLoader.openAmortizationSchedule(loanAmount, interestRate, loanTerm);
		} catch (NumberFormatException e) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText(null);
			alert.setContentText("All Fields are required for calculations.");
			alert.showAndWait();
		}
	}

}
