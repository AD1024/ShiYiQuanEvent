package ccoderad.bnds.shiyiquanevent.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import ccoderad.bnds.shiyiquanevent.global.DataBaseConstants;

/**
 * Created by CCoderAD on 2016/12/16.
 */

public class DataBaseManager {
    /*
    * Single Instance
    * */
    private static DatabaseHelper mHelper;

    public static DatabaseHelper getInstance(Context context) {
        if (mHelper == null) {
            mHelper = new DatabaseHelper(context);
        }
        return mHelper;
    }

    /*
    * Execute Raw SQL Query
    * */
    public static void execSQL(SQLiteDatabase db, String query) {
        if (query != null && !TextUtils.isEmpty(query)) {
            db.execSQL(query);
        }
    }

    /*
    * Raw Query with Columns name and values
    * */
    public static void execInsertionSQL(SQLiteDatabase db, String tableName, String[] colNames, String[] values) {
        if (colNames != null && values != null && tableName != null) {
            if (values.length != colNames.length) {
                Log.e("Insertion Error", "colName and values are not corresponding");
                return;
            }
            String query = "INSERT INTO " + tableName + "(";
            for (int i = 0; i < colNames.length - 1; ++i) {
                query += colNames[i] + ",";
            }
            query += colNames[colNames.length - 1] + ")";
            query += "values(";
            for (int i = 0; i < values.length - 1; ++i) {
                switch (colNames[i]) {
                    case DataBaseConstants.INT: {
                        query += values[i] + ",";
                        break;
                    }
                    default: {
                        query += "'" + values[i] + "'" + ",";
                        break;
                    }
                }
            }
            int pos = colNames.length - 1;
            switch (colNames[pos]) {
                case DataBaseConstants.INT: {
                    query += values[pos] + ")";
                    break;
                }
                default: {
                    query += values[pos] + ")";
                }
            }
            /*Debug Code Block*/
            Log.i("Insertion Query", query);
            db.execSQL(query);
        }
    }

    /*
    * Use Api to insert
    * */
    public static void Insert(SQLiteDatabase db, String tableName, String colName, String value) {
        if (tableName != null && colName != null && value != null) {
            ContentValues record = new ContentValues();
            record.put(colName, value);
            db.insert(tableName, null, record);
        }
    }
}
