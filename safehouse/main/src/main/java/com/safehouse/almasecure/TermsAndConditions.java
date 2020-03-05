package com.safehouse.almasecure;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.mixpanel.android.mpmetrics.MixpanelAPI;

import org.json.JSONException;
import org.json.JSONObject;

import de.blinkt.openvpn.R;

public class TermsAndConditions extends AppCompatActivity {

    WebView mWebView;
    LinearLayout mTermsAndConditionsLayout;
    ImageButton mCloseTermsAndConditions;
    Button mAcceptTermsAndConditions;
    MixpanelAPI mixpanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);
        getSupportActionBar().hide();

        mixpanel =
                MixpanelAPI.getInstance(getApplicationContext(), getResources().getString(R.string.MIXPANAL_TOKEN));
        JSONObject props = new JSONObject();
        try {
            props.put(getString(R.string.analytics_from), getString(R.string.login_screen));
            props.put(getString(R.string.analytics_to), getString(R.string.terms_and_condition_screen));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mixpanel.track("Terms and Conditions Screen Appeared", props);

        mTermsAndConditionsLayout = findViewById(R.id.terms_and_conditions);
        mCloseTermsAndConditions = findViewById(R.id.close_terms);
        mAcceptTermsAndConditions = findViewById(R.id.accept_terms);

        mCloseTermsAndConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mAcceptTermsAndConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mWebView = findViewById(R.id.webView);
        mWebView.loadUrl("file:///android_asset/bg_html.html");
    }
}
