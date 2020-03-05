package com.safehouse.almasecure;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.safehouse.almasecure.model.PawnedModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.blinkt.openvpn.R;


public class HBIPListActivity extends AppCompatActivity {

    private JSONArray arr;
    public ArrayList<PawnedModel> list =  new ArrayList<PawnedModel>();
    LinearLayout llLayout;
    MixpanelAPI mixpanel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_hibp_list);
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();


        if (actionBar != null) {
            actionBar.hide();
            actionBar.setTitle("");
        }
        mixpanel =
                MixpanelAPI.getInstance(getApplicationContext(), getResources().getString(R.string.MIXPANAL_TOKEN));

        llLayout = findViewById(R.id.ll_frame);

        TextView title = findViewById(R.id.title);
        title.setTypeface(Typeface.createFromAsset(getAssets(),
                "titillium_bold.otf"));

        Intent intent = getIntent();
        String arrStr = intent.getStringExtra("jsonArray");
        try {
            arr = new JSONArray(arrStr);
            ArrayList<PawnedModel> tmp =  new ArrayList<PawnedModel>();
            for (int i = 0; i < arr.length(); i++) {
                tmp.add(PawnedModel.fromJsonObject(arr.getJSONObject(i)));
            }
            list = tmp;

            if (list.size() > 0) {
                configureList();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ImageButton back = findViewById(R.id.backButton);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    void configureList() {

        for (final PawnedModel model : list) {
            View child = getLayoutInflater().inflate(R.layout.unsecure_list_item, null);
            ((TextView)child.findViewById(R.id.tv_title)).setText(model.getName());
            ((TextView)child.findViewById(R.id.tv_title)).setTypeface(Typeface.createFromAsset(getAssets(),
                    "titillium_bold.otf"));
            ((TextView)child.findViewById(R.id.tv_date)).setText(model.getBreachDate());
            ((TextView)child.findViewById(R.id.tv_date)).setTypeface(Typeface.createFromAsset(getAssets(),
                    "titillium_bold.otf"));
            llLayout.addView(child);
            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(HBIPListActivity.this, ReccomendationActivity.class);
                    String message = model.getObject().toString();
                    intent.putExtra("jsonObject", message);
                    JSONObject props = new JSONObject();
                    try {
                        props.put(getString(R.string.analytics_from), getString(R.string.am_i_exposed_view_all_screen));
                        props.put(getString(R.string.analytics_to)  , getString(R.string.am_i_exposed_specific_breach_screen));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mixpanel.track("Am I Exposed View Specific Report Appeared", props);
                    startActivity(intent);
                }
            });
        }

    }
}
