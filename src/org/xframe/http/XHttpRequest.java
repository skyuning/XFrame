package org.xframe.http;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

@XHttpAttr
abstract public class XHttpRequest implements XHttpResponseHandler {

    public static enum XHttpMethod {
        GET, POST, DELETE
    }

    private List<NameValuePair> mParams = new ArrayList<NameValuePair>();
    private MultipartEntity mMultipartEntity;
    private XHttpAttr mAttr;

    public XHttpRequest() {
        mAttr = this.getClass().getAnnotation(XHttpAttr.class);
    }

    public XHttpAttr getAttr() {
        return mAttr;
    }

    abstract protected String buildUrl();

    public void addParam(String key, int value) {
        addParam(key, String.valueOf(value));
    }

    public void addParam(String key, String value) {
        mParams.add(new BasicNameValuePair(key, value));
    }
    
    private void addMultipart(String name, ContentBody contentBody) {
        if (null == mMultipartEntity)
            mMultipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
        mMultipartEntity.addPart(name, contentBody);
    }
    
    public void addMultipartString(String name, String value) throws UnsupportedEncodingException {
        addMultipart(name, new StringBody(value));
    }

    public void addMultipartFile(String name, String path, String mineType) throws UnsupportedEncodingException {
        addMultipart(name, new FileBody(new File(path), mineType));
    }

    public HttpUriRequest buildRequest() throws IOException {
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(mParams, "utf-8");
        switch (mAttr.method()) {
            case GET:
                HttpGet get = new HttpGet(buildUrl() + "?" + EntityUtils.toString(entity));
                return get;
                
            case POST:
                HttpPost post = new HttpPost(buildUrl());
                if (null == mMultipartEntity)
                    post.setEntity(new UrlEncodedFormEntity(mParams));
                else
                    post.setEntity(mMultipartEntity);
                return post;
                
            default:
                return null;
        }
    }
}