package holidayiq.com.geofenced.geofence;

/**
 * Created by Manoj on 29/07/2016.
 */
public class GeoDbHelper {

    public static final String SIGHT_SEEING_TABLE_NAME = "attractions_dump";
    public static final String SIGHT_SEEING_COULUMN_ID = "attractionId";
    public static final String SIGHT_SEEING_COULUMN_NAME = "attractionName";
    public static final String SIGHT_SEEING_COULUMN_LAT = "latitude";
    public static final String SIGHT_SEEING_COULUMN_LON = "longitude";
    public static final String SIGHT_SEEING_COULUMN_DESITNATION = "destinationId";


    public static final String DESTINATION_TABLE_NAME = "destinations_dump";
    public static final String DESTINATION_COLUMN_ID = "destinationId";
    public static final String DESTINATION_COLUMN_NAME = "destinationName";
    public static final String DESTINATION_COLUMN_LAT = "latitude";
    public static final String DESTINATION_COLUMN_LON = "longitude";

    public static final String RESORT_TABLE_NAME = "resorts_dump";
    public static final String RESORT_COLUMN_ID = "resortId";
    public static final String RESORT_COLUMN_NAME = "displayName";
    public static final String RESORT_COLUMN_LAT = "latitude";
    public static final String RESORT_COLUMN_LON = "longitude";
    public static final String RESORT_COULUMN_DESITNATION = "destinationId";


    public static String getAllHotelsQuery(){
        return "SELECT * from "+RESORT_TABLE_NAME;
    }

    public static String getAllSightseeingQuery(){
        return "SELECT * from "+SIGHT_SEEING_TABLE_NAME;
    }

    public static String getAllDestinationQuery(){
        return "SELECT * from "+DESTINATION_TABLE_NAME;
    }

    public static String getAllHotelsQueryKey(String q){
        return "SELECT * from "+RESORT_TABLE_NAME+ " where displayName like \'"+q+"%\'";
    }

    public static String getAllSightseeingQueryKey(String q){
        return "SELECT * from "+SIGHT_SEEING_TABLE_NAME+" where attractionName like \'"+q+"%\'";
    }

    public static String getAllDestinationQueryKey(String q){
        return "SELECT * from "+DESTINATION_TABLE_NAME+ " where destinationName like \'"+q+"%\'";
    }

    public static String getHotelsQueryDest(String destId){
        return "SELECT * from "+SIGHT_SEEING_TABLE_NAME +" WHERE destinationId = "+destId;
    }

}
