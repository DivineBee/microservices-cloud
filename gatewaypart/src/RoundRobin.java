import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

public class RoundRobin {

    private int counter = 0;
    private final ReentrantLock lock;

//    LinkedHashMap<String, String> ipPool;

    ArrayList<Server> serversList;

    public RoundRobin(ArrayList<Server> serversList) {
        this.serversList = serversList;
        lock = new ReentrantLock();
    }

    public String getServer() {
        synchronized (lock) {
            try {
                Server currentServer = serversList.get(counter);
                System.out.println("HERE" + serversList.size());
                // String ip = (String) ipPool.keySet().toArray()[counter]; // get ip field from map
                //String port = (String) ipPool.values().toArray()[counter];
                // System.out.println(ipPool);
                String ip = currentServer.getServerIP();
                int port = currentServer.getServerPort();

                // TODO Client connect to the currentServer
                counter += 1;
                if (counter == serversList.size()) {
                    counter = 0;
                }
                return (String) ip + "," + port;
            } catch (Exception e) {
                System.out.println("WENT WRONG");
                return null;
            }
        }
    }
}