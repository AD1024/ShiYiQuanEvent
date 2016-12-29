package ccoderad.bnds.shiyiquanevent.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import ccoderad.bnds.shiyiquanevent.global.DataBaseConstances;

/**
 * Created by CCoderAD on 16/4/7.
 */
public class DatabaseHelper extends SQLiteOpenHelper {


    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DatabaseHelper(Context context){
        super(context, DataBaseConstances.QUAN_DATABASENAME,null, DataBaseConstances.QUAN_DATABASEVERSIONCODE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String Event_Create = "CREATE TABLE IF NOT EXISTS " + DataBaseConstances.EVENT_TABLENAME
                + "(" + DataBaseConstances.EVENT_COLNAME[0] + " " + DataBaseConstances.EVENT_CREATE_PARAM[0] + " " + DataBaseConstances.NN
                + "," + DataBaseConstances.EVENT_COLNAME[1] + " " + DataBaseConstances.EVENT_CREATE_PARAM[1] + " " + DataBaseConstances.NN
                + "," + DataBaseConstances.EVENT_COLNAME[2] + " " + DataBaseConstances.EVENT_CREATE_PARAM[2] + " " + DataBaseConstances.NN
                + "," + DataBaseConstances.EVENT_COLNAME[3] + " " + DataBaseConstances.EVENT_CREATE_PARAM[3] + " " + DataBaseConstances.NN
                + "," + DataBaseConstances.EVENT_COLNAME[4] + " " + DataBaseConstances.EVENT_CREATE_PARAM[4] + " " + DataBaseConstances.NN
                + "," + DataBaseConstances.EVENT_COLNAME[5] + " " + DataBaseConstances.EVENT_CREATE_PARAM[5] + " " + DataBaseConstances.NN
                + "," + DataBaseConstances.EVENT_COLNAME[6] + " " + DataBaseConstances.EVENT_CREATE_PARAM[6] + " " + DataBaseConstances.NN
                + "," + DataBaseConstances.EVENT_COLNAME[7] + " " + DataBaseConstances.EVENT_CREATE_PARAM[7] + " " + DataBaseConstances.NN
                + "," + DataBaseConstances.EVENT_COLNAME[8] + " " + DataBaseConstances.EVENT_CREATE_PARAM[8] + " " + DataBaseConstances.NN
                + ")";
        String SQUARE_Create = "CREATE TABLE IF NOT EXISTS " + DataBaseConstances.SQUARE_TABLENAME
                + "(" + DataBaseConstances.SQUARE_COLNAME[0] + " " + DataBaseConstances.SQUARE_CREATE_PARAM[0] + " " + DataBaseConstances.NN
                + "(" + DataBaseConstances.SQUARE_COLNAME[1] + " " + DataBaseConstances.SQUARE_CREATE_PARAM[1] + " " + DataBaseConstances.NN
                + "(" + DataBaseConstances.SQUARE_COLNAME[2] + " " + DataBaseConstances.SQUARE_CREATE_PARAM[2] + " " + DataBaseConstances.NN
                + "(" + DataBaseConstances.SQUARE_COLNAME[3] + " " + DataBaseConstances.SQUARE_CREATE_PARAM[3] + " " + DataBaseConstances.NN
                + "(" + DataBaseConstances.SQUARE_COLNAME[4] + " " + DataBaseConstances.SQUARE_CREATE_PARAM[4] + " " + DataBaseConstances.NN
                + "(" + DataBaseConstances.SQUARE_COLNAME[5] + " " + DataBaseConstances.SQUARE_CREATE_PARAM[5] + " " + DataBaseConstances.NN
                + "(" + DataBaseConstances.SQUARE_COLNAME[6] + " " + DataBaseConstances.SQUARE_CREATE_PARAM[6] + " " + DataBaseConstances.NN
                + ")";

        /*Debug Code Block*/
        Log.i("Create SQL",Event_Create);
        Log.i("Create SQL-1",SQUARE_Create);

        db.execSQL(Event_Create);
        db.execSQL(SQUARE_Create);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
