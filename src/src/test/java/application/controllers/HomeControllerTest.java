package application.controllers;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class HomeControllerTest {

	private HomeController homeController;

	@Before
	public void setUp() {
		homeController = new HomeController();
	}

	@Test
	public void testCalculateMonthlyPayment() {

		double purchasePrice = 987000;
		double downPayment = 250000;
		double interestRate = 6;
		double loanTerm = 20;

		double principal = purchasePrice - downPayment;
		double monthlyInterestRate = interestRate / 100 / 12;
		double totalLoanMonths = loanTerm * 12;

		double expectedMonthlyPayment = principal
				* ((monthlyInterestRate * Math.pow(1 + monthlyInterestRate, totalLoanMonths))
						/ (Math.pow(1 + monthlyInterestRate, totalLoanMonths) - 1));

		double actualMonthlyPayment = homeController.calculateMonthlyPayment(purchasePrice, downPayment, interestRate,
				loanTerm);

		System.out.println("Expected Monthly Payment: " + expectedMonthlyPayment);
		System.out.println("Actual Monthly Payment: " + actualMonthlyPayment);

		assertEquals("Monthly payment calculation error", expectedMonthlyPayment, actualMonthlyPayment, 0.01);
	}

	@Test
	public void testCalculateTotalPayment() {

		double monthlyPayment = 4430.09;
		double propertyTax = 200;
		double homeInsurance = 150;

		double expectedTotalPayment = monthlyPayment + propertyTax + homeInsurance;

		double actualTotalPayment = homeController.calculateTotalPayment(monthlyPayment, propertyTax, homeInsurance);

		System.out.println("Expected Total Payment: " + expectedTotalPayment);
		System.out.println("Actual Total Payment: " + actualTotalPayment);

		assertEquals("Total payment calculation error", expectedTotalPayment, actualTotalPayment, 0.01);
	}
}
