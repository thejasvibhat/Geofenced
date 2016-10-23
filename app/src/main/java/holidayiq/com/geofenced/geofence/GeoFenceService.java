package holidayiq.com.geofenced.geofence;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Manoj on 29/07/2016.
 */
public class GeoFenceService extends IntentService implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,ResultCallback<Status> {

    private static final String TAG = GeoFenceService.class.getName();

    List<DistanceObj> objs;


    static final String FENCE_SERVICE_LAT = "lattitude";

    static final String FENCE_SERVICE_LON = "longitude";

    private static final int FENCE_CIRCLE_NUMBER = 98;

    private static final int MAX_DYNAMIC_GEOFENCE_RANGE = 40*1000;

    static final String DYNAMIC_GEOFENCE_TAG_NAME = "dynamicCircle";

    static final String HOME_GEOFENCE_TAG_NAME = "homeCircle";

    public static final String HOME_LATTITUDE = "home_lat";

    public static final String HOME_LONGITUDE = "home_lon";

    private static int HOTEL_LOITERING_DELAY = 35*1000;

    private static int DESTINATION_LOITERING_DELAY = 30*1000;

    private static int SS_LOITERING_DELAY = 35*1000;

    private static int HOTEL_FENCE_RADIUS = 100;

    private static int SS_FENCE_RADIUS = 500;

    public static final String LAST_KNOWN_DESTINATION = "lastKnownDest";

    public static final String HOME_DESTINATION = "homeDestination";

    private static final String HOME_DESTINATION_ID = "homeDestinationId";

    // Internal List of Geofence objects. In a real app, these might be provided by an API based on
    // locations within the user's proximity.
    private List<Geofence> mGeofenceList;

    private GoogleApiClient mApiClient;

    private long timeTaken =0;


    public GeoFenceService() {
        super("GeoFenceService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent,flags,startId);
    }


    private void createGeoFence(double lat,double lon){
        if (!isGooglePlayServicesAvailable()) {
            // log this event
            return;
        }

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();



        mGeofenceList = new ArrayList<Geofence>();
        try {
            createGeofences(objs, lat, lon);
            mApiClient.connect();
        }catch (Exception e){
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(HIQSharedPrefrence.getInt("hotelDwellTime",this)!=0){
            HOTEL_LOITERING_DELAY = HIQSharedPrefrence.getInt("hotelDwellTime",this);
            HIQLog.d("HOTEL_LOITERING_DELAY",HOTEL_LOITERING_DELAY+"");
        }
        if(HIQSharedPrefrence.getInt("ssDwellTime",this)!=0){
            SS_LOITERING_DELAY = HIQSharedPrefrence.getInt("ssDwellTime",this);
            HIQLog.d("SS_LOITERING_DELAY",SS_LOITERING_DELAY+"");
        }
        if(HIQSharedPrefrence.getInt("destDwellTime",this)!=0){
            //DESTINATION_LOITERING_DELAY = HIQSharedPrefrence.getInt("destDwellTime",this);
            HIQLog.d("SS_LOITERING_DELAY",DESTINATION_LOITERING_DELAY+"");
        }
        if(HIQSharedPrefrence.getInt("hotelDwellRadius",this)!=0){
            HOTEL_FENCE_RADIUS = HIQSharedPrefrence.getInt("hotelDwellRadius",this);
            HIQLog.d("HOTEL_FENCE_RADIUS",HOTEL_FENCE_RADIUS+"");
        }
        if(HIQSharedPrefrence.getInt("ssDwellRadius",this)!=0){
            SS_FENCE_RADIUS = HIQSharedPrefrence.getInt("ssDwellRadius",this);
            HIQLog.d("SS_FENCE_RADIUS",SS_FENCE_RADIUS+"");
        }
        HIQLog.e("GeoFenceService","GeoFenceService onHandleIntent");
        double lat = intent.getExtras().getDouble(FENCE_SERVICE_LAT);
        double lon = intent.getExtras().getDouble(FENCE_SERVICE_LON);
        try {
            String previousLat = HIQSharedPrefrence.getString("previousLat",this);
            String previousLon = HIQSharedPrefrence.getString("previousLon",this);
            if(previousLat!=null && previousLon!=null && previousLat.equalsIgnoreCase(String.valueOf(lat)) && previousLon.equalsIgnoreCase(String.valueOf(lon))){
                //return;
            }else{
                HIQSharedPrefrence.putString("previousLat", String.valueOf(lat),this);
                HIQSharedPrefrence.putString("previousLon", String.valueOf(lon),this);
            }
            updateDistance(lat,lon);
            createGeoFence(lat,lon);
        } catch (Exception e) {
            ExceptionUtils.logException(e);
        } catch (OutOfMemoryError e){
            ExceptionUtils.logException(e);
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        try {
            PendingIntent mGeofenceRequestIntent = getGeofenceTransitionPendingIntent();
            final PendingResult<Status> statusPendingResult = LocationServices.GeofencingApi.removeGeofences(mApiClient, mGeofenceRequestIntent);
            new AsyncTask<Void,Void,Void>(){

                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        int status = statusPendingResult.await().getStatusCode();
                        if (status == GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE) {
                            ExceptionUtils.logException(new Throwable("GEOFENCE_NOT_AVAILABLE while removing"));
                        }
                        if (status == GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES) {
                            ExceptionUtils.logException(new Throwable("GEOFENCE_TOO_MANY_GEOFENCES while removing"));
                        }
                        if (status == GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS) {
                            ExceptionUtils.logException(new Throwable("GEOFENCE_TOO_MANY_PENDING_INTENTS while removing"));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return null;
                }
            }.execute();

            final PendingResult<Status> statusPendingResultAdd = LocationServices.GeofencingApi.addGeofences(
                    mApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    mGeofenceRequestIntent
            ); // Result processed in onResult().
            statusPendingResultAdd.setResultCallback(this);
        } catch (Exception securityException) {
            ExceptionUtils.logException(securityException);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull Status status) {

    }

    public void createGeofences(List<DistanceObj> list,double lat,double lon) {
        String home_lat = HIQSharedPrefrence.getString(HOME_LATTITUDE,this);
        String home_lon = HIQSharedPrefrence.getString(HOME_LONGITUDE,this);
        double home_lattitude = 0;
        double home_longitude = 0;
        ArrayList<DistanceObj> event_list = new ArrayList<>(); // for map activity
        StringBuilder sb = new StringBuilder();
        boolean isHome = false;
        if(home_lat==null || home_lon==null){
            isHome=true;
        }else{
            Location homeLoc = new Location("homeloc");
            homeLoc.setLatitude(Double.parseDouble(home_lat));
            homeLoc.setLongitude(Double.parseDouble(home_lon));
            Location CurrentLoc = new Location("currentLoc");
            CurrentLoc.setLatitude(lat);
            CurrentLoc.setLongitude(lon);
            double distanceFromHome = CurrentLoc.distanceTo(homeLoc);
            if(distanceFromHome>MAX_DYNAMIC_GEOFENCE_RANGE){
                HIQSharedPrefrence.putBoolean("exited", true, getApplicationContext()); // mark home exited true so that we can get the home notification
                isHome = false;
            }else{
                isHome = true;
            }
        }
        for (int i=0;i<FENCE_CIRCLE_NUMBER;i++){
            DistanceObj loc = list.get(i);
            String suffix = "";
            int delay = 0;
            if(loc.type.equalsIgnoreCase("hotel")){
                suffix = "Hotel:";
                delay = HOTEL_LOITERING_DELAY;
            }else if(loc.type.equalsIgnoreCase("sightseeing")){
                suffix = "SS:";
                delay = SS_LOITERING_DELAY;
            }
            SimpleGeofence fenceObj = new SimpleGeofence(suffix+String.valueOf(loc.place_id), loc.place_latitude, loc.place_longitude, HOTEL_FENCE_RADIUS, Geofence.GEOFENCE_TRANSITION_ENTER |
                    Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL ,delay,true);
            HIQLog.d("adding geofence ", loc.toString());
            Geofence fen = fenceObj.toGeofence();
            if (fen != null && loc.distance<MAX_DYNAMIC_GEOFENCE_RANGE) {
                if(isHome){
                    if(loc.type.equalsIgnoreCase("sightseeing")){
                        mGeofenceList.add(fen);
                    }
                }else{
                    mGeofenceList.add(fen);
                }
                event_list.add(loc); //for map activity
                if(loc.type.equalsIgnoreCase("hotel")){
                    sb.append("H:"+loc.place_latitude+","+loc.place_longitude+",");
                }else if(loc.type.equalsIgnoreCase("sightseeing")){
                    sb.append("S:"+loc.place_latitude+","+loc.place_longitude+",");
                }
            }else{
                //ExceptionUtils.logException(new Throwable("wrong geofence mapping "+loc.toString()));
            }
        }
        if(!event_list.isEmpty()) { // dynamic circle logic.
            float max_distance = (float) event_list.get(event_list.size() - 1).distance; // max distance of the dynamic circle. which listens to exit event and draws again.
            //Register the dynamic circle.
            SimpleGeofence fenceObj = new SimpleGeofence(DYNAMIC_GEOFENCE_TAG_NAME, lat, lon, max_distance, Geofence.GEOFENCE_TRANSITION_ENTER |
                    Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL,DESTINATION_LOITERING_DELAY,true);
            HIQLog.d("adding dynamic circle ", " tagg..");
            Geofence fen = fenceObj.toGeofence();
            if (fen != null) {
                mGeofenceList.add(fen);
                DistanceObj obj = new DistanceObj("Dynamic_Circle",100001,lat,lon,lat,lon,max_distance,"dynamic_circle");
                event_list.add(obj);
            }else{
                ExceptionUtils.logException(new Throwable("dynamic circle calculation went wrong : "+lat+" "+lon));
            }
        }

        // Home GeoFence Logic.
        int nearestDest = getNearestDestination(event_list.get(0));
       // DBManager dbManager = new DBManager();
       // String geoName = dbManager.getDestinationNamefromId(String.valueOf(nearestDest),this);
        HIQSharedPrefrence.putInt("nearestLocationForDestination",nearestDest,this);


        boolean isHomeLocTracked = HIQSharedPrefrence.getBoolean("isHomeLocTracked",this);
        if (home_lat == null || home_lon ==null){
            // calculate the home from the nearest destination parent.
            home_lattitude = lat;
            home_longitude = lon;
            HIQSharedPrefrence.putString(HOME_LATTITUDE,String.valueOf(home_lattitude),this);
            HIQSharedPrefrence.putString(HOME_LONGITUDE,String.valueOf(home_longitude),this);
           // HIQSharedPrefrence.putString(HOME_DESTINATION,String.valueOf(geoName),this);
            HIQSharedPrefrence.putString(HOME_DESTINATION_ID, String.valueOf(nearestDest),this);
            HIQSharedPrefrence.putBoolean("isHomeLocTracked",true,this);
        }else{
            if(!isHomeLocTracked){
                try {
                    String homeDestName = HIQSharedPrefrence.getString("HOME_DESTINATION", this);
                    if (homeDestName != null && !homeDestName.isEmpty()) {
//                        String destId = dbManager.getDestinationIdfromName(homeDestName, this);
//                        if (destId != null && !destId.isEmpty()) {
//                            HIQSharedPrefrence.putBoolean("isHomeLocTracked", true, this);
//                        }
                    }
                }catch (Exception e){
                    ExceptionUtils.logException(e);
                }
            }
        }


        float home_radius = MAX_DYNAMIC_GEOFENCE_RANGE;
        SimpleGeofence fenceObj = new SimpleGeofence(HOME_GEOFENCE_TAG_NAME, home_lattitude, home_longitude, home_radius, Geofence.GEOFENCE_TRANSITION_ENTER |
                Geofence.GEOFENCE_TRANSITION_EXIT,0,false);
        Geofence home_fence = fenceObj.toGeofence();
        if (home_fence != null) {
            mGeofenceList.add(home_fence);
            DistanceObj obj = new DistanceObj("Home_cirlce",100002,lat,lon,lat,lon,MAX_DYNAMIC_GEOFENCE_RANGE,"home");
            event_list.add(obj);
        } else {
        }

        logEvents(lat,lon,sb.toString());

    }


    /**
     * Checks if Google Play services is available.
     * @return true if it is.
     */
    private boolean isGooglePlayServicesAvailable() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == resultCode) {

            return true;
        } else {
            //Log.e(TAG, "Google Play services is unavailable.");
            return false;
        }
    }

    /**
     * Create a PendingIntent that triggers GeofenceTransitionIntentService when a geofence
     * transition occurs.
     */
    private PendingIntent getGeofenceTransitionPendingIntent() {
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void updateDistance(double lat, double lon) throws IOException {

        long startMs = System.currentTimeMillis();
        File dbFile = this.getDatabasePath("hiq_in_app.sqlite");

        SQLiteDatabase inAppDb = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        Cursor hotel_res = inAppDb.rawQuery(GeoDbHelper.getAllHotelsQuery(),null);
        Cursor ss_res = inAppDb.rawQuery(GeoDbHelper.getAllSightseeingQuery(),null);
        int count =0;
        objs = new ArrayList<>();
        try {
            if (hotel_res != null) {
                while (hotel_res.moveToNext()) {
                    double lattitude = hotel_res.getDouble(hotel_res.getColumnIndex(GeoDbHelper.RESORT_COLUMN_LAT));
                    double longitude = hotel_res.getDouble(hotel_res.getColumnIndex(GeoDbHelper.RESORT_COLUMN_LON));
                    int place_id = hotel_res.getInt(hotel_res.getColumnIndex(GeoDbHelper.RESORT_COLUMN_ID));
                    String name = hotel_res.getString(hotel_res.getColumnIndex(GeoDbHelper.RESORT_COLUMN_NAME));
                    Location selected_location=new Location("location_start");
                    selected_location.setLatitude(lat);
                    selected_location.setLongitude(lon);

                    Location near_locations=new Location("location_end");
                    near_locations.setLatitude(lattitude);
                    near_locations.setLongitude(longitude);

                    double distance=selected_location.distanceTo(near_locations);

                    DistanceObj obj = new DistanceObj(name,place_id,lattitude,longitude,lat,lon,distance,"hotel");
                    objs.add(obj);
                    count++;
                }

            }

            if (ss_res != null) {
                while (ss_res.moveToNext()) {
                    double lattitude = ss_res.getDouble(ss_res.getColumnIndex(GeoDbHelper.SIGHT_SEEING_COULUMN_LAT));
                    double longitude = ss_res.getDouble(ss_res.getColumnIndex(GeoDbHelper.SIGHT_SEEING_COULUMN_LON));
                    int place_id = ss_res.getInt(ss_res.getColumnIndex(GeoDbHelper.SIGHT_SEEING_COULUMN_ID));
                    String name = ss_res.getString(ss_res.getColumnIndex(GeoDbHelper.SIGHT_SEEING_COULUMN_NAME));
                    Location selected_location=new Location("location_start");
                    selected_location.setLatitude(lat);
                    selected_location.setLongitude(lon);

                    Location near_locations=new Location("location_end");
                    near_locations.setLatitude(lattitude);
                    near_locations.setLongitude(longitude);

                    double distance=selected_location.distanceTo(near_locations);

                    DistanceObj obj = new DistanceObj(name,place_id,lattitude,longitude,lat,lon,distance,"sightseeing");
                    objs.add(obj);
                    count++;
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        finally {

            if (hotel_res != null && !hotel_res.isClosed()) {
                hotel_res.close();
            }
            hotel_res = null;

            if (ss_res != null && !ss_res.isClosed()) {
                ss_res.close();
            }
            ss_res = null;

            if (inAppDb != null && inAppDb.isOpen()) {
                inAppDb.close();
            }
            inAppDb = null;

            Collections.sort(objs, new Comparator<DistanceObj>(){
                public int compare(DistanceObj o1, DistanceObj o2){
                    return Double.compare(o1.distance, o2.distance);
                }
            });
            long endMs = System.currentTimeMillis();
            timeTaken = endMs-startMs;

        }

    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_DWELL);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }


    private void logEvents(double lat,double lon,String geo){
//        try {
//            String home_lat = HIQSharedPrefrence.getString(HOME_LATTITUDE, this);
//            String home_lon = HIQSharedPrefrence.getString(HOME_LONGITUDE, this);
//            HashMap<String, Object> oMap = new HashMap<String, Object>();
//            oMap.put("Type", "Calculation");
//            oMap.put("CurrentLoc", lat + "," + lon);
//            oMap.put("HomeLoc", home_lat + "," + home_lon);
//            if (HIQUtil.isNetworkAvailable()) {
//                oMap.put("Online", "true");
//            } else {
//                oMap.put("Online", "false");
//            }
//            HIQWizRocketLayer.getInstance().pushEvents("GeoFenceTriggerEvent", oMap);
//            HIQUtil.sendEventToGAFromObject("GeoFenceEvent", "GeoFenceEvent in Android", oMap);
//        }catch (Exception e){
//            ExceptionUtils.logException(e);
//        }

    }

    private int getNearestDestination(DistanceObj obj){
        int nearestDest =-1;
        File dbFile = this.getDatabasePath("hiq_in_app.sqlite");
        SQLiteDatabase inAppDb = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        if(obj.type.equalsIgnoreCase("sightseeing")){
            Cursor hotel_res = inAppDb.rawQuery("select * from "+GeoDbHelper.SIGHT_SEEING_TABLE_NAME+" where "+GeoDbHelper.SIGHT_SEEING_COULUMN_ID+"="+obj.place_id,null);
            try{
                if (hotel_res != null) {
                    while (hotel_res.moveToNext()) {
                         nearestDest = hotel_res.getInt(hotel_res.getColumnIndex(GeoDbHelper.SIGHT_SEEING_COULUMN_DESITNATION));
                    }
                }
            }finally {
                if (hotel_res != null && !hotel_res.isClosed()) {
                    hotel_res.close();
                }
                hotel_res = null;

                if (inAppDb != null && inAppDb.isOpen()) {
                    inAppDb.close();
                }
                inAppDb = null;
            }
        }else if(obj.type.equalsIgnoreCase("hotel")){
            Cursor hotel_res = inAppDb.rawQuery("select * from "+GeoDbHelper.RESORT_TABLE_NAME+" where "+GeoDbHelper.RESORT_COLUMN_ID+"="+obj.place_id,null);
            try{
                if (hotel_res != null) {
                    while (hotel_res.moveToNext()) {
                        nearestDest = hotel_res.getInt(hotel_res.getColumnIndex(GeoDbHelper.RESORT_COULUMN_DESITNATION));
                    }
                }
            }finally {
                if (hotel_res != null && !hotel_res.isClosed()) {
                    hotel_res.close();
                }
                hotel_res = null;

                if (inAppDb != null && inAppDb.isOpen()) {
                    inAppDb.close();
                }
                inAppDb = null;
            }

        }

        return nearestDest;
    }


}