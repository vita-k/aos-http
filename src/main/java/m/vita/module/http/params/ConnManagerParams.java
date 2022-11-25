package m.vita.module.http.params;

import m.vita.module.http.annotation.Immutable;
import m.vita.module.http.header.HttpParams;
import m.vita.module.http.header.ClientConnectionManager;
import m.vita.module.http.header.HttpRoute;
import m.vita.module.http.util.Args;
import m.vita.module.http.connect.HttpConnectionParams;
import m.vita.module.http.header.ManagedClientConnection;

@Immutable
public final class ConnManagerParams implements ConnManagerPNames {

    /** The default maximum number of connections allowed overall */
    public static final int DEFAULT_MAX_TOTAL_CONNECTIONS = 20;

    /**
     * Returns the timeout in milliseconds used when retrieving a
     * {@link ManagedClientConnection} from the
     * {@link ClientConnectionManager}.
     *
     * @return timeout in milliseconds.
     *
     * @deprecated (4.1)  use {@link
     *   HttpConnectionParams#getConnectionTimeout(HttpParams)}
     */
    @Deprecated
    public static long getTimeout(final HttpParams params) {
        Args.notNull(params, "HTTP parameters");
        return params.getLongParameter(TIMEOUT, 0);
    }

    /**
     * Sets the timeout in milliseconds used when retrieving a
     * {@link ManagedClientConnection} from the
     * {@link ClientConnectionManager}.
     *
     * @param timeout the timeout in milliseconds
     *
     * @deprecated (4.1)  use {@link
     *   HttpConnectionParams#setConnectionTimeout(HttpParams, int)}
     */
    @Deprecated
    public static void setTimeout(final HttpParams params, final long timeout) {
        Args.notNull(params, "HTTP parameters");
        params.setLongParameter(TIMEOUT, timeout);
    }

    /** The default maximum number of connections allowed per host */
    private static final ConnPerRoute DEFAULT_CONN_PER_ROUTE = new ConnPerRoute() {

        public int getMaxForRoute(final HttpRoute route) {
            return ConnPerRouteBean.DEFAULT_MAX_CONNECTIONS_PER_ROUTE;
        }

    };

    /**
     * Sets lookup interface for maximum number of connections allowed per route.
     *
     * @param params HTTP parameters
     * @param connPerRoute lookup interface for maximum number of connections allowed
     *        per route
     */
    public static void setMaxConnectionsPerRoute(final HttpParams params,
                                                 final ConnPerRoute connPerRoute) {
        Args.notNull(params, "HTTP parameters");
        params.setParameter(MAX_CONNECTIONS_PER_ROUTE, connPerRoute);
    }

    /**
     * Returns lookup interface for maximum number of connections allowed per route.
     *
     * @param params HTTP parameters
     *
     * @return lookup interface for maximum number of connections allowed per route.
     */
    public static ConnPerRoute getMaxConnectionsPerRoute(final HttpParams params) {
        Args.notNull(params, "HTTP parameters");
        ConnPerRoute connPerRoute = (ConnPerRoute) params.getParameter(MAX_CONNECTIONS_PER_ROUTE);
        if (connPerRoute == null) {
            connPerRoute = DEFAULT_CONN_PER_ROUTE;
        }
        return connPerRoute;
    }

    /**
     * Sets the maximum number of connections allowed.
     *
     * @param params HTTP parameters
     * @param maxTotalConnections The maximum number of connections allowed.
     */
    public static void setMaxTotalConnections(
            final HttpParams params,
            final int maxTotalConnections) {
        Args.notNull(params, "HTTP parameters");
        params.setIntParameter(MAX_TOTAL_CONNECTIONS, maxTotalConnections);
    }

    /**
     * Gets the maximum number of connections allowed.
     *
     * @param params HTTP parameters
     *
     * @return The maximum number of connections allowed.
     */
    public static int getMaxTotalConnections(
            final HttpParams params) {
        Args.notNull(params, "HTTP parameters");
        return params.getIntParameter(MAX_TOTAL_CONNECTIONS, DEFAULT_MAX_TOTAL_CONNECTIONS);
    }

}
