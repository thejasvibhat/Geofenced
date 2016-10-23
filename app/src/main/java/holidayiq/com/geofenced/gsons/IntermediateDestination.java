package holidayiq.com.geofenced.gsons;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Manoj on 22/10/2016.
 */

public class IntermediateDestination {
    @SerializedName("destination_id")
    @Expose
    private Integer destinationId;
    @SerializedName("destination_name")
    @Expose
    private String destinationName;
    @SerializedName("reached_time")
    @Expose
    private long reachedTime;
    @SerializedName("exited_time")
    @Expose
    private long exitedTime;

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
     * The reachedTime
     */
    public long getReachedTime() {
        return reachedTime;
    }

    /**
     *
     * @param reachedTime
     * The reached_time
     */
    public void setReachedTime(long reachedTime) {
        this.reachedTime = reachedTime;
    }

    /**
     *
     * @return
     * The exitedTime
     */
    public long getExitedTime() {
        return exitedTime;
    }

    /**
     *
     * @param exitedTime
     * The exited_time
     */
    public void setExitedTime(long exitedTime) {
        this.exitedTime = exitedTime;
    }
}
