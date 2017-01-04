package ccoderad.bnds.shiyiquanevent.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by CCoderAD on 2016/12/30.
 */

public class PreferenceUtils {

    private static SharedPreferences mPref;

    public static void initialize(Context context,String tableName,int MODE){
        mPref = context.getSharedPreferences(tableName,MODE);
    }
    public static void shiftTable(Context context,String tableName,int MODE){
        mPref = context.getSharedPreferences(tableName,MODE);
    }
    public static void putString(String prefName,String value){
        SharedPreferences.Editor editor = mPref.edit();
        editor.putString(prefName,value);
        editor.apply();
    }
    public static void putBoolean(String prefName,boolean value){
        SharedPreferences.Editor editor = mPref.edit();
        editor.putBoolean(prefName,value);
        editor.apply();
    }
    public static void putInt(String prefName,int value){
        SharedPreferences.Editor editor = mPref.edit();
        editor.putInt(prefName,value);
        editor.apply();
    }
    public static String getString(String prefName,String defaultValue){
        return mPref.getString(prefName,defaultValue);
    }

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
        edit.apply();
    }
}
