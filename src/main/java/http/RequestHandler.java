package http;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class RequestHandler implements HttpHandler {
    /**
     * handles the incoming request from client, turns the load balancing and sends back and forth all the
     * requests from the system (This is the Gateway). It receives the requests and responses.
     * @param httpExchange
     * @throws IOException
     */
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            String requestBody = readRequestBody(httpExchange);
            String roundedAddress = HTTPListener.getIp(RequestParser.addressDocPool);
            System.out.println("\n--------------------------------------\n");
            System.out.println(requestBody);
            System.out.println(httpExchange.getRequestURI());
            System.out.println(httpExchange.getRequestMethod());
            HashMap<String, String> responseBody = RequestParser.processRequest(requestBody);
            // send response from microservice
            sendResponse(httpExchange, 200, "Response --" + responseBody);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    /**
     * reads the body of incoming request into a input stream via httpExchange class
     * @param httpExchange
     * @return String
     * @throws Exception
     */
    private String readRequestBody(HttpExchange httpExchange) throws Exception {
        try (InputStream isr = httpExchange.getRequestBody()) {
            byte[] buffer = new byte[isr.available()];
            isr.read(buffer);
            isr.close();
            return new String(buffer);
        } catch (IOException ex) {
            System.err.println("No request body passed...");
            throw new Exception(ex.getMessage());
        }
    }

    /**
     * Method of sending response from the microservice containing the information client required
     * through an output stream and receiving the response code the microservice
     * @param httpExchange
     * @param httpResponseCode
     * @param response
     * @throws IOException
     */
    private void sendResponse(HttpExchange httpExchange, int httpResponseCode, String response) throws IOException {
        OutputStream outputStream = null;
        try {
            httpExchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            httpExchange.sendResponseHeaders(httpResponseCode, response.length());
            outputStream = httpExchange.getResponseBody();
            outputStream.write(response.getBytes());
            httpExchange.close();
        } catch (Exception e){
            System.err.println("Whoops, there is no response from microservice :(");
        }
        finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }
}