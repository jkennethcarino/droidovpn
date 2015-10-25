package com.jkenneth.droidovpn.ui.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.webkit.WebView;

import com.jkenneth.droidovpn.R;

/**
 * Created by Jhon Kenneth Carino on 10/25/15.
 */
public class LicensesDialogFragment extends DialogFragment {

    public LicensesDialogFragment() {}

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        WebView webView = new WebView(getActivity());
        webView.loadUrl("file:///android_asset/licenses.html");

        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.open_source_licenses)
                .setView(webView)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }
                )
                .create();
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        if (manager.findFragmentByTag(tag) == null) {
            super.show(manager, tag);
        }
    }
}
