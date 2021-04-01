package com.mycompany.factoriocalculator;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import org.bson.Document;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit test of Recipe class
 *
 * @author Matthew
 */
public class RecipeTest {

    private Recipe testRecipe;
    private Recipe testExpensiveRecipe;

    @Before
    public void setUp() {

        testRecipe = new Recipe(createRecipeDocument());
        testExpensiveRecipe = new Recipe(createExpensiveRecipeDocument());
    }

    @Test
    public void testGetName() {
        assertEquals("recipe name", testRecipe.getName());
        assertEquals("recipe name", testExpensiveRecipe.getName());
    }

    @Test
    public void testGetDisplayName() {
        assertEquals("display", testRecipe.getDisplayName());
        assertEquals("display", testExpensiveRecipe.getDisplayName());
    }
    
    @Test
    public void testGetCategory(){
        assertEquals("basic", testRecipe.getCategory());
        assertEquals("expensive", testExpensiveRecipe.getCategory());
    }

    @Test
    public void testGetEnergyRequired() {
        assertEquals(7.0, testRecipe.getEnergyRequired(), 0);
        assertEquals(7.0, testExpensiveRecipe.getEnergyRequired(), 0);
        assertEquals(14.0, testExpensiveRecipe.getExpensiveEnergyRequired().get(), 0);
        assertTrue(testRecipe.getExpensiveEnergyRequired().isEmpty());
    }

    @Test
    public void testGetIngredients() {
        ImmutableMap expectedIngredients
                = ImmutableMap.of(
                        "ingredient_1", 2,
                        "ingredient_2", 7,
                        "ingredient_3", 1);
        ImmutableMap expensiveExpectedIngredients
                = ImmutableMap.of(
                        "ingredient_1", 4,
                        "ingredient_2", 14,
                        "ingredient_3", 2);
        assertEquals(expectedIngredients, testRecipe.getIngredients());
        assertEquals(expectedIngredients, testExpensiveRecipe.getIngredients());
        assertEquals(expensiveExpectedIngredients, 
                testExpensiveRecipe.getExpensiveIngredients().get());
        assertTrue(testRecipe.getExpensiveIngredients().isEmpty());
    }

    @Test
    public void testGetResults() {
        ImmutableMap expectedResults
                = ImmutableMap.of(
                        "result_1", 6,
                        "result_2", 3,
                        "result_3", 52);
         ImmutableMap expensiveExpectedResults
                = ImmutableMap.of(
                        "result_1", 12,
                        "result_2", 6,
                        "result_3", 104);       
        assertEquals(expectedResults, testRecipe.getResults());
        assertEquals(expectedResults, testExpensiveRecipe.getResults());
        assertEquals(expensiveExpectedResults, 
                testExpensiveRecipe.getExpensiveResults().get());
        assertTrue(testRecipe.getExpensiveResults().isEmpty());
    }

    private Document createRecipeDocument() {
        Document testDocument = new Document();
        testDocument.append("name", "recipe name");
        testDocument.append("display_name", "display");
        testDocument.append("category", "basic");
        testDocument.append("energy_required", 7.0);
        Recipe.Resource[] ingredients = {
            new Recipe.Resource("ingredient_1", 2),
            new Recipe.Resource("ingredient_2", 7),
            new Recipe.Resource("ingredient_3", 1)};
        testDocument.append("ingredients", ingredients);
        Recipe.Resource[] results = {
            new Recipe.Resource("result_1", 6),
            new Recipe.Resource("result_2", 3),
            new Recipe.Resource("result_3", 52)};
        testDocument.append("results", results);
        return testDocument;
    }

    private Document createExpensiveRecipeDocument() {
        Document testDocument = new Document();
        testDocument.append("name", "recipe name");
        testDocument.append("display_name", "display");
        testDocument.append("category", "expensive");
        Map<String, Object> normal = new HashMap<>();
        normal.put("energy_required", 7.0);
        Recipe.Resource[] ingredients = {
            new Recipe.Resource("ingredient_1", 2),
            new Recipe.Resource("ingredient_2", 7),
            new Recipe.Resource("ingredient_3", 1)};
        normal.put("ingredients", ingredients);
        Recipe.Resource[] results = {
            new Recipe.Resource("result_1", 6),
            new Recipe.Resource("result_2", 3),
            new Recipe.Resource("result_3", 52)};
        normal.put("results", results);
        testDocument.append("normal", normal);
        Map<String, Object> expensive = new HashMap<>();
        expensive.put("energy_required", 14.0);
        Recipe.Resource[] expensiveIngredients = {
            new Recipe.Resource("ingredient_1", 4),
            new Recipe.Resource("ingredient_2", 14),
            new Recipe.Resource("ingredient_3", 2)};
        expensive.put("ingredients", expensiveIngredients);
        Recipe.Resource[] expensiveResults = {
            new Recipe.Resource("result_1", 12),
            new Recipe.Resource("result_2", 6),
            new Recipe.Resource("result_3", 104)};
        expensive.put("results", expensiveResults);
        testDocument.append("expensive", expensive);
        return testDocument;
    }
}
