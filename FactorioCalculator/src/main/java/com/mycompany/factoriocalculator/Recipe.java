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
        if (recipeDocument.containsKey("normal")) {
            parseCosts((Map<String, Object>) recipeDocument.get("normal"));
            parseExpensiveCosts((Map<String, Object>) recipeDocument.get("expensive"));
        } else {
            parseCosts(recipeDocument);
            expensiveEnergyRequired = Optional.empty();
            expensiveIngredients = Optional.empty();
            expensiveResults = Optional.empty();
        }
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

    public Optional<Double> getExpensiveEnergyRequired() {
        return expensiveEnergyRequired;
    }

    public Optional<Map<String, Integer>> getExpensiveIngredients() {
        if(expensiveIngredients.isPresent()){
            Map<String, Integer> expensiveIngredientsCopy
                    = new HashMap<>(expensiveIngredients.get());
            return Optional.of(expensiveIngredientsCopy);
        }
        return Optional.empty();
    }

    public Optional<Map<String, Integer>> getExpensiveResults() {
        if(expensiveResults.isPresent()){
            Map<String, Integer> expensiveResultsCopy 
                    = new HashMap<>(expensiveResults.get());
            return Optional.of(expensiveResultsCopy);
        }
        return Optional.empty();
    }

    private void parseCosts(Map<String, Object> recipeDocument) {
        energyRequired = (double) recipeDocument.get("energy_required");
        for (Resource ingredient : (Resource[]) recipeDocument.get("ingredients")) {
            ingredients.put(ingredient.name, ingredient.amount);
        }
        if (recipeDocument.containsKey("result")) {
            Resource result = (Resource) recipeDocument.get("result");
            results.put(result.name, result.amount);
        } else {
            for (Resource result : (Resource[]) recipeDocument.get("results")) {
                results.put(result.name, result.amount);
            }
        }
    }

    private void parseExpensiveCosts(Map<String, Object> recipeDocument) {
        expensiveEnergyRequired 
                = Optional.of((double) recipeDocument.get("energy_required"));
        Map<String, Integer> ingredientMap = new HashMap<>();
        for (Resource ingredient : (Resource[]) recipeDocument.get("ingredients")) {
            ingredientMap.put(ingredient.name, ingredient.amount);
        }
        expensiveIngredients = Optional.of(ingredientMap);
        Map<String, Integer> resultMap = new HashMap<>();
        if (recipeDocument.containsKey("result")) {
            Resource result = (Resource) recipeDocument.get("result");
            resultMap.put(result.name, result.amount);
        } else {
            for (Resource result : (Resource[]) recipeDocument.get("results")) {
                resultMap.put(result.name, result.amount);
            }
        }
        expensiveResults = Optional.of(resultMap);
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
