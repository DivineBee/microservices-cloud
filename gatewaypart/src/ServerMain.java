import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ServerMain {
    public static ArrayList<Server> serversList = new ArrayList<>();

    public static void createServers(LinkedHashMap<String, String> listOfAddresses) throws IOException {
        //int currentServerNumber = 0;
        for(int i = 0; i < 4; i++){
            int port = Integer.parseInt((String) listOfAddresses.values().toArray()[i]);
            Server server = new Server(port);
            serversList.add(server);
        }
    }

    public static void main(String[] args) throws IOException {
        LinkedHashMap<String, String> networkAdresses = new LinkedHashMap<String, String>();
        networkAdresses.put("192.168.0.1", "3000");
        networkAdresses.put("192.168.0.2", "3001");
        networkAdresses.put("192.168.0.3", "3002");
        networkAdresses.put("192.168.0.4", "3003");

        createServers(networkAdresses);
    }
}
