package my.carfix.datastructure;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class WorkshopsData implements Parcelable
{
    public List<WorkshopData> workshops = new ArrayList<WorkshopData>();

    public WorkshopsData()
    {

    }

    public WorkshopsData(Parcel in)
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
        dest.writeList(workshops);
    }

    public void readFromParcel(Parcel in)
    {
        workshops = in.readArrayList(WorkshopData.class.getClassLoader());
    }

    public static WorkshopsData get(String jSonString)
    {
        WorkshopsData result = null;
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
        try
        {
            TypeFactory tf = mapper.getTypeFactory();
            JavaType listOfObjs = tf.constructCollectionType(ArrayList.class,WorkshopData.class);
            List<WorkshopData> workshops = mapper.readValue(jSonString, listOfObjs);
            result = new WorkshopsData();
            result.workshops = workshops;
        }
        catch (JsonParseException e)
        {
            e.printStackTrace();
            Log.e("error", e.getMessage());
        }
        catch (JsonMappingException e)
        {
            Log.e("error", e.getMessage());
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            Log.e("error", e.getMessage());
        }

        return result;
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator()
    {
        public WorkshopsData createFromParcel(Parcel in)
        {
            return new WorkshopsData(in);
        }

        public WorkshopsData[] newArray(int size)
        {
            return new WorkshopsData[size];
        }
    };
}
