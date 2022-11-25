package m.vita.module.http.client.impl.client;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import m.vita.module.http.annotation.ThreadSafe;
import m.vita.module.http.client.BasicCredentialsProvider;
import m.vita.module.http.client.CredentialsProvider;
import m.vita.module.http.client.auth.AuthScope;
import m.vita.module.http.client.auth.Credentials;
import m.vita.module.http.client.auth.NTCredentials;
import m.vita.module.http.client.auth.UsernamePasswordCredentials;
import m.vita.module.http.config.AuthSchemes;
import m.vita.module.http.util.Args;

@ThreadSafe
public class SystemDefaultCredentialsProvider implements CredentialsProvider {

    private static final Map<String, String> SCHEME_MAP;

    static {
        SCHEME_MAP = new ConcurrentHashMap<String, String>();
        SCHEME_MAP.put(AuthSchemes.BASIC.toUpperCase(Locale.ENGLISH), "Basic");
        SCHEME_MAP.put(AuthSchemes.DIGEST.toUpperCase(Locale.ENGLISH), "Digest");
        SCHEME_MAP.put(AuthSchemes.NTLM.toUpperCase(Locale.ENGLISH), "NTLM");
        SCHEME_MAP.put(AuthSchemes.SPNEGO.toUpperCase(Locale.ENGLISH), "SPNEGO");
        SCHEME_MAP.put(AuthSchemes.KERBEROS.toUpperCase(Locale.ENGLISH), "Kerberos");
    }

    private static String translateScheme(final String key) {
        if (key == null) {
            return null;
        }
        final String s = SCHEME_MAP.get(key);
        return s != null ? s : key;
    }

    private final BasicCredentialsProvider internal;

    /**
     * Default constructor.
     */
    public SystemDefaultCredentialsProvider() {
        super();
        this.internal = new BasicCredentialsProvider();
    }

    public void setCredentials(final AuthScope authscope, final Credentials credentials) {
        internal.setCredentials(authscope, credentials);
    }

    private static PasswordAuthentication getSystemCreds(
            final AuthScope authscope,
            final Authenticator.RequestorType requestorType) {
        final String hostname = authscope.getHost();
        final int port = authscope.getPort();
        final String protocol = port == 443 ? "https" : "http";
        return Authenticator.requestPasswordAuthentication(
                hostname,
                null,
                port,
                protocol,
                null,
                translateScheme(authscope.getScheme()),
                null,
                requestorType);
    }

    public Credentials getCredentials(final AuthScope authscope) {
        Args.notNull(authscope, "Auth scope");
        final Credentials localcreds = internal.getCredentials(authscope);
        if (localcreds != null) {
            return localcreds;
        }
        if (authscope.getHost() != null) {
            PasswordAuthentication systemcreds = getSystemCreds(
                    authscope, Authenticator.RequestorType.SERVER);
            if (systemcreds == null) {
                systemcreds = getSystemCreds(
                        authscope, Authenticator.RequestorType.PROXY);
            }
            if (systemcreds != null) {
                final String domain = System.getProperty("http.auth.ntlm.domain");
                if (domain != null) {
                    return new NTCredentials(
                            systemcreds.getUserName(),
                            new String(systemcreds.getPassword()),
                            null, domain);
                } else {
                    if (AuthSchemes.NTLM.equalsIgnoreCase(authscope.getScheme())) {
                        // Domian may be specified in a fully qualified user name
                        return new NTCredentials(
                                systemcreds.getUserName(),
                                new String(systemcreds.getPassword()),
                                null, null);
                    } else {
                        return new UsernamePasswordCredentials(
                                systemcreds.getUserName(),
                                new String(systemcreds.getPassword()));
                    }
                }
            }
        }
        return null;
    }

    public void clear() {
        internal.clear();
    }

}
