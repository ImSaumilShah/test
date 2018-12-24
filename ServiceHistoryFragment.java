package com.sprinkler.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sprinkler.Custom.CustomToolbar;
import com.sprinkler.R;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class ServiceHistoryFragment extends Fragment {

    Activity activity;
    FragmentManager fragmentManager;
    LinearLayout toolbarlayout;
    DrawerLayout drawer_layout;
    String title;
    View view;
    TabAdapter adapter;

    public ServiceHistoryFragment(Activity activity, FragmentManager fragmentManager, LinearLayout toolbarlayout, DrawerLayout drawer_layout, String title) {
        this.activity = activity;
        this.fragmentManager = fragmentManager;
        this.toolbarlayout = toolbarlayout;
        this.drawer_layout = drawer_layout;
        this.title = title;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_servicehistory, container, false);
        CustomToolbar.setToolbar(activity, drawer_layout, toolbarlayout, title);

        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        return view;
    }


    private void setupViewPager(ViewPager viewPager) {
        adapter = new TabAdapter(getChildFragmentManager());
        adapter.addFragment(new CompletedHistoryFragment(activity, fragmentManager, toolbarlayout, drawer_layout, "Completed"), "Completed");
        adapter.addFragment(new CancelledHistoryFragment(activity, fragmentManager, toolbarlayout, drawer_layout, "Cancelled"), "Cancelled");
        viewPager.setAdapter(adapter);
    }

    private class TabAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public TabAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }


        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}
