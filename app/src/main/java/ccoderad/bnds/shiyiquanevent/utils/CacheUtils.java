package ccoderad.bnds.shiyiquanevent.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

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
}
