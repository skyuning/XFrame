package org.xframe.http;

import org.apache.http.HttpResponse;

abstract public interface AHttpCallback {

    public static class AHttpResult {
        public boolean isSuccess;
        public HttpResponse response;
        public String content;
        public Object data;
        public Exception e;
    }

    public static final int MESSAGE_CONTENT_LEN = 1;
    public static final int MESSAGE_PROGRESS = 2;

    public void onPreExecute();

    public void onProgressUpdate(Object... values);

    public void onSuccess(AHttpResult result);

    public void onFaild(AHttpResult result);
}
