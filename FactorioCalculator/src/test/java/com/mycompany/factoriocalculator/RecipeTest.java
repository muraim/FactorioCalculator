package com.mycompany.factoriocalculator;

import com.google.common.collect.ImmutableMap;
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
 * @author Matthew
 */
public class RecipeTest {

    private Recipe testRecipe;

    @Before
    public void setUp() {
        Document testDocument = new Document();
        testDocument.append("name", "recipe name");
        testDocument.append("display_name", "display");
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
        testRecipe = new Recipe(testDocument);

    }

    @Test
    public void testGetName() {
        assertEquals("recipe name", testRecipe.getName());
    }

    @Test
    public void testGetDisplayName() {
        assertEquals("display", testRecipe.getDisplayName());
    }

    @Test
    public void testGetEnergyRequired() {
        assertEquals(7.0, testRecipe.getEnergyRequired(), 0);
    }

    @Test
    public void testGetIngredients() {
        ImmutableMap expectedIngredients
                = ImmutableMap.of(
                        "ingredient_1", 2,
                        "ingredient_2", 7,
                        "ingredient_3", 1);
        assertEquals(expectedIngredients, testRecipe.getIngredients());
    }

    @Test
    public void testGetResults() {
        ImmutableMap expectedResults
                = ImmutableMap.of(
                        "result_1", 6,
                        "result_2", 3,
                        "result_3", 52);
        assertEquals(expectedResults, testRecipe.getResults());
    }
}
