package com.safehouse.almasecure;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.safehouse.bodyguard.output.UrlOuputIntent;
import com.safehouse.bodyguard.utils.AccesibilitySettingUtils;

import de.blinkt.openvpn.R;

public class TextInterceptorResult extends AppCompatActivity {

    private UrlDataReceiver urlDataReceiver;
    private TextView textView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.text_interceptor_result);
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.hide();
            actionBar.setTitle("");
        }

        textView = findViewById(R.id.link);
        button = findViewById(R.id.button);

        urlDataReceiver = new UrlDataReceiver();
        this.registerReceiver(urlDataReceiver, UrlOuputIntent.Companion.getFilter());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AccesibilitySettingUtils.goAccess(TextInterceptorResult.this);
            }
        });
    }

    private void setText(String textToSet) {
        textView.append("\n");
        textView.append(textToSet);
    }

    private class UrlDataReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null) {
                return;
            }
            String urlToShow = intent.getStringExtra(UrlOuputIntent.URL_KEY);
            setText(urlToShow);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(urlDataReceiver);
    }
}
