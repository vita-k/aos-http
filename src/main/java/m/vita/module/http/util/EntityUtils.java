package m.vita.module.http.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;

import m.vita.module.http.HttpEntity;
import m.vita.module.http.HttpResponse;
import m.vita.module.http.exception.ParseException;
import m.vita.module.http.header.ContentType;
import m.vita.module.http.header.HeaderElement;
import m.vita.module.http.header.NameValuePair;

public final class EntityUtils {

    private EntityUtils() {
    }

    /**
     * Ensures that the entity content is fully consumed and the content stream, if exists,
     * is closed. The process is done, <i>quietly</i> , without throwing any IOException.
     *
     * @param entity the entity to consume.
     * @since 4.2
     */
    public static void consumeQuietly(final HttpEntity entity) {
        try {
            consume(entity);
        } catch (final IOException ignore) {
        }
    }

    /**
     * Ensures that the entity content is fully consumed and the content stream, if exists,
     * is closed.
     *
     * @param entity the entity to consume.
     * @throws IOException if an error occurs reading the input stream
     * @since 4.1
     */
    public static void consume(final HttpEntity entity) throws IOException {
        if (entity == null) {
            return;
        }
        if (entity.isStreaming()) {
            final InputStream instream = entity.getContent();
            if (instream != null) {
                instream.close();
            }
        }
    }

    /**
     * Updates an entity in a response by first consuming an existing entity, then setting the new one.
     *
     * @param response the response with an entity to update; must not be null.
     * @param entity   the entity to set in the response.
     * @throws IOException              if an error occurs while reading the input stream on the existing
     *                                  entity.
     * @throws IllegalArgumentException if response is null.
     * @since 4.3
     */
    public static void updateEntity(
            final HttpResponse response, final HttpEntity entity) throws IOException {
        Args.notNull(response, "Response");
        consume(response.getEntity());
        response.setEntity(entity);
    }

    /**
     * Read the contents of an entity and return it as a byte array.
     *
     * @param entity the entity to read from=
     * @return byte array containing the entity content. May be null if
     * {@link HttpEntity#getContent()} is null.
     * @throws IOException              if an error occurs reading the input stream
     * @throws IllegalArgumentException if entity is null or if content length > Integer.MAX_VALUE
     */
    public static byte[] toByteArray(final HttpEntity entity) throws IOException {
        Args.notNull(entity, "Entity");
        final InputStream instream = entity.getContent();
        if (instream == null) {
            return null;
        }
        try {
            Args.check(entity.getContentLength() <= Integer.MAX_VALUE,
                    "HTTP entity too large to be buffered in memory");
            int i = (int) entity.getContentLength();
            if (i < 0) {
                i = 4096;
            }
            final ByteArrayBuffer buffer = new ByteArrayBuffer(i);
            final byte[] tmp = new byte[4096];
            int l;
            while ((l = instream.read(tmp)) != -1) {
                buffer.append(tmp, 0, l);
            }
            return buffer.toByteArray();
        } finally {
            instream.close();
        }
    }

    /**
     * Obtains character set of the entity, if known.
     *
     * @param entity must not be null
     * @return the character set, or null if not found
     * @throws ParseException           if header elements cannot be parsed
     * @throws IllegalArgumentException if entity is null
     * @deprecated (4.1.3) use {@link ContentType#getOrDefault(HttpEntity)}
     */
    @Deprecated
    public static String getContentCharSet(final HttpEntity entity) throws ParseException {
        Args.notNull(entity, "Entity");
        String charset = null;
        if (entity.getContentType() != null) {
            final HeaderElement values[] = entity.getContentType().getElements();
            if (values.length > 0) {
                final NameValuePair param = values[0].getParameterByName("charset");
                if (param != null) {
                    charset = param.getValue();
                }
            }
        }
        return charset;
    }

    /**
     * Obtains MIME type of the entity, if known.
     *
     * @param entity must not be null
     * @return the character set, or null if not found
     * @throws ParseException           if header elements cannot be parsed
     * @throws IllegalArgumentException if entity is null
     * @since 4.1
     * @deprecated (4.1.3) use {@link ContentType#getOrDefault(HttpEntity)}
     */
    @Deprecated
    public static String getContentMimeType(final HttpEntity entity) throws ParseException {
        Args.notNull(entity, "Entity");
        String mimeType = null;
        if (entity.getContentType() != null) {
            final HeaderElement values[] = entity.getContentType().getElements();
            if (values.length > 0) {
                mimeType = values[0].getName();
            }
        }
        return mimeType;
    }

    /**
     * Get the entity content as a String, using the provided default character set
     * if none is found in the entity.
     * If defaultCharset is null, the default "ISO-8859-1" is used.
     *
     * @param entity         must not be null
     * @param defaultCharset character set to be applied if none found in the entity
     * @return the entity content as a String. May be null if
     * {@link HttpEntity#getContent()} is null.
     * @throws ParseException              if header elements cannot be parsed
     * @throws IllegalArgumentException    if entity is null or if content length > Integer.MAX_VALUE
     * @throws IOException                 if an error occurs reading the input stream
     * @throws UnsupportedCharsetException Thrown when the named charset is not available in
     *                                     this instance of the Java virtual machine
     */
    public static String toString(
            final HttpEntity entity, final Charset defaultCharset) throws IOException, ParseException {
        Args.notNull(entity, "Entity");
        final InputStream instream = entity.getContent();
        if (instream == null) {
            return null;
        }
        try {
            Args.check(entity.getContentLength() <= Integer.MAX_VALUE,
                    "HTTP entity too large to be buffered in memory");
            int i = (int) entity.getContentLength();
            if (i < 0) {
                i = 4096;
            }
            Charset charset = null;
            try {
                final ContentType contentType = ContentType.get(entity);
                if (contentType != null) {
                    charset = contentType.getCharset();
                }
            } catch (final UnsupportedCharsetException ex) {
                throw new UnsupportedEncodingException(ex.getMessage());
            }
            if (charset == null) {
                charset = defaultCharset;
            }
            if (charset == null) {
                charset = HTTP.DEF_CONTENT_CHARSET;
            }
            final Reader reader = new InputStreamReader(instream, charset);
            final CharArrayBuffer buffer = new CharArrayBuffer(i);
            final char[] tmp = new char[1024];
            int l;
            while ((l = reader.read(tmp)) != -1) {
                buffer.append(tmp, 0, l);
            }
            return buffer.toString();
        } finally {
            instream.close();
        }
    }

    /**
     * Get the entity content as a String, using the provided default character set
     * if none is found in the entity.
     * If defaultCharset is null, the default "ISO-8859-1" is used.
     *
     * @param entity         must not be null
     * @param defaultCharset character set to be applied if none found in the entity
     * @return the entity content as a String. May be null if
     * {@link HttpEntity#getContent()} is null.
     * @throws ParseException              if header elements cannot be parsed
     * @throws IllegalArgumentException    if entity is null or if content length > Integer.MAX_VALUE
     * @throws IOException                 if an error occurs reading the input stream
     * @throws UnsupportedCharsetException Thrown when the named charset is not available in
     *                                     this instance of the Java virtual machine
     */
    public static String toString(
            final HttpEntity entity, final String defaultCharset) throws IOException, ParseException {
        return toString(entity, defaultCharset != null ? Charset.forName(defaultCharset) : null);
    }

    /**
     * Read the contents of an entity and return it as a String.
     * The content is converted using the character set from the entity (if any),
     * failing that, "ISO-8859-1" is used.
     *
     * @param entity the entity to convert to a string; must not be null
     * @return String containing the content.
     * @throws ParseException              if header elements cannot be parsed
     * @throws IllegalArgumentException    if entity is null or if content length > Integer.MAX_VALUE
     * @throws IOException                 if an error occurs reading the input stream
     * @throws UnsupportedCharsetException Thrown when the named charset is not available in
     *                                     this instance of the Java virtual machine
     */
    public static String toString(final HttpEntity entity)
            throws IOException, ParseException {
        return toString(entity, (Charset) null);
    }
}
