import java.io.IOException;

public class ServerStop {
    public static void main(String[] args) throws IOException {
        for( Server server: ServerMain.serversList){
            server.stop();
        }
    }
}
