package com.mycompany.factoriocalculator;

import com.mongodb.MongoException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import java.util.HashMap;
import java.util.Map;
import org.bson.Document;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for DatabaseClient
 *
 * @author Matthew
 */
public class DatabaseClientTest {

    private static final String RESOURCE_COLLECTION_NAME = "TestResources";
    private static final String TEST_USERNAME = "TestUser";
    private static final String TEST_PASSWORD = "TestPassword";
    private DatabaseClient testClient;

    @Before
    public void setUp() {
        testClient = new DatabaseClient(TEST_USERNAME, TEST_PASSWORD);
        Map<String, Object> testItem = new HashMap<>();
        testItem.put("name", "test-item");
        insertResource(testItem);
        System.out.println("SetUP");
    }

    @After
    public void tearDown() {
        clearResourceCollection();
        System.out.println("teardown");
    }

    @Test
    public void testQueryByName() {
        Document result
                = testClient.queryByName("test-item", RESOURCE_COLLECTION_NAME);
        assertEquals("test-item", result.get("name"));
    }

    @Test
    public void testQueryByName_wrongName() {
        Document result
                = testClient.queryByName("wrong-item", RESOURCE_COLLECTION_NAME);
        assertEquals(null, result);
    }

    private void insertResource(Map<String, Object> fields) {
        MongoCollection<Document> resources
                = testClient.getCollection(RESOURCE_COLLECTION_NAME);
        try {
            resources.insertOne(new Document(fields));

        } catch (MongoException e) {
            fail("MongoException while inserting item");
        }
    }

    private void clearResourceCollection() {
        MongoCollection<Document> resources
                = testClient.getCollection(RESOURCE_COLLECTION_NAME);
        resources.deleteMany(new Document());
    }
}
