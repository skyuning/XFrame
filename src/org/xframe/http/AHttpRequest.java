package org.xframe.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import android.text.TextUtils;

@AHttpAttr
abstract public class AHttpRequest implements AHttpResponseHandler {

    public static enum AHttpMethod {
        GET, POST, DELETE
    }

    private Map<String, String> mParamMap = new HashMap<String, String>();
    private AHttpAttr mAttr;

    public AHttpRequest() {
        mAttr = this.getClass().getAnnotation(AHttpAttr.class);
    }

    public AHttpAttr getAttr() {
        return mAttr;
    }

    abstract protected String buildUrl();

    public void addParam(String key, int value) {
        addParam(key, String.valueOf(value));
    }

    public void addParam(String key, String value) {
        mParamMap.put(key, value.toString());
    }

    public HttpUriRequest buildRequest() throws UnsupportedEncodingException {
        switch (mAttr.method()) {
            case GET:
                return buildGet();
            case POST:
                return buildPost();
            default:
                return null;
        }
    }

    private HttpGet buildGet() {
        List<String> keyValueList = new ArrayList<String>();
        for (Object key : mParamMap.keySet()) {
            String value = mParamMap.get(key);
            try {
                keyValueList.add(key + "=" + URLEncoder.encode(value, "utf-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        String paramString = TextUtils.join("&", keyValueList.toArray());

        HttpGet get = new HttpGet(buildUrl() + "?" + paramString);
        return get;
    }

    private HttpPost buildPost() throws UnsupportedEncodingException {
        ArrayList<NameValuePair> postData = new ArrayList<NameValuePair>();
        for (String key : mParamMap.keySet()) {
            postData.add(new BasicNameValuePair(key, mParamMap.get(key)));
        }

        HttpPost post = new HttpPost(buildUrl());
        post.setEntity(new UrlEncodedFormEntity(postData));

        return post;
    }
}