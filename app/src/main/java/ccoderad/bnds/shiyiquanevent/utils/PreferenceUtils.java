package ccoderad.bnds.shiyiquanevent.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by CCoderAD on 2016/12/30.
 */

public class PreferenceUtils {
    public static void Insert(Context context, String PrefName, String TagName, String msg, int MODE) {
        SharedPreferences pref = context.getSharedPreferences(PrefName, MODE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(TagName, msg);
        editor.apply();
    }

    public static void Reset(Context context, String PrefName, String TagName, String stubMsg, int MODE) {
        SharedPreferences pref = context.getSharedPreferences(PrefName, MODE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(TagName, stubMsg);
        editor.apply();
    }

    public static void Remove(Context context, String PrefName, String[] TagName) {
        SharedPreferences pref = context.getSharedPreferences(PrefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        for (int i = 0; i < TagName.length; ++i) {
            edit.remove(TagName[i]);
        }
    }
}
