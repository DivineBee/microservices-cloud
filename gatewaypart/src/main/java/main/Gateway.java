package main;

import http.GatewayHandler;
import http.HttpUtil;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.RedisURI;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Gateway {
    public static void main (String[] args) throws IOException {
        //  establish communication channel with Redis
        RedisClient redisClient = new RedisClient(
                RedisURI.create("redis://@localhost:6379"));

        //  create http server that will handle all incoming requests and responses
        HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 8003), 0);
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(4);
        server.createContext("/", new GatewayHandler(redisClient.connect(), new HttpUtil()));
        server.setExecutor(threadPoolExecutor);
        server.start();
        System.out.println(" Server started on port 8003");
    }
}