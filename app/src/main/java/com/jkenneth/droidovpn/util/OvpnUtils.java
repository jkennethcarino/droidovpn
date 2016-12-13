package com.jkenneth.droidovpn.util;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;

import com.jkenneth.droidovpn.R;
import com.jkenneth.droidovpn.model.Server;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Jhon Kenneth Carino on 10/18/15.
 */
public class OvpnUtils {

    private static final String FILE_EXTENSION = ".ovpn";
    private static final String OPENVPN_PKG_NAME = "net.openvpn.openvpn";
    private static final String OPENVPN_MIME_TYPE = "application/x-openvpn-profile";

    /**
     * Imports OVPN configuration into OpenVPN Connect app (net.openvpn.openvpn), if available.
     * Otherwise, this opens Google Play Store to install OpenVPN Connect app.
     *
     * @param activity The context of an activity
     * @param server The {@link Server} that contains OVPN profile you want to import.
     */
    public static void importToOpenVpn(@NonNull final Activity activity, @NonNull Server server) {
        File file = getFile(activity, server);
        if (!file.exists()) {
            saveConfigData(activity, server);
        }

        Uri uri = FileProvider.getUriForFile(activity,
                activity.getApplicationContext().getPackageName() + ".fileprovider", file);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setDataAndType(uri, OPENVPN_MIME_TYPE);
        try {
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            AlertDialog dialog = new AlertDialog.Builder(activity)
                    .setTitle(R.string.title_import_dialog)
                    .setMessage(R.string.message_import_dialog)
                    .setCancelable(false)
                    .setPositiveButton(R.string.install, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            PlayStoreUtils.openApp(activity, OPENVPN_PKG_NAME);
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .create();
            dialog.show();
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

    /**
     * Writes and saves OVPN profile to a file
     *
     * @param context The context of an application
     * @param server The {@link Server} that contains OVPN profile
     */
    private static void saveConfigData(@NonNull Context context, @NonNull Server server) {
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

    /**
     * Creates an empty file for OVPN profile
     *
     * @param context The context of an application
     * @param server The {@link Server} that contains OVPN profile
     */
    private static File getFile(@NonNull Context context, @NonNull Server server) {
        File filePath;
        if (!Environment.isExternalStorageRemovable() || isExternalStorageWritable()) {
            filePath = context.getExternalCacheDir();
        } else {
            filePath = context.getCacheDir();
        }
        return new File(filePath, server.countryShort + "_" + server.hostName + "_" +
                server.protocol.toUpperCase() + FILE_EXTENSION);
    }

    /**
     * @return Whether the external storage is available for read and write.
     */
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static int getDrawableResource(@NonNull Context context, @NonNull String resource) {
        return context.getResources()
                .getIdentifier(resource, "drawable", context.getPackageName());
    }

    /**
     * Shows an intent chooser to share OVPN profile.
     *
     * @param activity The context of an activity
     * @param server The {@link Server} that contains OVPN profile
     */
    public static void shareOvpnFile(@NonNull Activity activity, @NonNull Server server) {
        File file = getFile(activity, server);
        if (!file.exists()) {
            saveConfigData(activity, server);
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(getFile(activity, server)));
        activity.startActivity(Intent.createChooser(intent, "Share Profile using"));
    }
}
