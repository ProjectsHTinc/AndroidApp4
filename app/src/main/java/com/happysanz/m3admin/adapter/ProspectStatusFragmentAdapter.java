package com.happysanz.m3admin.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.happysanz.m3admin.fragments.AllProspectsFragment;
import com.happysanz.m3admin.fragments.ConfirmedProspectsFragment;
import com.happysanz.m3admin.fragments.RejectedProspectsFragment;

public class ProspectStatusFragmentAdapter extends FragmentStatePagerAdapter {

    public ProspectStatusFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AllProspectsFragment();
            case 1:
                return new RejectedProspectsFragment();
            case 2:
                return new ConfirmedProspectsFragment();
        }
        return null;
    }


    @Override
    public int getCount() {
        return 3;
    }
}