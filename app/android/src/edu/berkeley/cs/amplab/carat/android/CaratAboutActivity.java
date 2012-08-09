package edu.berkeley.cs.amplab.carat.android;

import edu.berkeley.cs.amplab.carat.android.ui.LocalizedWebView;
import edu.berkeley.cs.amplab.carat.android.ui.SwipeListener;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class CaratAboutActivity extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.about, container, false);
        LocalizedWebView webview = (LocalizedWebView) v.findViewById(R.id.aboutView);
        webview.loadUrl("file:///android_asset/about.html");
        webview.setOnTouchListener(SwipeListener.instance);
        return v;
    }
}
