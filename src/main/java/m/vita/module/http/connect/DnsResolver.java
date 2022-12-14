package m.vita.module.http.connect;

import java.net.InetAddress;
import java.net.UnknownHostException;

public interface DnsResolver {

    /**
     * Returns the IP address for the specified host name, or null if the given
     * host is not recognized or the associated IP address cannot be used to
     * build an InetAddress instance.
     *
     * @see InetAddress
     *
     * @param host
     *            The host name to be resolved by this resolver.
     * @return The IP address associated to the given host name, or null if the
     *         host name is not known by the implementation class.
     */
    InetAddress[] resolve(String host) throws UnknownHostException;

}
