package http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class Connection {

    private static final String USER_API_URL = "http://localhost:8080/api/v1/user/openapi.json";
    private static final String DOCS_API_URL = "http://localhost:8080/api/v1/docs/";
    private static final String SERV_API_URL = "http://localhost:5001/api/docs.json";

    public static void main(String[] args) {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(SERV_API_URL)).build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(System.out::println)
                //.thenApply(Connection::parse)
                .join();
    }

    public static String parse(String responseBody) {
        JSONArray docs = new JSONArray(responseBody);
        for (int i = 0; i < docs.length(); i++) {
            JSONObject doc = docs.getJSONObject(i);
            int id = doc.getInt("id");
            JSONArray user_id = doc.getJSONArray("user_id");
            String title = doc.getString("title");
            String text = doc.getString("text");
            System.out.println(id + " " + user_id + " " + title + " " + text);
        }
        return null;
    }
}
