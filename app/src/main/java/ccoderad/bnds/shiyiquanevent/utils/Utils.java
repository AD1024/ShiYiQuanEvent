package ccoderad.bnds.shiyiquanevent.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ccoderad.bnds.shiyiquanevent.beans.EventBean;
import ccoderad.bnds.shiyiquanevent.beans.MomentDataModel;
import ccoderad.bnds.shiyiquanevent.global.URLConstances;

/**
 * Created by CCoderAD on 16/7/9.
 */
public class Utils {

    public static File getCacheFileDir(Context parent) {
        String cacheName = "";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && !Environment.isExternalStorageRemovable()) {
            cacheName = parent.getExternalCacheDir().getPath();
        } else {
            cacheName = parent.getCacheDir().getPath();
        }
        return new File(cacheName);
    }

    public static File getCacheFile(Context parent, String fileNameToAdd) {
        File dir = getCacheFileDir(parent);
        String cacheFile = dir.getPath();
        cacheFile += File.separator + fileNameToAdd;
        Log.i("CacheFile:", cacheFile);
        return new File(cacheFile);
    }

    public static int getAppVersion(Context parent) {
        try {
            PackageInfo info = parent.getPackageManager().getPackageInfo(parent.getPackageName(), 0);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * Name:Read String From InputStream
     */
    public static String ReadStringFromInputStream(InputStream is) {
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String tmp = "";
        String ans = "";
        try {
            while ((tmp = br.readLine()) != null) {
                ans += tmp;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ans;
    }

    public static String getCsrfToken(String HTML) {
        String ret = "";
        int prefixPos = HTML.indexOf(URLConstances.CSRF_PREFIX);
        if (prefixPos < 0) {
            return "error";
        }
        while (HTML.charAt(prefixPos) != '\'') {
            ret += HTML.charAt(prefixPos++);
        }
        return ret;
    }

    public static List<EventBean> parseEvent(JSONArray jsonArray) {
        JSONObject jsonObject;
        List<EventBean> ans = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                EventBean bean = new EventBean();
                if (jsonArray.get(i).equals(null)) continue;
                jsonObject = jsonArray.getJSONObject(i);
                JSONObject content = jsonObject.getJSONObject("data");
                bean.eventTitle = content.getString("subject");
                bean.eventContent = content.getString("content");
                bean.eventLocation = content.getString("location");
                bean.sponsorName = jsonObject.getString("sponsor_fname");
                bean.eventAvatar = jsonObject.getString("avatar");
                bean.eventDate = jsonObject.getString("day_set");
                bean.eventTime = jsonObject.getString("time_set");
                bean.eventDuration = jsonObject.getString("time_last");
                bean.eventFollower = jsonObject.getInt("follower");
                bean.eventURL = URLConstances.HOME_URL + URLConstances.EVENT_URL + Integer.toString(content.getInt("id")) + "/";
                bean.parseUrl();
                ans.add(bean);
            }
            return ans;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ans;
    }

    public static String cleanAvatarURL(String url, String middleFix) {
        int pos = url.indexOf(middleFix);
        if (pos < 0) return url;
        return url.substring(0, pos) + url.substring(pos + middleFix.length());
    }

    public static List<MomentDataModel> parseMoment(JSONObject jsonObject) {
        List<MomentDataModel> mRet = new ArrayList<>();
        MomentDataModel mData;
        JSONObject minorData, majorData;
        JSONObject mRawData;
        try {
            JSONArray msgArray = jsonObject.getJSONArray("msglist");
            for (int i = 0; i < msgArray.length(); ++i) {
                mData = new MomentDataModel();
                // Get Inner Data;
                mRawData = msgArray.getJSONObject(i);
                minorData = mRawData.getJSONObject("minor");
                majorData = mRawData.getJSONObject("major");

                // Parse Outter Data First
                mData.timeStamp = mRawData.getDouble("time_stamp");
                mData.platformText = mRawData.getString("platform");
                mData.timeAgo = mRawData.getString("time_ago");
                mData.headerText = mRawData.getString("head");
                mData.tailText = mRawData.getString("tail");
                mData.bodyText = mRawData.getString("body");

                // Parse Major Data
                mData.majorText = majorData.getString("text");
                mData.majorAvatarURL = cleanAvatarURL(majorData.getString("image"), "/small");
                mData.majorPageURL = (majorData.getString("link"));

                // Parse Minor Data
                mData.minorText = minorData.getString("text");
                mData.minorPageURL = minorData.getString("link");
                mData.minorAvatarURL = cleanAvatarURL(minorData.getString("image"), "/small");

                // Add to list
                mRet.add(mData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mRet;
    }

    public static String Int2String(int x) {
        return Integer.toString(x);
    }

    public static String Double2String(double x) {
        return Double.toString(x);
    }

    /**
     * This Function is used to judge wether the network is available
     *
     * @return NetWorkkStat
     */
    public static boolean isNetWorkAvailable(Context context) {
        Context currContext = context;
        if (currContext != null) {
            ConnectivityManager manager = (ConnectivityManager) currContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netWorkInfo = manager.getActiveNetworkInfo();
            if (netWorkInfo != null) {
                return netWorkInfo.isAvailable();
            }

        }
        return false;
    }
}
