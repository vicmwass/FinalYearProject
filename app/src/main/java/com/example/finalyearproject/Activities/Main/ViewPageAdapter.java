package com.example.finalyearproject.Activities.Main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.finalyearproject.Activities.Main.Notices.NoticeListFragment;
import com.example.finalyearproject.Activities.Main.SubDomains.DomainListFragment;

public class ViewPageAdapter extends FragmentPagerAdapter {


    private final String[] mTitles;

    public ViewPageAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
        mTitles = new String[]{"Notices","Subdomains"};
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new NotifyFragment();
            case 1:
                return new DomainListFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0: return mTitles[position];
            case 1: return mTitles[position];

        }
        return null;
    }

    public void switchTitles(int type){
        if(type==0){
            mTitles[0]="Chat page";
        }else {
            mTitles[0]="Notices";
        }
        notifyDataSetChanged();
    }
}
