package m.vita.module.http.util;

import java.nio.charset.Charset;

public final class HTTP {

    public static final int CR = 13; // <US-ASCII CR, carriage return (13)>
    public static final int LF = 10; // <US-ASCII LF, linefeed (10)>
    public static final int SP = 32; // <US-ASCII SP, space (32)>
    public static final int HT = 9;  // <US-ASCII HT, horizontal-tab (9)>

    /** HTTP header definitions */
    public static final String TRANSFER_ENCODING = "Transfer-Encoding";
    public static final String CONTENT_LEN  = "Content-Length";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String CONTENT_ENCODING = "Content-Encoding";
    public static final String EXPECT_DIRECTIVE = "Expect";
    public static final String CONN_DIRECTIVE = "Connection";
    public static final String TARGET_HOST = "Host";
    public static final String USER_AGENT = "User-Agent";
    public static final String DATE_HEADER = "Date";
    public static final String SERVER_HEADER = "Server";

    /** HTTP expectations */
    public static final String EXPECT_CONTINUE = "100-continue";

    /** HTTP connection control */
    public static final String CONN_CLOSE = "Close";
    public static final String CONN_KEEP_ALIVE = "Keep-Alive";

    /** Transfer encoding definitions */
    public static final String CHUNK_CODING = "chunked";
    public static final String IDENTITY_CODING = "identity";

    public static final Charset DEF_CONTENT_CHARSET = Consts.ISO_8859_1;
    public static final Charset DEF_PROTOCOL_CHARSET = Consts.ASCII;

    /**
     * @deprecated (4.2)
     */
    @Deprecated
    public static final String UTF_8 = "UTF-8";
    /**
     * @deprecated (4.2)
     */
    @Deprecated
    public static final String UTF_16 = "UTF-16";
    /**
     * @deprecated (4.2)
     */
    @Deprecated
    public static final String US_ASCII = "US-ASCII";
    /**
     * @deprecated (4.2)
     */
    @Deprecated
    public static final String ASCII = "ASCII";
    /**
     * @deprecated (4.2)
     */
    @Deprecated
    public static final String ISO_8859_1 = "ISO-8859-1";
    /**
     * @deprecated (4.2)
     */
    @Deprecated
    public static final String DEFAULT_CONTENT_CHARSET = ISO_8859_1;
    /**
     * @deprecated (4.2)
     */
    @Deprecated
    public static final String DEFAULT_PROTOCOL_CHARSET = US_ASCII;
    /**
     * @deprecated (4.2)
     */
    @Deprecated
    public final static String OCTET_STREAM_TYPE = "application/octet-stream";
    /**
     * @deprecated (4.2)
     */
    @Deprecated
    public final static String PLAIN_TEXT_TYPE = "text/plain";
    /**
     * @deprecated (4.2)
     */
    @Deprecated
    public final static String CHARSET_PARAM = "; charset=";
    /**
     * @deprecated (4.2)
     */
    @Deprecated
    public final static String DEFAULT_CONTENT_TYPE = OCTET_STREAM_TYPE;

    public static boolean isWhitespace(final char ch) {
        return ch == SP || ch == HT || ch == CR || ch == LF;
    }

    private HTTP() {
    }
}
