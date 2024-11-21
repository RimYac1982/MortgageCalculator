package application.utils;

public class HomeInsuranceCalculator {

    // Assuming the insurance rate is fixed, e.g., $1.25 per $100 of home value
    private static final double INSURANCE_RATE = 0.1;

    /**
     * Calculates the homeowner's insurance cost based on purchase price.
     *
     * @param purchasePrice the purchase price of the home (in dollars)
     * @return the annual insurance cost
     */
    public static double calculateHomeInsurance(double purchasePrice) {
        // Assuming the coverage amount equals the purchase price for the calculation
        double insuranceCost = (purchasePrice / 100/ 12) * INSURANCE_RATE;
        return insuranceCost;
    }

}
