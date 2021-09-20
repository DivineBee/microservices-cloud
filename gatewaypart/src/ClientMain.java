import java.io.IOException;

public class ClientMain {
    public static void main(String[] args) throws IOException {
//        Client client1 = new Client();
////        client1.startConnection("127.0.0.1", 6666);
////        String msg1 = client1.sendMessage("hello");
////        String msg2 = client1.sendMessage("world");
////        String terminate = client1.sendMessage(".");

        RoundRobin roundRobin = new RoundRobin(ServerMain.serversList);
        String[] address = roundRobin.getServer().split(",");
        String ip = address[0];
        int port = Integer.parseInt(address[1]);
        Client client = new Client();
        client.startConnection(ip, port);
    }
}
