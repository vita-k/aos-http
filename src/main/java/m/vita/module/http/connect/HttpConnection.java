package m.vita.module.http.connect;

import java.io.Closeable;
import java.io.IOException;

public interface HttpConnection extends Closeable {
    /**
     * Closes this connection gracefully.
     * This method will attempt to flush the internal output
     * buffer prior to closing the underlying socket.
     * This method MUST NOT be called from a different thread to force
     * shutdown of the connection. Use {@link #shutdown shutdown} instead.
     */
    void close() throws IOException;

    /**
     * Checks if this connection is open.
     * @return true if it is open, false if it is closed.
     */
    boolean isOpen();

    /**
     * Checks whether this connection has gone down.
     * Network connections may get closed during some time of inactivity
     * for several reasons. The next time a read is attempted on such a
     * connection it will throw an IOException.
     * This method tries to alleviate this inconvenience by trying to
     * find out if a connection is still usable. Implementations may do
     * that by attempting a read with a very small timeout. Thus this
     * method may block for a small amount of time before returning a result.
     * It is therefore an <i>expensive</i> operation.
     *
     * @return  <code>true</code> if attempts to use this connection are
     *          likely to succeed, or <code>false</code> if they are likely
     *          to fail and this connection should be closed
     */
    boolean isStale();

    /**
     * Sets the socket timeout value.
     *
     * @param timeout timeout value in milliseconds
     */
    void setSocketTimeout(int timeout);

    /**
     * Returns the socket timeout value.
     *
     * @return positive value in milliseconds if a timeout is set,
     * <code>0</code> if timeout is disabled or <code>-1</code> if
     * timeout is undefined.
     */
    int getSocketTimeout();

    /**
     * Force-closes this connection.
     * This is the only method of a connection which may be called
     * from a different thread to terminate the connection.
     * This method will not attempt to flush the transmitter's
     * internal buffer prior to closing the underlying socket.
     */
    void shutdown() throws IOException;

    /**
     * Returns a collection of connection metrics.
     *
     * @return HttpConnectionMetrics
     */
    HttpConnectionMetrics getMetrics();
}
