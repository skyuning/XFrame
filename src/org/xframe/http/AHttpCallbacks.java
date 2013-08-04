package org.xframe.http;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.xframe.R;

import android.app.AlertDialog;
import android.content.Context;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AHttpCallbacks {

    public static class DefaultHttpCallback implements AHttpCallback {

        @Override
        public void onPreExecute() {
        };

        @Override
        public void onProgressUpdate(Object... values) {
        };

        @Override
        public void onSuccess(AHttpResult result) {
        };

        @Override
        public void onFaild(AHttpResult result) {
        }
    }

    public static class DebugHttpCallback implements AHttpCallback {

        protected static final int MESSAGE_CONTENT_LEN = 1;
        protected static final int MESSAGE_PROGRESS = 2;

        private AlertDialog mDialog;
        private TextView mTitleTv;
        private WebView mWebView;
        private ProgressBar mProgressBar;

        public DebugHttpCallback(Context context) {
            mDialog = new AlertDialog.Builder(context).create();
        }

        @Override
        public void onPreExecute() {
            mDialog.show();
            mDialog.setContentView(R.layout.http_request_dialog);
            mTitleTv = (TextView) mDialog.findViewById(R.id.title);
            mWebView = (WebView) mDialog.findViewById(R.id.webView1);
            mProgressBar = (ProgressBar) mDialog.findViewById(R.id.progressBar1);
        }

        public void onProgressUpdate(Object... values) {
            int msgType = (Integer) values[0];
            switch (msgType) {
                case MESSAGE_CONTENT_LEN:
                    int max = (Integer) values[1];
                    mProgressBar.setMax(max);
                    mTitleTv.setText("Receiving data ...");
                    break;

                case MESSAGE_PROGRESS:
                    int progress = (Integer) values[1];
                    mProgressBar.setProgress(progress);
                    break;

                default:
                    break;
            }
        };

        public void onSuccess(AHttpResult result) {
            String mimeType = result.response.getEntity().getContentType().getElements()[0]
                    .getName();
            if (null == mimeType)
                mimeType = "text/plain";

            mTitleTv.setText("Success");
            mWebView.loadDataWithBaseURL(null, result.content, mimeType, "utf-8", null);
        }

        public void onFaild(AHttpResult result) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            result.e.printStackTrace(pw);
            String stackTrace = sw.toString();

            mTitleTv.setText("Faild");
            mWebView.loadDataWithBaseURL(null, stackTrace, "text/plain", "utf-8", null);
        }
    }
}
