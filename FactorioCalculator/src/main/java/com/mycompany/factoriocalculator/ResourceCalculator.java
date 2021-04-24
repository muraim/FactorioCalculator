package com.mycompany.factoriocalculator;

import com.google.common.collect.ImmutableSet;
import com.mongodb.client.FindIterable;
import com.mycompany.factoriocalculator.Recipe.Resource;
import java.util.List;
import java.util.Set;
import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Performs calculations for Rate and number of factories
 *
 * @author Matthew
 */
public class ResourceCalculator {

    private static final Set<String> RAW_MATERIALS = ImmutableSet.of(
            "wood",
            "coal",
            "stone",
            "iron-ore",
            "copper-ore",
            "uranium-ore", // Create uranium ore recipe due to special requirements for harvesting
            "water",
            "crude-oil");
    private DatabaseClient client;

    public ResourceCalculator(DatabaseClient client) {
        this.client = client;
    }

    // TODO include expensive resources, Find the rate of 1 factory worth of production for this resource
    public JSONObject buildRecipeTree(String name, RecipeParams params) {
        if (RAW_MATERIALS.contains(name)) {
            JSONObject rawResourceJSON = new JSONObject();
            rawResourceJSON.put("name", name);
            return rawResourceJSON;
        }
        Recipe root = getRecipe(name, params);
        JSONObject recipeTreeJSON = new JSONObject();
        double numFactories = 1;
        double energyRequired = root.getEnergyRequired();
        double probability = 1;
        String category = root.getCategory();
        double craftingSpeed = getDefaultCraftingSpeed(category);

        recipeTreeJSON.put("name", root.getName());
        recipeTreeJSON.put("display_name", root.getDisplayName());
        recipeTreeJSON.put("category", category);
        recipeTreeJSON.put("energy_required", energyRequired);
        recipeTreeJSON.put("num_factories", numFactories);
        recipeTreeJSON.put("target_result", name);

        JSONArray resultArray = new JSONArray();
        for (Resource result : root.getResults()) {
            JSONObject resultJSON = new JSONObject();
            resultJSON.put("name", result.name);
            resultJSON.put("amount", result.amount);
            resultJSON.put("probability", result.probability);
            if (name.equals(result.name)) {
                probability = result.probability;
            }
            resultJSON.put("rate", calculateRate(
                    numFactories,
                    energyRequired,
                    craftingSpeed,
                    result.amount,
                    result.probability));
            resultArray.add(resultJSON);
        }
        recipeTreeJSON.put("results", resultArray);

        JSONArray ingredientsArray = new JSONArray();
        for (Resource ingredient : root.getIngredients()) {
            double ingredientRate = calculateRate(
                    numFactories,
                    energyRequired,
                    craftingSpeed,
                    ingredient.amount,
                    probability
            );
            if (RAW_MATERIALS.contains(ingredient.name)) {
                JSONObject rawMaterial = new JSONObject();
                rawMaterial.put("name", ingredient.name);
                rawMaterial.put("rate", ingredientRate);
                ingredientsArray.add(rawMaterial);
            } else {
                ingredientsArray.add(buildRecipeTree(ingredient.name, ingredientRate, params));
            }
        }
        recipeTreeJSON.put("ingredients", ingredientsArray);

        return recipeTreeJSON;
    }

    private JSONObject buildRecipeTree(String name, double targetRate, RecipeParams params) {
        if (RAW_MATERIALS.contains(name)) {
            JSONObject rawResourceJSON = new JSONObject();
            rawResourceJSON.put("name", name);
            return rawResourceJSON;
        }
        Recipe recipe = getRecipe(name, params);
        JSONObject recipeTreeJSON = new JSONObject();
        double energyRequired = recipe.getEnergyRequired();
        String category = recipe.getCategory();
        double probability = 1;
        double craftingSpeed = getDefaultCraftingSpeed(category);
        double numFactories = calculateNumberOfFactories(
                targetRate,
                energyRequired,
                craftingSpeed,
                recipe.getResults(),
                name);

        recipeTreeJSON.put("name", recipe.getName());
        recipeTreeJSON.put("display_name", recipe.getDisplayName());
        recipeTreeJSON.put("category", category);
        recipeTreeJSON.put("energy_required", energyRequired);
        recipeTreeJSON.put("target_result", name);

        JSONArray resultArray = new JSONArray();
        for (Resource result : recipe.getResults()) {
            JSONObject resultJSON = new JSONObject();
            resultJSON.put("name", result.name);
            resultJSON.put("amount", result.amount);
            resultJSON.put("probability", result.probability);
            if (name.equals(result.name)) {
                probability = result.probability;
            }
            resultJSON.put("rate", calculateRate(
                    numFactories,
                    energyRequired,
                    craftingSpeed,
                    result.amount,
                    result.probability));
            resultArray.add(resultJSON);
        }
        recipeTreeJSON.put("results", resultArray);
        JSONArray ingredientsArray = new JSONArray();
        for (Resource ingredient : recipe.getIngredients()) {
            double ingredientRate = calculateRate(
                    numFactories,
                    energyRequired,
                    craftingSpeed,
                    ingredient.amount,
                    probability
            );
            if (RAW_MATERIALS.contains(ingredient.name)) {
                JSONObject rawMaterial = new JSONObject();
                rawMaterial.put("name", ingredient.name);
                rawMaterial.put("rate", ingredientRate);
                ingredientsArray.add(rawMaterial);
            } else {
                ingredientsArray.add(buildRecipeTree(
                        ingredient.name,
                        ingredientRate,
                        params));
            }
        }
        recipeTreeJSON.put("ingredients", ingredientsArray);
        return recipeTreeJSON;
    }

    // Visible for testing
    // TODO ITERATE through buildingList to find isFluidDefault Applicable, second if else clause
    public double getDefaultCraftingSpeed(String category) {
        FindIterable<Document> buildingList;
        if (category.equals("crafting-with-fluid")
                || category.equals("crafting")
                || category.equals("advanced-crafting")) {
            // All above categories use basic assembler machines 
            buildingList = client.queryByCategory(
                    "basic",
                    DatabaseClient.BUILDING_COLLECTION_NAME);
        } else {
            buildingList = client.queryByCategory(
                    category,
                    DatabaseClient.BUILDING_COLLECTION_NAME);
        }

        Document building = new Document();
        if (category.equals("crafting-with-fluid")) {
            building = buildingList.filter(
                    new Document("is_fluid_default", true)).first();

        } else {
            for (Document buildings : buildingList) {
                if (buildings.get("is_default", false)) {
                    return ((Number) buildings.get("crafting_speed")).doubleValue();
                }
            }
        }
        return ((Number) building.get("crafting_speed")).doubleValue();
    }

//    public double getCraftingSpeed(){
//        
//    }
//    
    // TODO parse multi-path resources (i.e. Petroleum gas -> Advanced Oil Processing)
    private Recipe getRecipe(String name, RecipeParams params) {
        if (params.useKovarex()
                && !params.isUraniumRecipe()
                && name.equals("uranium-235")) {
            // IsUraniumRecipe set to true to prevent infinite looping
            params.setIsUraniumRecipe(true);
            return new Recipe(client.queryByName(
                    "kovarex-enrichment-process",
                    DatabaseClient.RESOURCE_COLLECTION_NAME));
        } else if (name.equals("uranium-235")) {
            // IsUraniumRecipe set to false, exiting uranium recipe loop
            params.setIsUraniumRecipe(false);
            return new Recipe(client.queryByName(
                    "uranium-processing",
                    DatabaseClient.RESOURCE_COLLECTION_NAME));
        } else if (name.equals("uranium-238")) {
            return new Recipe(client.queryByName(
                    "uranium-processing",
                    DatabaseClient.RESOURCE_COLLECTION_NAME));
        } else if (name.equals(
                "petroleum-gas")
                || name.equals("light-oil")
                || name.equals("heavy-oil")) {
            switch (params.getOilProcessingType()) {
                case BASIC:
                    if (name.equals("petroleum-gas")) {
                        return new Recipe(client.queryByName(
                                "basic-oil-processing",
                                DatabaseClient.RESOURCE_COLLECTION_NAME));
                    }
                // else fall through
                case ADVANCED:
                    return new Recipe(client.queryByName(
                            "advanced-oil-processing",
                            DatabaseClient.RESOURCE_COLLECTION_NAME));
                case COAL_LIQUEFACTION:
                    return new Recipe(client.queryByName(
                            "coal-liquefaction",
                            DatabaseClient.RESOURCE_COLLECTION_NAME));
                default:
                    return new Recipe(client.queryByName(
                            "advanced-oil-processing",
                            DatabaseClient.RESOURCE_COLLECTION_NAME));
            }
        } else {
            return new Recipe(
                    client.queryByName(
                            name,
                            DatabaseClient.RESOURCE_COLLECTION_NAME));
        }
    }

    /**
     * Returns the number of factories required from the given parameters
     *
     * @param rate the total number of items produced per second from all
     * factories
     * @param energyRequired the amount of time in seconds required for one
     * recipe cycle
     * @param craftingSpeed variable dependent on building, modifies time for
     * recipe cycle
     * @param resultCount the number of items produced in a single recipe cycle
     * @return the minimum required number of factories to maintain given rate
     */
    public double calculateNumberOfFactories(
            double rate,
            double energyRequired,
            double craftingSpeed,
            int resultCount,
            double probability) {
        return (rate * energyRequired)
                / (craftingSpeed * resultCount * probability);
    }

    public double calculateNumberOfFactories(
            double rate,
            double energyRequired,
            double craftingSpeed,
            List<Resource> resources,
            String targetName)
            throws RuntimeException {
        for (Resource resource : resources) {
            if (resource.name.equals(targetName)) {
                return calculateNumberOfFactories(
                        rate,
                        energyRequired,
                        craftingSpeed,
                        resource.amount,
                        resource.probability);
            }
        }
        throw new RuntimeException("Target resource " + targetName + " not found.");
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
            double numberOfFactories,
            double energyRequired,
            double craftingSpeed,
            int resultCount,
            double probability
    ) {
        return (double) (numberOfFactories * resultCount * probability * craftingSpeed)
                / energyRequired;
    }

    public static class RecipeParams {

        private boolean isExpensive;
        private boolean useKovarex;
        private boolean isUraniumRecipe;
        private OilProcessingType oilProcessingType;

        enum OilProcessingType {
            BASIC,
            ADVANCED,
            COAL_LIQUEFACTION
        }

        public RecipeParams() {
            this.isExpensive = false;
            this.useKovarex = true;
            this.isUraniumRecipe = false;
            this.oilProcessingType = OilProcessingType.ADVANCED;
        }

        public RecipeParams(
                boolean isExpensive,
                boolean useKovarex,
                OilProcessingType oilProcessingType) {
            this.isExpensive = isExpensive;
            this.useKovarex = useKovarex;
            this.isUraniumRecipe = false;
            this.oilProcessingType = oilProcessingType;
        }

        public boolean isExpensive() {
            return isExpensive;
        }

        public void setIsExpensive(boolean isExpensive) {
            this.isExpensive = isExpensive;
        }

        public boolean useKovarex() {
            return useKovarex;
        }

        public void setUseKovarex(boolean useKovarex) {
            this.useKovarex = useKovarex;
        }

        public boolean isUraniumRecipe() {
            return isUraniumRecipe;
        }

        public void setIsUraniumRecipe(boolean isUraniumRecipe) {
            this.isUraniumRecipe = isUraniumRecipe;
        }

        public OilProcessingType getOilProcessingType() {
            return oilProcessingType;
        }

        public void setOilProcessingType(OilProcessingType oilProcessingType) {
            this.oilProcessingType = oilProcessingType;
        }

    }
}
