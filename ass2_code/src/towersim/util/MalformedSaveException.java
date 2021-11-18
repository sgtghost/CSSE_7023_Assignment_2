package towersim.util;

/**
 * Exception thrown when a save file is in an invalid format or contains incorrect data.
 */
public class MalformedSaveException extends Exception {
    /**
     * Constructs a new MalformedSaveException with no detail message or cause.
     *
     * @see Exception#Exception()
     * @ass2
     */
    public MalformedSaveException() {
        super();
    }

    /**
     * Constructs a MalformedSaveException that contains a helpful detail message explaining why
     * the exception occurred.
     * <p>
     * <b>Important:</b> do not write JUnit tests that expect a valid implementation of the
     * assignment to have a certain error message, as the official solution will use different
     * messages to those you are expecting, if any at all.
     *
     * @param message detail message
     * @see Exception#Exception(String)
     * @ass2
     */
    public MalformedSaveException(String message) {
        super(message);
    }

    /**
     * Constructs a MalformedSaveException that stores the underlying cause of the exception.
     * @param cause throwable that caused this exception
     * @see Exception#Exception(Throwable)
     * @ass2
     */
    public MalformedSaveException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a MalformedSaveException that contains a helpful detail message explaining why
     * the exception occurred and the underlying cause of the exception.
     * <p>
     * @param message detail message
     * @param cause throwable that caused this exception
     * @see Exception#Exception(String, Throwable)
     * @ass2
     */
    public MalformedSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
