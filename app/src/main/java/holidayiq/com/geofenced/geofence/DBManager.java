/**
 *
 */
package holidayiq.com.geofenced.geofence;

import android.content.Context;

/**
 * @author charan
 */
public class DBManager {

    private Context mContext = null;

    public String getGeoDestinationName(String geoId) {
        return DBAdapter.getInstance().fetchGeoName(geoId);
    }

    public String getDestinationNamefromId(String geoId,Context context) {
        return DBAdapter.getInstance().fetchDestinationNameFromId(geoId,context);
    }



    public String getHotelNameForId(int id) {
        return DBAdapter.getInstance().getHotelNameForId(id);
    }

    public String getSSNameForId(int id) {
        return DBAdapter.getInstance().getSSNameForId(id);
    }


}