package step3.exception;

public class NotExistsNextFrameException extends RuntimeException {
    public NotExistsNextFrameException() {
    }

    public NotExistsNextFrameException(String message) {
        super(message);
    }
}