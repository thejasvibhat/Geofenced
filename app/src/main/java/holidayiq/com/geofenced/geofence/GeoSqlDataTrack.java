package holidayiq.com.geofenced.geofence;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Manoj on 23/08/2016.
 */
public class GeoSqlDataTrack {

    @SerializedName("hiq_id")
    @Expose
    int hiq_id;
    @SerializedName("dest_type")
    @Expose
    String dest_type;
    @SerializedName("time")
    @Expose
    long time;
    @SerializedName("event_type")
    @Expose
    String event_type;
    @SerializedName("id")
    @Expose
    int id;
    @SerializedName("parent_id")
    @Expose
    int parent_id;
    @SerializedName("object_name")
    @Expose
    String object_name;
    @SerializedName("parent_name")
    @Expose
    String parent_name;

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    @SerializedName("trip_id")
    @Expose
    String trip_id;


    public int getHiq_id() {
        return hiq_id;
    }

    public void setHiq_id(int hiq_id) {
        this.hiq_id = hiq_id;
    }

    public String getDest_type() {
        return dest_type;
    }

    public void setDest_type(String dest_type) {
        this.dest_type = dest_type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getEvent_type() {
        return event_type;
    }

    public void setEvent_type(String event_type) {
        this.event_type = event_type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParent_id() {
        return parent_id;
    }

    public void setParent_id(int parent_id) {
        this.parent_id = parent_id;
    }

    public String getObject_name() {
        return object_name;
    }

    public void setObject_name(String object_name) {
        this.object_name = object_name;
    }

    public String getParent_name() {
        return parent_name;
    }

    public void setParent_name(String parent_name) {
        this.parent_name = parent_name;
    }
}
