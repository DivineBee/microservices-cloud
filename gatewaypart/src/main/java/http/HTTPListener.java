package http;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.Executors;

public class HTTPListener {
    private HttpServer httpServer;
    private static int port = 5001;
    public static int COUNTER = 0;
    private static final Object lock = new Object();

    public static void main(String[] args) {
        HttpServer httpServer;
        try {
            httpServer = HttpServer.create(new InetSocketAddress(port), 100);
            httpServer.setExecutor(Executors.newFixedThreadPool(5));
            httpServer.createContext("/", new RequestHandler());
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //Round Robin method
    public static synchronized String getIp(ArrayList<String> ipList) {
        synchronized (lock) {
            try {
                String ip = ipList.get(COUNTER);
                COUNTER += 1;
                if (COUNTER == ipList.size()) {
                    COUNTER = 0;
                }
                return ip;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void stop() {
        if (httpServer != null) {
            httpServer.stop(0);
            httpServer = null;
        }
    }
}