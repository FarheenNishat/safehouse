package com.safehouse.bodyguard.utils;

import android.accessibilityservice.AccessibilityService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import android.view.accessibility.AccessibilityNodeInfo;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;

import java.util.List;

public class AccessibilityManipulationUtils {

    public static AccessibilityNodeInfo findNodeInfosById(AccessibilityNodeInfo nodeInfo, String resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(resId);
            if(list != null && !list.isEmpty()) {
                return list.get(0);
            }
        }else{
            AccessibilityNodeInfoCompat compat = new AccessibilityNodeInfoCompat(nodeInfo);
            List<AccessibilityNodeInfoCompat> list = compat.findAccessibilityNodeInfosByViewId(resId);
            if(list != null && !list.isEmpty()) {
                return (AccessibilityNodeInfo) list.get(0).getInfo();
            }
        }
        return null;
    }
    public static AccessibilityNodeInfo findNodeInfosByIdLast(AccessibilityNodeInfo nodeInfo, String resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(resId);
            if(list != null && !list.isEmpty()) {
                return list.get(list.size() - 1);
            }
        }else{
            AccessibilityNodeInfoCompat compat = new AccessibilityNodeInfoCompat(nodeInfo);
            List<AccessibilityNodeInfoCompat> list = compat.findAccessibilityNodeInfosByViewId(resId);
            if(list != null && !list.isEmpty()) {
                return (AccessibilityNodeInfo) list.get(list.size() - 1).getInfo();
            }
        }
        return null;
    }

    public static AccessibilityNodeInfo findNodeInfosByText(AccessibilityNodeInfo nodeInfo, String text) {
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(text);
        if(list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public static AccessibilityNodeInfo findNodeInfosByTexts(AccessibilityNodeInfo nodeInfo, String... texts) {
        for(String key : texts) {
            AccessibilityNodeInfo info = findNodeInfosByText(nodeInfo, key);
            if(info != null) {
                return info;
            }
        }
        return null;
    }

    /** EditText setText */
    public static void setText(AccessibilityNodeInfo input, String text,Context context) {
        AccessibilityNodeInfoCompat compat = new AccessibilityNodeInfoCompat(input);
        if (Build.VERSION.SDK_INT > 21) {
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
            compat.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
        }else{
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("text", text);
            clipboard.setPrimaryClip(clip);
            compat.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
            compat.performAction(AccessibilityNodeInfo.ACTION_PASTE);
        }
    }

    public static void performHome(AccessibilityService service) {
        if(service == null) {
            return;
        }
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
    }

    public static void performBack(AccessibilityService service) {
        if(service == null) {
            return;
        }
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }

    public static void performClick(AccessibilityNodeInfo nodeInfo) {
        if(nodeInfo == null) {
            return;
        }
        if(nodeInfo.isClickable()) {
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } else {
            performClick(nodeInfo.getParent());
        }
    }

    public void performGlobalActionBack(AccessibilityService service, int globalAction) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);

    }






}
