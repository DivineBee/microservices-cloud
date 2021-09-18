import java.util.ArrayList;
import java.util.LinkedHashMap;
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

    public Server getIp() {
        synchronized (lock) {
            try {
                Server currentServer = serversList.get(counter);
                // String ip = (String) ipPool.keySet().toArray()[counter]; // get ip field from map
                //String port = (String) ipPool.values().toArray()[counter];
                // System.out.println(ipPool);

                // TODO Client connect to the currentServer
                counter += 1;
                if (counter == serversList.size()) {
                    counter = 0;
                }
                return currentServer;
            } catch (Exception e) {
                System.out.println("WENT WRONG");
                return null;
            }
        }
    }
}