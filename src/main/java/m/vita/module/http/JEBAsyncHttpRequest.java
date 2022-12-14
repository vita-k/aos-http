package m.vita.module.http;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicBoolean;

import m.vita.module.http.client.AbstractHttpClient;
import m.vita.module.http.client.HttpRequestRetryHandler;
import m.vita.module.http.handler.RangeFileJEBAsyncHttpResponseHandler;
import m.vita.module.http.handler.ResponseHandlerInterface;
import m.vita.module.http.header.HttpContext;
import m.vita.module.http.header.HttpUriRequest;
import m.vita.module.http.util.Utils;

public class JEBAsyncHttpRequest implements Runnable {
    private final AbstractHttpClient client;
    private final HttpContext context;
    private final HttpUriRequest request;
    private final ResponseHandlerInterface responseHandler;
    private final AtomicBoolean isCancelled = new AtomicBoolean();
    private int executionCount;
    private boolean cancelIsNotified;
    private volatile boolean isFinished;
    private boolean isRequestPreProcessed;

    public JEBAsyncHttpRequest(AbstractHttpClient client, HttpContext context, HttpUriRequest request, ResponseHandlerInterface responseHandler) {
        this.client = Utils.notNull(client, "client");
        this.context = Utils.notNull(context, "context");
        this.request = Utils.notNull(request, "request");
        this.responseHandler = Utils.notNull(responseHandler, "responseHandler");
    }

    /**
     * This method is called once by the system when the request is about to be
     * processed by the system. The library makes sure that a single request
     * is pre-processed only once.
     * <p>&nbsp;</p>
     * Please note: pre-processing does NOT run on the main thread, and thus
     * any UI activities that you must perform should be properly dispatched to
     * the app's UI thread.
     *
     * @param request The request to pre-process
     */
    public void onPreProcessRequest(JEBAsyncHttpRequest request) {
        // default action is to do nothing...
    }

    /**
     * This method is called once by the system when the request has been fully
     * sent, handled and finished. The library makes sure that a single request
     * is post-processed only once.
     * <p>&nbsp;</p>
     * Please note: post-processing does NOT run on the main thread, and thus
     * any UI activities that you must perform should be properly dispatched to
     * the app's UI thread.
     *
     * @param request The request to post-process
     */
    public void onPostProcessRequest(JEBAsyncHttpRequest request) {
        // default action is to do nothing...
    }

    @Override
    public void run() {
        if (isCancelled()) {
            return;
        }

        // Carry out pre-processing for this request only once.
        if (!isRequestPreProcessed) {
            isRequestPreProcessed = true;
            onPreProcessRequest(this);
        }

        if (isCancelled()) {
            return;
        }

        responseHandler.sendStartMessage();

        if (isCancelled()) {
            return;
        }

        try {
            makeRequestWithRetries();
        } catch (IOException e) {
            if (!isCancelled()) {
                responseHandler.sendFailureMessage(0, null, null, e);
            } else {
                JEBAsyncHttpClient.log.e("JEBAsyncHttpRequest", "makeRequestWithRetries returned error", e);
            }
        }

        if (isCancelled()) {
            return;
        }

        responseHandler.sendFinishMessage();

        if (isCancelled()) {
            return;
        }

        // Carry out post-processing for this request.
        onPostProcessRequest(this);

        isFinished = true;
    }

    private void makeRequest() throws IOException {
        if (isCancelled()) {
            return;
        }

        // Fixes #115
        if (request.getURI().getScheme() == null) {
            // subclass of IOException so processed in the caller
            throw new MalformedURLException("No valid URI scheme was provided");
        }

        if (responseHandler instanceof RangeFileJEBAsyncHttpResponseHandler) {
            ((RangeFileJEBAsyncHttpResponseHandler) responseHandler).updateRequestHeaders(request);
        }

        HttpResponse response = client.execute(request, context);

        if (isCancelled()) {
            return;
        }

        // Carry out pre-processing for this response.
        responseHandler.onPreProcessResponse(responseHandler, response);

        if (isCancelled()) {
            return;
        }

        // The response is ready, handle it.
        responseHandler.sendResponseMessage(response);

        if (isCancelled()) {
            return;
        }

        // Carry out post-processing for this response.
        responseHandler.onPostProcessResponse(responseHandler, response);
    }

    private void makeRequestWithRetries() throws IOException {
        boolean retry = true;
        IOException cause = null;
        HttpRequestRetryHandler retryHandler = client.getHttpRequestRetryHandler();
        try {
            while (retry) {
                try {
                    makeRequest();
                    return;
                } catch (UnknownHostException e) {
                    // switching between WI-FI and mobile data networks can cause a retry which then results in an UnknownHostException
                    // while the WI-FI is initialising. The retry logic will be invoked here, if this is NOT the first retry
                    // (to assist in genuine cases of unknown host) which seems better than outright failure
                    cause = new IOException("UnknownHostException exception: " + e.getMessage());
                    retry = (executionCount > 0) && retryHandler.retryRequest(e, ++executionCount, context);
                } catch (NullPointerException e) {
                    // there's a bug in HttpClient 4.0.x that on some occasions causes
                    // DefaultRequestExecutor to throw an NPE, see
                    // https://code.google.com/p/android/issues/detail?id=5255
                    cause = new IOException("NPE in HttpClient: " + e.getMessage());
                    retry = retryHandler.retryRequest(cause, ++executionCount, context);
                } catch (IOException e) {
                    if (isCancelled()) {
                        // Eating exception, as the request was cancelled
                        return;
                    }
                    cause = e;
                    retry = retryHandler.retryRequest(cause, ++executionCount, context);
                }
                if (retry) {
                    responseHandler.sendRetryMessage(executionCount);
                }
            }
        } catch (Exception e) {
            // catch anything else to ensure failure message is propagated
            JEBAsyncHttpClient.log.e("JEBAsyncHttpRequest", "Unhandled exception origin cause", e);
            cause = new IOException("Unhandled exception: " + e.getMessage());
        }

        // cleaned up to throw IOException
        throw (cause);
    }

    public boolean isCancelled() {
        boolean cancelled = isCancelled.get();
        if (cancelled) {
            sendCancelNotification();
        }
        return cancelled;
    }

    private synchronized void sendCancelNotification() {
        if (!isFinished && isCancelled.get() && !cancelIsNotified) {
            cancelIsNotified = true;
            responseHandler.sendCancelMessage();
        }
    }

    public boolean isDone() {
        return isCancelled() || isFinished;
    }

    public boolean cancel(boolean mayInterruptIfRunning) {
        isCancelled.set(true);
        request.abort();
        return isCancelled();
    }

    /**
     * Will set Object as TAG to this request, wrapped by WeakReference
     *
     * @param TAG Object used as TAG to this RequestHandle
     * @return this JEBAsyncHttpRequest to allow fluid syntax
     */
    public JEBAsyncHttpRequest setRequestTag(Object TAG) {
        this.responseHandler.setTag(TAG);
        return this;
    }

    /**
     * Will return TAG of this JEBAsyncHttpRequest
     *
     * @return Object TAG, can be null, if it's been already garbage collected
     */
    public Object getTag() {
        return this.responseHandler.getTag();
    }
}
