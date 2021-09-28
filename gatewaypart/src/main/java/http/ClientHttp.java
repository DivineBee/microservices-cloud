package http;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Scanner;

public class ClientHttp {
    private int clientId;
    private int clientAddress;

    private static final String GATEWAY_API_URL = "http://localhost:5001/api/requests.json";

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofMinutes(2))
            .build();

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
                    System.out.println("Input user's email and name");
                    String email = userInput.nextLine();
                    String name = userInput.nextLine();

                    String json = new StringBuilder()
                            .append("{")
                            .append("\"email\":" + "\"" + email + "\"" +",")
                            .append("\"name\":" + "\"" + name + "\"")
                            .append("}").toString();

                    sendClientRequest(json);
                    break;

                case(2):
                    System.out.println("Input user's id");
                    int user_id = userInput.nextInt();

                    // send user id as path
                    break;

                case(3):
                    String allFiles = "getfiles";

                    // send all files as path
                    break;

                case(4):
                    System.out.println("Input file's title, text, user id");
                    String title = userInput.nextLine();
                    String text = userInput.nextLine();
                    user_id = userInput.nextInt();

                    json = new StringBuilder()
                            .append("{")
                            .append("\"title\":" + "\"" + title + "\"" + ",")
                            .append("\"text\":" + "\"" + text + "\"" + ",")
                            .append("\"user_id\":" + "\"" + user_id + "\"")
                            .append("}").toString();

                    sendClientRequest(json);
                    break;

                case(5):
                    System.out.println("Input file's id");
                    int file_id = userInput.nextInt();

                    // send file id as path
                    break;
                case(6):
                    System.out.println("Update file's fields title, text");
                    title = userInput.nextLine();
                    text = userInput.nextLine();

                    json = new StringBuilder()
                            .append("{")
                            .append("\"title\":" + "\"" + title + "\"" + ",")
                            .append("\"text\":" + "\"" + text + "\"")
                            .append("}").toString();

                    sendClientRequest(json);
                    System.out.println();
                    break;

                case(7):
                    System.out.println("Which file to delete? Enter id");
                    file_id = userInput.nextInt();

                    // send as path
                    break;
                case(8):
                    isClientOn = false;
            }
        }

    }

    private static HttpRequest sendClientRequest(String json){
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create(GATEWAY_API_URL))
                .setHeader("User-Agent", "Java 11 HttpClient") // add request header
                .header("Content-Type", "application/json")
                .build();
        return request;
    }
}
