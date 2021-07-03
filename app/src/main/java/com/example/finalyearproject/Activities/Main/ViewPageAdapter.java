package com.example.finalyearproject.Activities.Main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.finalyearproject.Activities.Main.Notices.NoticeListFragment;
import com.example.finalyearproject.Activities.Main.SubDomains.DomainListFragment;

public class ViewPageAdapter extends FragmentPagerAdapter {
    public ViewPageAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new NoticeListFragment();
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
            case 0: return "Notices";
            case 1: return "Subdomains";
        }
        return null;
    }
}
