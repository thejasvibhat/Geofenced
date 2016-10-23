package holidayiq.com.geofenced.geofence;

import android.content.Context;
import android.content.Intent;

/**
 * Created by Manoj on 01/08/2016.
 */
public class GeoFenceHelper {


    public static void triggerGeoFence(Context context,double lat,double lon){
        Intent geoService = new Intent(context,GeoFenceService.class);
        geoService.putExtra(GeoFenceService.FENCE_SERVICE_LAT,lat);
        geoService.putExtra(GeoFenceService.FENCE_SERVICE_LON,lon);
        context.startService(geoService);
    }

}
