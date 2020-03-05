package com.safehouse.almasecure;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.safehouse.almasecure.model.PawnedModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.blinkt.openvpn.R;
import de.blinkt.openvpn.core.ConnectionStatus;
import de.blinkt.openvpn.core.VpnStatus;

public class HIBPActivity extends AppCompatActivity implements VpnStatus.StateListener {


    public ArrayList<PawnedModel> list = new ArrayList<PawnedModel>();

    EditText editText;
    Button checkBtn, backBtn;
    RelativeLayout llLayout;
    ImageView ivRecommended;
    RelativeLayout recommendedLayout;
    TextView tvCheck, tvSecure, tvSecure2, tvUnsecure, view2, view3, view4, viewAll, tvrecommend, tvtakeAction, tvOnlineBreachTitle, tvBreachReport;
    View view1;
    ProgressBar progressbar;
    JSONArray arr;
    MixpanelAPI mixpanel;
    LottieAnimationView animationView;
    private RecyclerView breachListRecyclerView;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_hibp);
        androidx.appcompat.app.ActionBar actionBar = getSupportActionBar();
        mixpanel =
                MixpanelAPI.getInstance(getApplicationContext(), getResources().getString(R.string.MIXPANAL_TOKEN));

        if (actionBar != null) {
            actionBar.hide();
            actionBar.setTitle("");
        }

        Typeface face = Typeface.createFromAsset(getAssets(),
                "titillium_regular.otf");
        Typeface boldFace = Typeface.createFromAsset(getAssets(),
                "titillium_bold.otf");
        Typeface MuliFace = Typeface.createFromAsset(getAssets(),
                "Muli_Regular.ttf");

       /* JSONObject props = new JSONObject();
        try {
            props.put("from", "Main Screen");
            props.put("to", "Am I Exposed Search");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mixpanel.track("Am I Exposed Search Screen Appeared", props);
*/
        animationView = findViewById(R.id.animationView);
        animationView.setAnimation(R.raw.blue);
        animationView.playAnimation();
        animationView.setRepeatCount(Integer.MAX_VALUE);


        ImageButton btnAmISecure = findViewById(R.id.backButton);
        btnAmISecure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editText = findViewById(R.id.EditText);
        editText.setTypeface(face);
        ivRecommended = findViewById(R.id.recommended_icon);
        tvOnlineBreachTitle = findViewById(R.id.tvOnlineBreachTitle);
        breachListRecyclerView = (RecyclerView) findViewById(R.id.breach_list);
        breachListRecyclerView.setHasFixedSize(true);
        breachListRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        tvOnlineBreachTitle.setTypeface(boldFace);
        checkBtn = findViewById(R.id.buttonLogin);
        checkBtn.setTypeface(boldFace);
        checkBtn.setTransformationMethod(null);
        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidEmail(editText.getText().toString().trim())) {
                    JSONObject btnProps = new JSONObject();
                    try {
                        btnProps.put(getString(R.string.analytics_from), getString(R.string.am_i_exposed_screen));
                        btnProps.put("Searched email", editText.getText().toString().trim());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mixpanel.track("Lets Check Button Clicked on Am I Exposed Search Screen", btnProps);
                    checkMail(editText.getText().toString().trim());
                    progressbar.setVisibility(View.VISIBLE);
                    checkBtn.setEnabled(false);
                } else {
                    Toast.makeText(HIBPActivity.this, "Please enter a valid email ID", Toast.LENGTH_LONG).show();
                }
            }
        });

        llLayout = (RelativeLayout) findViewById(R.id.ll_frame);
        llLayout.setVisibility(View.GONE);

        recommendedLayout = findViewById(R.id.recommended_view);
        recommendedLayout.setVisibility(View.GONE);

        tvtakeAction = findViewById(R.id.recommend);
        tvtakeAction.setTypeface(face);
        tvtakeAction.setVisibility(View.GONE);

        backBtn = findViewById(R.id.back_btn);
        backBtn.setTransformationMethod(null);
        backBtn.setVisibility(View.GONE);
        backBtn.setTypeface(boldFace);

        tvCheck = findViewById(R.id.tv_check);

        SpannableStringBuilder secureSs = new SpannableStringBuilder("AM I EXPOSED?");
        secureSs.setSpan(new CustomTypefaceSpan("", face), 0, 5, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        secureSs.setSpan(new CustomTypefaceSpan("", boldFace), 5, 13, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        tvCheck.setText(secureSs);

        tvrecommend = findViewById(R.id.recommend_text);
        tvrecommend.setTypeface(face);
        tvBreachReport = findViewById(R.id.tvBreachReport);
        tvBreachReport.setTypeface(boldFace);

        tvSecure = findViewById(R.id.tv_secure);
        tvSecure.setVisibility(View.GONE);
        tvSecure.setTypeface(boldFace);
        tvSecure2 = findViewById(R.id.tv_secure2);
        tvSecure2.setVisibility(View.GONE);
        tvSecure2.setTypeface(face);
        tvUnsecure = findViewById(R.id.tv_not_secure);

        SpannableStringBuilder unsecureSs = new SpannableStringBuilder("AM I EXPOSED?");
        unsecureSs.setSpan(new CustomTypefaceSpan("", face), 0, 5, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        unsecureSs.setSpan(new CustomTypefaceSpan("", boldFace), 6, 13, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        tvUnsecure.setText(unsecureSs);

        tvUnsecure.setVisibility(View.GONE);

        view1 = findViewById(R.id.view1);
        view2 = findViewById(R.id.view2);
        view2.setTypeface(face);
        view3 = findViewById(R.id.view3);
        view3.setTypeface(face);
        view4 = findViewById(R.id.view4);
        view4.setTypeface(boldFace);

        progressbar = findViewById(R.id.progressbar);

        Button back = findViewById(R.id.back_btn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject btnProps = new JSONObject();
                try {
                    if (list.size() == 0) {
                        btnProps.put("Breaches Count", "0");
                        mixpanel.track("Back to Bodyguard Button Clicked on Am I Exposed No breaches Found", btnProps);
                    } else {
                        btnProps.put("Breaches Count", list.size());
                        mixpanel.track("Back to Bodyguard Button Clicked on Am I Exposed some breaches were Found", btnProps);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                finish();
            }
        });

        viewAll = findViewById(R.id.viewAll);
        viewAll.setTypeface(MuliFace);
        viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(HIBPActivity.this, HBIPListActivity.class);
                    String message = arr.toString();
                    intent.putExtra("jsonArray", message);
                    JSONObject props = new JSONObject();
                    try {
                        props.put("From", getString(R.string.am_i_exposed_screen));
                        props.put("To", getString(R.string.am_i_exposed_view_all_screen));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mixpanel.track("Am I Exposed View All Breaches Appeared", props);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        VpnStatus.addStateListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        VpnStatus.removeStateListener(this);
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }


    void checkMail(String mail) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://18.200.83.183:80/verdict/hibp/" + mail;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        progressbar.setVisibility(View.GONE);
                        checkBtn.setEnabled(true);

                        try {
                            arr = response.getJSONArray("data");
                            ArrayList<PawnedModel> tmp = new ArrayList<PawnedModel>();
                            for (int i = 0; i < arr.length(); i++) {
                                tmp.add(PawnedModel.fromJsonObject(arr.getJSONObject(i)));
                            }
                            list = tmp;
                            if (list.size() > 0) {
                                mAdapter = new BreachAdapter(list, HIBPActivity.this);
                                breachListRecyclerView.setAdapter(mAdapter);
                                configUnsecure();
                            } else {
                                configSecure();
                            }
                            setAnimation();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressbar.setVisibility(View.GONE);
                        checkBtn.setEnabled(true);
                        AlertDialog.Builder builder = new AlertDialog.Builder(HIBPActivity.this);
                        builder.setTitle("");
                        builder.setMessage(R.string.experiencing_high_requests);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        builder.show();
                        error.printStackTrace();

                    }
                });
        queue.add(jsonObjectRequest);
    }

    private void setAnimation() {
        if (list.size() > 0) {
            if (VpnStatus.isVPNActive()) {
                animationView.setAnimation(R.raw.yellow);
                animationView.playAnimation();
                animationView.setRepeatCount(Integer.MAX_VALUE);
            } else {
                animationView.setAnimation(R.raw.red);
                animationView.playAnimation();
                animationView.setRepeatCount(Integer.MAX_VALUE);
            }
        } else {
            animationView.setAnimation(R.raw.blue);
            animationView.playAnimation();
            animationView.setRepeatCount(Integer.MAX_VALUE);
        }
    }


    void configSecure() {
        JSONObject props = new JSONObject();
        try {
            props.put("Breaches Count", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mixpanel.track("Am I Exposed No Breaches Were Found", props);
        llLayout.setVisibility(View.GONE);
        backBtn.setVisibility(View.VISIBLE);
        editText.setVisibility(View.GONE);
        tvCheck.setVisibility(View.GONE);
        tvSecure.setVisibility(View.VISIBLE);
        tvSecure2.setVisibility(View.VISIBLE);
        tvUnsecure.setVisibility(View.GONE);
        checkBtn.setVisibility(View.GONE);

        view1.setVisibility(View.GONE);
        view2.setVisibility(View.GONE);
        view4.setVisibility(View.GONE);
    }

    void setIconandTextForBodyGuardRecommendation() {
        if (VpnStatus.isVPNActive()) {
            ivRecommended.setImageResource(R.drawable.v_yellow);
            tvrecommend.setText("BodyGuard connected");
        } else {
            ivRecommended.setImageResource(R.drawable.v_red);
            tvrecommend.setText("BodyGuard is NOT connected");
        }
    }

    void configUnsecure() {
        JSONObject props = new JSONObject();
        try {
            props.put("Breaches Count", list.size());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mixpanel.track("Am I Exposed Some Breaches Were Found", props);
        recommendedLayout.setVisibility(View.VISIBLE);
        tvtakeAction.setVisibility(View.VISIBLE);
        setIconandTextForBodyGuardRecommendation();
        llLayout.setVisibility(View.VISIBLE);
        backBtn.setVisibility(View.VISIBLE);
        editText.setVisibility(View.GONE);
        tvCheck.setVisibility(View.GONE);
        tvSecure.setVisibility(View.GONE);
        tvUnsecure.setVisibility(View.VISIBLE);
        checkBtn.setVisibility(View.GONE);
        // LottieAnimationView animationView = findViewById(R.id.animationView);
       /* animationView.setAnimation(R.raw.animation3);
        animationView.playAnimation();
        animationView.setRepeatCount(Integer.MAX_VALUE);*/

        view1.setVisibility(View.GONE);
        view2.setVisibility(View.GONE);
        view4.setVisibility(View.GONE);

        if (list.size() > 1) {
            String text = "Hackers got your sensitive details on " + list.size() + " breached sites in the past";
            Typeface face = Typeface.createFromAsset(getAssets(),
                    "titillium_regular.otf");
            TextView tvLine = findViewById(R.id.tvLine);
            tvLine.setTypeface(face);
            tvLine.setText(text);
        }

        if (list.size() >= 2) {
            viewAll.setVisibility(View.VISIBLE);
        }

   /*      Integer count = 0;
        for (final PawnedModel model : list) {
           *//* if (count >= 2) {
                viewAll.setVisibility(View.VISIBLE);
                return;
            }*//*
            View child = getLayoutInflater().inflate(R.layout.unsecure_list_item, null);
            ((TextView)child.findViewById(R.id.tv_title)).setText(model.getName());
            ((TextView)child.findViewById(R.id.tv_date)).setText(model.getBreachDate());
            llLayout.addView(child);
            count++;

            child.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mixpanel.track("Specific Breach Button Clicked on Am I Exposed Some breaches Found Screen");
                    Intent intent = new Intent(HIBPActivity.this, ReccomendationActivity.class);
                    String message = model.getObject().toString();
                    intent.putExtra("jsonObject", message);
                    startActivity(intent);
                }
            });
        }*/
    }

    @Override
    public void updateState(String state, String logmessage, int localizedResId, ConnectionStatus level) {

        switch (level) {
            case LEVEL_CONNECTED:
                System.out.println("LEVEL_CONNECTED");
                setIconandTextForBodyGuardRecommendation();
                break;
            case LEVEL_VPNPAUSED:
                break;
            case LEVEL_CONNECTING_SERVER_REPLIED:
            case LEVEL_CONNECTING_NO_SERVER_REPLY_YET:
                break;
            case LEVEL_NONETWORK:
                System.out.println("LEVEL_NONETWORK");
                break;
            case LEVEL_NOTCONNECTED:
                System.out.println("LEVEL_NOTCONNECTED");
                setIconandTextForBodyGuardRecommendation();
                break;
            case LEVEL_START:

                break;

            case LEVEL_AUTH_FAILED:

                break;
            case LEVEL_WAITING_FOR_USER_INPUT:

                break;

            case UNKNOWN_LEVEL:
            default:

                break;
        }
    }

    @Override
    public void setConnectedVPN(String uuid) {

    }
}

