package http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class RequestHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String requestBody = readRequestBody(httpExchange);
            String roundedAddress = HTTPListener.getIp(RequestParser.addressDocPool);
            System.out.println("ROUNDED " + roundedAddress);
            System.out.println(requestBody);
            System.out.println(httpExchange.getRequestURI());
            System.out.println(httpExchange.getRequestMethod());
            HashMap<String, String> responseBody = RequestParser.processRequest(requestBody);
            // send response from microservice
            sendResponse(httpExchange, 200, "Response 200" + responseBody);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private String readRequestBody(HttpExchange httpExchange) throws Exception {
        try (InputStream isr = httpExchange.getRequestBody()) {
            byte[] buffer = new byte[isr.available()];
            isr.read(buffer);
            return new String(buffer);
        } catch (IOException ex) {
            throw new Exception(ex.getMessage());
        }
    }

    private void sendResponse(HttpExchange httpExchange, int httpResponseCode, String response) throws IOException {
        OutputStream outputStream = null;
        try {
            httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            httpExchange.sendResponseHeaders(httpResponseCode, response.length());
            outputStream = httpExchange.getResponseBody();
            outputStream.write(response.getBytes());
            httpExchange.close();
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }

    }
}