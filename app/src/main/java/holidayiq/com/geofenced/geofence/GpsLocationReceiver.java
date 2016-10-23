package holidayiq.com.geofenced.geofence;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;


import java.util.HashMap;

/**
 * Created by Manoj on 01/08/2016.
 */
public class GpsLocationReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(final Context context, Intent intent) {
        try {

            if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {

                ContentResolver contentResolver = context.getContentResolver();
                // Find out what the settings say about which providers are enabled
                int mode = getLocationMode(context);

                HIQSharedPrefrence.putString("locationProviderUsed","used",context);

                if (mode == Settings.Secure.LOCATION_MODE_OFF) {
                    // Location is turned OFF!
                } else {

                    // Location is turned ON!
                    String locationMode = null;
                    // Get the Mode value from Location system setting
                    LocationManager locationManager = (LocationManager) context.
                            getSystemService(Context.LOCATION_SERVICE);
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                            && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        locationMode = "High accuracy. Uses GPS, Wi-Fi, and mobile networks to determine location";
                    } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                        locationMode = "Device only. Uses GPS to determine location";
                    } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        locationMode = "Battery saving. Uses Wi-Fi and mobile networks to determine location";
                    }

                    if (locationMode != null) {
//                        if (Singleton.getInstance().userinfo.latitude != null && !Singleton.getInstance().userinfo.latitude.equals("0.0")) {
//                            GeoFenceHelper.triggerGeoFence(HolidayIQ.getHIQApplicationContext(), Double.parseDouble(Singleton.getInstance().userinfo.latitude), Double.parseDouble(Singleton.getInstance().userinfo.latitude));
//                            HashMap<String, Object> map = new HashMap<String, Object>();
//                            map.put("source", "LocationOn");
//                            map.put("type", "TriggerUserInfo");
//                            HIQWizRocketLayer.getInstance().pushEvents("GeoFenceTriggerEvent", map);
//                        }

                            HIQLocationManager locmanager = new HIQLocationManager(context);
                            locmanager.setLocationResponseHandler(new HIQLocationManager.LocationResponseHandler() {
                                @Override
                                public void onLocationAvailable(Location oLocation,boolean plausible) {
                                    String lastLocationTimeStamp = HIQSharedPrefrence.getString("lastLocationTimeStamp",context);
                                    if(lastLocationTimeStamp==null || lastLocationTimeStamp.isEmpty()){
                                        HIQSharedPrefrence.putString("lastLocationTimeStamp",System.currentTimeMillis()+"",context);
                                    }else{
                                        long currentTime = System.currentTimeMillis();
                                        long lastTime = Long.parseLong(lastLocationTimeStamp);
                                        long diff = currentTime - lastTime;
                                        if(diff<1000){
                                            HIQSharedPrefrence.putString("lastLocationTimeStamp",System.currentTimeMillis()+"",context);
                                            Log.e("diff",diff+"");
                                            //return;
                                        }
                                    }
                                    GeoFenceHelper.triggerGeoFence(context, oLocation.getLatitude(), oLocation.getLongitude());
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                                        boolean isMoc = oLocation.isFromMockProvider();
                                        if(isMoc){
                                            HIQSharedPrefrence.putString("latError",oLocation.getLatitude()+"",context);
                                            HIQSharedPrefrence.putString("lonError",oLocation.getLongitude()+"",context);
                                            HashMap<String, Object> events = new HashMap<>();
                                            String deviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                                            events.put("userDevice", deviceId);
                                        }
                                    }
                                }

                                @Override
                                public void onLocationFailed() {

                                }
                            });
                            locmanager.GetMyLocation(false,true);

                    }
                }

            }

        }catch (Exception e){
            e.printStackTrace();
            ExceptionUtils.logException(e);
        }
    }

    public boolean isMockLocationEnabled(Context mContext) {
        return !Settings.Secure.getString(mContext.getContentResolver(),
                Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
    }

    public  int getLocationMode(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }


        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);

            if (TextUtils.isEmpty(locationProviders)){
                locationMode = Settings.Secure.LOCATION_MODE_OFF;
            }else if (locationProviders.contains(LocationManager.GPS_PROVIDER) && locationProviders.contains(LocationManager.NETWORK_PROVIDER)){
                locationMode = Settings.Secure.LOCATION_MODE_HIGH_ACCURACY;
            }
            else if (locationProviders.contains(LocationManager.GPS_PROVIDER)){
                locationMode = Settings.Secure.LOCATION_MODE_SENSORS_ONLY;
            }
            else if (locationProviders.contains(LocationManager.NETWORK_PROVIDER)){
                locationMode = Settings.Secure.LOCATION_MODE_BATTERY_SAVING;
            }

        }

        return locationMode;
    }
}
