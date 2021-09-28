package http;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerHttp {
    private int port;
    private String directory;

    public ServerHttp(int port, String directory) {
        this.port = port;
        this.directory = directory;
    }

    void start(){
        try (var server = new ServerSocket(this.port)){
            while (true) {
                var socket = server.accept();
                var thread = new Handler(socket, this.directory);
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        var port = 5001;
        var directory = "D:\\Git\\microservices_lab1\\gatewaypart\\src\\main\\java\\files";
        new ServerHttp(port, directory).start();
    }
}
