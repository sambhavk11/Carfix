package my.carfix.carfix.model;

import android.content.ContentValues;
import android.database.Cursor;

public class Truck
{
    public static final String TABLE_NAME = "truck";

    private String _id;

    private String truckNo;

    private String userCode;

    private long autoCheckInTime;

    public String get_id()
    {
        return _id;
    }

    public void set_id(String _id)
    {
        this._id = _id;
    }

    public String getTruckNo()
    {
        return truckNo;
    }

    public void setTruckNo(String truckNo)
    {
        this.truckNo = truckNo;
    }

    public String getUserCode()
    {
        return userCode;
    }

    public void setUserCode(String userCode)
    {
        this.userCode = userCode;
    }

    public long getAutoCheckInTime() { return autoCheckInTime; }

    public void setAutoCheckInTime(long autoCheckInTime) { this.autoCheckInTime = autoCheckInTime; }

    public void loadCursor(Cursor cursor)
    {
        set_id(cursor.getString(cursor.getColumnIndex("_id")));
        setTruckNo(cursor.getString(cursor.getColumnIndex("truckNo")));
        setUserCode(cursor.getString(cursor.getColumnIndex("userCode")));
        setAutoCheckInTime(cursor.getLong(cursor.getColumnIndex("autoCheckInTime")));
    }

    public ContentValues toContentValues()
    {
        ContentValues contentValues = new ContentValues();

        contentValues.put("truckNo", truckNo);
        contentValues.put("userCode", userCode);
        contentValues.put("autoCheckInTime", autoCheckInTime);

        return contentValues;
    }
}
