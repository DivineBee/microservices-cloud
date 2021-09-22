import java.util.concurrent.locks.ReentrantLock;

public class RoundRobin {
    public static int counter = 0;
    private final ReentrantLock lock;

    public RoundRobin() {
        lock = new ReentrantLock();
    }

    public String getServer() {
        synchronized (lock) {
            try {
                String ip = (String) ServerMain.networkAdresses.keySet().toArray()[counter]; // get ip field from map
                String port = (String) ServerMain.networkAdresses.values().toArray()[counter];
//                System.out.println("IP " + ip);
//                System.out.println("PORT " + port);

                counter += 1;
                if (counter == ServerMain.networkAdresses.size()) {
                    counter = 0;
                }
                return ip + "," + port;
            } catch (Exception e) {
                System.out.println("WENT WRONG");
                return null;
            }
        }
    }

    public static int getCounter() {
        return counter;
    }
}