package poc.eventchain.accountservice.web;

public class EventChainException extends RuntimeException {
    public EventChainException() {
        super();
    }

    public EventChainException(String message) {
        super(message);
    }

    public EventChainException(String message, Throwable cause) {
        super(message, cause);
    }

    public EventChainException(Throwable cause) {
        super(cause);
    }

    protected EventChainException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
