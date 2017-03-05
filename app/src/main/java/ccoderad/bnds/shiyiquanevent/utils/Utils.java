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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ccoderad.bnds.shiyiquanevent.beans.EventBean;
import ccoderad.bnds.shiyiquanevent.beans.MomentDataModel;
import ccoderad.bnds.shiyiquanevent.global.URLConstants;

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
        int prefixPos = HTML.indexOf(URLConstants.CSRF_PREFIX);
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
                bean.eventURL = URLConstants.HOME_URL + URLConstants.EVENT_URL + Integer.toString(content.getInt("id")) + "/";
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
                // Stub API : mData.headerText = mRawData.getString("head");

                mData.tailText = mRawData.has("tail") ? mRawData.getString("tail") : "";
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

    public static String generateSign(Map<String, String> paramList) {
        String ret = "";
        container hash = new container(paramList.size() + 100);
        for (Map.Entry<String, String> param : paramList.entrySet()) {
            GetParam P = new GetParam(param.getKey(), param.getValue());
            hash.put(P);
        }
        while (hash.p > 0) {
            GetParam parsed = hash.top();
            hash.remLst();
            ret += parsed.key + "=" + parsed.value + "&";
        }
        ret = ret.substring(0, ret.length() - 1);
        ret = ret + "20ecf44a13386ad76239f75f3be476";
        return MD5Util.HASH(ret);
    }

    public static long mogic(long x, int a) {
        long base = x;
        x = 1;
        while (a != 0) {
            if ((a & 1) == 1) {
                x = x * base;
            }
            base *= base;
            a >>= 1;
        }
        return x;
    }

    /*
    * Do not add slash at the beginning and ending of param suburl
    * */
    public static String makeRequest(String subrul, String[] keys, String[] values) {
        String ret = URLConstants.HOME_URL + subrul + '/';
        GetParamBuilder builder = new Utils.GetParamBuilder()
                .setKey(keys)
                .setValue(values);
        ret += builder.createGetParam();
        ret += "&sign=";
        ret += Utils.generateSign(builder.build());
        return ret;
    }

    private static class GetParam {
        String key;
        String value;
        long weight;

        public GetParam(String K, String V) {
            key = K;
            value = V;
            long self = (long) key.charAt(0);
            weight = self ^ mogic(3, (int) mogic(3, 3));
        }

        public GetParam() {
            key = "";
            value = "";
            weight = 0;
        }
    }

    private static class container {
        int p;
        GetParam[] k;

        public container(int size) {
            p = 0;
            k = new GetParam[size];
        }

        private int lson(int x) {
            return x << 1;
        }

        private int rson(int x) {
            return x << 1 | 1;
        }

        private int fa(int x) {
            return x >> 1;
        }

        private void siftU(int i) {
            while (fa(i) >= 1) {
                if (k[i].weight < k[fa(i)].weight) {
                    GetParam tmp = k[i];
                    k[i] = k[fa(i)];
                    k[fa(i)] = tmp;
                    i = fa(i);
                } else break;
            }
        }

        private void siftD(int i) {
            int c, l, r;
            while (lson(i) <= p) {
                c = i;
                l = lson(i);
                r = rson(i);
                if (k[c].weight > k[l].weight) {
                    c = l;
                }
                if (r <= p && k[c].weight > k[r].weight) {
                    c = r;
                }
                if (c != i) {
                    GetParam tmp = k[c];
                    k[c] = k[i];
                    k[i] = tmp;
                    i = c;
                } else break;
            }
        }

        public void put(GetParam g) {
            k[++p] = g;
            siftU(p);
        }

        public void remLst() {
            k[1] = k[p--];
            siftD(1);
        }

        public GetParam top() {
            return k[1];
        }
    }

    public static class GetParamBuilder {
        String[] key;
        String[] value;

        public GetParamBuilder setKey(String[] key) {
            this.key = key;
            return this;
        }

        public GetParamBuilder setValue(String[] value) {
            this.value = value;
            return this;
        }

        public Map<String, String> build() {
            Map<String, String> mRet = new HashMap<>();
            if (key == null || value == null || key.length == 0 || value.length == 0) {
                return null;
            } else if (key.length != value.length) return null;
            else {
                for (int i = 0; i < key.length; ++i) {
                    if (key[i].equals("user-agent") && value[i].equals(URLConstants.USER_AGENT)) {
                        mRet.put("user-agent", URLConstants.HASH_UA);
                    } else {
                        mRet.put(key[i], value[i]);
                    }
                }
                return mRet;
            }
        }

        public String createGetParam() {
            String mRet = "?";
            if (key == null || value == null || key.length == 0 || value.length == 0) {
                return null;
            } else if (key.length != value.length) return null;
            else {
                for (int i = 0; i < key.length; ++i) {
                    mRet += key[i] + '=' + value[i] + '&';
                }
                return mRet.substring(0, mRet.length() - 1);
            }
        }
    }
}
