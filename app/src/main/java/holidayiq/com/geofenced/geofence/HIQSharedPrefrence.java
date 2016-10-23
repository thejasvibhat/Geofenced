package holidayiq.com.geofenced.geofence;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class HIQSharedPrefrence {

    public static final String REACT_SERVER_BUNDLE_NAME = "reactServerBundleName";
    private static SharedPreferences sharedPreferences = null;

    public static SharedPreferences getSharedPrefernces(Context context) {
        if (context != null) {
            if (sharedPreferences == null) {
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            }
        }
        return sharedPreferences;
    }

    public static String getString(String key, Context context) {
        return getSharedPrefernces(context).getString(key, null);
    }

    public static boolean getBoolean(String key, Context context) {
        return getSharedPrefernces(context).getBoolean(key, false);
    }

    public static void putBoolean(String key, boolean value, Context context) {
        getSharedPrefernces(context).edit().putBoolean(key, value).apply();
    }

    public static void putString(String key, String value, Context context) {
        getSharedPrefernces(context).edit().putString(key, value).apply();
    }

    public static void putInt(String key, int value, Context context) {
        getSharedPrefernces(context).edit().putInt(key, value).apply();
    }

    public static int getInt(String key, Context context) {
        return getSharedPrefernces(context).getInt(key, 0);
    }

    public static void clearData(Context context) {
        getSharedPrefernces(context).edit().clear().apply();
    }

    public static void clearStringData(String key, Context context) {
        getSharedPrefernces(context).edit().remove(key).apply();
    }

}
