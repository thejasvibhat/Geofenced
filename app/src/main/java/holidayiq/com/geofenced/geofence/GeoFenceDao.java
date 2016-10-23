package holidayiq.com.geofenced.geofence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Manoj on 23/08/2016.
 */
public class GeoFenceDao {
    private SQLiteDatabase database;
    private GeoFenceSqlHelper dbHelper;
    Context context;
    private String[] allColumns = { GeoFenceSqlHelper.COLUMN_ID,GeoFenceSqlHelper.COLUMN_HIQ_ID,GeoFenceSqlHelper.COLUMN_TYPE,GeoFenceSqlHelper.COLUMN_EVENT_TYPE,GeoFenceSqlHelper.COLUMN_OBJECT_NAME,GeoFenceSqlHelper.COLUMN_PARENT_ID,GeoFenceSqlHelper.COLUMN_PARENT_NAME,GeoFenceSqlHelper.COLUMN_TIME,GeoFenceSqlHelper.COLUMN_TRIP_ID};

    public GeoFenceDao(Context context) {
        this.context = context;
        dbHelper = new GeoFenceSqlHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void createRecord(int hiq_id,String dest_type,String event_type,String parent_name,String parent_id,String object_name,Context oContext) {
        createRecord(hiq_id,dest_type,event_type,parent_name,parent_id,object_name,-1,oContext) ;
    }
    public void createRecord(int hiq_id,String dest_type,String event_type,String parent_name,String parent_id,String object_name,long timestap,Context oContext) {

        ContentValues values = new ContentValues();
        values.put(GeoFenceSqlHelper.COLUMN_HIQ_ID, hiq_id);
        values.put(GeoFenceSqlHelper.COLUMN_TYPE, dest_type);
        values.put(GeoFenceSqlHelper.COLUMN_EVENT_TYPE, event_type);
        values.put(GeoFenceSqlHelper.COLUMN_OBJECT_NAME, object_name);
        values.put(GeoFenceSqlHelper.COLUMN_PARENT_ID, parent_id);
        values.put(GeoFenceSqlHelper.COLUMN_PARENT_NAME, parent_name);
        if(timestap == -1)
            values.put(GeoFenceSqlHelper.COLUMN_TIME, System.currentTimeMillis() + "");
        else
            values.put(GeoFenceSqlHelper.COLUMN_TIME, timestap + "");
        String tripId = HIQSharedPrefrence.getString("tripId", oContext);
        if(!tripId.equals("0")) {
            HIQSharedPrefrence.putString("processTripId", tripId,context);
            values.put(GeoFenceSqlHelper.COLUMN_TRIP_ID, tripId);
        }
        long insertId = database.insert(GeoFenceSqlHelper.TABLE_DATA, null,
                values);
    }

    public List<GeoSqlDataTrack> getAllRecords(String tripId) {
        List<GeoSqlDataTrack> notificationDatas = new ArrayList<GeoSqlDataTrack>();

        Cursor cursor = database.query(GeoFenceSqlHelper.TABLE_DATA,
                allColumns, GeoFenceSqlHelper.COLUMN_TRIP_ID+" = ?", new String[]{tripId}, null, null, "time DESC");

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            GeoSqlDataTrack data = cursorToNotification(cursor);
            notificationDatas.add(data);
            cursor.moveToNext();
        }
        cursor.close();
        return notificationDatas;
    }


    private GeoSqlDataTrack cursorToNotification(Cursor cursor) {
        GeoSqlDataTrack data = new GeoSqlDataTrack();
        data.setId(cursor.getInt(cursor.getColumnIndex(GeoFenceSqlHelper.COLUMN_ID)));
        data.setHiq_id(cursor.getInt(cursor.getColumnIndex(GeoFenceSqlHelper.COLUMN_HIQ_ID)));
        data.setDest_type(cursor.getString(cursor.getColumnIndex(GeoFenceSqlHelper.COLUMN_TYPE)));
        data.setEvent_type(cursor.getString(cursor.getColumnIndex(GeoFenceSqlHelper.COLUMN_EVENT_TYPE)));
        data.setObject_name(cursor.getString(cursor.getColumnIndex(GeoFenceSqlHelper.COLUMN_OBJECT_NAME)));
        try {
            data.setParent_id(Integer.parseInt(cursor.getString(cursor.getColumnIndex(GeoFenceSqlHelper.COLUMN_PARENT_ID))));
        }
        catch (Exception e)
        {
            data.setParent_id(-1);
        }
        data.setParent_name(cursor.getString(cursor.getColumnIndex(GeoFenceSqlHelper.COLUMN_PARENT_NAME)));
        data.setTime(Long.parseLong(cursor.getString(cursor.getColumnIndex(GeoFenceSqlHelper.COLUMN_TIME))));
        data.setTrip_id(cursor.getString(cursor.getColumnIndex(GeoFenceSqlHelper.COLUMN_TRIP_ID)));
        return data;
    }

}
