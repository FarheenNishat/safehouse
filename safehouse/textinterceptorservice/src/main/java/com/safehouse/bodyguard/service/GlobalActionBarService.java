package com.safehouse.bodyguard.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.os.Build;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;
import com.safehouse.bodyguard.R;
import com.safehouse.bodyguard.TextInterceptorManager;
import com.safehouse.bodyguard.data.UrlRepository;
import com.safehouse.bodyguard.session.SessionPolicy;
import com.safehouse.bodyguard.output.OutputService;
import com.safehouse.bodyguard.output.TextInterceptorLogger;
import com.safehouse.bodyguard.urlProcessing.UrlProcessor;
import com.safehouse.bodyguard.urlProcessing.UrlSearchStrategyRegexBased;
import com.safehouse.bodyguard.utils.LogUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * main accesibility service
 * Receives accessibility events and responsible for setting the dynamic configuration
 */
public class GlobalActionBarService extends AccessibilityService  {

    public static final String TAG = "GlobalActionBarService";
    private static final boolean DEBUG = false;
    private TextInterceptorManager mTextInterceptorManager;
    private List<String> allowedPackages;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
       mTextInterceptorManager.process(accessibilityEvent);
    }


    @Override
    public void onInterrupt() {

    }

    /**
     * Here we will set the allowed packages
     */
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        TextServiceConfiguration config = TextServiceConfiguration.getConfigurationFromFile(getApplicationContext());

        allowedPackages = config.packages;
        mTextInterceptorManager = TextInterceptorManager.createTextInterceptorManager(new UrlProcessor(new UrlSearchStrategyRegexBased()), new OutputService(this.getApplicationContext(), new TextInterceptorLogger()), new SessionPolicy(config.getConfig()), new UrlRepository());

        if(DEBUG) {
            // Toast
            try {
                Toast.makeText(this, getString(R.string.app_name) + " Accessibility service connected", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                LogUtil.d(TAG, e.getMessage());
                e.printStackTrace();
            }

        }


        boolean isSucceesfull = setAllowedPackages(config.allowAll);

        if (!isSucceesfull) {
            disable();
        }

    }

    @Nullable
    private boolean setAllowedPackages(boolean allowAll) {
        AccessibilityServiceInfo accessibilityServiceInfo = null;
        try {
            accessibilityServiceInfo = getServiceInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (accessibilityServiceInfo == null)
            return false;

        if(!allowAll) {
            String[] packageNamesArray = new String[allowedPackages.size()];

            for (int i = 0; i < allowedPackages.size(); i++) {
                packageNamesArray[i] = allowedPackages.get(i);
            }
            accessibilityServiceInfo.packageNames = packageNamesArray;
        }

        try {
            setServiceInfo(accessibilityServiceInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return false;

        }

        return true;
    }

    private void disable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                disableSelf();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }






}