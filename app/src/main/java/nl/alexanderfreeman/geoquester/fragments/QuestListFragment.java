package nl.alexanderfreeman.geoquester.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.nlopez.smartlocation.SmartLocation;
import nl.alexanderfreeman.geoquester.R;

/**
 * Created by A on 18-6-2017.
 */

public class QuestListFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;

    FoundFragment found;
    NotFoundFragment not_found;
    NearbyFragment nearby;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.quest_list_fragment, container, false);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) root.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) root.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        return root;
    }

    private void setupViewPager(ViewPager viewPager) {

        not_found = new NotFoundFragment();
        found = new FoundFragment();
        nearby = new NearbyFragment();

        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(not_found, "Not Found");
        adapter.addFragment(found, "Found");
        adapter.addFragment(nearby, "Nearby");

        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(adapter);

        if (getArguments() != null) {
            if (getArguments().containsKey("selected")) {
                viewPager.setCurrentItem(getArguments().getInt("selected"));
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        SmartLocation.with(getContext()).location().stop();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }



}
