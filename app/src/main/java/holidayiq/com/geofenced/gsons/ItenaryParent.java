package holidayiq.com.geofenced.gsons;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Manoj on 22/10/2016.
 */

public class ItenaryParent {
    @SerializedName("iternary_list")
    @Expose
    private List<IternaryList> iternaryList = new ArrayList<IternaryList>();

    /**
     *
     * @return
     * The iternaryList
     */
    public List<IternaryList> getIternaryList() {
        return iternaryList;
    }

    /**
     *
     * @param iternaryList
     * The iternary_list
     */
    public void setIternaryList(List<IternaryList> iternaryList) {
        this.iternaryList = iternaryList;
    }

    public void addItineraryList(IternaryList oIternary,int index)
    {
        this.iternaryList.add(index,oIternary);
    }
}
