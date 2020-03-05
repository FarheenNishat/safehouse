package com.safehouse.almasecure;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.safehouse.almasecure.model.PawnedModel;

import org.json.JSONException;
import org.json.JSONObject;


import de.blinkt.openvpn.R;

public class ReccomendationActivity extends AppCompatActivity {

    private PawnedModel data;
    Typeface face;
    Typeface boldFace;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_recomendations);
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        face =  Typeface.createFromAsset(getAssets(),
                "titillium_regular.otf");
        boldFace = Typeface.createFromAsset(getAssets(),
                "titillium_bold.otf");

        if (actionBar != null) {
            actionBar.hide();
            actionBar.setTitle("");
        }

        Intent intent = getIntent();
        String arrStr = intent.getStringExtra("jsonObject");
        try {
            JSONObject obj = new JSONObject(arrStr);
            data = PawnedModel.fromJsonObject(obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        TextView title = findViewById(R.id.title);
        title.setText(data.getName());
        title.setTypeface(boldFace);

        TextView changePasword = findViewById(R.id.tv3);
        changePasword.setTypeface(boldFace);
        TextView changePaswdDesc = findViewById(R.id.changePaswdDesc);
        changePaswdDesc.setTypeface(face);
        TextView complexPaswd = findViewById(R.id.tv2);
        complexPaswd.setTypeface(boldFace);
        TextView complexPaswdDescr = findViewById(R.id.complexPaswdDescr);
        complexPaswdDescr.setTypeface(face);
        TextView uniquePaswd = findViewById(R.id.tv1);
        uniquePaswd.setTypeface(boldFace);
        TextView uniquePaswdDesc = findViewById(R.id.uniquePaswdDesc);
        uniquePaswdDesc.setTypeface(face);


        TextView description = findViewById(R.id.description);
        description.setText(Html.fromHtml(data.getDescription()));
        description.setTypeface(face);
        description.setMovementMethod(LinkMovementMethod.getInstance());

        Button btn =  findViewById(R.id.back_btn);
        btn.setTypeface(boldFace);
        
        findViewById(R.id.back_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
