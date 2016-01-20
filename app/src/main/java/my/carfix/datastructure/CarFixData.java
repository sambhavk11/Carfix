package my.carfix.datastructure;

import java.io.IOException;
import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CarFixData implements Parcelable{
	public String VehReg = "";
	public String PhoneNo = "";
	public String Address = "";
	public double Latitude = 0;
	public double Longitude = 0;
	public String VehModel = "";
	public int PolicyID = 0;
	public String Passcode = "";
	public String ServiceNeeded = "";
	public ArrayList<PoliciesData> MatchedPolicies = new ArrayList<PoliciesData>();
	public ArrayList<PoliciesData> GeneralHelps =  new ArrayList<PoliciesData>();
	
	public CarFixData(){};
	public CarFixData(Parcel in){
		readFromParcel(in);
	}

	public void readFromParcel(Parcel in){
		VehReg = in.readString();
		PhoneNo = in.readString();
		Address = in.readString();
		Latitude = in.readDouble();
		Longitude = in.readDouble();
		VehModel = in.readString();
		PolicyID = in.readInt();
		Passcode = in.readString();
		ServiceNeeded = in.readString();
		MatchedPolicies = in.readArrayList(PoliciesData.class.getClassLoader());
		GeneralHelps = in.readArrayList(PoliciesData.class.getClassLoader());
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(VehReg);
		dest.writeString(PhoneNo);
		dest.writeString(Address);
		dest.writeDouble(Latitude);
		dest.writeDouble(Longitude);
		dest.writeString(VehModel);
		dest.writeInt(PolicyID);
		dest.writeString(Passcode);
		dest.writeString(ServiceNeeded);
		dest.writeList(MatchedPolicies);
		dest.writeList(GeneralHelps);
	}
	
	public static CarFixData get(String jSonString){
		CarFixData result = null;
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
		try {
			result = mapper.readValue(jSonString, CarFixData.class);
			//if (Constants.DEBUG)
			//	Log.i("result mapper", result.toString());
		} catch (JsonParseException e) {
			e.printStackTrace();
			Log.e("error", e.getMessage());
		} catch (JsonMappingException e) {
			Log.e("error", e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e("error", e.getMessage());
		}
		return result;
	}
}
