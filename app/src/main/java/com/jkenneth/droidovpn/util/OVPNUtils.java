package com.jkenneth.droidovpn.util;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.io.File;

/**
 * Created by Jhon Kenneth Carino on 10/18/15.
 */
public class OVPNUtils {

    public static final String OPENVPN_PKG_NAME = "net.openvpn.openvpn";
    public static final String OPENVPN_CLASS_NAME = "net.openvpn.openvpn.OpenVPNAttachmentReceiver";
    public static final String OPENVPN_MIME_TYPE = "application/x-openvpn-profile";
    public static final String OPENVPN_MARKET_URI = "market://details?id=net.openvpn.openvpn";
    public static final String OPENVPN_PLAYSTORE_URL =
            "http://play.google.com/store/apps/details?id=net.openvpn.openvpn";

    public static void importToOpenVPN(Context context, String filePath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setComponent(new ComponentName(OPENVPN_PKG_NAME, OPENVPN_CLASS_NAME));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(Uri.fromFile(new File(filePath)), OPENVPN_MIME_TYPE);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // TODO: show install OpenVPN dialog
        }
    }

    public static String humanReadableCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        char pre = "KMGTPE".charAt(exp-1);
        return String.format("%.2f %s" + (si ? "bps" : "B"),
                bytes / Math.pow(unit, exp), pre);
    }
}
