package ccoderad.bnds.shiyiquanevent.global;

/**
 * Created by CCoderAD on 2016/12/16.
 */

public class DataBaseConstants {
    public static final String QUAN_DATABASENAME = "app_shiyiquan233.db";
    public static final int QUAN_DATABASEVERSIONCODE = 1;

    public static final String PrimKey = "Integer PRIMARY KEY AUTOINCREMENT";
    public static final String INT = "Integer";
    public static final String UNICODE_TEXT = "NTEXT";
    public static final String ASC_TEXT = "TEXT";
    public static final String SHORT_TEXT = "VARCHAR(50)";
    public static final String NN = "NOT NULL";

    public static final String EVENT_TABLENAME = "quan_event";
    public static final String SQUARE_TABLENAME = "quan_square";

    public static final String[] EVENT_COLNAME = {"_id", "sponsor_sname", "avatar", "description", "date", "time", "duration", "location", "num_follower"};
    public static final String[] EVENT_CREATE_PARAM = {PrimKey,
            UNICODE_TEXT,
            SHORT_TEXT,
            UNICODE_TEXT,
            SHORT_TEXT,
            SHORT_TEXT,
            SHORT_TEXT,
            UNICODE_TEXT,
            INT};

    public static final String[] SQUARE_COLNAME = {"_id", "sponsor_sname", "avatar", "short_intro", "full_intro", "num_follower", "num_like"};
    public static final String[] SQUARE_CREATE_PARAM = {PrimKey,
            UNICODE_TEXT,
            SHORT_TEXT,
            UNICODE_TEXT,
            UNICODE_TEXT,
            INT,
            INT};
}
