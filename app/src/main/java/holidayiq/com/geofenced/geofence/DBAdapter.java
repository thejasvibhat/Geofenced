/**
 *
 */
package holidayiq.com.geofenced.geofence;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import holidayiq.com.geofenced.HolidayIQ;

/**
 * @author charan
 */
public class DBAdapter {

    private static DBAdapter mDatabaseAdapter = null;
    private final int MAX_HOTELS_SS = 25;
    private Context mContext = null;

    private DBAdapter(Context context) {
        mContext = context;
    }

    private DBAdapter() {

    }

    public static DBAdapter getInstance() {

        if (mDatabaseAdapter == null) {
            mDatabaseAdapter = new DBAdapter();
        }

        return mDatabaseAdapter;
    }

    private static DBAdapter getInstance(Context context) {

        if (mDatabaseAdapter == null) {
            mDatabaseAdapter = new DBAdapter(context);
        }

        return mDatabaseAdapter;
    }


    public String getHotelNameForId(int id) {
        return new InAppDBHelper(HolidayIQ.getHIQApplicationContext()).getHotelNameForId(id);

    }

    public String getSSNameForId(int id) {
        return new InAppDBHelper(HolidayIQ.getHIQApplicationContext()).getSSNameForId(id);
    }

    public String fetchDestinationNameFromId(String geoId,Context context) {
        File dbFile = context.getDatabasePath(HIQConstant.IN_APP_DATABASE_NAME);
        SQLiteDatabase inAppDb = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        Cursor c = inAppDb.rawQuery("select * from "+ GeoDbHelper.DESTINATION_TABLE_NAME+" where "+GeoDbHelper.DESTINATION_COLUMN_ID+"="+geoId,null);
        String name = "";
        try {
            if (c != null && c.getCount() != 0) {
                c.moveToFirst();
                name = c.getString(c.getColumnIndex(DBConstants.DBColumns.DESTINATION_NAME));
            }
        }finally {
            if (c != null && !c.isClosed()) {
                c.close();
            }
            c = null;

            if (inAppDb != null && inAppDb.isOpen()) {
                inAppDb.close();
            }
            inAppDb = null;
        }
        return name;
    }

    public String fetchGeoName(String geoId) {
        Cursor c = HolidayIQ.getHIQApplicationContext().getContentResolver().query(DBConstants.DBUri.GEOCENTERS, null, DBConstants.DBColumns.OBJECT_ID + " = '" + geoId + "'", null, null);
        if (c != null && c.getCount() != 0) {
            c.moveToFirst();
            return c.getString(c.getColumnIndex(DBConstants.DBColumns.OBJECT_NAME));
        }
        return "";
    }

}
