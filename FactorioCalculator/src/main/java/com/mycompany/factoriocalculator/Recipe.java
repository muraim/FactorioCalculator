package com.mycompany.factoriocalculator;

import java.util.ArrayList;
import java.util.List;
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
    private List<Resource> ingredients = new ArrayList<>();
    private List<Resource> results = new ArrayList<>();
    private Optional<Double> expensiveEnergyRequired;
    private Optional<List<Resource>> expensiveIngredients;
    private Optional<List<Resource>> expensiveResults;

    public Recipe(Document recipeDocument) {
        name = (String) recipeDocument.get("name");
        displayName = (String) recipeDocument.get("display_name");
        category = (String) recipeDocument.get("category");
        if (recipeDocument.containsKey("normal")) {
            parseCosts((Map<String, Object>) recipeDocument.get("normal"));
            parseExpensiveCosts(
                    (Map<String, Object>) recipeDocument.get("expensive"));
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

    public List<Resource> getIngredients() {
        List<Resource> ingredientsCopy = new ArrayList<>(ingredients);
        return ingredientsCopy;
    }

    /**
     *
     * @return
     */
    public List<Resource> getResults() {
        List<Resource> resultsCopy = new ArrayList<>(results);
        return resultsCopy;
    }

    public Optional<Double> getExpensiveEnergyRequired() {
        return expensiveEnergyRequired;
    }

    public Optional<List<Resource>> getExpensiveIngredients() {
        if (expensiveIngredients.isPresent()) {
            List<Resource> expensiveIngredientsCopy
                    = new ArrayList<>(expensiveIngredients.get());
            return Optional.of(expensiveIngredientsCopy);
        }
        return Optional.empty();
    }

    public Optional<List<Resource>> getExpensiveResults() {
        if (expensiveResults.isPresent()) {
            List<Resource> expensiveResultsCopy
                    = new ArrayList<>(expensiveResults.get());
            return Optional.of(expensiveResultsCopy);
        }
        return Optional.empty();
    }

    private void parseCosts(Map<String, Object> recipeDocument) {
        Number nEnergyRequired = (Number) recipeDocument.get("energy_required");
        energyRequired = nEnergyRequired.doubleValue();
        List<Document> ingredientList = (List<Document>) recipeDocument.get("ingredients");
        for (Document ingredientDocument : ingredientList) {
            ingredients.add(new Resource(ingredientDocument));
        }
        if (recipeDocument.containsKey("result")) {
            Resource result = new Resource((Document) recipeDocument.get("result"));
            results.add(result);
        } else {
            List<Document> resultsList = (List<Document>) recipeDocument.get("results");
            for (Document resultsDocument : resultsList) {
                results.add(new Resource(resultsDocument));
            }
        }
    }

    private void parseExpensiveCosts(Map<String, Object> recipeDocument) {
        Number nExEnergyRequired = (Number) recipeDocument.get("energy_required");
        expensiveEnergyRequired = Optional.of(nExEnergyRequired.doubleValue());
        List<Resource> expensiveIngredientsList = new ArrayList<>();
        List<Document> ingredientList = (List<Document>) recipeDocument.get("ingredients");
        for (Document ingredientDocument : ingredientList) {
            expensiveIngredientsList.add(new Resource(ingredientDocument));
        }
        expensiveIngredients = Optional.of(expensiveIngredientsList);
        if (recipeDocument.containsKey("result")) {
            Resource result = new Resource((Document) recipeDocument.get("result"));
            List<Resource> resultsList = new ArrayList<>();
            resultsList.add(result);
            expensiveResults = Optional.of(resultsList);
        } else {
            List<Resource> expensiveResultsList = new ArrayList<>();
            List<Document> resultsList = (List<Document>) recipeDocument.get("results");
            for (Document resultsDocument : resultsList) {
                expensiveResultsList.add(new Resource(resultsDocument));
            }
            expensiveResults = Optional.of(expensiveResultsList);
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

        public Resource(Document resourceDocument) {
            this.name = (String) resourceDocument.get("name");
            this.amount = ((Number) resourceDocument.get("amount")).intValue();
            if (resourceDocument.containsKey("probability")) {
                this.probability = ((Number) resourceDocument.get("probability")).doubleValue();
            } else {
                this.probability = 1;
            }
        }

        @Override
        public String toString() {
            StringBuilder writer = new StringBuilder();
            writer.append("{\"name\": ");
            writer.append("\"" + name + "\"");
            writer.append(", \"amount\": ");
            writer.append(amount);
            writer.append(", \"probability\": ");
            writer.append(probability);
            writer.append("}");
            return writer.toString();
        }
    }
}
