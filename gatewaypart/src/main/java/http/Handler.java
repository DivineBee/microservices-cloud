package http;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Handler extends Thread {

    private static final Map<String, String> CONTENT_TYPES = new HashMap<>() {{
        put("jpg", "image/jpeg");
        put("html", "text/html");
        put("json", "application/json");
        put("txt", "text/plain");
        put("", "text/plain");
    }};

    private static final String NOT_FOUND_MSG = "NOT FOUND";

    private Socket socket;
    private String directory;

    Handler(Socket socket, String directory) {
        this.socket = socket;
        this.directory = directory;
    }

    @Override
    public void run() {
        try (var input = this.socket.getInputStream(); var output = this.socket.getOutputStream()) {
            var url = this.getRequestUrl(input);
            var filePath = Path.of(this.directory + url);
            if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
                String extension = this.getFileExtension(filePath);
                System.out.println("extension " +extension);
                String type = CONTENT_TYPES.get(extension);
                System.out.println("type " +type);
                byte[] fileBytes = Files.readAllBytes(filePath);
                System.out.println("fileBytes " + Arrays.toString(fileBytes));

                this.sendHeader(output, 200, "OK", type, fileBytes.length);
                output.write(fileBytes);
            } else {
                String type = CONTENT_TYPES.get("text");
                this.sendHeader(output, 404, "Not Found", type, NOT_FOUND_MSG.length());
                output.write(NOT_FOUND_MSG.getBytes(StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.run();
    }

    private String getRequestUrl(InputStream input) {
        Scanner reader = new Scanner(input).useDelimiter("\r\n");
        String line = reader.next();
        System.out.println("getRequestUrl " + line.split(" ")[1] +"\n" + line);
        return line.split(" ")[1];
    }

    private String getFileExtension(Path path) {
        String name = path.getFileName().toString();
        int extensionStart = name.lastIndexOf(".");
        return extensionStart == -1 ? "" : name.substring(extensionStart + 1);
    }

    private void sendHeader(OutputStream output, int statusCode, String statusText, String type, long length) {
        PrintStream ps = new PrintStream(output);
        ps.printf("HTTP/1.1 %s %s%n", statusCode, statusText);
        ps.printf("Content-Type %s%n", type);
        ps.printf("Content-Length: %s%n%n", length);
    }
}
