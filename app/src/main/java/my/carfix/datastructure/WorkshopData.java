package my.carfix.datastructure;

import android.os.Parcel;
import android.os.Parcelable;

public class WorkshopData implements Parcelable
{
    public String CompanyName = "";
    public String ContactNo = "";
    public String ContactName = "";
    public double Latitude = 0;
    public double Longitude = 0;

    public WorkshopData()
    {

    }

    public WorkshopData(Parcel in)
    {
        readFromParcel(in);
    }

    @Override
    public int describeContents()
    {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(CompanyName);
        dest.writeString(ContactNo);
        dest.writeString(ContactName);
        dest.writeDouble(Latitude);
        dest.writeDouble(Longitude);
    }

    public void readFromParcel(Parcel in)
    {
        CompanyName = in.readString();
        ContactNo = in.readString();
        ContactName = in.readString();
        Latitude = in.readDouble();
        Longitude = in.readDouble();
    }
}
