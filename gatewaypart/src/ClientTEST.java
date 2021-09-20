import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.stream.IntStream;

public class ClientTEST {

    public static void main(String[] args) {

        int NUM_OF_REQUESTS = 20;
        ClientTEST client = new ClientTEST();

//        LinkedHashMap<String, String> networkAdresses = new LinkedHashMap<String, String>();
//        networkAdresses.put("192.168.0.1", "3000");
//        networkAdresses.put("192.168.0.2", "3001");
//        networkAdresses.put("192.168.0.3", "3002");
//        networkAdresses.put("192.168.0.4", "3003");

   //     RoundRobin roundRobbin = new RoundRobin(ServerMain.serversList);
   //     client.simulateConcurrentClientRequest(roundRobbin, NUM_OF_REQUESTS);

        System.out.println("Main exits");
    }

    private void simulateConcurrentClientRequest(RoundRobin roundRobbin, int numOfCalls) {

        IntStream
                .range(0, numOfCalls)
                .parallel()
                .forEach(i ->
                        System.out.println(
                                "IP: " + roundRobbin.getServer()
                                        + " --- Request from ClientTEST: " + i
                                        + " --- [Thread: " + Thread.currentThread().getName() + "]")
                );
    }
}