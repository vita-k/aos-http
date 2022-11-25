package m.vita.module.http.exception;

public class SSLInitializationException extends IllegalStateException {

    private static final long serialVersionUID = -8243587425648536702L;

    public SSLInitializationException(final String message, final Throwable cause) {
        super(message, cause);
    }

}
