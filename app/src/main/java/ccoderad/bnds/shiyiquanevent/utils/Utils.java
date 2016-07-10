package ccoderad.bnds.shiyiquanevent.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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

import ccoderad.bnds.shiyiquanevent.Beans.EventBean;

/**
 * Created by CCoderAD on 16/7/9.
 */
public class Utils {

    public File getCacheFileDir(Context parent){
        String cacheName="";
        if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) && !Environment.isExternalStorageRemovable()){
            cacheName = parent.getExternalCacheDir().getPath();
        }else{
            cacheName = parent.getCacheDir().getPath();
        }
        return new File(cacheName);
    }

    public File getCacheFile(Context parent,String fileNameToAdd){
        File dir = getCacheFileDir(parent);
        String cacheFile = dir.getPath();
        cacheFile+=File.separator+fileNameToAdd;
        Log.i("CacheFile:",cacheFile);
        return new File(cacheFile);
    }

    public int getAppVersion(Context parent){
        try {
            PackageInfo info = parent.getPackageManager().getPackageInfo(parent.getPackageName(),0);
            return  info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }
    /**
     * Name:Read String From InputStream
     * */
    public String ReadStringFromInputStream(InputStream is){
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String tmp="";String ans="";
        try {
            while((tmp=br.readLine())!=null){
                ans+=tmp;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ans;
    }

    public List<EventBean> parseEvent(JSONArray jsonArray){
        JSONObject jsonObject;
        List<EventBean> ans = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                EventBean bean = new EventBean();
                if(jsonArray.get(i).equals(null)) continue;
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
                bean.parseUrl();
                ans.add(bean);
            }
            return ans;
        }catch (JSONException e){
            e.printStackTrace();
        }
        return ans;
    }
}
