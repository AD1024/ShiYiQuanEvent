package ccoderad.bnds.shiyiquanevent.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by CCoderAD on 2017/1/2.
 */

public class ToastUtil {
    private static Toast mToast;

    public static Toast getInstance(Context context) {
        if (mToast == null) {
            mToast = ViewTools.MakeToast(context, "NULL", false);
            return mToast;
        } else {
            return mToast;
        }
    }

    public static void initialize(Context context) {
        mToast = ViewTools.MakeToast(context, "NULL", false);
    }

    public static void makeText(String msg, boolean islong) {
        if (mToast == null) {
            Log.e("ToastUtil", "Error: Please run getInstance before use it");
        } else {
            mToast.setText(msg);
            mToast.setDuration(islong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT);
            mToast.show();
        }
    }

    public static void cancel() {
        mToast.cancel();
    }
}
