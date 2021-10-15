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

    /**
     * Runner method of the gateway, opens up the connection to be available for the clients.
     * Creates the http server on the specified port, with the second parameter being the backlog.
     * Backlog represents the maximum number of queued incoming connections.
     * by setting the executor it puts 5 threads on which each connection will operate into
     * the thread pool so with the possibility to reuse them.
     * @param args
     */
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

    /**
     * Round Robin method, picks the next available server from the pool of servers
     * @param ipList
     * @return String
     */
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