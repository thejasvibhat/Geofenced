package holidayiq.com.geofenced;

import android.app.Application;
import android.content.Context;

/**
 * Created by Manoj on 21/10/2016.
 */

public class HolidayIQ extends Application {

    private static Context mContext;

    public static Context getHIQApplicationContext() {
        return mContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();

    }
}
