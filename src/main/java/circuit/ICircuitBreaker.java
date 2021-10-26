package circuit;

public interface ICircuitBreaker {
    void recordSuccess();
    void recordFailure(String response);

    String getState();
    void setState(State state);
}

