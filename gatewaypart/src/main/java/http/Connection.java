package http;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class Connection {

    private static final String USER_API_URL = "http://localhost:8080/api/v1/user/openapi.json";
    private static final String DOCS_API_URL = "http://localhost:8080/api/v1/docs/";

    public static void main(String[] args) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .header("accept", "application/json")
                .uri(URI.create(DOCS_API_URL))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());

        // parse JSON to objects
        ObjectMapper mapper = new ObjectMapper();
        List<Document> docs = mapper.readValue(response.body(), new TypeReference<>() {});

        docs.forEach(System.out::println);
    }
}
