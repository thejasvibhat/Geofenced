package holidayiq.com.geofenced.geofence;


public class HIQLog {

    // set as true when we need to enable the debug log
    static final boolean LOG = true;

    public static void i(String tag, String string) {
        if (LOG)
            android.util.Log.i(tag, string);
    }

    public static void e(String tag, String string) {
        if (LOG)
            android.util.Log.e(tag, string);
    }

    public static void d(String tag, String string) {
        if (LOG)
            android.util.Log.d(tag, string);
    }

    public static void v(String tag, String string) {
        if (LOG)
            android.util.Log.v(tag, string);
    }

    public static void w(String tag, String string) {
        if (LOG)
            android.util.Log.w(tag, string);
    }

    public static void e(String tag, Exception e) {
        if (LOG)
            android.util.Log.e(tag, tag, e);
    }

}
