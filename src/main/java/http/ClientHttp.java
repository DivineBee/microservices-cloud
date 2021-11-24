package http;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import static http.HTTPListener.os;
import static http.HTTPListener.sendLog;

public class ClientHttp {
    public static int failureCount;
    private static final int clientId = ThreadLocalRandom.current().nextInt(1, 1000 + 1);

    private static final String GATEWAY_API_URL = "http://localhost:5001/api/requests.json";

    /**
     * Client main method with requests being sent from console
     * @param args
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        sendLog(os,"{\"message\": \"Client Connected\"," +
                " \"status\":\"OK\"}");
        HttpClient httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofMinutes(2))
                .executor(Executors.newFixedThreadPool(3))
                .build();

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
                    "8. Exit\n");
            //     "--------------------\n" +
            // "9. Administer Cache");

            int userAnswer = userInput.nextInt();  // Read user input

            switch (userAnswer) {
                case (1):
                    System.out.println("Input user's email");
                    String email = userInput.next();
                    System.out.println("Input user's name");
                    String name = userInput.next();
                    String json = "{" +
                            "\"email\":" + "\"" + email + "\"," +
                            "\"name\":" + "\"" + name + "\"," +
                            "\"clientId\":" + "\"" + clientId + "\"," +
                            "\"command\":" + "\"1\"" +
                            "}";

                    sendClientRequest(json, httpClient);
                    break;

                case (2):
                    System.out.println("Input user's id");
                    String user_id = userInput.next();

                    json = "{" +
                            "\"userId\":" + "\"" + user_id + "\"," +
                            "\"clientId\":" + "\"" + clientId + "\"," +
                            "\"command\":" + "\"2\"" +
                            "}";

                    sendClientRequest(json, httpClient);
                    break;

                case (3):
                    String allFiles = "getfiles";
                    json = "{" +
                            "\"allFiles\":" + "\"" + allFiles + "\"," +
                            "\"clientId\":" + "\"" + clientId + "\"," +
                            "\"command\":" + "\"3\"" +
                            "}";

                    sendClientRequest(json, httpClient);
                    break;

                case (4):
                    System.out.println("Input file's title");
                    String title = userInput.next();
                    System.out.println("Input file's text");
                    String text = userInput.next();
                    System.out.println("Input file's user id");
                    user_id = userInput.next();

                    json = "{" +
                            "\"title\":" + "\"" + title + "\"," +
                            "\"text\":" + "\"" + text + "\"," +
                            "\"userId\":" + "\"" + user_id + "\"," +
                            "\"clientId\":" + "\"" + clientId + "\"," +
                            "\"command\":" + "\"4\"" +
                            "}";

                    sendClientRequest(json, httpClient);
                    break;

                case (5):
                    System.out.println("Input file's id");
                    String file_id = userInput.next();

                    json = "{" +
                            "\"fileId\":" + "\"" + file_id + "\"," +
                            "\"clientId\":" + "\"" + clientId + "\"," +
                            "\"command\":" + "\"5\"" +
                            "}";

                    sendClientRequest(json, httpClient);
                    break;

                case (6):
                    System.out.println("Update file's fields title");
                    title = userInput.next();
                    System.out.println("Update file's text");
                    text = userInput.next();

                    json = "{" +
                            "\"title\":" + "\"" + title + "\"," +
                            "\"text\":" + "\"" + text + "\"," +
                            "\"clientId\":" + "\"" + clientId + "\"," +
                            "\"command\":" + "\"6\"" +
                            "}";

                    sendClientRequest(json, httpClient);
                    break;

                case (7):
                    System.out.println("Which file to delete? Enter id");
                    file_id = userInput.next();

                    json = "{" +
                            "\"fileId\":" + "\"" + file_id + "\"," +
                            "\"clientId\":" + "\"" + clientId + "\"," +
                            "\"command\":" + "\"7\"" +
                            "}";

                    sendClientRequest(json, httpClient);
                    break;
                case (8):
                    isClientOn = false;
                    break;

                case (9):
                    String enterCache = "cache";
                    json = "{" +
                            "\"enterCache\":" + "\"" + enterCache + "\"," +
                            "\"clientId\":" + "\"" + clientId + "\"," +
                            "\"command\":" + "\"9\"" +
                            "}";

                    sendClientRequest(json, httpClient);
                    break;
            }
        }
    }

    /**
     * each client request is build and sent as a post request to the gateway
     * @param json
     * @param httpClient
     */
    private static void sendClientRequest(String json, HttpClient httpClient) {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(URI.create(GATEWAY_API_URL))
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .exceptionally(e -> "Error: " + e.getMessage())
                .thenAccept(System.out::println);
    }

    /**
     * Method of getting status code, response, body and headers
     * @param request
     * @param httpClient
     * @throws IOException
     * @throws InterruptedException
     */
    private static void getResponse(HttpRequest request, HttpClient httpClient) throws IOException, InterruptedException {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("Status code " + response.statusCode()); // print status code
        System.out.println("Response body " + response.body()); // print response body
        System.out.println("Headers " + response.headers()); // print response headers
    }
}