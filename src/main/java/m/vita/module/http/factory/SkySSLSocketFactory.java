package m.vita.module.http.factory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import m.vita.module.http.client.Scheme;
import m.vita.module.http.client.impl.client.DefaultHttpClient;
import m.vita.module.http.connect.tsccm.ThreadSafeClientConnManager;
import m.vita.module.http.header.BasicHttpParams;
import m.vita.module.http.header.ClientConnectionManager;
import m.vita.module.http.header.HttpParams;
import m.vita.module.http.params.HttpProtocolParams;
import m.vita.module.http.util.HTTP;
import m.vita.module.http.util.HttpVersion;


public class JEBSSLSocketFactory  extends SSLSocketFactory {
    final SSLContext sslContext = SSLContext.getInstance("TLS");

    /**
     * Creates a new SSL Socket Factory with the given KeyStore.
     *
     * @param truststore A KeyStore to create the SSL Socket Factory in context of
     * @throws NoSuchAlgorithmException  NoSuchAlgorithmException
     * @throws KeyManagementException    KeyManagementException
     * @throws KeyStoreException         KeyStoreException
     * @throws UnrecoverableKeyException UnrecoverableKeyException
     */
    public JEBSSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        super(truststore);

        X509TrustManager tm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };

        sslContext.init(null, new TrustManager[]{tm}, null);
    }

    /**
     * Gets a KeyStore containing the Certificate
     *
     * @param cert InputStream of the Certificate
     * @return KeyStore
     */
    public static KeyStore getKeystoreOfCA(InputStream cert) {

        // Load CAs from an InputStream
        InputStream caInput = null;
        Certificate ca = null;
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            caInput = new BufferedInputStream(cert);
            ca = cf.generateCertificate(caInput);
        } catch (CertificateException e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (caInput != null) {
                    caInput.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Create a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = null;
        try {
            keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keyStore;
    }

    /**
     * Gets a Default KeyStore
     *
     * @return KeyStore
     */
    public static KeyStore getKeystore() {
        KeyStore trustStore = null;
        try {
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return trustStore;
    }

    /**
     * Returns a SSlSocketFactory which trusts all certificates
     *
     * @return SSLSocketFactory
     */
    public static SSLSocketFactory getFixedSocketFactory() {
        SSLSocketFactory socketFactory;
        try {
            socketFactory = new JEBSSLSocketFactory(getKeystore());
            socketFactory.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        } catch (Throwable t) {
            t.printStackTrace();
            socketFactory = SSLSocketFactory.getSocketFactory();
        }
        return socketFactory;
    }

    /**
     * Gets a DefaultHttpClient which trusts a set of certificates specified by the KeyStore
     *
     * @param keyStore custom provided KeyStore instance
     * @return DefaultHttpClient
     */
    public static DefaultHttpClient getNewHttpClient(KeyStore keyStore) {

        try {
            SSLSocketFactory sf = new JEBSSLSocketFactory(keyStore);
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
        return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
    }

    @Override
    public Socket createSocket() throws IOException {
        return sslContext.getSocketFactory().createSocket();
    }

    /**
     * Makes HttpsURLConnection trusts a set of certificates specified by the KeyStore
     */
    public void fixHttpsURLConnection() {
        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
    }

}
