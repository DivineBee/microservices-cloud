package http;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RequestParser {
    private static final String USER_API_URL = "http://localhost:8080/api/v1/user/";
    private static final String DOCS_API_URL = "http://localhost:8080/api/v1/docs/";

    public void forwardRequest(String body) throws IOException, InterruptedException {
        parse(body);
    }

    public static String parse(String responseBody) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        //check which command then proceed
        final JSONObject response = new JSONObject(responseBody);
        int userCommand = response.getInt("command");
        int clientId = response.getInt("clientId");
        if (userCommand == 1){

        } else if (userCommand == 2){
            int userId = response.getInt("userId");
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(USER_API_URL + userId)).build();

            HttpResponse<String> responseToSend = client.send(request, HttpResponse.BodyHandlers.ofString());
            return responseToSend.body();
        }
        System.out.println(response.getString("email"));
        System.out.println(response.getString("name"));
        return null;
    }
}
