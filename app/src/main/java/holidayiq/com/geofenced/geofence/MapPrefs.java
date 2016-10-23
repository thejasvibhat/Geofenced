/**
 *
 */
package holidayiq.com.geofenced.geofence;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author charan
 */
public class MapPrefs {

    private static MapPrefs mMAP_PREFS = null;

    private SharedPreferences mMapPreferences = null;

    private Context mContext = null;

    private MapPrefs() {

    }

    private MapPrefs(Context context) {
        mContext = context;
        mMapPreferences = mContext.getSharedPreferences("", Context.MODE_PRIVATE);
    }

    public static MapPrefs getInstance(Context context) {
        if (mMAP_PREFS == null) {
            mMAP_PREFS = new MapPrefs(context);
        }
        return mMAP_PREFS;
    }

    public boolean getBoolean(String key) {
        return mMapPreferences.getBoolean(key, false);
    }

    public String getString(String key) {
        return mMapPreferences.getString(key, null);
    }

    public void setBoolean(String key, boolean value) {
        mMapPreferences.edit().putBoolean(key, value).commit();
    }

    public void setString(String key, String value) {
        mMapPreferences.edit().putString(key, value).commit();
    }

    public int getInt(String key) {
        return mMapPreferences.getInt(key, -1);
    }

    public void setInt(String key, int value) {
        mMapPreferences.edit().putInt(key, value).commit();
    }

}
