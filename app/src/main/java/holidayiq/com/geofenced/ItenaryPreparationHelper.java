package holidayiq.com.geofenced;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.anupcowkur.reservoir.Reservoir;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import holidayiq.com.geofenced.geofence.GeoFenceDao;
import holidayiq.com.geofenced.geofence.GeoSqlDataTrack;
import holidayiq.com.geofenced.geofence.HIQSharedPrefrence;
import holidayiq.com.geofenced.gsons.IntermediateDestination;
import holidayiq.com.geofenced.gsons.ItenaryParent;
import holidayiq.com.geofenced.gsons.IternaryList;

/**
 * Created by Manoj on 22/10/2016.
 */

public class ItenaryPreparationHelper {

    public static void prepareItenary(Context context){
        try {
            GeoFenceDao dao = new GeoFenceDao(context);
            dao.open();
            String tripId = HIQSharedPrefrence.getString("tripId",context);
            List<GeoSqlDataTrack> dataTracks = dao.getAllRecords(tripId);
            dao.close();
            Gson gson = new Gson();
            if(dataTracks.size()>0) {
                processItenary(dataTracks, context);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void processItenary(List<GeoSqlDataTrack> dataTracks,Context context){
        GeoSqlDataTrack startData = dataTracks.get(dataTracks.size()-1);
        GeoSqlDataTrack endData = null;
        for(int i=0;i<dataTracks.size();i++){
            if(dataTracks.get(i).getHiq_id()!=startData.getHiq_id()){
                endData = dataTracks.get(i);
                break;
            }
        }
        Gson gson = new Gson();
        ItenaryParent itenaryParent = new ItenaryParent();
        List<IternaryList> list = new ArrayList<>();
        IternaryList journey = new IternaryList();
        journey.setType("journey");
        journey.setStartDestinationId(startData.getParent_id());
        journey.setStartDestination(startData.getParent_name());
        journey.setEndDestinationId(endData.getParent_id());
        journey.setEndDestination(endData.getParent_name());
        journey.setStartTime(startData.getTime());
        journey.setEndTime(dataTracks.get(0).getTime());
        List<PhotoObject> photoObjects = getPhotosBetweenTimeStamp(context,String.valueOf(startData.getTime()/1000),String.valueOf(journey.getEndTime()/1000));
        journey.setPhotos(photoObjects);
        journey.setApprovedPhotos(new ArrayList<String>());
        journey.setRejectedPhotos(new ArrayList<String>());

        List<IntermediateDestination> intermediateDestinations = new ArrayList<>();
        for(int i=0;i<dataTracks.size();i++){
            if(dataTracks.get(i).getParent_id() != startData.getParent_id() && dataTracks.get(i).getParent_id() != endData.getParent_id()){
                if(intermediateDestinations.isEmpty()){
                    IntermediateDestination newObj = new IntermediateDestination();
                    newObj.setDestinationId(dataTracks.get(i).getParent_id());
                    newObj.setDestinationName(dataTracks.get(i).getParent_name());
                    if(dataTracks.get(i).getEvent_type().equalsIgnoreCase("enter")){
                        newObj.setReachedTime(dataTracks.get(i).getTime());
                    }else if(dataTracks.get(i).getEvent_type().equalsIgnoreCase("exit")){
                        newObj.setExitedTime(dataTracks.get(i).getTime());
                    }else if(dataTracks.get(i).getEvent_type().equalsIgnoreCase("dwell")){
                        newObj.setExitedTime(dataTracks.get(i).getTime());
                    }
                    intermediateDestinations.add(newObj);
                }else {
                    boolean recordExist = false;
                    for (int j = 0; j < intermediateDestinations.size(); j++) {
                        if (intermediateDestinations.get(j).getDestinationId() == dataTracks.get(i).getParent_id()) {
                            if(dataTracks.get(i).getEvent_type().equalsIgnoreCase("enter")){
                                intermediateDestinations.get(j).setReachedTime(dataTracks.get(i).getTime());
                            }else if(dataTracks.get(i).getEvent_type().equalsIgnoreCase("exit")){
                                intermediateDestinations.get(j).setExitedTime(dataTracks.get(i).getTime());
                            }else if(dataTracks.get(i).getEvent_type().equalsIgnoreCase("dwell")){
                                intermediateDestinations.get(j).setExitedTime(dataTracks.get(i).getTime());
                            }
                            if(intermediateDestinations.get(j).getReachedTime()==0){
                                if(intermediateDestinations.get(j).getExitedTime()!=0){
                                    intermediateDestinations.get(j).setReachedTime(intermediateDestinations.get(j).getExitedTime()-60*1000);
                                }
                            }else if(intermediateDestinations.get(j).getExitedTime()==0){
                                if(intermediateDestinations.get(j).getReachedTime()!=0){
                                    intermediateDestinations.get(j).setExitedTime(intermediateDestinations.get(j).getReachedTime()+60*1000);
                                }
                            }
                            recordExist = true;
                        }
                    }
                    if(!recordExist){
                        //new object
                        IntermediateDestination newObj = new IntermediateDestination();
                        newObj.setDestinationId(dataTracks.get(i).getParent_id());
                        newObj.setDestinationName(dataTracks.get(i).getParent_name());
                        if(dataTracks.get(i).getEvent_type().equalsIgnoreCase("enter")){
                            newObj.setReachedTime(dataTracks.get(i).getTime());
                        }else if(dataTracks.get(i).getEvent_type().equalsIgnoreCase("exit")){
                            newObj.setExitedTime(dataTracks.get(i).getTime());
                        }else if(dataTracks.get(i).getEvent_type().equalsIgnoreCase("dwell")){
                            newObj.setExitedTime(dataTracks.get(i).getTime());
                        }
                        intermediateDestinations.add(newObj);
                    }
                }
            }
        }
        journey.setIntermediateDestinations(intermediateDestinations);
        list.add(journey);

        for(int i=0;i<intermediateDestinations.size();i++){
            IntermediateDestination dst = intermediateDestinations.get(i);
            IternaryList destination = new IternaryList();
            destination.setType("destination");
            destination.setObjectId(dst.getDestinationId());
            destination.setObjectName(dst.getDestinationName());
            destination.setParentDestinationId(dst.getDestinationId());
            destination.setEnterTime(dst.getReachedTime());
            destination.setExitTime(dst.getExitedTime());
            List<PhotoObject> photoObjects_int = getPhotosBetweenTimeStamp(context,String.valueOf(dst.getReachedTime()/1000),String.valueOf(dst.getExitedTime()/1000));
            destination.setPhotos(photoObjects_int);
            list.add(destination);
        }

        IternaryList destination = new IternaryList();
        destination.setType("destination");
        destination.setObjectId(endData.getParent_id());
        destination.setObjectName(endData.getParent_name());
        destination.setParentDestinationId(endData.getParent_id());
        destination.setEnterTime(endData.getTime());
        destination.setPhotos(photoObjects);
        list.add(destination);

        List<IternaryList> hotel_ss_objs = new ArrayList<>();
        for(int k=0;k<dataTracks.size();k++){
            if(dataTracks.get(k).getDest_type().equalsIgnoreCase("hotel") || dataTracks.get(k).getDest_type().equalsIgnoreCase("ss")) {
                if (hotel_ss_objs.isEmpty()) {
                    IternaryList hotel_ss_obj = new IternaryList();
                    if (dataTracks.get(k).getDest_type().equalsIgnoreCase("hotel")) {
                        hotel_ss_obj.setType("hotel");
                    } else if (dataTracks.get(k).getDest_type().equalsIgnoreCase("ss")) {
                        hotel_ss_obj.setType("ss");
                    }
                    hotel_ss_obj.setObjectId(dataTracks.get(k).getHiq_id());
                    hotel_ss_obj.setObjectName(dataTracks.get(k).getObject_name());
                    hotel_ss_obj.setParentDestinationId(dataTracks.get(k).getParent_id());
                    hotel_ss_obj.setParentDestinationName(dataTracks.get(k).getParent_name());
                    if (dataTracks.get(k).getEvent_type().equalsIgnoreCase("enter")) {
                        hotel_ss_obj.setEnterTime(dataTracks.get(k).getTime());
                    } else if (dataTracks.get(k).getEvent_type().equalsIgnoreCase("dwell")) {
                        hotel_ss_obj.setExitTime(dataTracks.get(k).getTime());
                    } else if (dataTracks.get(k).getEvent_type().equalsIgnoreCase("exit")) {
                        hotel_ss_obj.setExitTime(dataTracks.get(k).getTime());
                    }
                    hotel_ss_obj.setPhotos(photoObjects);
                    hotel_ss_objs.add(hotel_ss_obj);

                } else {
                    boolean recordExist = false;
                    for (int m = 0; m < hotel_ss_objs.size(); m++) {
                        if (hotel_ss_objs.get(m).getObjectId() == dataTracks.get(k).getHiq_id()){
                            IternaryList existObj = hotel_ss_objs.get(m);
                            if (dataTracks.get(k).getEvent_type().equalsIgnoreCase("enter")) {
                                existObj.setEnterTime(dataTracks.get(k).getTime());
                            } else if (dataTracks.get(k).getEvent_type().equalsIgnoreCase("dwell")) {
                                existObj.setExitTime(dataTracks.get(k).getTime());
                            } else if (dataTracks.get(k).getEvent_type().equalsIgnoreCase("exit")) {
                                existObj.setExitTime(dataTracks.get(k).getTime());
                            }
                            if(existObj.getExitTime()==0){
                                if(existObj.getEnterTime()!=0){
                                    existObj.setExitTime(existObj.getEnterTime()+60*1000);
                                }
                            }
                            if(existObj.getEnterTime()==0){
                                if(existObj.getExitTime()!=0){
                                    existObj.setEnterTime(existObj.getExitTime()-60*1000);
                                }
                            }
                            recordExist = true;
                        }
                    }
                    if(!recordExist){
                            IternaryList hotel_ss_obj = new IternaryList();
                            if (dataTracks.get(k).getDest_type().equalsIgnoreCase("hotel")) {
                                hotel_ss_obj.setType("hotel");
                            } else if (dataTracks.get(k).getDest_type().equalsIgnoreCase("ss")) {
                                hotel_ss_obj.setType("ss");
                            }
                            hotel_ss_obj.setObjectId(dataTracks.get(k).getHiq_id());
                            hotel_ss_obj.setObjectName(dataTracks.get(k).getObject_name());
                            hotel_ss_obj.setParentDestinationId(dataTracks.get(k).getParent_id());
                            hotel_ss_obj.setParentDestinationName(dataTracks.get(k).getParent_name());
                            if (dataTracks.get(k).getEvent_type().equalsIgnoreCase("enter")) {
                                hotel_ss_obj.setEnterTime(dataTracks.get(k).getTime());
                            } else if (dataTracks.get(k).getEvent_type().equalsIgnoreCase("dwell")) {
                                hotel_ss_obj.setExitTime(dataTracks.get(k).getTime());
                            } else if (dataTracks.get(k).getEvent_type().equalsIgnoreCase("exit")) {
                                hotel_ss_obj.setExitTime(dataTracks.get(k).getTime());
                            }
                            hotel_ss_obj.setPhotos(photoObjects);
                            hotel_ss_objs.add(hotel_ss_obj);
                    }
                }
            }
        }

        for (int i=0;i<hotel_ss_objs.size();i++){
            list.add(hotel_ss_objs.get(i));
        }


        itenaryParent.setIternaryList(list);
        String journsada = gson.toJson(itenaryParent);
        Log.e("dataItenary",journsada);
        try {
            Reservoir.put("dataObj",itenaryParent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<PhotoObject> getPhotosBetweenTimeStamp(Context context,String fromtime,String totime) {
        ArrayList<PhotoObject> mAlbumsList = new ArrayList<>();
        try {
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA, MediaStore.Images.Media.DATE_ADDED};
            Cursor cursor = context.getContentResolver().query(uri, projection, MediaStore.Images.Media.DATE_ADDED + ">= ? AND " + MediaStore.Images.Media.DATE_ADDED + " <= ?", new String[]{fromtime, totime}, null);

            ArrayList<String> ids = new ArrayList<String>();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    PhotoObject album = new PhotoObject();
                    int columnIndex = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                    album.coverID = cursor.getLong(columnIndex);
                    columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
                    album.display_name = cursor.getString(columnIndex);
                    columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                    album.path = cursor.getString(columnIndex);
                    columnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED);
                    album.time_added = cursor.getLong(columnIndex);
                    mAlbumsList.add(album);
                    ids.add(album.id);
                }
                cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return mAlbumsList;
    }
}
