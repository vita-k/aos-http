package m.vita.module.http.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import m.vita.module.http.exception.ConnectTimeoutException;
import m.vita.module.http.factory.SchemeSocketFactory;
import m.vita.module.http.factory.SocketFactory;
import m.vita.module.http.header.HttpParams;

class SchemeSocketFactoryAdaptor implements SchemeSocketFactory {

    private final SocketFactory factory;

    SchemeSocketFactoryAdaptor(final SocketFactory factory) {
        super();
        this.factory = factory;
    }

    public Socket connectSocket(
            final Socket sock,
            final InetSocketAddress remoteAddress,
            final InetSocketAddress localAddress,
            final HttpParams params) throws IOException, UnknownHostException, ConnectTimeoutException {
        final String host = remoteAddress.getHostName();
        final int port = remoteAddress.getPort();
        InetAddress local = null;
        int localPort = 0;
        if (localAddress != null) {
            local = localAddress.getAddress();
            localPort = localAddress.getPort();
        }
        return this.factory.connectSocket(sock, host, port, local, localPort, params);
    }

    public Socket createSocket(final HttpParams params) throws IOException {
        return this.factory.createSocket();
    }

    public boolean isSecure(final Socket sock) throws IllegalArgumentException {
        return this.factory.isSecure(sock);
    }

    public SocketFactory getFactory() {
        return this.factory;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof SchemeSocketFactoryAdaptor) {
            return this.factory.equals(((SchemeSocketFactoryAdaptor)obj).factory);
        } else {
            return this.factory.equals(obj);
        }
    }

    @Override
    public int hashCode() {
        return this.factory.hashCode();
    }

}
