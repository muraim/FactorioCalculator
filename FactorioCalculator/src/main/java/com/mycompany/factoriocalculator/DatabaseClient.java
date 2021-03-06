/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.factoriocalculator;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * Connects to Mongo Database
 * @author Matthew
 */
public class DatabaseClient {

    private static final String CLUSTER_URL
            = "@cluster0.ldu8s.mongodb.net/myFirstDatabase?retryWrites=true&w=majority";
    private static final String DATABASE_NAME = "FactorioData";
    private static final String RESOURCE_COLLECTION_NAME = "Resources";
    private MongoClient mongoClient;
    private MongoDatabase database;
    private MongoCollection<Document> resources;

    public DatabaseClient(String connUser, String connPass) {
        connectToDatabase(connUser, connPass);
    }

    private void connectToDatabase(
            String connectionUser,
            String connectionPass) {
        mongoClient = MongoClients.create("mongodb+srv://" + connectionUser
                + ":" + connectionPass + CLUSTER_URL);
        database = mongoClient.getDatabase(DATABASE_NAME);
        resources = database.getCollection(RESOURCE_COLLECTION_NAME);
    }
}
