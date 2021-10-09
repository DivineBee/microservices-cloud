package http;

import cache.Cache;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class RequestParser {
    private static final String USER_API_URL = "http://localhost:8080/api/v1/user/";
    private static final String USER_API_URL2 = "http://localhost:9090/api/v2/user/";
    private static final String DOCS_API_URL = "http://localhost:8080/api/v1/docs/";
    private static final String DOCS_API_URL2 = "http://localhost:9090/api/v2/docs/";

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
            request = HttpRequest.newBuilder().uri(URI.create(USER_API_URL + userId)).build();

            client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    //.thenApply(Connection::parse)
                    .thenAccept(System.out::println)
                    .join();

            return null;
        } else if (userCommand == 3){
            String roundedAddress = HTTPListener.getIp(addressDocPool);
            request = HttpRequest.newBuilder().uri(URI.create(roundedAddress)).build();
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
        }
        return null;
    }

    private static void sendClientRequest(String json, HttpClient httpClient, HttpRequest request){
        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .exceptionally(e -> "Error: " + e.getMessage())
                .thenAccept(System.out::println);
    }

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
        System.out.println("RESULT" + result);
        return result;
    }
}
