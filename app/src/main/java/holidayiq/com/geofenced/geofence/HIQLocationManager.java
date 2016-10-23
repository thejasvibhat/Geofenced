package holidayiq.com.geofenced.geofence;

import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.util.HashMap;
import java.util.List;

public class HIQLocationManager implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    public static final int REQUEST_CHECK_SETTINGS = 1256;
    /**
     * Activities that need location updates should call getMyLocation() and can implement LocationResponseHandler to get location updates
     * location update stops after the time specified by LOCATION_UPDATE_STOPS_AFTER constant
     * IMPORTANT:
     * if the location settings are not enabled, HIQLocationManager invokes a dialog to enable required settings.
     * Callback for this dialog needs to be hnadled in the activities that use this manager
     **/

    ///
    // Constants
    ///
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 2000;
    private static final int LOCATION_UPDATE_STOPS_AFTER = 20000;
    private static final long VALID_LOCATION_ELAPSE_SEC = 60;

    ///
    // Member variables
    ///
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationResponseHandler mLocationResponseHandler;
    private LocationManager locationManager = null;
    private LocationListener mFusedLocationListener = null;
    private Location mLocation;
    private Context mContext;
    private Handler serviceHandler;
    private boolean mRequestingLocationUpdates;
    private boolean mSingleLocationUpdate;

    private boolean isFused;
    private android.location.LocationListener mLocationListener;

    private boolean mockLocationsEnabled;
    private static Location lastMockLocation;

    private static int numGoodReadings;

    ///
    // Constructor
    ///
    public HIQLocationManager(Context context) {
        mContext = context;
        checkMockLocations();
        // building and connecting api client
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

        // creating location request
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // creating location setting request builder
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        mLocationSettingsRequest = builder.build();

    }

    public void setIsFused(boolean isFused){
        isFused = isFused;

    }


    public static boolean areThereMockPermissionApps(Context context) {
        int count = 0;

        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages =
                pm.getInstalledApplications(PackageManager.GET_META_DATA);

        String currentAppPackage = context.getPackageName();

        for (ApplicationInfo applicationInfo : packages) {
            if (!applicationInfo.packageName.equals(currentAppPackage)) {
                try {
                    PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName,
                            PackageManager.GET_PERMISSIONS);

                    ApplicationInfo appInfo = pm.getApplicationInfo(applicationInfo.packageName, 0);

                    String[] requestedPermissions = packageInfo.requestedPermissions;
                    if (requestedPermissions != null) {
                        for (String permission : requestedPermissions) {
                            if (permission.equals("android.permission.ACCESS_MOCK_LOCATION") && ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)) {
                                HashMap<String, Object> events = new HashMap<>();
                                events.put("appPackage", applicationInfo.packageName);
                                String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                                events.put("deviceId",deviceId);
                                count++;
                            }
                        }
                    }
                    requestedPermissions = null;
                    appInfo = null;
                } catch (PackageManager.NameNotFoundException e) {
                    HIQLog.e("Got exception ", e);
                }
            }
        }

        return count > 0;

    }

    public void getLocationGps(boolean popupCheck){

        boolean isGPSEnabled = false;

        // flag for network status
        boolean isNetworkEnabled = false;

        boolean canGetLocation = false;

        try{
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled) {
            // no network provider is enabled
        } else {
            mLocationListener = new android.location.LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        boolean isMoc = location.isFromMockProvider();
                        if(isMoc){
                            HIQSharedPrefrence.putString("latError",location.getLatitude()+"",mContext);
                            HIQSharedPrefrence.putString("lonError",location.getLongitude()+"",mContext);
                        }
                    }
                    boolean isFakeLoc = predictIsFake(location);
                    if(mLocationResponseHandler!=null){
                        mLocationResponseHandler.onLocationAvailable(location,isFakeLoc);
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
            if (isNetworkEnabled) {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                locationManager.requestSingleUpdate(criteria, mLocationListener,null);
                Location location = locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                }
                return;
            }
            if (isGPSEnabled) {
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                    locationManager.requestSingleUpdate(criteria, mLocationListener,null);
                Location location = locationManager
                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                   double latitude = location.getLatitude();
                   double longitude = location.getLongitude();
                }
            }
        }

    } catch (Exception e) {
        e.printStackTrace();
            ExceptionUtils.logException(e);
    }
 }

    ///
    // starts location request and returns a location
    ///
    public void GetMyLocation(boolean popupCheck, boolean singleLocationUpdate) {

        mRequestingLocationUpdates = true;
        mSingleLocationUpdate = singleLocationUpdate;

        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        // Define a listener that responds to location updates
        if (mFusedLocationListener == null) {
            mFusedLocationListener = new LocationListener() {

                public void onLocationChanged(Location location) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        boolean isMoc = location.isFromMockProvider();
                        if(isMoc){
                            HIQSharedPrefrence.putString("latError",location.getLatitude()+"",mContext);
                            HIQSharedPrefrence.putString("lonError",location.getLongitude()+"",mContext);
                        }
                    }
                    boolean isFakeLoc = predictIsFake(location);

                    if (mLocation == null) {
                        mLocation = location;

                        if (mLocationResponseHandler != null) {
                            mLocationResponseHandler.onLocationAvailable(location,isFakeLoc);
                        }

                    }
                    if (mSingleLocationUpdate) {
                        stopLocationUpdates();
                    }

                }


                public void onStatusChanged(String provider, int status, Bundle extras) {
                    HIQLog.d("LOCATION", "on status changed " + provider + " " + status);
                }

                public void onProviderEnabled(String provider) {
                    HIQLog.d("LOCATION", "provider enabled " + provider);
                }

                public void onProviderDisabled(String provider) {

                    HIQLog.d("LOCATION", "provider disabled" + provider);
                }
            };
        }
        if (mLocation != null) {
            if (mLocationResponseHandler != null) {
                boolean isFakeLoc = predictIsFake(mLocation);
                mLocationResponseHandler.onLocationAvailable(mLocation,isFakeLoc);
            }
        } else {
            checkForLocation(popupCheck);
        }
    }

    public void setLocationResponseHandler(LocationResponseHandler handler) {
        mLocationResponseHandler = handler;
    }

    public LocationResponseHandler getLocationResponseHandler()
    {
        return mLocationResponseHandler;
    }
    ///
    // Checks if required location settings are enabled. If not invokes a dialog to allow user to enable
    ///
    private void checkForLocation(final boolean popupCheck) {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mGoogleApiClient,
                        mLocationSettingsRequest
                );

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        startLocationUpdates();


                        break;

                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            if (popupCheck) {
                                if(mContext instanceof  Activity) {
                                    status.startResolutionForResult((Activity) mContext, REQUEST_CHECK_SETTINGS);
                                }
                            }
                        } catch (IntentSender.SendIntentException e) {
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        if (mLocationResponseHandler != null) {
                            mLocationResponseHandler.onLocationFailed();
                        }
                        break;
                }
            }
        });
    }

    ///
    // starts location update request
    ///
    public void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                mFusedLocationListener
        ).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                mRequestingLocationUpdates = true;

                if (!mSingleLocationUpdate) {
                    // by default location update stops after LOCATION_UPDATE_STOPS_AFTER milliseconds
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            stopLocationUpdates();
                        }
                    }, LOCATION_UPDATE_STOPS_AFTER);
                }
            }
        });
    }

    ///
    // stops location update request
    ///
    public void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, mFusedLocationListener)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        mRequestingLocationUpdates = false;
                    }
                });
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (mLocationResponseHandler != null) {
            mLocationResponseHandler.onLocationFailed();
        }
    }

    public Location getLastKnownLocation() {
        return LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }


    public boolean isMockLocationEnabled() {
        return !Settings.Secure.getString(mContext.getContentResolver(),
                Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
    }

    public boolean isMockLocation(Location location) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (location.isFromMockProvider()) {
                 return true;
            }
        } else {
            if (isMockLocationEnabled()
                    || areThereMockPermissionApps(mContext)) {
                 return true;
            }
        }
        return false;
    }

    public boolean hasLocationSpoofer() {
        boolean result = false;

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
             result = isMockLocationEnabled();
        }
        return result;
    }

    public boolean isOldLocation(Location location) {
        long age_in_ms, age_in_sec;
        if (location == null)
            return true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            age_in_ms = (SystemClock.elapsedRealtimeNanos() - location.getElapsedRealtimeNanos()) / 1000000;
        else
            age_in_ms = System.currentTimeMillis() - location.getTime();

        age_in_sec = age_in_ms / 1000;

        if (age_in_sec > VALID_LOCATION_ELAPSE_SEC) {
            return true;
        }
        return true;
    }

    private boolean isLocationPlausible(Location location) {
        if (location == null) return false;
        boolean isMock = false;
        if(Build.VERSION.SDK_INT >= 18){
            isMock = mockLocationsEnabled || location.isFromMockProvider();
        }else{

        }
        //boolean isMock = mockLocationsEnabled || (Build.VERSION.SDK_INT >= 18 && location.isFromMockProvider());
        if (isMock) {
            lastMockLocation = location;
            numGoodReadings = 0;
        } else
            numGoodReadings = Math.min(numGoodReadings + 1, 1000000); // Prevent overflow

        // We only clear that incident record after a significant show of good behavior
        if (numGoodReadings >= 20) lastMockLocation = null;

        // If there's nothing to compare against, we have to trust it
        if (lastMockLocation == null) return true;

        // And finally, if it's more than 1km away from the last known mock, we'll trust it
        double d = location.distanceTo(lastMockLocation);
        return (d > 1000);
    }

    private void checkMockLocations() {
        // Starting with API level >= 18 we can (partially) rely on .isFromMockProvider()
        // (http://developer.android.com/reference/android/location/Location.html#isFromMockProvider%28%29)
        // For API level < 18 we have to check the Settings.Secure flag
        if (Build.VERSION.SDK_INT < 18 &&
                !Settings.Secure.getString(mContext.getContentResolver(), Settings
                        .Secure.ALLOW_MOCK_LOCATION).equals("0")) {
            mockLocationsEnabled = true;
//            if (listener != null)
//                listener.onMockLocationsDetected(onGoToDevSettingsFromView, onGoToDevSettingsFromDialog);
        } else
            mockLocationsEnabled = false;
    }

    ///
    // Location response handler interface
    ///
    public interface LocationResponseHandler {
        void onLocationAvailable(Location oLocation, boolean isFakeLocation);

        void onLocationFailed();
    }

    private boolean predictIsFake(Location location){
        String lastKnownFakeLat = HIQSharedPrefrence.getString("latError",mContext);
        String lastKnownFakeLon = HIQSharedPrefrence.getString("lonError",mContext);
        if(lastKnownFakeLat==null || lastKnownFakeLon==null || lastKnownFakeLat.isEmpty() || lastKnownFakeLon.isEmpty()){
            return false; // not a mock location
        }
        Location mockLocation = new Location("mock_location");
        mockLocation.setLatitude(Double.parseDouble(lastKnownFakeLat));
        mockLocation.setLongitude(Double.parseDouble(lastKnownFakeLon));
        float distance = location.distanceTo(mockLocation);
        if(distance>100*1000){ // 100 kms
            return false;
        }else{
            return true;
        }
    };



}
