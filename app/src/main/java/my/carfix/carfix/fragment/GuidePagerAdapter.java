package my.carfix.carfix.fragment;

import android.content.ContentValues;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import my.carfix.carfix.activity.MainDrawerActivity;
import my.carfix.carfix.R;
import my.carfix.carfix.content.CarfixSQLiteOpenHelper;

public class GuidePagerAdapter extends PagerAdapter
{
    private GuideFragment guideFragment;

    public GuidePagerAdapter(GuideFragment guideFragment)
    {
        this.guideFragment = guideFragment;
    }

    @Override
    public int getCount()
    {
        return 3;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        LayoutInflater inflater = LayoutInflater.from(guideFragment.getActivity());

        ViewGroup viewGroup = null;

        switch (position)
        {
            case 0:
                viewGroup = (ViewGroup)inflater.inflate(R.layout.fragment_guide_page1, null);
                break;

            case 1:
                viewGroup = (ViewGroup)inflater.inflate(R.layout.fragment_guide_page2, null);
                break;

            case 2:
                viewGroup = (ViewGroup)inflater.inflate(R.layout.fragment_guide_page3, null);
                break;

        }

        if (viewGroup != null)
        {
            Button button = (Button)viewGroup.findViewById(R.id.get_started);

            button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent mIntent = new Intent(guideFragment.getActivity(), MainDrawerActivity.class);

                    guideFragment.getActivity().startActivity(mIntent);

                    guideFragment.getActivity().finish();

                    ContentValues cv = new ContentValues();

                    cv.put("remark", "N/A");

                    new CarfixSQLiteOpenHelper(guideFragment.getActivity()).getWritableDatabase().insert("guide", null, cv);
                }
            });

            container.addView(viewGroup);
        }

        return viewGroup;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object)
    {
        container.removeView((View)object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view == object;
    }
}
