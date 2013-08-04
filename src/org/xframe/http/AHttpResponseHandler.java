package org.xframe.http;

import org.apache.http.HttpResponse;

public interface AHttpResponseHandler {
    public Object handleResponse(HttpResponse response, String content) throws Exception;
}
