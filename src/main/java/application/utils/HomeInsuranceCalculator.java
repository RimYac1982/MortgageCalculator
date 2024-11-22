package application.utils;

public class HomeInsuranceCalculator {
	 
     //The fixed insurance rate used for calculations.
    private static final double INSURANCE_RATE = 0.1;

    /**
     * Calculates the homeowner's insurance cost based on the purchase price.
     *
     * <p>
     * This method computes the annual insurance cost by applying a fixed
     * percentage rate to the purchase price, divided into monthly premiums.
     * </p>
     *
     * @param purchasePrice the purchase price of the home (in dollars)
     * @return the annual insurance cost (in dollars)
     */
    public static double calculateHomeInsurance(double purchasePrice) {
        double insuranceCost = (purchasePrice / 100 / 12) * INSURANCE_RATE;
        return insuranceCost;
    }
}