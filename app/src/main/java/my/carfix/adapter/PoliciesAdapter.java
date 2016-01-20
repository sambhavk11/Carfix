package my.carfix.adapter;

import java.util.ArrayList;
import java.util.List;

import com.squareup.picasso.Picasso;

import my.carfix.carfix.R;
import my.carfix.datastructure.PoliciesData;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PoliciesAdapter extends ArrayAdapter<PoliciesData>{
	private ArrayList<PoliciesData> mPoliciesDataList;
	private LayoutInflater mInflater = null;
	private Context mContext;
	private Activity mActivity;

	public PoliciesAdapter(Context context, int resource, List<PoliciesData> objects, int policyType) {
		super(context, resource, objects);
		mContext = context;
		mActivity = (Activity) context;
		mPoliciesDataList = (ArrayList<PoliciesData>) objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Holder holder;
		PoliciesData policyData =  mPoliciesDataList.get(position);
		if(convertView == null){
			holder = new Holder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.policy_row, parent, false);
			holder.policyImage =  (ImageView) convertView.findViewById(R.id.policy_image);
			holder.policyHotline = (TextView) convertView.findViewById(R.id.policy_hotline);
			holder.policyName = (TextView) convertView.findViewById(R.id.policy_name);
			convertView.setTag(holder);
		}else{
			holder = (Holder) convertView.getTag();
		}
		
		holder.policyName.setText(policyData.SubscriberName);
		holder.policyHotline.setText("Hotline: " + policyData.Hotline);
		
		if(!policyData.Logo.equals("")){
			Picasso.with(mContext)
			.load(policyData.Logo)
			.into(holder.policyImage);
		}else{
			Picasso.with(mContext)
			.load("asd")
			.into(holder.policyImage);
		}
	
		/*
		if (!reward_list.get(position).pic_url.equals("")) {
			Picasso.with(mContext)
			.load(reward_list.get(position).pic_url)
			.placeholder(R.drawable.image_holder)
			.error(R.drawable.image_broken)
			.resize(getImageWidth(), getImageHeight(320, 180))
			.into(holder.campaign_img);
		}else{
			Picasso.with(mContext)
			.load(R.drawable.image_broken)
			.resize(getImageWidth(), getImageHeight(320, 180))
			.into(holder.campaign_img);
		}
		*/
		return convertView;
	}
	
	static class Holder{
		public ImageView policyImage;
		public TextView policyName;
		public TextView policyHotline;
	}
	
}
