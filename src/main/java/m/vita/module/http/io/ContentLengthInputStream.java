package m.vita.module.http.io;

import java.io.IOException;
import java.io.InputStream;

import m.vita.module.http.annotation.NotThreadSafe;
import m.vita.module.http.exception.ConnectionClosedException;
import m.vita.module.http.util.Args;

@NotThreadSafe
public class ContentLengthInputStream extends InputStream {

    private static final int BUFFER_SIZE = 2048;
    /**
     * The maximum number of bytes that can be read from the stream. Subsequent
     * read operations will return -1.
     */
    private final long contentLength;

    /** The current position */
    private long pos = 0;

    /** True if the stream is closed. */
    private boolean closed = false;

    /**
     * Wrapped input stream that all calls are delegated to.
     */
    private SessionInputBuffer in = null;

    /**
     * Wraps a session input buffer and cuts off output after a defined number
     * of bytes.
     *
     * @param in The session input buffer
     * @param contentLength The maximum number of bytes that can be read from
     * the stream. Subsequent read operations will return -1.
     */
    public ContentLengthInputStream(final SessionInputBuffer in, final long contentLength) {
        super();
        this.in = Args.notNull(in, "Session input buffer");
        this.contentLength = Args.notNegative(contentLength, "Content length");
    }

    /**
     * <p>Reads until the end of the known length of content.</p>
     *
     * <p>Does not close the underlying socket input, but instead leaves it
     * primed to parse the next response.</p>
     * @throws IOException If an IO problem occurs.
     */
    @Override
    public void close() throws IOException {
        if (!closed) {
            try {
                if (pos < contentLength) {
                    final byte buffer[] = new byte[BUFFER_SIZE];
                    while (read(buffer) >= 0) {
                    }
                }
            } finally {
                // close after above so that we don't throw an exception trying
                // to read after closed!
                closed = true;
            }
        }
    }

    @Override
    public int available() throws IOException {
        if (this.in instanceof BufferInfo) {
            final int len = ((BufferInfo) this.in).length();
            return Math.min(len, (int) (this.contentLength - this.pos));
        } else {
            return 0;
        }
    }

    /**
     * Read the next byte from the stream
     * @return The next byte or -1 if the end of stream has been reached.
     * @throws IOException If an IO problem occurs
     * @see InputStream#read()
     */
    @Override
    public int read() throws IOException {
        if (closed) {
            throw new IOException("Attempted read from closed stream.");
        }

        if (pos >= contentLength) {
            return -1;
        }
        final int b = this.in.read();
        if (b == -1) {
            if (pos < contentLength) {
                throw new ConnectionClosedException(
                        "Premature end of Content-Length delimited message body (expected: "
                                + contentLength + "; received: " + pos);
            }
        } else {
            pos++;
        }
        return b;
    }

    /**
     * Does standard {@link InputStream#read(byte[], int, int)} behavior, but
     * also notifies the watcher when the contents have been consumed.
     *
     * @param b     The byte array to fill.
     * @param off   Start filling at this position.
     * @param len   The number of bytes to attempt to read.
     * @return The number of bytes read, or -1 if the end of content has been
     *  reached.
     *
     * @throws IOException Should an error occur on the wrapped stream.
     */
    @Override
    public int read (final byte[] b, final int off, final int len) throws IOException {
        if (closed) {
            throw new IOException("Attempted read from closed stream.");
        }

        if (pos >= contentLength) {
            return -1;
        }

        int chunk = len;
        if (pos + len > contentLength) {
            chunk = (int) (contentLength - pos);
        }
        final int count = this.in.read(b, off, chunk);
        if (count == -1 && pos < contentLength) {
            throw new ConnectionClosedException(
                    "Premature end of Content-Length delimited message body (expected: "
                            + contentLength + "; received: " + pos);
        }
        if (count > 0) {
            pos += count;
        }
        return count;
    }


    /**
     * Read more bytes from the stream.
     * @param b The byte array to put the new data in.
     * @return The number of bytes read into the buffer.
     * @throws IOException If an IO problem occurs
     * @see InputStream#read(byte[])
     */
    @Override
    public int read(final byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    /**
     * Skips and discards a number of bytes from the input stream.
     * @param n The number of bytes to skip.
     * @return The actual number of bytes skipped. <= 0 if no bytes
     * are skipped.
     * @throws IOException If an error occurs while skipping bytes.
     * @see InputStream#skip(long)
     */
    @Override
    public long skip(final long n) throws IOException {
        if (n <= 0) {
            return 0;
        }
        final byte[] buffer = new byte[BUFFER_SIZE];
        // make sure we don't skip more bytes than are
        // still available
        long remaining = Math.min(n, this.contentLength - this.pos);
        // skip and keep track of the bytes actually skipped
        long count = 0;
        while (remaining > 0) {
            final int l = read(buffer, 0, (int)Math.min(BUFFER_SIZE, remaining));
            if (l == -1) {
                break;
            }
            count += l;
            remaining -= l;
        }
        return count;
    }
}
