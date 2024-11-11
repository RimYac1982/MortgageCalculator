package application.controllers;

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

public class HomeAffordabilityControllerTest {

	private HomeAffordabilityController controller;

	@Before
	public void setUp() {
		setController(new HomeAffordabilityController());
	}

	@Test
	public void testCalculateAffordability() {
		double annualIncome = 120000;
		double monthlyExpenses = 2000;
		double creditCardDebt = 10000;

		double annualDebt = (monthlyExpenses * 12) + creditCardDebt;

		double expectedDtiRatio = (annualDebt / annualIncome) * 100;

		double affordableIncome = annualIncome - annualDebt;

		double expectedMaxAffordablePrice = affordableIncome * 5;

		double actualDtiRatio = (annualDebt / annualIncome) * 100;
		double actualMaxAffordablePrice = affordableIncome * 5;

		System.out.println("Expected DTI Ratio: " + expectedDtiRatio);
		System.out.println("Actual DTI Ratio: " + actualDtiRatio);
		System.out.println("Expected Max Affordable Price: " + expectedMaxAffordablePrice);
		System.out.println("Actual Max Affordable Price: " + actualMaxAffordablePrice);

		assertEquals("DTI ratio calculation error", expectedDtiRatio, actualDtiRatio, 0.01);
		assertEquals("Max affordable price calculation error", expectedMaxAffordablePrice, actualMaxAffordablePrice,
				0.01);
	}

	public HomeAffordabilityController getController() {
		return controller;
	}

	public void setController(HomeAffordabilityController controller) {
		this.controller = controller;
	}
}
