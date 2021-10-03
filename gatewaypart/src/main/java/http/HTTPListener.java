package http;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class HTTPListener {
    private HttpServer httpServer;
    private int port = 1253;

    public void stop() {
        if (httpServer != null) {
            httpServer.stop(0);
            httpServer = null;
        }
    }

    public static void main(String[] args) {
        HttpServer httpServer;
        try {
            httpServer = HttpServer.create(new InetSocketAddress(5001), 100);
            httpServer.setExecutor(Executors.newFixedThreadPool(5));
            httpServer.createContext("/", new RequestHandler());
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}