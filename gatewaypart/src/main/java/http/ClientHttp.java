package http;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class ClientHttp {
    private int clientId;
    private int clientAddress;

    public static void main(String[] args) {
        boolean isClientOn = true;
        while (isClientOn) {
            System.out.println("WELCOME TO YOUR CONSOLE-DRIVE!");
            Scanner userInput = new Scanner(System.in);  // Create a Scanner object
            System.out.println("Enter what you would like to do:\n" +
                    "1. Create user\n" +
                    "2. Get user\n" +
                    "3. Get all files\n" +
                    "4. Create a new file\n" +
                    "5. Get a certain file\n" +
                    "6. Update a file\n" +
                    "7. Delete a file\n" +
                    "8. Exit");

            int userAnswer = userInput.nextInt();  // Read user input

            switch (userAnswer){
                case(1):

            }
        }

    }
}
