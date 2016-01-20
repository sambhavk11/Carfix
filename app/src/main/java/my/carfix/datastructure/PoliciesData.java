package my.carfix.datastructure;

import android.os.Parcel;
import android.os.Parcelable;

public class PoliciesData implements Parcelable{
	public int PolicyID = 0;
	public String SubscriberName = "";
	public String Hotline = "";
	public String Logo = "";

	public PoliciesData(){};
	public PoliciesData(Parcel in){
		readFromParcel(in);
	}

	@Override
	public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(PolicyID);
		dest.writeString(SubscriberName);
		dest.writeString(Hotline);
		dest.writeString(Logo);

	}

	public void readFromParcel(Parcel in){
		PolicyID = in.readInt();
		SubscriberName = in.readString();
		Hotline = in.readString();
		Logo = in.readString();
	}

	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public PoliciesData createFromParcel(Parcel in) {
			return new PoliciesData(in);
		}

		public PoliciesData[] newArray(int size) {
			return new PoliciesData[size];
		}
	};
}
