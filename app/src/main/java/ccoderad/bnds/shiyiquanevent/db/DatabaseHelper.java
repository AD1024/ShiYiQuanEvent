package ccoderad.bnds.shiyiquanevent.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import ccoderad.bnds.shiyiquanevent.global.DataBaseConstants;

/**
 * Created by CCoderAD on 16/4/7.
 */
public class DatabaseHelper extends SQLiteOpenHelper {


    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelper(Context context) {
        super(context, DataBaseConstants.QUAN_DATABASENAME, null, DataBaseConstants.QUAN_DATABASEVERSIONCODE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String Event_Create = "CREATE TABLE IF NOT EXISTS " + DataBaseConstants.EVENT_TABLENAME
                + "(" + DataBaseConstants.EVENT_COLNAME[0] + " " + DataBaseConstants.EVENT_CREATE_PARAM[0] + " " + DataBaseConstants.NN
                + "," + DataBaseConstants.EVENT_COLNAME[1] + " " + DataBaseConstants.EVENT_CREATE_PARAM[1] + " " + DataBaseConstants.NN
                + "," + DataBaseConstants.EVENT_COLNAME[2] + " " + DataBaseConstants.EVENT_CREATE_PARAM[2] + " " + DataBaseConstants.NN
                + "," + DataBaseConstants.EVENT_COLNAME[3] + " " + DataBaseConstants.EVENT_CREATE_PARAM[3] + " " + DataBaseConstants.NN
                + "," + DataBaseConstants.EVENT_COLNAME[4] + " " + DataBaseConstants.EVENT_CREATE_PARAM[4] + " " + DataBaseConstants.NN
                + "," + DataBaseConstants.EVENT_COLNAME[5] + " " + DataBaseConstants.EVENT_CREATE_PARAM[5] + " " + DataBaseConstants.NN
                + "," + DataBaseConstants.EVENT_COLNAME[6] + " " + DataBaseConstants.EVENT_CREATE_PARAM[6] + " " + DataBaseConstants.NN
                + "," + DataBaseConstants.EVENT_COLNAME[7] + " " + DataBaseConstants.EVENT_CREATE_PARAM[7] + " " + DataBaseConstants.NN
                + "," + DataBaseConstants.EVENT_COLNAME[8] + " " + DataBaseConstants.EVENT_CREATE_PARAM[8] + " " + DataBaseConstants.NN
                + ")";
        String SQUARE_Create = "CREATE TABLE IF NOT EXISTS " + DataBaseConstants.SQUARE_TABLENAME
                + "(" + DataBaseConstants.SQUARE_COLNAME[0] + " " + DataBaseConstants.SQUARE_CREATE_PARAM[0] + " " + DataBaseConstants.NN
                + "," + DataBaseConstants.SQUARE_COLNAME[1] + " " + DataBaseConstants.SQUARE_CREATE_PARAM[1] + " " + DataBaseConstants.NN
                + "," + DataBaseConstants.SQUARE_COLNAME[2] + " " + DataBaseConstants.SQUARE_CREATE_PARAM[2] + " " + DataBaseConstants.NN
                + "," + DataBaseConstants.SQUARE_COLNAME[3] + " " + DataBaseConstants.SQUARE_CREATE_PARAM[3] + " " + DataBaseConstants.NN
                + "," + DataBaseConstants.SQUARE_COLNAME[4] + " " + DataBaseConstants.SQUARE_CREATE_PARAM[4] + " " + DataBaseConstants.NN
                + "," + DataBaseConstants.SQUARE_COLNAME[5] + " " + DataBaseConstants.SQUARE_CREATE_PARAM[5] + " " + DataBaseConstants.NN
                + "," + DataBaseConstants.SQUARE_COLNAME[6] + " " + DataBaseConstants.SQUARE_CREATE_PARAM[6] + " " + DataBaseConstants.NN
                + ")";

        /*Debug Code Block*/
        Log.i("Create SQL", Event_Create);
        Log.i("Create SQL-1", SQUARE_Create);

        db.execSQL(Event_Create);
        db.execSQL(SQUARE_Create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
