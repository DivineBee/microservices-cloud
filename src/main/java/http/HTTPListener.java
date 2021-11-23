package http;

import cache.Cache;
import cache.CacheItem;
import circuit.CircuitBreaker;
import com.sun.net.httpserver.HttpServer;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.Executors;

public class HTTPListener {
    private HttpServer httpServer;
    private static int port = 5001;
    public static int COUNTER = 0;
    private static final Object lock = new Object();

    private static HashMap<String, String> allowedUsers = new HashMap<>();
    static {
        allowedUsers.put("sam01admin", "Seer23");
        allowedUsers.put("jessica02admin", "Hunter23");
    }

    public static CircuitBreaker circuitBreaker = new CircuitBreaker(3000,
            2,2000*1000*1000);

    public static CircuitBreaker getCircuitBreaker() {
        return circuitBreaker;
    }

    /**
     * Runner method of the gateway, opens up the connection to be available for the clients.
     * Creates the http server on the specified port, with the second parameter being the backlog.
     * Backlog represents the maximum number of queued incoming connections.
     * by setting the executor it puts 5 threads on which each connection will operate into
     * the thread pool so with the possibility to reuse them.
     * @param args
     */
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 5000);
        DataOutputStream os = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        os.writeBytes("{\"message\": {\"someField\":\"someValue\"} }" + '\n');
        os.flush();
        socket.close();

        HttpServer httpServer;
        Scanner binaryAnswer = new Scanner(System.in);
        try {
            httpServer = HttpServer.create(new InetSocketAddress(port), 100);
            httpServer.setExecutor(Executors.newFixedThreadPool(5));
            httpServer.createContext("/", new RequestHandler());
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(true) {
            System.out.println("Administer cache?Y/N");
            String answer = binaryAnswer.next();
            if (answer.equalsIgnoreCase("Y")) {
                queryCache(RequestParser.requestsCache);
            } else {
                System.out.println("OK.");
            }
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
    /* query language is the following:
            Insert data:
                put into <key> <value>
            Get cache by key:
                get <key>
            Delete cache node:
                delete <key>
            Get the cache size:
                show size
            Display all available data:
                show all
         */
    public static void queryCache(Cache<String, HashMap<String, String>> cache){
        boolean isUserAuthenticated = authenticateUserCache();
        while (isUserAuthenticated) {
            Scanner userInput = new Scanner(System.in);
            System.out.println("Insert query:");
            String answer = userInput.nextLine();
            String[] query = answer.strip().split(" ");

            if (query[0].equalsIgnoreCase("put")) {
                String key = query[1];
                String value = query[2];
                HashMap<String, String> valueMap = new HashMap<>();
                valueMap.put(key, value);
                cache.put(key, valueMap);
                System.out.println("Inserted successfully");
            } else if (query[0].equalsIgnoreCase("get")) {
                String key =  query[1];
                System.out.println("Here is the cache value:" + cache.get(key));
            } else if (query[0].equalsIgnoreCase("delete")){
                String key =  query[1];
                CacheItem node = cache.getMap().get(key);
                cache.deleteNode(node);
                System.out.println("Node deleted successfully");
            } else if (query[0].equalsIgnoreCase("show") && query[1].equalsIgnoreCase("size")){
                System.out.println("Cache size is: " + cache.size());
            } else if (query[0].equalsIgnoreCase("show") && query[1].equalsIgnoreCase("all")){
                cache.getMap().entrySet().forEach(entry -> {
                    System.out.println(entry.getKey() + " " + entry.getValue());
                });
            } else if (query[0].equalsIgnoreCase("exit")) {
                isUserAuthenticated = false;
            }
        }
    }

    public static boolean authenticateUserCache(){
        Scanner userInput = new Scanner(System.in);
        System.out.println("Please insert username");
        String username = userInput.next();

        if (allowedUsers.containsKey(username)) {
            System.out.println("Please insert password");
            String password = userInput.next();
            if (allowedUsers.get(username).equals(password))
                return true;
        }
        return false;
    }
}