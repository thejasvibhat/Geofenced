package holidayiq.com.geofenced.geofence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by Manoj on 23/08/2016.
 */
public class GeoFenceSqlHelper extends SQLiteOpenHelper {


    public static final String TABLE_DATA = "datatracking";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_HIQ_ID = "hiq_id";
    public static final String COLUMN_TYPE = "dest_type";
    public static final String COLUMN_EVENT_TYPE = "event_type";
    public static final String COLUMN_OBJECT_NAME = "object_name";
    public static final String COLUMN_PARENT_NAME = "parent_name";
    public static final String COLUMN_PARENT_ID = "parent_id";
    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_TRIP_ID = "trip_id";

    private static final String DATABASE_NAME = "datatracking.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_DATA + "( " + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_HIQ_ID
            + " integer ,"
            +COLUMN_TYPE+" text not null ,"+COLUMN_EVENT_TYPE+" text not null ,"+COLUMN_OBJECT_NAME+" text not null,"+COLUMN_PARENT_ID+" text not null,"+COLUMN_PARENT_NAME+" text not null,"+COLUMN_TRIP_ID+" text not null,"+COLUMN_TIME+" text not null );";

    public GeoFenceSqlHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        HIQLog.w(GeoFenceSqlHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DATA);
        onCreate(db);
    }
}
