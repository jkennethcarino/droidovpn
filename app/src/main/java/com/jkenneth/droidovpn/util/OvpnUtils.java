package com.jkenneth.droidovpn.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import com.jkenneth.droidovpn.model.Server;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Jhon Kenneth Carino on 10/18/15.
 */
public class OvpnUtils {

    public static final String FILE_EXTENSION = ".ovpn";
    public static final String OPENVPN_PKG_NAME = "net.openvpn.openvpn";
    public static final String OPENVPN_CLASS_NAME = "net.openvpn.openvpn.OpenVPNAttachmentReceiver";
    public static final String OPENVPN_MIME_TYPE = "application/x-openvpn-profile";
    public static final String OPENVPN_MARKET_URI = "market://details?id=net.openvpn.openvpn";
    public static final String OPENVPN_PLAYSTORE_URL =
            "http://play.google.com/store/apps/details?id=net.openvpn.openvpn";

    public static void importToOpenVPN(Activity context, Server server) {
        File file = getFile(context, server);
        if (!file.exists()) {
            saveConfigData(context, server);
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setComponent(new ComponentName(OPENVPN_PKG_NAME, OPENVPN_CLASS_NAME));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(Uri.fromFile(file), OPENVPN_MIME_TYPE);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Log.d("test", "");
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

    public static void saveConfigData(Context context, Server server) {
        File file;
        FileOutputStream outputStream;

        try {
            file = getFile(context, server);
            outputStream = new FileOutputStream(file);
            outputStream.write(server.ovpnConfigData.getBytes("UTF-8"));
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File getFile(Context context, Server server) {
        File filePath;
        if (!Environment.isExternalStorageRemovable() || isExternalStorageWritable()) {
            filePath = context.getExternalCacheDir();
        } else {
            filePath = context.getCacheDir();
        }
        return new File(filePath, server.countryShort + "_" + server.hostName + "_" +
                server.protocol.toUpperCase() + FILE_EXTENSION);
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public static int getDrawableResource(Context context, @NonNull String resource) {
        return context.getResources()
                .getIdentifier(resource, "drawable", context.getPackageName());
    }

    public static void shareOVPNFile(Activity context, Server server) {
        File file = getFile(context, server);
        if (!file.exists()) {
            saveConfigData(context, server);
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(getFile(context, server)));
        context.startActivity(Intent.createChooser(intent, "Share Profile using"));
    }
}
