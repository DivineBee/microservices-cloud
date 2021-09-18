import java.io.IOException;
import java.net.ServerSocket;

public class Server {
    //  port that will listen for requests
    private int serverPort;

    // number of maximum conected clients
    private static final int SERVER_CAPACITY = 100;

    //  connection socket
    private ServerSocket serverSocket;

    //  constructor defining port of listening incoming requests
    public Server(int whichPortToOpen) throws IOException {
        serverSocket = new ServerSocket(whichPortToOpen, SERVER_CAPACITY);
        this.serverPort = whichPortToOpen;
    }

    //  getters
    public int getServerPort() { return serverPort; }
    public ServerSocket getServerSocket() { return serverSocket; }
}
