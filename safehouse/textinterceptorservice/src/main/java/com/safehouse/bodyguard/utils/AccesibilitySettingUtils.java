package com.safehouse.bodyguard.utils;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.accessibility.AccessibilityManager;

import java.util.List;

@SuppressWarnings("unused")
public class AccesibilitySettingUtils {

    public static boolean checkAccessibilityEnabled(Context context,String serviceName) {

        AccessibilityManager accessibilityManager = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);

        List<AccessibilityServiceInfo> accessibilityServices;

        if (accessibilityManager != null) {
            accessibilityServices = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);


            for (AccessibilityServiceInfo info :
                    accessibilityServices) {
                if (info.getId().equals(serviceName)) {
                    return true;
                }
            }

        }

        return false;
    }

    public static void goAccess(Context context) {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
