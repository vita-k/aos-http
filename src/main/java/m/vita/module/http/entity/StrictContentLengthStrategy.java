package m.vita.module.http.entity;

import m.vita.module.http.HttpMessage;
import m.vita.module.http.annotation.Immutable;
import m.vita.module.http.exception.HttpException;
import m.vita.module.http.exception.ProtocolException;
import m.vita.module.http.header.Header;
import m.vita.module.http.util.Args;
import m.vita.module.http.util.HTTP;
import m.vita.module.http.util.HttpVersion;

@Immutable
public class StrictContentLengthStrategy implements ContentLengthStrategy {

    public static final StrictContentLengthStrategy INSTANCE = new StrictContentLengthStrategy();

    private final int implicitLen;

    /**
     * Creates <tt>StrictContentLengthStrategy</tt> instance with the given length used per default
     * when content length is not explicitly specified in the message.
     *
     * @param implicitLen implicit content length.
     *
     * @since 4.2
     */
    public StrictContentLengthStrategy(final int implicitLen) {
        super();
        this.implicitLen = implicitLen;
    }

    /**
     * Creates <tt>StrictContentLengthStrategy</tt> instance. {@link ContentLengthStrategy#IDENTITY}
     * is used per default when content length is not explicitly specified in the message.
     */
    public StrictContentLengthStrategy() {
        this(IDENTITY);
    }

    public long determineLength(final HttpMessage message) throws HttpException {
        Args.notNull(message, "HTTP message");
        // Although Transfer-Encoding is specified as a list, in practice
        // it is either missing or has the single value "chunked". So we
        // treat it as a single-valued header here.
        final Header transferEncodingHeader = message.getFirstHeader(HTTP.TRANSFER_ENCODING);
        if (transferEncodingHeader != null) {
            final String s = transferEncodingHeader.getValue();
            if (HTTP.CHUNK_CODING.equalsIgnoreCase(s)) {
                if (message.getProtocolVersion().lessEquals(HttpVersion.HTTP_1_0)) {
                    throw new ProtocolException(
                            "Chunked transfer encoding not allowed for " +
                                    message.getProtocolVersion());
                }
                return CHUNKED;
            } else if (HTTP.IDENTITY_CODING.equalsIgnoreCase(s)) {
                return IDENTITY;
            } else {
                throw new ProtocolException(
                        "Unsupported transfer encoding: " + s);
            }
        }
        final Header contentLengthHeader = message.getFirstHeader(HTTP.CONTENT_LEN);
        if (contentLengthHeader != null) {
            final String s = contentLengthHeader.getValue();
            try {
                final long len = Long.parseLong(s);
                if (len < 0) {
                    throw new ProtocolException("Negative content length: " + s);
                }
                return len;
            } catch (final NumberFormatException e) {
                throw new ProtocolException("Invalid content length: " + s);
            }
        }
        return this.implicitLen;
    }

}
