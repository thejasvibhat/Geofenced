package holidayiq.com.geofenced.gsons;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Manoj on 22/10/2016.
 */

public class Destination {

    @SerializedName("destination_id")
    @Expose
    private Integer destinationId;
    @SerializedName("destination_name")
    @Expose
    private String destinationName;
    @SerializedName("enter_time")
    @Expose
    private Integer enterTime;
    @SerializedName("exit_time")
    @Expose
    private Integer exitTime;

    /**
     *
     * @return
     * The destinationId
     */
    public Integer getDestinationId() {
        return destinationId;
    }

    /**
     *
     * @param destinationId
     * The destination_id
     */
    public void setDestinationId(Integer destinationId) {
        this.destinationId = destinationId;
    }

    /**
     *
     * @return
     * The destinationName
     */
    public String getDestinationName() {
        return destinationName;
    }

    /**
     *
     * @param destinationName
     * The destination_name
     */
    public void setDestinationName(String destinationName) {
        this.destinationName = destinationName;
    }

    /**
     *
     * @return
     * The enterTime
     */
    public Integer getEnterTime() {
        return enterTime;
    }

    /**
     *
     * @param enterTime
     * The enter_time
     */
    public void setEnterTime(Integer enterTime) {
        this.enterTime = enterTime;
    }

    /**
     *
     * @return
     * The exitTime
     */
    public Integer getExitTime() {
        return exitTime;
    }

    /**
     *
     * @param exitTime
     * The exit_time
     */
    public void setExitTime(Integer exitTime) {
        this.exitTime = exitTime;
    }
}
