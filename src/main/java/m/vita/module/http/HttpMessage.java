package m.vita.module.http;

import m.vita.module.http.header.Header;
import m.vita.module.http.header.HeaderIterator;
import m.vita.module.http.header.HttpParams;
import m.vita.module.http.util.ProtocolVersion;

@SuppressWarnings("deprecation")
public interface HttpMessage {

    /**
     * Returns the protocol version this message is compatible with.
     */
    ProtocolVersion getProtocolVersion();

    /**
     * Checks if a certain header is present in this message. Header values are
     * ignored.
     *
     * @param name the header name to check for.
     * @return true if at least one header with this name is present.
     */
    boolean containsHeader(String name);

    /**
     * Returns all the headers with a specified name of this message. Header values
     * are ignored. Headers are orderd in the sequence they will be sent over a
     * connection.
     *
     * @param name the name of the headers to return.
     * @return the headers whose name property equals <code>name</code>.
     */
    Header[] getHeaders(String name);

    /**
     * Returns the first header with a specified name of this message. Header
     * values are ignored. If there is more than one matching header in the
     * message the first element of {@link #getHeaders(String)} is returned.
     * If there is no matching header in the message <code>null</code> is
     * returned.
     *
     * @param name the name of the header to return.
     * @return the first header whose name property equals <code>name</code>
     *   or <code>null</code> if no such header could be found.
     */
    Header getFirstHeader(String name);

    /**
     * Returns the last header with a specified name of this message. Header values
     * are ignored. If there is more than one matching header in the message the
     * last element of {@link #getHeaders(String)} is returned. If there is no
     * matching header in the message <code>null</code> is returned.
     *
     * @param name the name of the header to return.
     * @return the last header whose name property equals <code>name</code>.
     *   or <code>null</code> if no such header could be found.
     */
    Header getLastHeader(String name);

    /**
     * Returns all the headers of this message. Headers are orderd in the sequence
     * they will be sent over a connection.
     *
     * @return all the headers of this message
     */
    Header[] getAllHeaders();

    /**
     * Adds a header to this message. The header will be appended to the end of
     * the list.
     *
     * @param header the header to append.
     */
    void addHeader(Header header);

    /**
     * Adds a header to this message. The header will be appended to the end of
     * the list.
     *
     * @param name the name of the header.
     * @param value the value of the header.
     */
    void addHeader(String name, String value);

    /**
     * Overwrites the first header with the same name. The new header will be appended to
     * the end of the list, if no header with the given name can be found.
     *
     * @param header the header to set.
     */
    void setHeader(Header header);

    /**
     * Overwrites the first header with the same name. The new header will be appended to
     * the end of the list, if no header with the given name can be found.
     *
     * @param name the name of the header.
     * @param value the value of the header.
     */
    void setHeader(String name, String value);

    /**
     * Overwrites all the headers in the message.
     *
     * @param headers the array of headers to set.
     */
    void setHeaders(Header[] headers);

    /**
     * Removes a header from this message.
     *
     * @param header the header to remove.
     */
    void removeHeader(Header header);

    /**
     * Removes all headers with a certain name from this message.
     *
     * @param name The name of the headers to remove.
     */
    void removeHeaders(String name);

    /**
     * Returns an iterator of all the headers.
     *
     * @return Iterator that returns Header objects in the sequence they are
     *         sent over a connection.
     */
    HeaderIterator headerIterator();

    /**
     * Returns an iterator of the headers with a given name.
     *
     * @param name      the name of the headers over which to iterate, or
     *                  <code>null</code> for all headers
     *
     * @return Iterator that returns Header objects with the argument name
     *         in the sequence they are sent over a connection.
     */
    HeaderIterator headerIterator(String name);

    /**
     * Returns the parameters effective for this message as set by
     * {@link #setParams(HttpParams)}.
     *
     * @deprecated (4.3) use configuration classes provided 'cz.msebera.android.httpclient.config'
     *  and 'cz.msebera.android.httpclient.client.config'
     */
    @Deprecated
    HttpParams getParams();

    /**
     * Provides parameters to be used for the processing of this message.
     * @param params the parameters
     *
     * @deprecated (4.3) use configuration classes provided 'cz.msebera.android.httpclient.config'
     *  and 'cz.msebera.android.httpclient.client.config'
     */
    @Deprecated
    void setParams(HttpParams params);

}