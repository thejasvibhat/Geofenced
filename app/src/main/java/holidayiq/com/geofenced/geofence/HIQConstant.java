package holidayiq.com.geofenced.geofence;

import android.content.Context;
import android.util.DisplayMetrics;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Manoj on 21/10/2016.
 */

public class HIQConstant {

    public static final String IN_APP_DATABASE_NAME = "hiq_in_app.sqlite";
    public static final int IN_APP_DATABASE_VERSION = 7;
    public static final String json_obj = "{\"iternary_list\":[{\"approved_photos\":[],\"destinations\":[],\"end_destination\":\"Mysore\",\"end_destination_id\":472,\"type\":\"journey\",\"start_destination_id\":293,\"start_destination\":\"Bangalore\",\"intermediate_destinations\":[{\"destination_id\":339,\"destination_name\":\"Coorg\",\"exited_time\":1477221817029,\"reached_time\":1477221405026}],\"photos\":[],\"rejected_photos\":[],\"exit_time\":0,\"enter_time\":0,\"start_time\":1477221249473,\"end_time\":1477222562822},{\"approved_photos\":[],\"destinations\":[],\"type\":\"destination\",\"intermediate_destinations\":[],\"object_id\":339,\"object_name\":\"Coorg\",\"parent_destination_id\":339,\"photos\":[],\"rejected_photos\":[],\"exit_time\":1477221817029,\"enter_time\":1477221405026,\"start_time\":0,\"end_time\":0},{\"approved_photos\":[],\"destinations\":[],\"type\":\"destination\",\"intermediate_destinations\":[],\"object_id\":472,\"object_name\":\"Mysore\",\"parent_destination_id\":472,\"photos\":[],\"rejected_photos\":[],\"exit_time\":0,\"enter_time\":1477222562822,\"start_time\":0,\"end_time\":0},{\"approved_photos\":[],\"destinations\":[],\"type\":\"ss\",\"intermediate_destinations\":[],\"object_id\":665,\"object_name\":\"Mysore Zoo\",\"parent_destination_id\":472,\"parent_destination_name\":\"Mysore\",\"photos\":[],\"rejected_photos\":[],\"exit_time\":1477222143707,\"enter_time\":0,\"start_time\":0,\"end_time\":0},{\"approved_photos\":[],\"destinations\":[],\"type\":\"hotel\",\"intermediate_destinations\":[],\"object_id\":446777,\"object_name\":\"Zip Rooms Mysore Palace\",\"parent_destination_id\":472,\"parent_destination_name\":\"Mysore\",\"photos\":[],\"rejected_photos\":[],\"exit_time\":1477222059229,\"enter_time\":0,\"start_time\":0,\"end_time\":0},{\"approved_photos\":[],\"destinations\":[],\"type\":\"hotel\",\"intermediate_destinations\":[],\"object_id\":3523,\"object_name\":\"Regaalis Hotel\",\"parent_destination_id\":472,\"parent_destination_name\":\"Mysore\",\"photos\":[],\"rejected_photos\":[],\"exit_time\":1477221960193,\"enter_time\":1477221898190,\"start_time\":0,\"end_time\":0},{\"approved_photos\":[],\"destinations\":[],\"type\":\"ss\",\"intermediate_destinations\":[],\"object_id\":1378,\"object_name\":\"Mysore Palace\",\"parent_destination_id\":472,\"parent_destination_name\":\"Mysore\",\"photos\":[],\"rejected_photos\":[],\"exit_time\":0,\"enter_time\":1477221996508,\"start_time\":0,\"end_time\":0},{\"approved_photos\":[],\"destinations\":[],\"type\":\"ss\",\"intermediate_destinations\":[],\"object_id\":28809,\"object_name\":\"Chouti Mariamma Temple\",\"parent_destination_id\":339,\"parent_destination_name\":\"Coorg\",\"photos\":[],\"rejected_photos\":[],\"exit_time\":1477221796109,\"enter_time\":0,\"start_time\":0,\"end_time\":0},{\"approved_photos\":[],\"destinations\":[],\"type\":\"ss\",\"intermediate_destinations\":[],\"object_id\":13769,\"object_name\":\"Abbi Falls\",\"parent_destination_id\":339,\"parent_destination_name\":\"Coorg\",\"photos\":[],\"rejected_photos\":[],\"exit_time\":1477221649404,\"enter_time\":1477221592979,\"start_time\":0,\"end_time\":0},{\"approved_photos\":[],\"destinations\":[],\"type\":\"hotel\",\"intermediate_destinations\":[],\"object_id\":2927,\"object_name\":\"Club Mahindra Madikeri\",\"parent_destination_id\":339,\"parent_destination_name\":\"Coorg\",\"photos\":[],\"rejected_photos\":[],\"exit_time\":1477221551799,\"enter_time\":0,\"start_time\":0,\"end_time\":0}]}";



    public static int dpToPx(int dp, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public static int pxToDp(int px, Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }


    public static String getAmPmTime(long milliseconds) {

        try {

            SimpleDateFormat sfdate = new SimpleDateFormat("hh:mm a");
            Date date = new Date();
            date.setTime(milliseconds);
            return sfdate.format(date);
        }catch (Exception e){

        }
        return "";

    }
}
