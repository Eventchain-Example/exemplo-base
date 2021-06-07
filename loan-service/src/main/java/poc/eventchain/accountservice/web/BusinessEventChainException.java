package poc.eventchain.accountservice.web;

public class BusinessEventChainException extends RuntimeException {
    public BusinessEventChainException() {
        super();
    }

    public BusinessEventChainException(String message) {
        super(message);
    }

    public BusinessEventChainException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessEventChainException(Throwable cause) {
        super(cause);
    }

    protected BusinessEventChainException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
