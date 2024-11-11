package application.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.FileOutputStream;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Controller class for managing the amortization schedule view. This class is
 * responsible for generating the loan amortization schedule and displaying it
 * in a table and chart format. It also provides functionalities to navigate
 * between views and exit the application.
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
	private Button downloadButton;

	@FXML
	private LineChart<Number, Number> paymentChart;

	private static final DecimalFormat df = new DecimalFormat("#.00");

	/**
	 * Initializes the controller class. This method is called after the FXML file
	 * has been loaded. It sets up the table columns with the correct data
	 * properties.
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
	 * Populates the amortization schedule table and payment chart based on the loan
	 * parameters.
	 *
	 * @param loanAmount   the initial loan amount
	 * @param interestRate the annual interest rate (in percentage)
	 * @param loanTerm     the loan term in years
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
	 * @param loanAmount          the initial loan amount
	 * @param monthlyInterestRate the monthly interest rate
	 * @param loanTerm            the loan term in years
	 * @return the calculated monthly payment
	 */
	private double calculateMonthlyPayment(double loanAmount, double monthlyInterestRate, int loanTerm) {
		double numerator = monthlyInterestRate * Math.pow(1 + monthlyInterestRate, loanTerm * 12);
		double denominator = Math.pow(1 + monthlyInterestRate, loanTerm * 12) - 1;
		return loanAmount * (numerator / denominator);
	}

	/**
	 * Navigates back to the home view by delegating to ToolbarController's goHome
	 * method.
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
	 * Handles the action of downloading the amortization schedule as a PDF file.
	 * Generates a unique file name with a timestamp, creates the PDF, and attempts
	 * to open it in the default PDF viewer if supported by the desktop environment.
	 *
	 * @param event the ActionEvent triggered by the download button
	 */
	@FXML
	public void downloadAsPDF(ActionEvent event) {
	    // Create the Saved-PDF-Files directory in the user's home directory
	    File pdfDir = new File(System.getProperty("user.home") + "/Saved-PDF-Files");
	    if (!pdfDir.exists()) {
	        pdfDir.mkdir();
	    }

	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
	    String timestamp = LocalDateTime.now().format(formatter);
	    String filePath = System.getProperty("user.home") + "/Saved-PDF-Files/AmortizationSchedule_" + timestamp + ".pdf";

	    try {
	        createPdf(filePath);

	        if (Desktop.isDesktopSupported()) {
	            File file = new File(filePath);
	            Desktop.getDesktop().open(file);
	        }

	    } catch (DocumentException | IOException e) {
	        e.printStackTrace();
	    }
	}



	/**
	 * Creates a PDF file containing the amortization schedule. The PDF includes a
	 * title and a table with columns for payment number, date, amount, principal,
	 * and interest. Each row corresponds to a payment entry in the schedule.
	 *
	 * @param dest the file path where the PDF will be saved
	 * @throws IOException       if there is an error during file creation or
	 *                           writing
	 * @throws DocumentException if there is an error in PDF document creation
	 */
	public void createPdf(String dest) throws IOException, DocumentException {
		Document document = new Document();
		PdfWriter.getInstance(document, new FileOutputStream(dest));
		document.open();

		Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
		Font cellFont = new Font(Font.FontFamily.HELVETICA, 10);

		Paragraph title = new Paragraph("Amortization Schedule", headerFont);
		title.setAlignment(Element.ALIGN_CENTER);
		document.add(title);
		document.add(new Paragraph(" "));

		PdfPTable table = new PdfPTable(5);
		table.setWidthPercentage(100);
		table.setWidths(new int[] { 1, 3, 3, 3, 3 });

		String[] headers = { "No.", "Date", "Amount", "Principal", "Interest" };
		for (String header : headers) {
			PdfPCell headerCell = new PdfPCell(new Phrase(header, headerFont));
			headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
			headerCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
			headerCell.setPadding(5);
			table.addCell(headerCell);
		}

		for (Payment payment : scheduleTable.getItems()) {
			table.addCell(new PdfPCell(new Phrase(String.valueOf(payment.getPaymentNumber()), cellFont)));
			table.addCell(new PdfPCell(new Phrase(payment.getPaymentDate().toString(), cellFont)));
			table.addCell(new PdfPCell(new Phrase(payment.getPaymentAmountFormatted(), cellFont)));
			table.addCell(new PdfPCell(new Phrase(payment.getPrincipalPaidFormatted(), cellFont)));
			table.addCell(new PdfPCell(new Phrase(payment.getInterestPaidFormatted(), cellFont)));
		}

		document.add(table);
		document.close();
	}

	/**
	 * Inner class representing a single loan payment in the amortization schedule.
	 * It contains details about the payment amount, principal paid, and interest
	 * paid.
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
		public Payment(int paymentNumber, LocalDate paymentDate, double paymentAmount, double principalPaid,
				double interestPaid) {
			this.paymentNumber = paymentNumber;
			this.paymentDate = paymentDate;
			this.paymentAmount = paymentAmount;
			this.principalPaid = principalPaid;
			this.interestPaid = interestPaid;
		}

		/**
		 * Retrieves the payment number for this entry in the amortization schedule.
		 *
		 * @return the payment number as an integer
		 */
		public int getPaymentNumber() {
			return paymentNumber;
		}

		/**
		 * Retrieves the date of this payment.
		 *
		 * @return the payment date as a LocalDate object
		 */
		public LocalDate getPaymentDate() {
			return paymentDate;
		}

		/**
		 * Retrieves the formatted payment amount for this entry.
		 *
		 * @return the payment amount as a formatted String
		 */
		public String getPaymentAmountFormatted() {
			return df.format(paymentAmount);
		}

		/**
		 * Retrieves the formatted principal paid for this entry.
		 *
		 * @return the principal amount paid as a formatted String
		 */
		public String getPrincipalPaidFormatted() {
			return df.format(principalPaid);
		}

		/**
		 * Retrieves the formatted interest paid for this entry.
		 *
		 * @return the interest amount paid as a formatted String
		 */
		public String getInterestPaidFormatted() {
			return df.format(interestPaid);
		}
	}
}
