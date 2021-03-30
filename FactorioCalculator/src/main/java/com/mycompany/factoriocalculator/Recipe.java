package com.mycompany.factoriocalculator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.bson.Document;

/**
 * Represents a Recipe Object.
 *
 * @author Matthew
 */
public class Recipe {

    private String name;
    private String displayName;
    private String category;
    private double energyRequired;
    private Map<String, Integer> ingredients = new HashMap<>();
    private Map<String, Integer> results = new HashMap<>();
    private Optional<Double> expensiveEnergyRequired;
    private Optional<Map<String, Integer>> expensiveIngredients;
    private Optional<Map<String, Integer>> expensiveResults;

    public Recipe(Document recipeDocument) {
        name = (String) recipeDocument.get("name");
        displayName = (String) recipeDocument.get("display_name");
        category = (String) recipeDocument.get("category");
        energyRequired = (double) recipeDocument.get("energy_required");
        for (Resource ingredient : (Resource[]) recipeDocument.get("ingredients")) {
            ingredients.put(ingredient.name, ingredient.amount);
        }
        parseResult(recipeDocument);
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }
    
    public String getCategory() {
        return category;
    }

    public double getEnergyRequired() {
        return energyRequired;
    }

    public Map<String, Integer> getIngredients() {
        Map<String, Integer> ingredientsCopy = new HashMap<>(ingredients);
        return ingredientsCopy;
    }

    public Map<String, Integer> getResults() {
        Map<String, Integer> resultsCopy = new HashMap<>(results);
        return resultsCopy;
    }

    private void parseResult(Document recipeDocument) {
        if (recipeDocument.containsKey("result")) {
            Resource result = (Resource) recipeDocument.get("result");
            results.put(result.name, result.amount);
        } else {
            for (Resource result : (Resource[]) recipeDocument.get("results")) {
                results.put(result.name, result.amount);
            }
        }
    }
    
    // visible for testing
    public static class Resource {

        String name;
        int amount;
        double probability;  

        public Resource(String name, int amount) {
            this(name, amount, 1);
        }
        public Resource(String name, int amount, double probability) {
            this.name = name;
            this.amount = amount;
            this.probability = probability;
        }
    }
}
