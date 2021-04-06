package com.mycompany.factoriocalculator;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test file for the ResourceCalculator
 * @author Matthew
 */
public class ResourceCalculatorTest {

    @Test
    public void testCalculateNumberOfFactories() {
        double rate = 5.0;
        double energyRequired = 0.2;
        double craftingSpeed = 2;
        int resultCount = 1;
        ResourceCalculator instance = new ResourceCalculator();
        int expResult = 1;
        int result = instance.calculateNumberOfFactories(
                rate, 
                energyRequired, 
                craftingSpeed, 
                resultCount);
        assertEquals(expResult, result);
    }

    @Test
    public void testCalculateRate() {
        System.out.println("calculateRate");
        int numberOfFactories = 7;
        double energyRequired = 3.2;
        double craftingSpeed = 1;
        int resultCount = 1;
        ResourceCalculator instance = new ResourceCalculator();
        double expResult = 2.1875;
        double result = instance.calculateRate(
                numberOfFactories,
                energyRequired, 
                craftingSpeed, 
                resultCount);
        assertEquals(expResult, result, 0.0);
    }
}
