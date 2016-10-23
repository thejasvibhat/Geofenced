package holidayiq.com.geofenced.geofence;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by charan on 26/02/16.
 */
public class InAppDBHelper {

    private Context mContext = null;

    public InAppDBHelper(Context context) {
        mContext = context;
        initDatabase();
    }

    public void initDatabase() {
        File dbFile = mContext.getDatabasePath("hiq_in_app.sqlite");

        if (!dbFile.exists()) {
            dbFile.getParentFile().mkdirs();
            try {
                dbFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                copyInAppDatabase(dbFile);

            } catch (IOException e) {
                HIQLog.e("InAppDB", e);
            }
        }else{
            Log.e("dada","db exists");
        }
    }


    public void deleteInAppDatabase() {
        mContext.deleteDatabase(HIQConstant.IN_APP_DATABASE_NAME);
    }

    public void checkForUpdate() {
        int currentInAppVersion = MapPrefs.getInstance(mContext).getInt("inAppDBVersion");
        if (currentInAppVersion != HIQConstant.IN_APP_DATABASE_VERSION) {
            HIQLog.d("inappdb", "in app version" + currentInAppVersion + "::" + HIQConstant.IN_APP_DATABASE_VERSION);
            deleteInAppDatabase();
            initDatabase();
            MapPrefs.getInstance(mContext).setInt("inAppDBVersion", HIQConstant.IN_APP_DATABASE_VERSION);

        } else {
            HIQLog.d("inappdb", "same in app version");

        }
    }

    private void copyInAppDatabase(File dbFile) throws IOException {
        InputStream is = null;
        OutputStream os = null;

        try {
            is = mContext.getAssets().open(HIQConstant.IN_APP_DATABASE_NAME);
            os = new FileOutputStream(dbFile);

            byte[] buffer = new byte[2048];
            while (is.read(buffer) > 0) {
                os.write(buffer);
            }

        } finally {
            if (os != null) {
                os.flush();
                os.close();
            }
            os = null;

            if (is != null) {
                is.close();
            }
            is = null;
        }
    }

    public ArrayList<GenericHotelSS> getHotelList(String destinationId, boolean isHotel) {

        ArrayList<GenericHotelSS> resorts = new ArrayList<>();

        if (destinationId != null) {

            File dbFile = mContext.getDatabasePath(HIQConstant.IN_APP_DATABASE_NAME);

            SQLiteDatabase inAppDb = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);

            Cursor res = null;

            if (isHotel) {
                res = inAppDb.rawQuery("select * from resorts_dump where displayName LIKE '%" + destinationId + "%' COLLATE NOCASE;", null);
            } else {
                res = inAppDb.rawQuery("select * from resorts_dump where destinationId = '" + destinationId + "';", null);
            }

            try {
                if (res != null && res.moveToFirst()) {
                    int namePosition = res.getColumnIndex("displayName");
                    int resortIdPosition = res.getColumnIndex("resortId");
                    int latiPosition = res.getColumnIndex("latitude");
                    int longiPosition = res.getColumnIndex("longitude");
                    int destinationPosition = res.getColumnIndex("destinationId");

                    do {
                        GenericHotelSS anHotel = new GenericHotelSS();
                        anHotel.setName(res.getString(namePosition));
                        anHotel.setDestinationId(Integer.valueOf(res.getString(destinationPosition)));
                        anHotel.setId(res.getInt(resortIdPosition));
                        if (res.getString(latiPosition) != null && res.getString(latiPosition) != "" &&
                                res.getString(longiPosition) != null && res.getString(longiPosition) != "") {
                            anHotel.setLatitude(Double.valueOf(res.getString(latiPosition)));
                            anHotel.setLongitude(Double.valueOf(res.getString(longiPosition)));
                        }
                        anHotel.setType("hotel");
                        resorts.add(anHotel);
                    } while (res.moveToNext());
                }
            } finally {

                if (res != null && !res.isClosed()) {
                    res.close();
                }
                res = null;

                if (inAppDb != null && inAppDb.isOpen()) {
                    inAppDb.close();
                }
                inAppDb = null;
            }
        }

        return resorts;
    }

    public ArrayList<GenericHotelSS> getHotelListQuery(String query) {

        ArrayList<GenericHotelSS> resorts = null;

        if (query != null) {

            File dbFile = mContext.getDatabasePath(HIQConstant.IN_APP_DATABASE_NAME);

            SQLiteDatabase inAppDb = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
            Cursor res = inAppDb.rawQuery(query, null);
            HIQLog.d("query", query);

            try {
                if (res != null && res.moveToFirst()) {
                    resorts = new ArrayList<>();
                    int namePosition = res.getColumnIndex("displayName");
                    int resortIdPosition = res.getColumnIndex("resortId");
                    int latiPosition = res.getColumnIndex("latitude");
                    int longiPosition = res.getColumnIndex("longitude");
                    int destinationId = res.getColumnIndex("destinationId");

                    do {
                        GenericHotelSS anHotel = new GenericHotelSS();
                        anHotel.setName(res.getString(namePosition));
                        anHotel.setDestinationId(res.getInt(destinationId));
                        anHotel.setId(res.getInt(resortIdPosition));
                        anHotel.setLatitude(Double.valueOf(res.getString(latiPosition)));
                        anHotel.setLongitude(Double.valueOf(res.getString(longiPosition)));
                        anHotel.setType("hotel");
                        resorts.add(anHotel);
                    } while (res.moveToNext());
                }
            } finally {

                if (res != null && !res.isClosed()) {
                    res.close();
                }
                res = null;

                if (inAppDb != null && inAppDb.isOpen()) {
                    inAppDb.close();
                }
                inAppDb = null;
            }
        }

        return resorts;
    }

    public ArrayList<GenericHotelSS> getAttractionListQuery(String query) {

        ArrayList<GenericHotelSS> attractions = null;

        if (query != null) {

            File dbFile = mContext.getDatabasePath(HIQConstant.IN_APP_DATABASE_NAME);

            SQLiteDatabase inAppDb = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY| SQLiteDatabase.NO_LOCALIZED_COLLATORS);
            Cursor res = inAppDb.rawQuery(query, null);
            HIQLog.d("query", query);

            try {
                if (res != null && res.moveToFirst()) {
                    attractions = new ArrayList<>();
                    int namePosition = res.getColumnIndex("attractionName");
                    int resortIdPosition = res.getColumnIndex("attractionId");
                    int latiPosition = res.getColumnIndex("latitude");
                    int longiPosition = res.getColumnIndex("longitude");
                    int destinationId = res.getColumnIndex("destinationId");

                    do {
                        GenericHotelSS anHotel = new GenericHotelSS();
                        anHotel.setName(res.getString(namePosition));
                        anHotel.setDestinationId(res.getInt(destinationId));
                        anHotel.setId(res.getInt(resortIdPosition));
                        anHotel.setLatitude(Double.valueOf(res.getString(latiPosition)));
                        anHotel.setLongitude(Double.valueOf(res.getString(longiPosition)));
                        anHotel.setType("sightseeing");
                        attractions.add(anHotel);
                    } while (res.moveToNext());
                }
            } finally {

                if (res != null && !res.isClosed()) {
                    res.close();
                }
                res = null;

                if (inAppDb != null && inAppDb.isOpen()) {
                    inAppDb.close();
                }
                inAppDb = null;
            }
        }

        return attractions;
    }

    public ArrayList<GenericHotelSS> getAttractionList(String destinationId, boolean isByName) {

        ArrayList<GenericHotelSS> attractions = new ArrayList<>();

        if (destinationId != null) {

            File dbFile = mContext.getDatabasePath(HIQConstant.IN_APP_DATABASE_NAME);

            SQLiteDatabase inAppDb = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);
            Cursor res = null;

            if (isByName) {
                res = inAppDb.rawQuery("select * from attractions_dump where attractionName LIKE '%" + destinationId + "%' COLLATE NOCASE;", null);

            } else {
                res = inAppDb.rawQuery("select * from attractions_dump where destinationId = '" + destinationId + "';", null);
            }

            try {
                if (res != null && res.moveToFirst()) {
                    int namePosition = res.getColumnIndex("attractionName");
                    int resortIdPosition = res.getColumnIndex("attractionId");
                    int latiPosition = res.getColumnIndex("latitude");
                    int longiPosition = res.getColumnIndex("longitude");
                    int destinationPosition = res.getColumnIndex("destinationId");

                    do {
                        GenericHotelSS aSightseeing = new GenericHotelSS();
                        aSightseeing.setName(res.getString(namePosition));
                        aSightseeing.setDestinationId(Integer.valueOf(res.getString(destinationPosition)));
                        aSightseeing.setId(res.getInt(resortIdPosition));
                        if (res.getString(latiPosition) != null && res.getString(latiPosition) != "" &&
                                res.getString(longiPosition) != null && res.getString(longiPosition) != "") {
                            aSightseeing.setLatitude(Double.valueOf(res.getString(latiPosition)));
                            aSightseeing.setLongitude(Double.valueOf(res.getString(longiPosition)));
                        }
                        aSightseeing.setType("sightseeing");
                        attractions.add(aSightseeing);
                    } while (res.moveToNext());
                }
            } finally {

                if (res != null && !res.isClosed()) {
                    res.close();
                }
                res = null;

                if (inAppDb != null && inAppDb.isOpen()) {
                    inAppDb.close();
                }
                inAppDb = null;
            }
        }

        return attractions;
    }

    public ArrayList<String> getInAppDestinations(String entry) {
        ArrayList<String> destinations = new ArrayList<>();

        if (entry != null) {

            File dbFile = mContext.getDatabasePath(HIQConstant.IN_APP_DATABASE_NAME);

            SQLiteDatabase inAppDb = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
            Cursor res = inAppDb.rawQuery("select destinationName from destinations_dump where destinationName LIKE '" + entry + "%' COLLATE NOCASE;", null);

            try {
                if (res != null && res.moveToFirst()) {
                    int destinationPosition = res.getColumnIndex("destinationName");

                    do {
                        destinations.add(res.getString(destinationPosition));
                    } while (res.moveToNext());
                }
            } finally {

                if (res != null && !res.isClosed()) {
                    res.close();
                }
                res = null;

                if (inAppDb != null && inAppDb.isOpen()) {
                    inAppDb.close();
                }
                inAppDb = null;
            }
        }

        return destinations;
    }

    public String getHotelNameForId(int id) {
        String name = "";


        File dbFile = mContext.getDatabasePath(HIQConstant.IN_APP_DATABASE_NAME);

        SQLiteDatabase inAppDb = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        Cursor res = inAppDb.rawQuery("select displayName from resorts_dump where resortId = '" + id + "';", null);

        try {
            if (res != null && res.moveToFirst()) {
                int pos = res.getColumnIndex("displayName");
                name = res.getString(pos);
            }
        } finally {

            if (res != null && !res.isClosed()) {
                res.close();
            }
            res = null;

            if (inAppDb != null && inAppDb.isOpen()) {
                inAppDb.close();
            }
            inAppDb = null;
        }

        return name;
    }

    public String getSSNameForId(int id) {
        String name = "";

        File dbFile = mContext.getDatabasePath(HIQConstant.IN_APP_DATABASE_NAME);

        SQLiteDatabase inAppDb = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        Cursor res = inAppDb.rawQuery("select attractionName from attractions_dump where attractionId = '" + id + "';", null);

        try {
            if (res != null && res.moveToFirst()) {
                int pos = res.getColumnIndex("attractionName");
                name = res.getString(pos);
            }
        } finally {

            if (res != null && !res.isClosed()) {
                res.close();
            }
            res = null;

            if (inAppDb != null && inAppDb.isOpen()) {
                inAppDb.close();
            }
            inAppDb = null;
        }

        return name;
    }

    public ArrayList<GenericHotelSS> getGeoFenceDestinations() {

        ArrayList<GenericHotelSS> destinations = new ArrayList<>();
        File dbFile = mContext.getDatabasePath(HIQConstant.IN_APP_DATABASE_NAME);

        SQLiteDatabase inAppDb = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        Cursor res = inAppDb.rawQuery("select * from destinations_dump where isGeoFence = 1;", null);

        try {
            if (res != null && res.moveToFirst()) {
                int id = res.getColumnIndex("destinationId");
                int name = res.getColumnIndex("destinationName");
                int latiPosition = res.getColumnIndex("latitude");
                int longiPosition = res.getColumnIndex("longitude");
                do {
                    GenericHotelSS aDestination = new GenericHotelSS();
                    aDestination.setId(Integer.valueOf(res.getString(id)));
                    aDestination.setName(res.getString(name));
                    aDestination.setLatitude(Double.valueOf(res.getString(latiPosition)));
                    aDestination.setLongitude(Double.valueOf(res.getString(longiPosition)));
                    destinations.add(aDestination);
                } while (res.moveToNext());
            }
        } finally {

            if (res != null && !res.isClosed()) {
                res.close();
            }
            res = null;

            if (inAppDb != null && inAppDb.isOpen()) {
                inAppDb.close();
            }
            inAppDb = null;
        }

        return destinations;
    }
}
