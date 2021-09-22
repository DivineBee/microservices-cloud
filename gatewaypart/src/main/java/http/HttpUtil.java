package http;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;

import java.io.IOException;
import java.net.http.HttpClient;

/**
 * Class for making http requests
 */
public class HttpUtil {
    //  setting HttpClient that will make requests
    private final HttpClient client = HttpClient.newHttpClient();

    /**
     * send JSON formatted POST request that will start discussion about
     * @param destinationPage where request must be delivered
     * @param functionName name of function that will be called remotely
     * @param firstArgument first argument of function to be delivered
     * @return response to request
     * @throws IOException i/o error
     */
    public String sendJsonPost(String destinationPage, String functionName, String firstArgument) throws IOException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("functionName", functionName);
        jsonRequest.put("amount", firstArgument);
        return sendJsonPost(destinationPage, jsonRequest.toString());
    }

    /**
     * send JSON formatted PUT request that will continue discussion
     * @param destinationPage where request must be delivered
     * @param id index of process that will be continued
     * @param nameOfArgument name of argument that must be delivered in JSON
     * @param argumentValue value of argument
     * @return response to request
     * @throws IOException i/o error
     */
    public String sendJsonPut(String destinationPage, Long id, String nameOfArgument, String argumentValue) throws IOException {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("id", id);
        jsonRequest.put(nameOfArgument, argumentValue);
        return sendJsonPut(destinationPage, jsonRequest.toString());
    }

    /**
     * send JSON formatted GET request that will finish discussion and get result
     * @param destinationPageWithId where request will be delivered and index of process
     * @return response to request
     * @throws IOException i/o error
     */
    public String sendJsonGet(String destinationPageWithId) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(destinationPageWithId);

        CloseableHttpResponse response = httpClient.execute(request);
        ResponseHandler<String> handler = new BasicResponseHandler();
        String responsePayload = handler.handleResponse(response);
        httpClient.close();
        response.close();
        return responsePayload;
    }

    /**
     * send POST request that will start discussion with service
     * @param destinationPage where request must be delivered
     * @param jsonRequest JSON request
     * @return response to request
     * @throws IOException i/o error
     */
    public String sendJsonPost(String destinationPage, String jsonRequest) throws IOException {
        StringEntity entity = new StringEntity(jsonRequest,
                ContentType.APPLICATION_JSON);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost request = new HttpPost(destinationPage);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-Type", "application/json");
        request.setEntity(entity);

        CloseableHttpResponse response = httpClient.execute(request);
        ResponseHandler<String> handler = new BasicResponseHandler();
        String responsePayload = handler.handleResponse(response);
        httpClient.close();
        response.close();
        return responsePayload;
    }

    public String sendServiceBroadcastJsonPost(String destinationPage, String jsonRequest) throws IOException {
        StringEntity entity = new StringEntity(jsonRequest,
                ContentType.APPLICATION_JSON);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost request = new HttpPost(destinationPage);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-Type", "application/json");
        request.setHeader("Service-Call", "broadcast");
        request.setEntity(entity);

        CloseableHttpResponse response = httpClient.execute(request);
        ResponseHandler<String> handler = new BasicResponseHandler();
        String responsePayload = handler.handleResponse(response);
        httpClient.close();
        response.close();
        return responsePayload;
    }

    /**
     * send PUT request that will continue discussion with service
     * @param destinationPage where request must be delivered
     * @param jsonRequest JSON request
     * @return response to request
     * @throws IOException i/o error
     */
    public String sendJsonPut(String destinationPage, String jsonRequest) throws IOException {
        StringEntity entity = new StringEntity(jsonRequest,
                ContentType.APPLICATION_JSON);

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPut request = new HttpPut(destinationPage);
        request.setHeader("Accept", "application/json");
        request.setHeader("Content-Type", "application/json");
        request.setEntity(entity);

        CloseableHttpResponse response = httpClient.execute(request);
        ResponseHandler<String> handler = new BasicResponseHandler();
        String responsePayload = handler.handleResponse(response);
        httpClient.close();
        response.close();
        return responsePayload;
    }
}