package circuit;

import http.ClientHttp;

public class CircuitBreaker implements ICircuitBreaker{
    private final long timeout;
    private final long retryTimePeriod;
    long lastFailureTime;
    public String lastFailureResponse;
    private final int failureThreshold;
    public State state;
    private final long futureTime = 1000 * 1000 * 1000 * 1000;

    /**
     * @param timeout          Timeout for the API request.
     * @param failureThreshold Number of failures from service before changing state to 'OPEN'
     * @param retryTimePeriod  Time period after which a new request is made to remote service for status check.
     */
    public CircuitBreaker(long timeout, int failureThreshold, long retryTimePeriod) {
        this.state = State.CLOSED; // Start in closed state
        this.failureThreshold = failureThreshold;
        this.timeout = timeout; // break calls if limit is exceeded
        this.retryTimePeriod = retryTimePeriod;
        this.lastFailureTime = System.nanoTime() + futureTime; // future time which will show that failure didn't happen
        ClientHttp.failureCount = 0;
    }

    // Reset everything to defaults
    @Override
    public void recordSuccess() {
        ClientHttp.failureCount = 0;
        this.lastFailureTime = System.nanoTime() + futureTime;
        this.state = State.CLOSED;
    }

    @Override
    public void recordFailure(String response) {
        ClientHttp.failureCount = ClientHttp.failureCount + 1;
        this.lastFailureTime = System.nanoTime();
        // Cache the failure response for returning on open state
        this.lastFailureResponse = response;
    }

    // Evaluate the current state based on failureThreshold, failureCount and lastFailureTime.
    public void evaluateState() {
        if (ClientHttp.failureCount >= failureThreshold) {
            if ((System.nanoTime() - lastFailureTime) > retryTimePeriod)
                state = State.HALF_OPEN ;// Timer passed, try checking if service is up
            else
                state = State.OPEN; // Service would still probably be down
        } else {
            state = State.CLOSED;  // Everything is ok
        }
    }

    @Override
    public String getState() {
        evaluateState();
        return state.name();
    }

    /**
     * Break the circuit beforehand if it is known service is down Or connect the circuit manually if
     * service comes online before expected.
     * @param state State at which circuit is in
     */
    @Override
    public void setState(State state) {
        this.state = state;
        switch (state) {
            case OPEN:
                ClientHttp.failureCount = failureThreshold;
                this.lastFailureTime = System.nanoTime();
                break;
            case HALF_OPEN:
                ClientHttp.failureCount = failureThreshold;
                this.lastFailureTime = System.nanoTime() - retryTimePeriod;
                break;
            default:
                ClientHttp.failureCount = 0;
        }
    }
}
