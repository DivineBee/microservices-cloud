import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class ServerMain {
    public static ArrayList<Server> serversList = new ArrayList<>();
    public static LinkedHashMap<String, String> networkAdresses = new LinkedHashMap<String, String>();
    static {
        networkAdresses.put("127.0.0.1", "3001");
        networkAdresses.put("127.0.0.1", "3002");
        networkAdresses.put("127.0.0.1", "3003");
        networkAdresses.put("127.0.0.1", "3004");
    }

    public static void createAndStartServers(LinkedHashMap<String, String> listOfAddresses) {
        try {
            for (int i = 0; i < listOfAddresses.size(); i++) {
                System.out.println("size" + listOfAddresses.size());
                int port = Integer.parseInt((String) listOfAddresses.values().toArray()[i]);
                String ip = (String) listOfAddresses.keySet().toArray()[i]; // get ip field from map
                Server server = new Server(port);
                System.out.println(port);
                System.out.println("----STARTING");
                server.start(port);
                server.setServerIP(ip);
                //server.setServerPort(port);
                serversList.add(server);
                System.out.println("----SUCCESS");
            }
        }catch (IOException e){
            System.err.println("WRONGWRONG");
        }
    }

    public static void main(String[] args) throws IOException {
        createAndStartServers(networkAdresses);
    }
//    public static void main(String[] args) throws IOException {
//        Server server = new Server();
//        server.setServerIP("192.168.0.1");
//        server.start(3001);
//
//        Server server1 = new Server();
//        server.setServerIP("192.168.0.2");
//        server.start(3002);
//
//        Server server2 = new Server();
//        server.setServerIP("192.168.0.3");
//        server.start(3003);
//
//        Server server3 = new Server();
//        server.setServerIP("192.168.0.4");
//        server.start(3004);
//    }
}
