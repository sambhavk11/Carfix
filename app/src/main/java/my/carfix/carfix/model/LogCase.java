package my.carfix.carfix.model;

import android.content.ContentValues;
import android.database.Cursor;

public class LogCase
{
    public static final String TABLE_NAME = "log_case";

    private String _id;

    private String caseNo;

    private String truckNo;

    private long arrivedTime;

    private String location;

    private String driverName;

    private String imageURL;

    public LogCase()
    {

    }

    public String get_id()
    {
        return _id;
    }

    public void set_id(String _id)
    {
        this._id = _id;
    }

    public String getCaseNo()
    {
        return caseNo;
    }

    public void setCaseNo(String caseNo)
    {
        this.caseNo = caseNo;
    }

    public String getTruckNo()
    {
        return truckNo;
    }

    public void setTruckNo(String truckNo)
    {
        this.truckNo = truckNo;
    }

    public long getArrivedTime()
    {
        return arrivedTime;
    }

    public void setArrivedTime(long arrivedTime)
    {
        this.arrivedTime = arrivedTime;
    }

    public String getLocation()
    {
        return location;
    }

    public void setLocation(String location)
    {
        this.location = location;
    }

    public String getDriverName()
    {
        return driverName;
    }

    public void setDriverName(String driverName)
    {
        this.driverName = driverName;
    }

    public String getImageURL()
    {
        return imageURL;
    }

    public void setImageURL(String imageURL)
    {
        this.imageURL = imageURL;
    }

    public void loadCursor(Cursor cursor)
    {
        set_id(cursor.getString(cursor.getColumnIndex("_id")));
        setCaseNo(cursor.getString(cursor.getColumnIndex("caseNo")));
        setTruckNo(cursor.getString(cursor.getColumnIndex("truckNo")));
        setArrivedTime(cursor.getLong(cursor.getColumnIndex("arrivedTime")));
        setLocation(cursor.getString(cursor.getColumnIndex("location")));
        setDriverName(cursor.getString(cursor.getColumnIndex("driverName")));
        setImageURL(cursor.getString(cursor.getColumnIndex("imageURL")));
    }

    public ContentValues toContentValues()
    {
        ContentValues contentValues = new ContentValues();

        contentValues.put("caseNo", caseNo);
        contentValues.put("truckNo", truckNo);
        contentValues.put("arrivedTime", arrivedTime);
        contentValues.put("location", location);
        contentValues.put("driverName", driverName);
        contentValues.put("imageURL", imageURL);

        return contentValues;
    }

    @Override
    public String toString()
    {
        return String.format("Case #%s", caseNo);
    }
}
