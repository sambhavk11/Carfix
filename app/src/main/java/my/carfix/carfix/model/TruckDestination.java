package my.carfix.carfix.model;

import android.content.ContentValues;
import android.database.Cursor;

public class TruckDestination
{
    public static final String TABLE_NAME = "truck_destination";

    private String _id;

    private String caseNo;

    private String regNo;

    private String model;

    private String phoneNo;

    private String service;

    private String breakdownAddress;

    private double breakdownLatitude;

    private double breakdownLongitude;

    private String destinationAddress;

    private double destinationLatitude;

    private double destinationLongitude;

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

    public String getRegNo()
    {
        return regNo;
    }

    public void setRegNo(String regNo)
    {
        this.regNo = regNo;
    }

    public String getModel()
    {
        return model;
    }

    public void setModel(String model)
    {
        this.model = model;
    }

    public String getPhoneNo()
    {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo)
    {
        this.phoneNo = phoneNo;
    }

    public String getService()
    {
        return service;
    }

    public void setService(String service)
    {
        this.service = service;
    }

    public String getBreakdownAddress()
    {
        return breakdownAddress;
    }

    public void setBreakdownAddress(String breakdownAddress)
    {
        this.breakdownAddress = breakdownAddress;
    }

    public double getBreakdownLatitude()
    {
        return breakdownLatitude;
    }

    public void setBreakdownLatitude(double breakdownLatitude)
    {
        this.breakdownLatitude = breakdownLatitude;
    }

    public double getBreakdownLongitude()
    {
        return breakdownLongitude;
    }

    public void setBreakdownLongitude(double breakdownLongitude)
    {
        this.breakdownLongitude = breakdownLongitude;
    }

    public String getDestinationAddress()
    {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress)
    {
        this.destinationAddress = destinationAddress;
    }

    public double getDestinationLatitude()
    {
        return destinationLatitude;
    }

    public void setDestinationLatitude(double destinationLatitude)
    {
        this.destinationLatitude = destinationLatitude;
    }

    public double getDestinationLongitude()
    {
        return destinationLongitude;
    }

    public void setDestinationLongitude(double destinationLongitude)
    {
        this.destinationLongitude = destinationLongitude;
    }

    public void loadCursor(Cursor cursor)
    {
        set_id(cursor.getString(cursor.getColumnIndex("_id")));
        setCaseNo(cursor.getString(cursor.getColumnIndex("caseNo")));
        setRegNo(cursor.getString(cursor.getColumnIndex("regNo")));
        setModel(cursor.getString(cursor.getColumnIndex("model")));
        setPhoneNo(cursor.getString(cursor.getColumnIndex("phoneNo")));
        setService(cursor.getString(cursor.getColumnIndex("service")));
        setBreakdownAddress(cursor.getString(cursor.getColumnIndex("breakdownAddress")));
        setBreakdownLatitude(cursor.getDouble(cursor.getColumnIndex("breakdownLatitude")));
        setBreakdownLongitude(cursor.getDouble(cursor.getColumnIndex("breakdownLongitude")));
        setDestinationAddress(cursor.getString(cursor.getColumnIndex("destinationAddress")));
        setDestinationLatitude(cursor.getDouble(cursor.getColumnIndex("destinationLatitude")));
        setDestinationLongitude(cursor.getDouble(cursor.getColumnIndex("destinationLongitude")));
    }

    public ContentValues toContentValues()
    {
        ContentValues contentValues = new ContentValues();

        contentValues.put("caseNo", caseNo);
        contentValues.put("regNo", regNo);
        contentValues.put("model", model);
        contentValues.put("phoneNo", phoneNo);
        contentValues.put("service", service);
        contentValues.put("breakdownAddress", breakdownAddress);
        contentValues.put("breakdownLatitude", breakdownLatitude);
        contentValues.put("breakdownLongitude", breakdownLongitude);
        contentValues.put("destinationAddress", destinationAddress);
        contentValues.put("destinationLatitude", destinationLatitude);
        contentValues.put("destinationLongitude", destinationLongitude);

        return contentValues;
    }
}
