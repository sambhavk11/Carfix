package my.carfix.carfix.model;

import android.content.ContentValues;
import android.database.Cursor;

public class Vehicle
{
    public static final String TABLE_NAME = "vehicle";

    private String _id;

    private String regNo;

    private String model;

    private String phoneNo;

    private String service;

    public Vehicle()
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

    public void loadCursor(Cursor cursor)
    {
        set_id(cursor.getString(cursor.getColumnIndex("_id")));
        setRegNo(cursor.getString(cursor.getColumnIndex("regNo")));
        setModel(cursor.getString(cursor.getColumnIndex("model")));
        setPhoneNo(cursor.getString(cursor.getColumnIndex("phoneNo")));
        setService(cursor.getString(cursor.getColumnIndex("service")));
    }

    public ContentValues toContentValues()
    {
        ContentValues contentValues = new ContentValues();

        contentValues.put("regNo", regNo);
        contentValues.put("model", model);
        contentValues.put("phoneNo", phoneNo);
        contentValues.put("service", service);

        return contentValues;
    }

    @Override
    public String toString()
    {
        return String.format("%s - %s", regNo, model.equals("") ? "?" : model);
    }
}
