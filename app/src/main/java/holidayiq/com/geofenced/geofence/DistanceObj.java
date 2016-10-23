package holidayiq.com.geofenced.geofence;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Manoj on 28/07/2016.
 */
public class DistanceObj implements Parcelable{

    String name;
    int place_id;

    double place_latitude;
    double place_longitude;

    double start_latitude;
    double start_longitude;

    double distance;
    String type;

    public DistanceObj(String name, int place_id, double place_latitude, double place_longitude, double start_latitude, double start_longitude, double distance,String type) {
        this.name = name;
        this.place_id = place_id;
        this.place_latitude = place_latitude;
        this.place_longitude = place_longitude;
        this.start_latitude = start_latitude;
        this.start_longitude = start_longitude;
        this.distance = distance;
        this.type = type;
    }


    protected DistanceObj(Parcel in) {
        name = in.readString();
        place_id = in.readInt();
        place_latitude = in.readDouble();
        place_longitude = in.readDouble();
        start_latitude = in.readDouble();
        start_longitude = in.readDouble();
        distance = in.readDouble();
    }

    public static final Creator<DistanceObj> CREATOR = new Creator<DistanceObj>() {
        @Override
        public DistanceObj createFromParcel(Parcel in) {
            return new DistanceObj(in);
        }

        @Override
        public DistanceObj[] newArray(int size) {
            return new DistanceObj[size];
        }
    };

    @Override
    public String toString() {
        return type+" "+name+" "+distance+" "+place_latitude+" "+place_longitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(place_id);
        dest.writeDouble(place_latitude);
        dest.writeDouble(place_longitude);
        dest.writeDouble(start_latitude);
        dest.writeDouble(start_longitude);
        dest.writeDouble(distance);
    }
}
