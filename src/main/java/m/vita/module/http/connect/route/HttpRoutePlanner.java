package m.vita.module.http.connect.route;

import m.vita.module.http.exception.HttpException;
import m.vita.module.http.header.HttpContext;
import m.vita.module.http.header.HttpHost;
import m.vita.module.http.header.HttpRequest;
import m.vita.module.http.header.HttpRoute;

public interface HttpRoutePlanner {

    /**
     * Determines the route for a request.
     *
     * @param target    the target host for the request.
     *                  Implementations may accept <code>null</code>
     *                  if they can still determine a route, for example
     *                  to a default target or by inspecting the request.
     * @param request   the request to execute
     * @param context   the context to use for the subsequent execution.
     *                  Implementations may accept <code>null</code>.
     *
     * @return  the route that the request should take
     *
     * @throws HttpException    in case of a problem
     */
    public HttpRoute determineRoute(HttpHost target,
                                    HttpRequest request,
                                    HttpContext context) throws HttpException;

}
