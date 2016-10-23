package holidayiq.com.geofenced.gsons;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import holidayiq.com.geofenced.PhotoObject;

/**
 * Created by Manoj on 22/10/2016.
 */

public class IternaryList {

    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("start_destination")
    @Expose
    private String startDestination;
    @SerializedName("end_destination")
    @Expose
    private String endDestination;
    @SerializedName("start_destination_id")
    @Expose
    private Integer startDestinationId;
    @SerializedName("end_destination_id")
    @Expose
    private Integer endDestinationId;
    @SerializedName("start_time")
    @Expose
    private long startTime;
    @SerializedName("end_time")
    @Expose
    private long endTime;
    @SerializedName("day")
    @Expose
    private Integer day;
    @SerializedName("destinations")
    @Expose
    private List<Destination> destinations = new ArrayList<Destination>();
    @SerializedName("photos")
    @Expose
    private List<PhotoObject> photos;
    @SerializedName("approved_photos")
    @Expose
    private List<String> approvedPhotos = new ArrayList<String>();
    @SerializedName("rejected_photos")
    @Expose
    private List<String> rejectedPhotos = new ArrayList<String>();
    @SerializedName("intermediate_destinations")
    @Expose
    private List<IntermediateDestination> intermediateDestinations = new ArrayList<IntermediateDestination>();
    @SerializedName("object_id")
    @Expose
    private Integer objectId;
    @SerializedName("object_name")
    @Expose
    private String objectName;
    @SerializedName("parent_destination_id")
    @Expose
    private Integer parentDestinationId;

    @SerializedName("parent_destination_name")
    @Expose
    private String parentDestinationName;


    @SerializedName("enter_time")
    @Expose
    private long enterTime;
    @SerializedName("exit_time")
    @Expose
    private long exitTime;

    /**
     *
     * @return
     * The type
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     * The type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @return
     * The startDestination
     */
    public String getStartDestination() {
        return startDestination;
    }

    /**
     *
     * @param startDestination
     * The start_destination
     */
    public void setStartDestination(String startDestination) {
        this.startDestination = startDestination;
    }

    /**
     *
     * @return
     * The endDestination
     */
    public String getEndDestination() {
        return endDestination;
    }

    /**
     *
     * @param endDestination
     * The end_destination
     */
    public void setEndDestination(String endDestination) {
        this.endDestination = endDestination;
    }

    /**
     *
     * @return
     * The startDestinationId
     */
    public Integer getStartDestinationId() {
        return startDestinationId;
    }

    /**
     *
     * @param startDestinationId
     * The start_destination_id
     */
    public void setStartDestinationId(Integer startDestinationId) {
        this.startDestinationId = startDestinationId;
    }

    /**
     *
     * @return
     * The endDestinationId
     */
    public Integer getEndDestinationId() {
        return endDestinationId;
    }

    /**
     *
     * @param endDestinationId
     * The end_destination_id
     */
    public void setEndDestinationId(Integer endDestinationId) {
        this.endDestinationId = endDestinationId;
    }

    /**
     *
     * @return
     * The startTime
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     *
     * @param startTime
     * The start_time
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     *
     * @return
     * The endTime
     */
    public long getEndTime() {
        return endTime;
    }

    /**
     *
     * @param endTime
     * The end_time
     */
    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    /**
     *
     * @return
     * The day
     */
    public Integer getDay() {
        return day;
    }

    /**
     *
     * @param day
     * The day
     */
    public void setDay(Integer day) {
        this.day = day;
    }

    /**
     *
     * @return
     * The destinations
     */
    public List<Destination> getDestinations() {
        return destinations;
    }

    /**
     *
     * @param destinations
     * The destinations
     */
    public void setDestinations(List<Destination> destinations) {
        this.destinations = destinations;
    }

    /**
     *
     * @return
     * The photos
     */
    public List<PhotoObject> getPhotos() {
        return photos;
    }

    /**
     *
     * @param photos
     * The photos
     */
    public void setPhotos(List<PhotoObject> photos) {
        this.photos = photos;
    }

    /**
     *
     * @return
     * The approvedPhotos
     */
    public List<String> getApprovedPhotos() {
        return approvedPhotos;
    }

    /**
     *
     * @param approvedPhotos
     * The approved_photos
     */
    public void setApprovedPhotos(List<String> approvedPhotos) {
        this.approvedPhotos = approvedPhotos;
    }

    /**
     *
     * @return
     * The rejectedPhotos
     */
    public List<String> getRejectedPhotos() {
        return rejectedPhotos;
    }

    /**
     *
     * @param rejectedPhotos
     * The rejected_photos
     */
    public void setRejectedPhotos(List<String> rejectedPhotos) {
        this.rejectedPhotos = rejectedPhotos;
    }

    /**
     *
     * @return
     * The intermediateDestinations
     */
    public List<IntermediateDestination> getIntermediateDestinations() {
        return intermediateDestinations;
    }

    /**
     *
     * @param intermediateDestinations
     * The intermediate_destinations
     */
    public void setIntermediateDestinations(List<IntermediateDestination> intermediateDestinations) {
        this.intermediateDestinations = intermediateDestinations;
    }

    /**
     *
     * @return
     * The objectId
     */
    public Integer getObjectId() {
        return objectId;
    }

    /**
     *
     * @param objectId
     * The object_id
     */
    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
    }

    /**
     *
     * @return
     * The objectName
     */
    public String getObjectName() {
        return objectName;
    }

    /**
     *
     * @param objectName
     * The object_name
     */
    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    /**
     *
     * @return
     * The parentDestinationId
     */
    public Integer getParentDestinationId() {
        return parentDestinationId;
    }

    /**
     *
     * @param parentDestinationId
     * The parent_destination_id
     */
    public void setParentDestinationId(Integer parentDestinationId) {
        this.parentDestinationId = parentDestinationId;
    }

    /**
     *
     * @return
     * The enterTime
     */
    public long getEnterTime() {
        return enterTime;
    }

    /**
     *
     * @param enterTime
     * The enter_time
     */
    public void setEnterTime(long enterTime) {
        this.enterTime = enterTime;
    }

    /**
     *
     * @return
     * The exitTime
     */
    public long getExitTime() {
        return exitTime;
    }

    /**
     *
     * @param exitTime
     * The exit_time
     */
    public void setExitTime(long exitTime) {
        this.exitTime = exitTime;
    }

    public String getParentDestinationName() {
        return parentDestinationName;
    }

    public void setParentDestinationName(String parentDestinationName) {
        this.parentDestinationName = parentDestinationName;
    }
}
