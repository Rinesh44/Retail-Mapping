package com.example.android.retailmapping;

/**
 * Created by Shaakya on 9/4/2017.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Shaakya on 3/3/2017.
 */
public class ClientFragmentPagerAdapter extends FragmentPagerAdapter {
    private String tabTitles[] = new String[]{"Home", "Outlet List", "Furniture", "Board"};

    public ClientFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    //required overidding method
    //define the fragment according to the position
    @Override
    public Fragment getItem(int position) {
        if (position == 0)
            return new AdminHomeFragment();
        else if (position == 1)
            return new ShopList();
        else if (position == 2)
            return new Furniture();
        else if (position == 3)
            return new Material();
        else
            return new AdminHomeFragment();
    }

    //required override method
    //gets the no of fragments
    @Override
    public int getCount() {
        return 4;
    }

    //display the respective text on the tabs according to position
    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
