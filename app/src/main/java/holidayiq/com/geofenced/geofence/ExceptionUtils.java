package holidayiq.com.geofenced.geofence;

import android.content.Context;



/**
 * Created by Manoj on 24/06/2016.
 *
 * This class is used to handle crashlytics initialization and reporting.
 * This class will decouple dependencies on android.
 *
 */

public class ExceptionUtils {

    /**
     * Initializes crashlytics object. usually called in oncreate of application class
     * @param context
     */
    public static void initCrashlytics(Context context){

    }

    /**
     * Log crashlytics user details.
     * @param context
     */
    //Todo: Remove dependency on FbSharedPrefrences
    public static void logUser(Context context){
        try {

        }catch (Exception e){

        }
    }


    public static void logUserWithData(Context context,String data,Throwable tr){

    }

    public static void logException(Throwable e){
        e.printStackTrace();
    }


}
