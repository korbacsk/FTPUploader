package com.korbacsk.ftpuploader.helper;

import static com.korbacsk.ftpuploader.config.Config.DEBUG_MODE;
import android.util.Log;

public class Debug {

    public static void LogMessage(String message) {
        if (DEBUG_MODE) {
            Log.d("ftpuploader", message);
        }
    }

    public static void LogError(String message) {
        if (DEBUG_MODE) {
            Log.e("ftpuploader", message);
        }
    }

    public static void LogError(Exception ex) {
        if (DEBUG_MODE) {
            Log.e("ftpuploader", "error", ex);
        }
    }


}
