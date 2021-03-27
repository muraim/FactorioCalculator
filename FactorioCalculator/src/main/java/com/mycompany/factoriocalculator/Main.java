/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.factoriocalculator;
import com.mongodb.MongoSecurityException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

/**
 *
 * @author Matthew
 */
@SpringBootApplication(exclude={SecurityAutoConfiguration.class})
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        BufferedReader objReader = null;
        try {
            objReader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Input Username:");
            String username = objReader.readLine();
            System.out.println("Input Password:");
            String password = objReader.readLine();
            //TODO use username and password to connect to database client
            DatabaseClient client = new DatabaseClient(username, password);
            //TODO initialize spring
        } catch (IOException | MongoSecurityException e) {
            System.out.println("ERROR Connecting to Database");
        } finally {

            try {
                if (objReader != null) {
                    objReader.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}




  
  