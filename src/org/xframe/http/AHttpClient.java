package org.xframe.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.CharArrayBuffer;
import org.apache.http.util.EntityUtils;
import org.xframe.http.AHttpCallback.AHttpResult;

public class AHttpClient {

    public static void sendRequest(final AHttpRequest request, final AHttpCallback... callbacks) {

        try {
            sendRequest(request.buildRequest(), request, request.getAttr().charset(), callbacks);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            AHttpResult result = new AHttpResult();
            result.isSuccess = false;
            result.e = e;
            for (AHttpCallback callback : callbacks)
                callback.onFaild(result);
        }
    }

    public static void sendRequest(final HttpUriRequest request,
            final AHttpResponseHandler responseHandler, final String charset,
            final AHttpCallback... callbacks) {

        AHttpAsyncTask reqTask = new AHttpAsyncTask() {

            @Override
            protected void onPreExecute() {
                for (AHttpCallback callback : callbacks)
                    callback.onPreExecute();
            }

            @Override
            protected AHttpResult doInBackground(Void... params) {
                AHttpResult result = new AHttpResult();
                try {
                    HttpClient client = new DefaultHttpClient();
                    HttpResponse response = client.execute(request);
                    String content = readContent(response, charset);

                    result.isSuccess = true;
                    result.response = response;
                    result.content = content;
                    result.data = responseHandler.handleResponse(response, content);
                } catch (Exception e) {
                    e.printStackTrace();

                    result.isSuccess = false;
                    result.e = e;
                }
                return result;
            }

            @Override
            protected void onProgressUpdate(Object... values) {
                for (AHttpCallback callback : callbacks)
                    callback.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(AHttpResult result) {
                if (result.isSuccess) {
                    for (AHttpCallback callback : callbacks)
                        callback.onSuccess(result);
                } else {
                    for (AHttpCallback callback : callbacks)
                        callback.onFaild(result);
                }
            }

        };
        reqTask.execute();
    }

    private static String readContent(final HttpResponse response, final String defaultCharset)
            throws IOException, ParseException {
        // get instream
        HttpEntity entity = response.getEntity();
        InputStream instream = entity.getContent();
        Header contentEncoding = response.getFirstHeader("Content-Encoding");
        if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
            instream = new GZIPInputStream(instream);
        }

        // get content len
        int contentLen = (int) entity.getContentLength();
        if (contentLen < 0) {
            contentLen = 4096;
        }

        // get charset
        String charset = EntityUtils.getContentCharSet(entity);
        if (charset == null) {
            charset = defaultCharset;
        }
        if (charset == null) {
            charset = "utf-8";
        }

        Reader reader = new InputStreamReader(instream, charset);
        CharArrayBuffer buffer = new CharArrayBuffer(contentLen);
        try {
            char[] tmp = new char[1024];
            int l;
            int curLen = 0;
            while ((l = reader.read(tmp)) != -1) {
                buffer.append(tmp, 0, l);
                curLen += l;
                if (curLen > contentLen) {
                    curLen -= 4096;
                    curLen = curLen > 0 ? curLen : 0;
                }
            }
        } finally {
            reader.close();
        }
        return buffer.toString();
    }
}
