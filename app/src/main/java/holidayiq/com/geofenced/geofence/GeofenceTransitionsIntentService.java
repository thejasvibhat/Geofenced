/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package holidayiq.com.geofenced.geofence;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.TextUtils;
import android.view.View;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import holidayiq.com.geofenced.MainActivity;
import holidayiq.com.geofenced.R;

import static holidayiq.com.geofenced.geofence.GeoFenceService.LAST_KNOWN_DESTINATION;


/**
 * Listener for geofence transition changes.
 * <p/>
 * Receives geofence transition events from Location Services in the form of an Intent containing
 * the transition type and geofence id(s) that triggered the transition. Creates a notification
 * as the output.
 */
public class GeofenceTransitionsIntentService extends IntentService {

    protected static final String TAG = "GeofenceTransitionsIS";

    static boolean isDebug = false;

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public GeofenceTransitionsIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private static String Underscored(String stringExtra) {
        stringExtra = TextUtils.join("_", stringExtra.split(" "));
        return stringExtra;
    }

    public static void SendNotificationForEntry(Context mContext,String geoId, String geoName, String notificationStringTitle, String notificationStringContent, String notificationStringDeeplink, String objType,Context context,String eventType,boolean isBanner) {
        String currentTime = String.valueOf(System.currentTimeMillis());
        if (notificationStringTitle != null) {
            notificationStringTitle = notificationStringTitle.replace("$name$", geoName);
            notificationStringContent = notificationStringContent.replace("$name$", geoName);
            notificationStringDeeplink = notificationStringDeeplink.replace("$name$", Underscored(geoName));
            notificationStringDeeplink = notificationStringDeeplink.replace("$id$", geoId);
            Calendar c = new GregorianCalendar();
            c.set(Calendar.HOUR_OF_DAY, 6);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            Date d1 = c.getTime();
            boolean isNotification = false;
            File dbFile = mContext.getDatabasePath("hiq_in_app.sqlite");
            SQLiteDatabase inAppDb = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.OPEN_READWRITE);
            if(geoName.equalsIgnoreCase("home")){
                String lastTriggeredHome = HIQSharedPrefrence.getString("lastTriggeredHome",mContext);
                if(lastTriggeredHome==null){
                    sendNotification(notificationStringTitle, notificationStringContent, notificationStringDeeplink,context,isBanner);
                    HIQSharedPrefrence.putString("lastTriggeredHome", System.currentTimeMillis() + "", mContext);
                    isNotification = true;
                }else{
                    long previouslastTriggeredHome = Long.parseLong(HIQSharedPrefrence.getString("lastTriggeredHome",mContext));
                    if(previouslastTriggeredHome<d1.getTime()) {
                        sendNotification(notificationStringTitle, notificationStringContent, notificationStringDeeplink,context,isBanner);
                        HIQSharedPrefrence.putString("lastTriggeredHome", System.currentTimeMillis() + "", mContext);
                        isNotification = true;
                    }
                }

            }else if(objType.equalsIgnoreCase("Hotel")){
                String lastTriggeredHome = HIQSharedPrefrence.getString("lastTriggeredHotel",mContext);
                if(lastTriggeredHome==null){
                    sendNotification(notificationStringTitle, notificationStringContent, notificationStringDeeplink,context,isBanner);
                    HIQSharedPrefrence.putString("lastTriggeredHotel", System.currentTimeMillis() + "", mContext);
                    isNotification = true;
                }else{
                    long previouslastTriggeredHome = Long.parseLong(HIQSharedPrefrence.getString("lastTriggeredHotel",mContext));
                    if(previouslastTriggeredHome<d1.getTime()) {
                        sendNotification(notificationStringTitle, notificationStringContent, notificationStringDeeplink,context,isBanner);
                        HIQSharedPrefrence.putString("lastTriggeredHotel", System.currentTimeMillis() + "", mContext);
                        isNotification = true;
                    }
                }
            }else if(objType.equalsIgnoreCase("SS")){
                Cursor res = null;
                try {
                    String lastNotificationTriggerd = null;
                    res = inAppDb.rawQuery("Select * from " + GeoDbHelper.SIGHT_SEEING_TABLE_NAME + " where " + GeoDbHelper.SIGHT_SEEING_COULUMN_ID + " = " + geoId, null);
                    if(res!=null){
                        if(res.moveToFirst()){
                            lastNotificationTriggerd = res.getString(res.getColumnIndex("lastTimeStamp"));
                        }
                    }
                    if(lastNotificationTriggerd==null){
                        sendNotification(notificationStringTitle, notificationStringContent, notificationStringDeeplink, context, isBanner);
                        isNotification = true;
                        Cursor cur = inAppDb.rawQuery("update "+GeoDbHelper.SIGHT_SEEING_TABLE_NAME+" SET lastTimeStamp = '"+currentTime+"' where "+GeoDbHelper.SIGHT_SEEING_COULUMN_ID+" = "+geoId,null);
                        cur.moveToFirst();
                        cur.close();
                    }else{
                        long time = Long.parseLong(lastNotificationTriggerd);
                        if(time<d1.getTime()){
                            sendNotification(notificationStringTitle, notificationStringContent, notificationStringDeeplink, context, isBanner);
                            isNotification = true;
                            Cursor cur = inAppDb.rawQuery("update "+GeoDbHelper.SIGHT_SEEING_TABLE_NAME+" SET lastTimeStamp = '"+currentTime+"' where "+GeoDbHelper.SIGHT_SEEING_COULUMN_ID+" = "+geoId,null);
                            cur.moveToFirst();
                            cur.close();
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                        ExceptionUtils.logException(e);
                }finally {
                    if (res != null && !res.isClosed()) {
                        res.close();
                    }
                    res = null;
                    if (inAppDb != null && inAppDb.isOpen()) {
                        inAppDb.close();
                    }
                    inAppDb = null;
                }
            }else if(objType.equalsIgnoreCase("Destination")){
                Cursor res = null;
                try {
                    String lastNotificationTriggerd = null;
                    res = inAppDb.rawQuery("Select * from " + GeoDbHelper.DESTINATION_TABLE_NAME + " where " + GeoDbHelper.DESTINATION_COLUMN_ID + " = " + geoId, null);
                    if(res!=null){
                        try {
                            if (res.moveToFirst()) {
                                lastNotificationTriggerd = res.getString(res.getColumnIndex("lastTimeStamp"));
                            }
                        }catch (Exception e){

                        }
                    }
                    if(lastNotificationTriggerd==null){
                        sendNotification(notificationStringTitle, notificationStringContent, notificationStringDeeplink, context, isBanner);
                        isNotification = true;
                        Cursor cur = inAppDb.rawQuery("update "+GeoDbHelper.DESTINATION_TABLE_NAME+" SET lastTimeStamp = '"+currentTime+"' where "+GeoDbHelper.DESTINATION_COLUMN_ID+" = "+geoId,null);
                        cur.moveToFirst();
                        cur.close();
                    }else{
                        long time = Long.parseLong(lastNotificationTriggerd);
                        if(time<d1.getTime()){
                            sendNotification(notificationStringTitle, notificationStringContent, notificationStringDeeplink, context, isBanner);
                            isNotification = true;
                            Cursor cur = inAppDb.rawQuery("update "+GeoDbHelper.DESTINATION_TABLE_NAME+" SET lastTimeStamp = '"+currentTime+"' where "+GeoDbHelper.DESTINATION_COLUMN_ID+" = "+geoId,null);
                            cur.moveToFirst();
                            cur.close();
                        }
                    }

                }catch (Exception e){
                    ExceptionUtils.logException(e);
                }finally {
                    if (res != null && !res.isClosed()) {
                        res.close();
                    }
                    res = null;
                    if (inAppDb != null && inAppDb.isOpen()) {
                        inAppDb.close();
                    }
                    inAppDb = null;
                }
            }
            HashMap<String, Object> oMap = new HashMap<String, Object>();
            oMap.put("Type", eventType);
            oMap.put("Name", geoName);
            oMap.put("ID", geoId);
            oMap.put("Object Type", objType);
            oMap.put("Date", new Date());

            if(isNotification) {
            }
        }

       // HIQUtil.sendEventToGAFromObject("GeoFenceEvent", "GeoFenceEvent in Android", oMap);

    }

    /**
     * Handles incoming intents.
     *
     * @param intent sent by Location Services. This Intent is provided to Location
     *               Services (inside a PendingIntent) when addGeofences() is called.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        DBManager dbManager = new DBManager();
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    geofencingEvent.getErrorCode());
            HIQLog.e(TAG, errorMessage);
            HIQLog.d("geofence", "return::" + errorMessage);
            return;
        }
        int nearestDest = HIQSharedPrefrence.getInt("nearestLocationForDestination", this);
        String parentDestName = dbManager.getDestinationNamefromId(String.valueOf(nearestDest), this);

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        HIQLog.d("geofence", "" + geofenceTransition);
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {

            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            String geoId = triggeringGeofences.get(0).getRequestId();
//            if (triggeringGeofences.size() > 1)
//                geoId = triggeringGeofences.get(1).getRequestId();
            String oId = geoId;
            if (oId.contains(":"))
                oId = oId.split(":")[1];


            if(geoId.equalsIgnoreCase(GeoFenceService.HOME_GEOFENCE_TAG_NAME)){
                // show the home notification
                if(HIQSharedPrefrence.getBoolean("exited",getApplicationContext())) {
                    SendNotificationForEntry(this,geoId, "home", HIQSharedPrefrence.getString("entryGeoStringTitleHome", getApplicationContext()),
                            HIQSharedPrefrence.getString("entryGeoStringContentHome", getApplicationContext()),
                            HIQSharedPrefrence.getString("entryGeoStringDeeplinkHome", getApplicationContext()), "Destination",getApplicationContext(),"Enter",false);
                    HIQSharedPrefrence.putBoolean("exited", false, getApplicationContext());
                    HIQSharedPrefrence.putString("processTripId", HIQSharedPrefrence.getString("tripId", getApplicationContext()), getApplicationContext());
                    HIQSharedPrefrence.putString("tripId", "0", getApplicationContext());

                }
            }else if(geoId.startsWith("Hotel")){
                String geoName = dbManager.getHotelNameForId(Integer.valueOf(oId));
                if(isDebug) {
                SendNotificationForEntry(this,oId, geoName, "→ "+HIQSharedPrefrence.getString("entryGeoStringTitleHotel", getApplicationContext()),
                        HIQSharedPrefrence.getString("entryGeoStringContentHotel", getApplicationContext()),
                        HIQSharedPrefrence.getString("entryGeoStringDeeplinkHotel", getApplicationContext()), "Hotel",getApplicationContext(),"Enter",false);
                }
                logEvents(geoName,geoId,"Enter");
                try{
                    GeoFenceDao dao = new GeoFenceDao(this);
                    dao.open();
                    dao.createRecord(Integer.parseInt(oId),"Hotel","Enter",parentDestName,String.valueOf(nearestDest),geoName,getApplicationContext());
                    dao.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else if(geoId.startsWith("SS")){
                String geoName = dbManager.getSSNameForId(Integer.valueOf(oId));
                if(isDebug){
                    SendNotificationForEntry(this,oId, geoName, "→ "+HIQSharedPrefrence.getString("entryGeoStringTitleSS", getApplicationContext()),
                        HIQSharedPrefrence.getString("entryGeoStringContentSS", getApplicationContext()),
                        HIQSharedPrefrence.getString("entryGeoStringDeeplinkSS", getApplicationContext()), "SS",getApplicationContext(),"Enter",false);
                }
                //logEvents(geoName,geoId,"Enter");
                try{
                    GeoFenceDao dao = new GeoFenceDao(this);
                    dao.open();
                    dao.createRecord(Integer.parseInt(oId),"SS","Enter",parentDestName,String.valueOf(nearestDest),geoName,getApplicationContext());
                    dao.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else if(geoId.equalsIgnoreCase(GeoFenceService.DYNAMIC_GEOFENCE_TAG_NAME)){
                if(isDebug){
                    SendNotificationForEntry(this,geoId,"geofence entered","→ Dynamic circle entered",
                            HIQSharedPrefrence.getString("entryGeoStringContentHome", getApplicationContext()),
                            HIQSharedPrefrence.getString("entryGeoStringDeeplinkHome", getApplicationContext()), "Destination",getApplicationContext(),"Enter",false);
                }
                try{
                    GeoFenceDao dao = new GeoFenceDao(this);
                    dao.open();
                    dao.createRecord(nearestDest,"Destination","Enter",parentDestName,String.valueOf(nearestDest),parentDestName,getApplicationContext());
                    dao.close();
                }catch (Exception e){
                    e.printStackTrace();
                }

                //logEvents("DynamicCircle",geoId,"Enter");
            }

        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {

            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();

            //first is always home
            String geoId = triggeringGeofences.get(0).getRequestId();
            String oId = geoId;
            if (oId.contains(":"))
                oId = oId.split(":")[1];
            //String name = dbManager.getGeoDestinationName(oId);

//            if ((!(geoId.startsWith("Hotel") || geoId.startsWith("SS"))) && (!geoId.equals("-1"))) {
//                RepopulateDestinationGeoFences();
//                HIQSharedPrefrence.putString("enteredDestinationId", "-1", getApplicationContext());
//            }
//            if (geoId.equals("-1")) {
//                HIQSharedPrefrence.putBoolean("exited", true, getApplicationContext());
//            }

            if(geoId.equalsIgnoreCase(GeoFenceService.HOME_GEOFENCE_TAG_NAME)){
                // show the home notification
                HIQSharedPrefrence.putBoolean("exited", true, getApplicationContext());
                HIQSharedPrefrence.putString("tripId", UUID.randomUUID().toString(),getApplicationContext());
                //logEvents("Home",geoId,"Exit");
            }else if(geoId.startsWith("Hotel")){
                String geoName = dbManager.getHotelNameForId(Integer.valueOf(oId));
                if(isDebug) {
                    SendNotificationForEntry(this,oId, geoName, "← "+HIQSharedPrefrence.getString("entryGeoStringTitleHotel", getApplicationContext()),
                            HIQSharedPrefrence.getString("entryGeoStringContentHotel", getApplicationContext()),
                            HIQSharedPrefrence.getString("entryGeoStringDeeplinkHotel", getApplicationContext()), "Hotel",getApplicationContext(),"Exit",false);
                }
                //logEvents(geoName,geoId,"Exit");
                try{
                    GeoFenceDao dao = new GeoFenceDao(this);
                    dao.open();
                    dao.createRecord(Integer.parseInt(oId),"Hotel","Exit",parentDestName,String.valueOf(nearestDest),geoName,getApplicationContext());
                    dao.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else if(geoId.startsWith("SS")){
                String geoName = dbManager.getSSNameForId(Integer.valueOf(oId));
                if(isDebug){
                    SendNotificationForEntry(this,oId, geoName, "← "+HIQSharedPrefrence.getString("entryGeoStringTitleSS", getApplicationContext()),
                            HIQSharedPrefrence.getString("entryGeoStringContentSS", getApplicationContext()),
                            HIQSharedPrefrence.getString("entryGeoStringDeeplinkSS", getApplicationContext()), "SS",getApplicationContext(),"Exit",false);
                }
                //logEvents(geoName,geoId,"Exit");
                try{
                    GeoFenceDao dao = new GeoFenceDao(this);
                    dao.open();
                    dao.createRecord(Integer.parseInt(oId),"SS","Exit",parentDestName,String.valueOf(nearestDest),geoName,getApplicationContext());
                    dao.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else if(geoId.equalsIgnoreCase(GeoFenceService.DYNAMIC_GEOFENCE_TAG_NAME)){
                if(isDebug){
                    SendNotificationForEntry(this,geoId,"geofence exited","← "+"Dynamic circle exited",
                            HIQSharedPrefrence.getString("entryGeoStringContentHome", getApplicationContext()),
                            HIQSharedPrefrence.getString("entryGeoStringDeeplinkHome", getApplicationContext()), "Destination",getApplicationContext(),"Exit",false);
                }
                //logEvents("DynamicCircle",geoId,"Exit");
                HIQLocationManager locmanager = new HIQLocationManager(getApplicationContext());
                locmanager.setLocationResponseHandler(new HIQLocationManager.LocationResponseHandler() {
                    @Override
                    public void onLocationAvailable(Location oLocation,boolean plausible) {
                        GeoFenceHelper.triggerGeoFence(getApplicationContext(),oLocation.getLatitude(),oLocation.getLongitude());
                    }

                    @Override
                    public void onLocationFailed() {

                    }
                });
                String tripId = HIQSharedPrefrence.getString("tripId",getApplicationContext());
                if(tripId == null)
                    HIQSharedPrefrence.putString("tripId", UUID.randomUUID().toString(),getApplicationContext());

                locmanager.getLocationGps(false);
                try{
                    GeoFenceDao dao = new GeoFenceDao(this);
                    dao.open();
                    dao.createRecord(nearestDest,"Destination","Exit",parentDestName,String.valueOf(nearestDest),parentDestName,getApplicationContext());
                    dao.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

        } else if((geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL)){
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            String geoId = triggeringGeofences.get(0).getRequestId();
            String oId = geoId;
            if (oId.contains(":"))
                oId = oId.split(":")[1];
            if(geoId.equalsIgnoreCase(GeoFenceService.HOME_GEOFENCE_TAG_NAME)){
                // show the home notification

            }else if(geoId.startsWith("Hotel")){
                    String geoName = dbManager.getHotelNameForId(Integer.valueOf(oId));
                    String lastknownDwell = HIQSharedPrefrence.getString("lastknownDwell",getApplicationContext());
                    String lastknownHotelDwellTime = HIQSharedPrefrence.getString("lastknownHotelDwellTime",getApplicationContext());
                    long lastknownHotelDwellTimeMill = 0;

                if(lastknownHotelDwellTime!=null) {
                    try {
                        lastknownHotelDwellTimeMill = Long.parseLong(lastknownHotelDwellTime);
                    }catch (Exception e){

                    }
                }
                if(lastknownDwell!=null && lastknownDwell.equalsIgnoreCase(geoName)){
                    // do nothing in the same dwell location
                }else {
                    //if((System.currentTimeMillis()-lastknownHotelDwellTimeMill>60*60*1000)) {
                        SendNotificationForEntry(this,oId, geoName, HIQSharedPrefrence.getString("entryGeoStringTitleHotel", getApplicationContext()),
                                HIQSharedPrefrence.getString("entryGeoStringContentHotel", getApplicationContext()),
                                HIQSharedPrefrence.getString("entryGeoStringDeeplinkHotel", getApplicationContext()), "Hotel", getApplicationContext(), "Dwell",true);
                        HIQSharedPrefrence.putString("lastknownDwell", geoName, getApplicationContext());
                        HIQSharedPrefrence.putString("lastknownHotelDwellTime", System.currentTimeMillis() + "", getApplicationContext());
                   // }
                }
                try{
                    GeoFenceDao dao = new GeoFenceDao(this);
                    dao.open();
                    dao.createRecord(Integer.parseInt(oId),"Hotel","Dwell",parentDestName,String.valueOf(nearestDest),geoName,getApplicationContext());
                    dao.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else if(geoId.startsWith("SS")){
                String geoName = dbManager.getSSNameForId(Integer.valueOf(oId));
                String lastknownDwell = HIQSharedPrefrence.getString("lastknownDwell",getApplicationContext());
                if(lastknownDwell!=null && lastknownDwell.equalsIgnoreCase(geoName)){
                    // do nothing in the same dwell location
                }else{
                    SendNotificationForEntry(this,oId, geoName, HIQSharedPrefrence.getString("entryGeoStringTitleSS", getApplicationContext()),
                            HIQSharedPrefrence.getString("entryGeoStringContentSS", getApplicationContext()),
                            HIQSharedPrefrence.getString("entryGeoStringDeeplinkSS", getApplicationContext()), "SS",getApplicationContext(),"Dwell",false);
                }
                try{
                    GeoFenceDao dao = new GeoFenceDao(this);
                    dao.open();
                    dao.createRecord(Integer.parseInt(oId),"SS","Dwell",parentDestName,String.valueOf(nearestDest),geoName,getApplicationContext());
                    dao.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else if(geoId.equalsIgnoreCase(GeoFenceService.DYNAMIC_GEOFENCE_TAG_NAME)){
                if(isDebug){
                    SendNotificationForEntry(this,geoId,"geofence dwelled","↻ Dynamic circle dwelled",
                            HIQSharedPrefrence.getString("entryGeoStringContentHome", getApplicationContext()),
                            HIQSharedPrefrence.getString("entryGeoStringDeeplinkHome", getApplicationContext()), "Destination",getApplicationContext(),"Dwell",false);
                }
                try {
                    if (nearestDest != 0 && nearestDest != -1) {

                        String lastKnownDest = HIQSharedPrefrence.getString(LAST_KNOWN_DESTINATION, this);
                        String homeDest = HIQSharedPrefrence.getString(GeoFenceService.HOME_DESTINATION, this);
                        if (lastKnownDest == null || lastKnownDest.isEmpty() || !lastKnownDest.equalsIgnoreCase(parentDestName)) {
                            if (homeDest != null && !homeDest.isEmpty() && homeDest.equalsIgnoreCase(parentDestName)) {
                                if (HIQSharedPrefrence.getBoolean("exited", getApplicationContext())) {
                                    GeofenceTransitionsIntentService.SendNotificationForEntry(this,String.valueOf(nearestDest), "home", HIQSharedPrefrence.getString("entryGeoStringTitleHome", getApplicationContext()),
                                            HIQSharedPrefrence.getString("entryGeoStringContentHome", getApplicationContext()),
                                            HIQSharedPrefrence.getString("entryGeoStringDeeplinkHome", getApplicationContext()), "Destination", getApplicationContext(), "Enter", false);
                                    HIQSharedPrefrence.putBoolean("exited", false, getApplicationContext());
                                }
                            } else {
                                GeofenceTransitionsIntentService.SendNotificationForEntry(this,String.valueOf(nearestDest), parentDestName, HIQSharedPrefrence.getString("entryGeoStringTitleDest", getApplicationContext()),
                                        HIQSharedPrefrence.getString("entryGeoStringContentDest", getApplicationContext()),
                                        HIQSharedPrefrence.getString("entryGeoStringDeeplinkDest", getApplicationContext()), "Destination", getApplicationContext(), "EnterNewDestination", false);
                            }
                            String tripId = HIQSharedPrefrence.getString("tripId",getApplicationContext());
                            if(tripId == null)
                                HIQSharedPrefrence.putString("tripId", UUID.randomUUID().toString(),getApplicationContext());

                            HIQSharedPrefrence.putString(LAST_KNOWN_DESTINATION, parentDestName, this);
                            try{
                                GeoFenceDao dao = new GeoFenceDao(this);
                                dao.open();
                                dao.createRecord(nearestDest,"Destination","Dwell",parentDestName,String.valueOf(nearestDest),parentDestName,getApplicationContext());
                                dao.close();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    } else {
                        //log this event
                    }
                }catch (Exception e){
                    ExceptionUtils.logException(e);
                }
            }
        }else {
            ExceptionUtils.logException(new Throwable("Geofence unknown transition "+geofenceTransition));
        }
    }


    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the MainActivity.
     */
    public static void sendNotification(final String title, final String content, String deeplink, final Context context, boolean isBanner) {
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(context, MainActivity.class);
        //notificationIntent.putExtra(HIQConstant.DATA_DEEPLINK_FLOW, deeplink);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(MainActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        final PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setSmallIcon(R.drawable.ic_launcher);
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
               // .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
               //         R.mipmap.ic_launcher))
        builder.setContentTitle(title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setContentIntent(notificationPendingIntent);
        builder.setAutoCancel(true);
        String bannerUrl = HIQSharedPrefrence.getString("hotelDwellNotificationImage",context);
        if(isBanner && bannerUrl!=null && !bannerUrl.isEmpty()){



        }else {

            // Define the notification settings.
            builder.setSmallIcon(R.drawable.ic_launcher);
                    // In a real app, you may want to use a library like Volley
                    // to decode the Bitmap.
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                            R.mipmap.ic_launcher))
                    .setContentTitle(title)
                    .setContentText(content)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                    .setContentIntent(notificationPendingIntent);
            builder.setAutoCancel(true);
            int id = 0;

            // Get an instance of the Notification manager
            NotificationManager mNotificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(id, builder.build());

        }

        // Dismiss notification once the user touches it.

        int id = 0;
        // Issue the notification
        if(isDebug){
            long time = new Date().getTime();
            String tmpStr = String.valueOf(time);
            String last4Str = tmpStr.substring(tmpStr.length() - 5);
            id = Integer.valueOf(last4Str);
        }
    }



    private void logEvents(String name,String geoId,String type){
        try {
            HashMap<String, Object> oMap = new HashMap<String, Object>();
            oMap.put("Type", type);
            oMap.put("Name", name);
            oMap.put("ID", geoId);
            if (geoId.startsWith("Hotel"))
                oMap.put("Object Type", "Hotel");
            else if (geoId.startsWith("SS"))
                oMap.put("Object Type", "SS");
            else
                oMap.put("Object Type", "Destination");
            oMap.put("Date", new Date());

            //HIQUtil.sendEventToGAFromObject("GeoFenceEvent", "GeoFenceEvent in Android", oMap);
        }catch (Exception e){
            ExceptionUtils.logException(e);
        }
    }
}
