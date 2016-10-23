package holidayiq.com.geofenced;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Manoj on 22/10/2016.
 */

public class PhotoObject {
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("name")
    @Expose
    public String name;
    @SerializedName("path")
    @Expose
    public String path;
    @SerializedName("display_name")
    @Expose
    public String display_name;
    @SerializedName("coverID")
    @Expose
    public long coverID;
    @SerializedName("time_added")
    @Expose
    public long time_added;
    @SerializedName("isSelected")
    @Expose
    public Boolean isSelected = true;
}
