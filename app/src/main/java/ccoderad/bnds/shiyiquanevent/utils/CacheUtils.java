package ccoderad.bnds.shiyiquanevent.utils;

import android.os.Environment;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

import ccoderad.bnds.shiyiquanevent.beans.EventBean;

/**
 * Created by CCoderAD on 2016/11/26.
 */

public class CacheUtils {

    /*Constance*/
    public static String EVENT_CACHE_NAME = "cacheEvent.json";
    public static String CLUB_SQUARE_CACHE_DIR_NAME = "ClubSquareCache";
    public static String CLUB_SQUARE_CACHE_NAME = "clubSquare.json";
    public static String UPDATE_FILE_STORAGE_PATH = "ShiYiQuanEvent-Update";

    /*
    * Clear cache and write new data into cache
    * */
    public static void flushToCache(File path, String data) {
        if (path.exists()) path.delete();
        try {
            path.createNewFile();
            PrintStream writer = new PrintStream(new FileOutputStream(path));
            writer.print(data);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    * Read cache from a file as a String
    * */
    public static String readStringFromCache(File path) {
        String ret = "";
        if (path.exists()) {
            try {
                InputStream is = new FileInputStream(path);
                ret = Utils.ReadStringFromInputStream(is);
                return ret;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    public static String getUpdateFileStoragePath() {
        return Environment.getExternalStorageDirectory()
                + File.separator + UPDATE_FILE_STORAGE_PATH + File.separator;
    }
    public static void saveFavEventCache(EventBean event, File favedEvents, JSONArray rawData
            , int itemPosition){
        if (!favedEvents.exists()) {
            try {
                favedEvents.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        InputStream is;
        String saved;
        try {
            is = new FileInputStream(favedEvents);
            saved = Utils.ReadStringFromInputStream(is);
            JSONArray array;
            if (saved.isEmpty()) {
                array = new JSONArray();
            } else {
                array = new JSONArray(saved);
            }
            array.put(itemPosition, rawData.get(itemPosition));
            saved = array.toString();
            favedEvents.createNewFile();
            PrintStream printer = new PrintStream(new FileOutputStream(favedEvents));
            printer.print(saved);
            Log.i("Fav", "Fav_Saved!");
            printer.close();
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void removeFavEventCache(EventBean delEvent,JSONArray rawData, File favedEvents, int position){
        InputStream is;
        String saved;
        try {
            is = new FileInputStream(favedEvents);
            saved = Utils.ReadStringFromInputStream(is);
            JSONArray array;
            if (saved.isEmpty()) {
                array = new JSONArray();
            } else {
                array = new JSONArray(saved);
            }
            EventBean del = delEvent;
            JSONObject obj;
            JSONObject data;
            for (int i = 0; i < array.length(); i++) {
                if (array.get(i).equals(null)) continue;
                obj = array.getJSONObject(i);
                data = obj.getJSONObject("data");
                if (data.getString("content").equals(del.eventContent)
                        && obj.getString("sponsor_fname").equals(del.sponsorName)) {
                    array.remove(i);
                    break;
                }
            }
            saved = array.toString();
            PrintStream stream = new PrintStream(new FileOutputStream(favedEvents));
            favedEvents.createNewFile();
            stream.print(saved);
            stream.close();
            is.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
