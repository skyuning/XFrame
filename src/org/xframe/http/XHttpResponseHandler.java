package org.xframe.http;

import org.apache.http.HttpResponse;

public interface XHttpResponseHandler {
    public Object handleResponse(HttpResponse response, String content) throws Exception;
}
