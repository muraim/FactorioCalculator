package com.mycompany.factoriocalculator;

import com.mongodb.MongoSecurityException;
import com.mycompany.factoriocalculator.ResourceCalculator.RecipeParams;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;

/**
 * Test file for the ResourceCalculator
 *
 * @author Matthew
 */
public class ResourceCalculatorTest {

    private static DatabaseClient client = new DatabaseClient(
            "ReadOnlyUser", 
            "ReadOnlyPassword");

    @Test
    public void testCalculateRate() {
        System.out.println("calculateRate");
        int numberOfFactories = 7;
        double energyRequired = 3.2;
        double craftingSpeed = 1;
        int resultCount = 1;
        double probability = 1;
        ResourceCalculator instance = new ResourceCalculator(client);
        double expResult = 2.1875;
        double result = instance.calculateRate(
                numberOfFactories,
                energyRequired,
                craftingSpeed,
                resultCount,
                probability);
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of buildRecipeTree method, of class ResourceCalculator.
     */
    @Test
    public void testBuildRecipeTree() {
        System.out.println("buildRecipeTree");
        RecipeParams params = new RecipeParams();
        String name = "sulfuric-acid";
        ResourceCalculator instance = new ResourceCalculator(client);
        JSONObject RecipeTree = instance.buildRecipeTree(name, params);
        double expResult = 1.0;
        double result = ((Number)RecipeTree.get("energy_required")).doubleValue();
        System.out.println(result);
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of getDefaultCraftingSpeed method, of class ResourceCalculator.
     */
    @Test
    public void testGetDefaultCraftingSpeed() {
        System.out.println("getDefaultCraftingSpeed");
        String category = "crafting-with-fluid";
        ResourceCalculator instance = new ResourceCalculator(client);
        double expResult = 0.75;
        double result = instance.getDefaultCraftingSpeed(category);
        assertEquals(expResult, result, 0.0);
    }

    /**
     * Test of calculateNumberOfFactories method, of class ResourceCalculator.
     */
    @Test
    public void testCalculateNumberOfFactories() {
        System.out.println("calculateNumberOfFactories");
        double rate = 0.25;
        double energyRequired = 6.0;
        double craftingSpeed = 0.5;
        int resultCount = 1;
        double probability = 1.0;
        ResourceCalculator instance = new ResourceCalculator(client);
        double expResult = 3.0;
        double result = instance.calculateNumberOfFactories(
                rate,
                energyRequired,
                craftingSpeed,
                resultCount,
                probability);
        assertEquals(expResult, result, 0.0);
    }

}
