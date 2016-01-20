package my.carfix.carfix.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import my.carfix.carfix.R;

public class GuideFragment extends Fragment
{
    public static GuideFragment newInstance()
    {
        return new GuideFragment();
    }

    public GuideFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        ViewGroup viewGroup = (ViewGroup)inflater.inflate(R.layout.fragment_guide, container, false);

        ViewPager guidePager  = (ViewPager)viewGroup.findViewById(R.id.guidePager);
        guidePager.setAdapter(new GuidePagerAdapter(this));

        return viewGroup;
    }
}
