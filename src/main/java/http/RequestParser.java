package http;

import cache.Cache;
import static cache.ConsistentHashing.*;
import circuit.CircuitBreaker;
import circuit.State;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    public static Cache<String, HashMap<String, String>> requestsCache = new Cache<>(400);
    public static Cache<String, HashMap<String, String>> requestsCache2 = new Cache<>(400);
    public static Cache<String, HashMap<String, String>> requestsCache3 = new Cache<>(400);

    public static ArrayList<Cache<String, HashMap<String, String>>> cacheList = new ArrayList<>();
    static {
        cacheList.add(requestsCache);
        cacheList.add(requestsCache2);
        cacheList.add(requestsCache3);
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
        //   String roundedAddress = "http://localhost:8982/api/v1/docs/";
        CircuitBreaker circuitBreaker = HTTPListener.getCircuitBreaker();

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request;

            System.out.println("response body" + responseBody);
            //check which command then proceed
            final JSONObject body = new JSONObject(responseBody);
            int userCommand = body.getInt("command");
            String clientId = body.getString("clientId");

            if (userCommand == 1) {

            } else if (userCommand == 2) {
                int userId = body.getInt("userId");
                String roundedAddress = HTTPListener.getIp(addressUserPool);
                request = HttpRequest.newBuilder().uri(URI.create(roundedAddress + userId)).build();

                String requestUri = request.uri().toString();

                if (requestsCache.get(requestUri) != null) {
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
            } else if (userCommand == 3) {
                System.out.println("FAILURE COUNTER - " + ClientHttp.failureCount);
                String roundedAddress = HTTPListener.getIp(addressDocPool);
                System.out.println("Service address: " + roundedAddress);
                request = HttpRequest.newBuilder().uri(URI.create(roundedAddress))
                        .version(HttpClient.Version.HTTP_1_1).build();
                String requestUri = request.uri().toString();


                circuitBreaker.evaluateState();
                if (circuitBreaker.state == State.OPEN) {
                    HashMap<String, String> serviceDown = new HashMap<>();
                    serviceDown.put("Circuit Break: ", circuitBreaker.lastFailureResponse);
                    return serviceDown; // return cached response if the circuit is in OPEN state
                } else {
                    if (requestsCache.get(requestUri) != null) {
                        System.out.println("HI FROM CACHE");
                        return requestsCache.get(requestUri);
                    } else {
                        var result = makeCall(client, request);

                        int resultHash = getHash(result.toString());
                        var routedCache = getCache(result.toString());

                        if (routedCache.endsWith("1")){
                            requestsCache.put(String.valueOf(resultHash), result);
                        } else if (routedCache.endsWith("2")) {
                            requestsCache2.put(String.valueOf(resultHash), result);
                        } else if (routedCache.endsWith("3")) {
                            requestsCache3.put(String.valueOf(resultHash), result);
                        }
                        propagateChange(cacheList, String.valueOf(resultHash), result);
                        System.out.println("result " + result);
                       // requestsCache.put(requestUri, result);
                        System.out.println("HI I JUST JOINED THE CACHE");

                        circuitBreaker.recordSuccess();

                        return result;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("error in parsing");
            circuitBreaker.recordFailure(e.getMessage());
          //  throw e;
        }
        return null;
    }

    public static HashMap<String, String> makeCall(HttpClient client, HttpRequest request) throws ExecutionException, InterruptedException, TimeoutException {
        CompletableFuture<HttpResponse<String>> response =
                client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        HashMap<String, String> result = response.thenApply(HttpResponse::body)
                .thenApply(RequestParser::parse)
                .get(5, TimeUnit.SECONDS);

        return result;
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
}