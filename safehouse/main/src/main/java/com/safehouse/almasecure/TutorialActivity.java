package com.safehouse.almasecure;

import android.animation.Animator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.RenderMode;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.safehouse.almasecure.model.TutorialModal;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import de.blinkt.openvpn.R;

public class TutorialActivity extends AppCompatActivity {

    public ArrayList<TutorialModal> mTutorialInfo = new ArrayList<>();
    ViewPager mViewPager;
    private LinearLayout pager_indicator;
    private Data mData;
    private int dotsCount;
    static String androidId = "";

    private ImageView[] dots;
    MixpanelAPI mixpanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_tutorial);
        mData = Data.getInstance(this);
        mData.setFirstTime(false);
        mData.save();
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        setArray();
        new TutorialActivity.SendTutorialView().execute();
        pager_indicator = findViewById(R.id.viewPagerCountDots);
        mViewPager = findViewById(R.id.viewpager);
        mViewPager.setAdapter(new CustomPagerAdapter(this, mTutorialInfo));
        mViewPager.setOffscreenPageLimit(mTutorialInfo.size());
        mixpanel =
                MixpanelAPI.getInstance(getApplicationContext(), getResources().getString(R.string.MIXPANAL_TOKEN));
        Typeface face = Typeface.createFromAsset(getAssets(),
                "titillium_regular.otf");
        TextView skipButton = findViewById(R.id.skip_button);
        skipButton.setTypeface(face);

        final Button nextButton = findViewById(R.id.next_button);
        nextButton.setTypeface(face);

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mViewPager.getCurrentItem() == 0){
                    JSONObject props = new JSONObject();
                    try {
                        props.put(getString(R.string.analytics_from), "Tutorial 1");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mixpanel.track("Skip Button Clicked on First Tutorial Screen", props);
                }else if(mViewPager.getCurrentItem() == 1){
                    JSONObject props = new JSONObject();
                    try {
                        props.put(getString(R.string.analytics_from) , "Tutorial 2");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mixpanel.track("Skip Button Clicked on Second Tutorial Screen", props);
                }else {
                    JSONObject props = new JSONObject();
                    try {
                        props.put(getString(R.string.analytics_from) , "Tutorial 3");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mixpanel.track("Skip Button Clicked on Third Tutorial Screen", props);
                }
                mixpanel.track("Tutorial Skipped");
                Intent i = new Intent(TutorialActivity.this, MainActivity.class);
                startActivity(i);
                JSONObject props = new JSONObject();
                try {
                    props.put(getString(R.string.analytics_from) , getString(R.string.tutorial_screen));
                    props.put(getString(R.string.analytics_to) , getString(R.string.main_screen));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mixpanel.track("Tutorial Completed", props);
                TutorialActivity.this.finish();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mViewPager.getCurrentItem() == 2){
                    JSONObject props = new JSONObject();
                    try {
                        props.put(getString(R.string.analytics_from) , "Tutorial 3");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mixpanel.track("Got it Button Clicked on Third Tutorial Page", props);
                    Intent i = new Intent(TutorialActivity.this, MainActivity.class);
                    JSONObject props1 = new JSONObject();
                    try {
                        props1.put(getString(R.string.analytics_from) , getString(R.string.tutorial_screen));
                        props1.put(getString(R.string.analytics_to) , getString(R.string.main_screen));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mixpanel.track("Tutorial Completed", props1);
                    startActivity(i);
                    TutorialActivity.this.finish();
                }else {
                   if(mViewPager.getCurrentItem() == 0){
                       JSONObject props = new JSONObject();
                       try {
                           props.put(getString(R.string.analytics_from), "Tutorial 1");
                       } catch (JSONException e) {
                           e.printStackTrace();
                       }
                       mixpanel.track("Next Button Clicked on first Tutorial Page", props);
                   }else if(mViewPager.getCurrentItem() == 1){
                       JSONObject props = new JSONObject();
                       try {
                           props.put(getString(R.string.analytics_from) , "Tutorial 2");
                       } catch (JSONException e) {
                           e.printStackTrace();
                       }
                       mixpanel.track("Next Button Clicked on second Tutorial Page", props);
                   }
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem()+1);
                }

            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for (int i = 0; i < dotsCount; i++) {
                    dots[i].setImageDrawable(getResources().getDrawable(R.drawable.non_selected_item_dot));
                }
                dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selected_item_dot));

                if (position == 2) {
                    mixpanel.track("Third Tutorial Screen Appeared");
                    nextButton.setText("Got it!");
                } else if(position == 1){
                    mixpanel.track("Second Tutorial Screen Appeared");
                    nextButton.setText("Next");
                } else{
                    nextButton.setText("Next");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        setPageViewIndicator();
    }

    private void setPageViewIndicator() {

        Log.d("###setPageViewIndicator", " : called");
        dotsCount = mTutorialInfo.size();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.non_selected_item_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(26, 26);

            params.setMargins(20, 0, 20, 0);

            final int presentPosition = i;
            dots[presentPosition].setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mViewPager.setCurrentItem(presentPosition);
                    return true;
                }

            });


            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selected_item_dot));
    }

    public void setArray() {
        mTutorialInfo.clear();

        TutorialModal modal1 = new TutorialModal();
        modal1.setFileLocation("onBoardState1.zip");
        modal1.setButtonName("Next");
        mTutorialInfo.add(modal1);

        TutorialModal modal2 = new TutorialModal();
        modal2.setFileLocation("onBoardState2.zip");
        modal2.setButtonName("Next");
        mTutorialInfo.add(modal2);

        TutorialModal modal3 = new TutorialModal();
        modal3.setFileLocation("onBoardState3.zip");
        modal3.setButtonName("Got it!");
        mTutorialInfo.add(modal3);
    }

    class SendTutorialView extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            return SendUserTutorialViewed(TutorialActivity.this.getApplicationContext());
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("RESPONSE : ", result);
        }
    }

    public String SendUserTutorialViewed(Context context) {
        try {

            Data data = Data.getInstance(context);

            URL url = new URL(context.getString(R.string.server_url) + "/apiv1/tutorial-viewed/");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
            urlConnection.setRequestProperty("Accept", "*/*");
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            JSONObject obj = new JSONObject();
            obj.put("user", mData.getUsername());
            obj.put("pass", mData.getPassword());


            HttpURLConnection urlConnection2 = null;
            try {
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(obj.toString());
                writer.flush();
                writer.close();
                os.close();

                InputStream inputStream;

                int status = urlConnection.getResponseCode();

                if (status != HttpURLConnection.HTTP_OK) {
                    return "Error";
                } else {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    String json = stringBuilder.toString();

                    return json;
                }

            } finally {
                urlConnection.disconnect();
                if (urlConnection2 != null) {
                    urlConnection2.disconnect();
                }
            }
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage(), e);
        }

        return "Error";
    }

    class CustomPagerAdapter extends PagerAdapter{
        private LayoutInflater mInflater;
        private final ArrayList<TutorialModal> mTutorialList;
        private Context mContext;

        public CustomPagerAdapter(Context context, ArrayList<TutorialModal> tutorialList) {
            mContext = context;
            this.mTutorialList = tutorialList;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, final int position) {
            mInflater = LayoutInflater.from(mContext);
            View convertView = mInflater.inflate(R.layout.tutorial_adapter, collection, false);

            TutorialModal tutorialModal = mTutorialList.get(position);


            final LottieAnimationView lottieAnimationView = convertView.findViewById(R.id.tutorial_view);
            lottieAnimationView.enableMergePathsForKitKatAndAbove(true);
            lottieAnimationView.setRenderMode(RenderMode.HARDWARE);
            lottieAnimationView.setRepeatCount(LottieDrawable.INFINITE);
            lottieAnimationView.setAnimation(tutorialModal.getFileLocation());
            lottieAnimationView.playAnimation();

            lottieAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    lottieAnimationView.playAnimation();
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });

            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    lottieAnimationView.playAnimation();
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });

            (collection).addView(convertView);
            return convertView;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Override
        public int getCount() {
            return mTutorialList.size();
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((FrameLayout) view);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTutorialList.get(position).getFileLocation();
        }

    }
}
