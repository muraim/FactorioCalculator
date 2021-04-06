package com.mycompany.factoriocalculator;

/**
 * Performs calculations for Rate and number of factories
 * @author Matthew
 */
public class ResourceCalculator {

    /**
     * Returns the integer number of factories required from the given
     * parameters
     *
     * @param rate the total number of items produced per second from all
     * factories
     * @param energyRequired the amount of time in seconds required for one
     * recipe cycle
     * @param craftingSpeed variable dependent on building, modifies time for
     * recipe cycle
     * @param resultCount the number of items produced in a single recipe cycle
     * @return the minimum required number of factories to maintain given rate,
     * number of factories must be whole integer, always round up
     */
    public int calculateNumberOfFactories(
            double rate,
            double energyRequired,
            double craftingSpeed,
            int resultCount) {
        return (int) Math.ceil((rate * energyRequired)
                / (craftingSpeed * resultCount));
    }

    /**
     * Returns the double number rate of production needed to maintain given 
     * number of factories constantly producing
     * 
     * @param numberOfFactories the number of factories maintaining constant
     * production
     * @param energyRequired the amount of time in seconds required for one
     * recipe cycle
     * @param craftingSpeed variable dependent on building, modifies time for
     * recipe cycle
     * @param resultCount the number of items produced in a single recipe cycle
     * @return the total number of items produced per second from all factories
     */
    public double calculateRate(
            int numberOfFactories,
            double energyRequired,
            double craftingSpeed,
            int resultCount) {
        return (double) (numberOfFactories * resultCount * craftingSpeed)
                / energyRequired;
    }
}
