import java.io.IOException;
import java.util.LinkedHashMap;

public class ClientMain {
    public static void main(String[] args) throws IOException {

        RoundRobin roundRobin = new RoundRobin();
        String[] address = roundRobin.getServer().split(",");
        String ip = address[0];
        int port = Integer.parseInt(address[1]);
        System.out.println("IP " + ip);
        System.out.println("PORT " + port);
        Client client = new Client();
        client.startConnection(ip, port);
    }
}
