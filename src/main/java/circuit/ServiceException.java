package circuit;

// Exception thrown when service does not respond successfully.
public class ServiceException extends Exception {

    public ServiceException(String message) {
        super(message);
    }
}
