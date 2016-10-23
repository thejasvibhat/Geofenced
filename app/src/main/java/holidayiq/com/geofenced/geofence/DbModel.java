package holidayiq.com.geofenced.geofence;

import android.os.Parcel;
import android.os.Parcelable;

public class DbModel implements Parcelable {

    private int rowId;
    private int taskStatus;
    private long taskId;

    private String taskValue;
    private String type;
    private String hotelname;
    private String destName;
    private String userId;
    // this is used to regenerate your object. All Parcelables must have a
    // CREATOR that implements these two methods
    public static final Creator<DbModel> CREATOR = new Creator<DbModel>() {
        public DbModel createFromParcel(Parcel source) {

            DbModel model = new DbModel();
            model.rowId = source.readInt();
            model.taskId = source.readLong();
            model.taskValue = source.readString();
            model.taskStatus = source.readInt();
            model.type = source.readString();
            model.hotelname = source.readString();
            model.destName = source.readString();
            model.userId = source.readString();

            return model;
        }

        public DbModel[] newArray(int size) {
            return new DbModel[size];
        }
    };

    public static Creator<DbModel> getCreator() {
        return CREATOR;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getRowId() {
        return rowId;
    }

    public void setRowId(int rowId) {
        this.rowId = rowId;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getTaskValue() {
        return taskValue;
    }

    public void setTaskValue(String taskValue) {
        this.taskValue = taskValue;
    }

    public int getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHotelname() {
        return hotelname;
    }

    public void setHotelname(String hotelname) {
        this.hotelname = hotelname;
    }

    public String getDestName() {
        return destName;
    }

    public void setDestName(String destName) {
        this.destName = destName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(rowId);
        out.writeLong(taskId);
        out.writeString(taskValue);
        out.writeInt(taskStatus);
        out.writeString(type);
        out.writeString(hotelname);
        out.writeString(destName);
        out.writeString(userId);

    }
}
