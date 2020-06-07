package com.korbacsk.ftpuploader.common;

import android.content.Context;
import android.content.DialogInterface;
import com.korbacsk.ftpuploader.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class UICommon {
    public static void showAlertDialog(Context context, String title, String message, String buttonLabel) {
        new MaterialAlertDialogBuilder(context, R.style.AlertDialog)
                .setTitle(title)
                .setMessage(message)

                .setPositiveButton(buttonLabel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }
}
