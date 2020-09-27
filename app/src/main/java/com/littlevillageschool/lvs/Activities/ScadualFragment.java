package com.littlevillageschool.lvs.Activities;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import com.littlevillageschool.lvs.LvsApplication;
import com.littlevillageschool.lvs.Model.Student;
import com.littlevillageschool.lvs.R;

/**
 * Created by Alalaa Center on 15/08/2016.
 */
public class ScadualFragment extends android.support.v4.app.Fragment {

    private WebView webView;
    String url;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.schadual_fragment_layout, container, false);

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        webView = (WebView) view.findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Loading..");
        progressDialog.setMax(100);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100)
                    progressDialog.setProgress(newProgress);
                else
                    progressDialog.hide();
            }


        });

        url = ((Student) LvsApplication.currUser).getSchadualURL();
        if (TextUtils.isEmpty(url))
            Toast.makeText(getActivity(), R.string.schedual_has_not_been_uploaded, Toast.LENGTH_SHORT).show();
        else {
            progressDialog.show();
            webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + url);
        }

    }
}
