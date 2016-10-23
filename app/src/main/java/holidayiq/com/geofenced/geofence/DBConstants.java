/**
 *
 */
package holidayiq.com.geofenced.geofence;

import android.net.Uri;

/**
 * @author charan
 */
public class DBConstants {


    public interface DBTables {

        String HOTEL_LIST = "hotelList";
        String SIGHTSEEING_LIST = "sightSeeingList";
        String OFFLINE_LIST = "offlineList";
        String URL_RESPONSE = "urlResponse";
        String READ_CONTENT = "readContent";
        String VIDEO_REVIEW = "videoreviews";
        String TEMP_VIDEO_REVIEW = "tempVideoReview";

        String DRAFT = "reviews";

        String RECENT_SEARCH = "recentSearch";
        String INSPIRATION = "inspiration";
        String GEOCENTERS = "geocenters";
    }

    public interface DBColumns {

        String HOTEL_ID = "hotelId";
        String DESTINATION_ID = "destinationId";
        String CONTENT_ID = "contentId";
        String CONTENT_TYPE = "contentType";
        String HOTEL_NAME = "hotelName";
        String RANK = "rank";
        String STAR_RATING = "starRating";
        String PRICE = "price";
        String PREFERRED_BY = "preferredBy";
        String HOTEL_TYPE = "hotelType";
        String RESPONSE = "response";
        String DESTINATION_NAME = "destinationName";
        String URL = "url";
        String SIGHTSEEING_ID = "sightSeeingId";
        String SIGHTSEEING_NAME = "sightSeeingName";
        String SIGHTSEEING_TYPE_ID = "sightSeeingTypeId";
        String BEST_TIME = "bestTime";
        String LENGTH_OF_VISIT = "lengthOfVisit";
        String STATUS = "status";
        String IS_MAP_AVAILABLE = "isMapAvailable";
        String LAST_UPDATE = "lastUpdate";
        String LOCATION = "location";
        String STATE_NAME = "stateName";
        String IMAGE_URL = "imageURL";

        String SEARCH_TYPE = "type";
        String TITLE = "title";
        String TIME_IN_MILLIS = "timeInMillis";
        String SUB_TITLE = "subTitle";
        String TYPE_ID = "typeId";
        String NAME = "name";
        String TITLE_ORDER = "titleOrder";
        String FLOW = "flow";

        String COUNTRY_ID = "countryId";
        String OVERVIEW_URL = "overviewURL";
        String INSPIRATION_ID = "inspirationId";
        String OBJECT_ID = "objectId";
        String OBJECT_NAME = "objectName";
        String OBJECT_LATITUDE = "objectLat";
        String OBJECT_LONGITUDE = "objectLon";
        String OBJECT_TYPE = "objectType";
        String OBJECT_TYPE_ID_KEY = "objectTypeIdKey";
        String INSPIRATION_TYPE = "slType";
        String USER_NOTES = "userNotes";
    }

    public interface DBContentType {

        int OFFLINE_LIST = 10;
        int HOTEL_LIST = 11;
        int SIGHTSEEING_LIST = 12;
        int URL_RESPONSE = 13;
        int REVIEW_DRAFT = 14;
        int READ_CONTENT = 15;
        int RECENT_SEARCH = 16;
        int VIDEO_REVIEW = 17;
        int TEMP_VIDEO_REVIEW = 18;
        int INSPIRATION = 19;
        int GEOCENTERS = 20;
    }

    public interface DBInfo {

        String AUTHORITY = "com.holidayiq.provider";

        String STR_CONTENT = "content://";

    }

    public interface DBUri {

        Uri URI_OFFLINE_LIST = Uri.parse(DBInfo.STR_CONTENT + DBInfo.AUTHORITY + "/" + DBTables.OFFLINE_LIST);

        Uri URI_HOTELS_LIST = Uri.parse(DBInfo.STR_CONTENT + DBInfo.AUTHORITY + "/" + DBTables.HOTEL_LIST);

        Uri URI_SIGHTSEEING_LIST = Uri.parse(DBInfo.STR_CONTENT + DBInfo.AUTHORITY + "/" + DBTables.SIGHTSEEING_LIST);

        Uri URI_URL_RESPONSE = Uri.parse(DBInfo.STR_CONTENT + DBInfo.AUTHORITY + "/" + DBTables.URL_RESPONSE);

        Uri URI_REVIEW_DRAFT = Uri.parse(DBInfo.STR_CONTENT + DBInfo.AUTHORITY + "/" + DBTables.DRAFT);

        Uri URI_READ_CONTENT = Uri.parse(DBInfo.STR_CONTENT + DBInfo.AUTHORITY + "/" + DBTables.READ_CONTENT);

        Uri URI_RECENT_SEARCH = Uri.parse(DBInfo.STR_CONTENT + DBInfo.AUTHORITY + "/" + DBTables.RECENT_SEARCH);

        Uri VIDEO_REVIEWS = Uri.parse(DBInfo.STR_CONTENT + DBInfo.AUTHORITY + "/" + DBTables.VIDEO_REVIEW);

        Uri TEMP_VIDEO_REVIEWS = Uri.parse(DBInfo.STR_CONTENT + DBInfo.AUTHORITY + "/" + DBTables.TEMP_VIDEO_REVIEW);

        Uri INSPIRATION = Uri.parse(DBInfo.STR_CONTENT + DBInfo.AUTHORITY + "/" + DBTables.INSPIRATION);
        Uri GEOCENTERS = Uri.parse(DBInfo.STR_CONTENT + DBInfo.AUTHORITY + "/" + DBTables.GEOCENTERS);

    }
}
