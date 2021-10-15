package http;

import cache.Cache;
import cache.CacheItem;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RequestParser {
    private static final String USER_API_URL = "http://localhost:8080/api/v1/user/";
    private static final String USER_API_URL2 = "http://localhost:9090/api/v2/user/";
    private static final String DOCS_API_URL = "http://localhost:8080/api/v1/docs/";
    private static final String DOCS_API_URL2 = "http://localhost:9090/api/v2/docs/";

    // primitive of service registry
    private static final ArrayList<String> addressUserPool = new ArrayList <>();
    static {
        addressUserPool.add(USER_API_URL);
        addressUserPool.add(USER_API_URL2);
    };

    public static final ArrayList<String> addressDocPool = new ArrayList <>();
    static {
        addressDocPool.add(DOCS_API_URL);
        addressDocPool.add(DOCS_API_URL2);
    };

    private static Cache<String, HashMap<String, String>> requestsCache = new Cache<>(400);
    private static HashMap<String, String> allowedUsers = new HashMap<>();
    static {
        allowedUsers.put("sam01admin", "Seer23");
        allowedUsers.put("jessica02admin", "Hunter23");
    }

    /**
     * Helper method of processing client's request, such as reading its contents and parsing.
     * Because client sends a specific request, the gateway has to decide what to do with it.
     * This is done through the reading of incoming JSON data. It's decomposed and based on the
     * client command the next steps are performed. Such as extracting useful information to add to
     * the uri path, to pick the available server, to insert and retrieve from the cache.
     * After that the method returns a hashmap back to the caller.
     * @param responseBody
     * @return HashMap<String, String>
     * @throws IOException
     * @throws InterruptedException
     * @throws ExecutionException
     * @throws TimeoutException
     */
    public static HashMap<String, String> processRequest(String responseBody) throws IOException, InterruptedException, ExecutionException, TimeoutException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request;

        System.out.println("response body" + responseBody);
        //check which command then proceed
        final JSONObject body = new JSONObject(responseBody);
        int userCommand = body.getInt("command");
        int clientId = body.getInt("clientId");

        if (userCommand == 1){

        } else if (userCommand == 2){
            int userId = body.getInt("userId");
            String roundedAddress = HTTPListener.getIp(addressUserPool);
            request = HttpRequest.newBuilder().uri(URI.create(roundedAddress + userId)).build();

            String requestUri = request.uri().toString();

            if (requestsCache.get(requestUri) != null){
                System.out.println("HI FROM CACHE");
                return requestsCache.get(requestUri);
            } else {
                CompletableFuture<HttpResponse<String>> response =
                        client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

                HashMap<String, String> result = response.thenApply(HttpResponse::body)
                        .thenApply(RequestParser::parseUser)
                        .get(5, TimeUnit.SECONDS);

                System.out.println("result " + result);

                requestsCache.put(requestUri, result);
                System.out.println("HI I JUST JOINED THE CACHE");
                return result;
            }
        } else if (userCommand == 3){
            String roundedAddress = HTTPListener.getIp(addressDocPool);
            request = HttpRequest.newBuilder().uri(URI.create(roundedAddress))
                    .version(HttpClient.Version.HTTP_1_1).build();
            String requestUri = request.uri().toString();

            if (requestsCache.get(requestUri) != null){
                System.out.println("HI FROM CACHE");
                return requestsCache.get(requestUri);
            } else {
                CompletableFuture<HttpResponse<String>> response =
                        client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

                HashMap<String, String> result = response.thenApply(HttpResponse::body)
                        .thenApply(RequestParser::parse)
                        .get(5, TimeUnit.SECONDS);

                System.out.println("result " + result);

                requestsCache.put(requestUri, result);
                System.out.println("HI I JUST JOINED THE CACHE");
                return result;
            }
        } else if (userCommand == 9){
            String query = queryCache(requestsCache);
            System.out.println(query);
//                CompletableFuture<HttpResponse<String>> response =
//                        client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
//
//                HashMap<String, String> result = response.thenApply(HttpResponse::body)
//                        .thenApply(RequestParser::parse)
//                        .get(5, TimeUnit.SECONDS);
//
//                System.out.println("result " + result);


                return null;
            }
        return null;
    }

    /**
     * Method for parsing the incoming json for the client
     * @param responseBody
     * @return HashMap<String, String>
     */
    public static HashMap<String, String> parse(String responseBody) {
        HashMap<String, String> result = new HashMap();
        JSONArray docs = new JSONArray(responseBody);
        String title = null;
        String text = null;
        for (int i = 0; i < docs.length(); i++) {
            JSONObject doc = docs.getJSONObject(i);
//            int id = doc.getInt("id");
//            JSONArray user_id = doc.getJSONArray("user_id");
            title = doc.getString("title");
            text = doc.getString("text");
            result.put(title, text);
            System.out.println(title + " " + text);
        }
        return result;
    }

    public static HashMap<String, String> parseUser(String responseBody) {
        HashMap<String, String> result = new HashMap();
        System.out.println("r" + responseBody);
        JSONObject user = new JSONObject(responseBody);

        String name = null;
        String email = null;
        name = user.getString("name");
        email = user.getString("text");
        result.put(name, email);
        System.out.println(name + " " + email);

        return result;
    }

    public static String queryCache(Cache<String, HashMap<String, String>> cache){
        boolean isUserAuthenticated = authenticateUserCache();
        while (isUserAuthenticated) {
            Scanner userInput = new Scanner(System.in);
            System.out.println("Insert query:");
            String answer = userInput.nextLine();
        // Insert data to cache
            if (answer.contains("put")) {
                System.out.println("Insert key");
                String key = userInput.next();
                System.out.println("Insert value");
                String value = userInput.next();
                HashMap<String, String> valueMap = new HashMap<>();
                valueMap.put(key, value);
                cache.put(key, valueMap);
                System.out.println("Inserted successfully");
                return "Inserted successfully";
            } else if (answer.contains("get")) {
                System.out.println("Insert key");
                String key = userInput.next();
                System.out.println("Here is the cache value:" + cache.get(key));
            } else if (answer.contains("delete")){
                System.out.println("Insert what node to delete");
                String key = userInput.next();
                CacheItem node = cache.getMap().get(key);
                cache.deleteNode(node);
                System.out.println("Node deleted successfully");
                return "Node deleted successfully";
            } else if (answer.contains("size")){
                System.out.println("Cache size is: " + cache.size());
                return "Cache size is: " + cache.size();
            } else if (answer.contains("display")){
                cache.getMap().entrySet().forEach(entry -> {
                    System.out.println(entry.getKey() + " " + entry.getValue());
                });
            } else if (answer.contains("exit")) {
                isUserAuthenticated = false;
            }
        }
        return null;
    }

    public static boolean authenticateUserCache(){
        boolean isAuthenticated = false;
        Scanner userInput = new Scanner(System.in);
        System.out.println("Please insert username");
        String username = userInput.next();

        if (allowedUsers.containsKey(username)) {
            System.out.println("Please insert password");
            String password = userInput.next();
            if (allowedUsers.get(username).equals(password)){
                return isAuthenticated = true;
            }
        }
        return false;
    }
}
