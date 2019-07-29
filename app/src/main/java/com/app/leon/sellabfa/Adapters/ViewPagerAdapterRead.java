package com.app.leon.sellabfa.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import com.app.leon.sellabfa.Fragment.ReadFragment;
import com.app.leon.sellabfa.Models.DbTables.OnLoad;

import java.util.List;

public class ViewPagerAdapterRead extends FragmentStatePagerAdapter {
    int select;
    private LayoutInflater inflater;
    private Context context;
    private List<OnLoad> onLoads;

    public ViewPagerAdapterRead(FragmentManager fragmentManager, Context context, List<OnLoad> onLoads, int select) {
        super(fragmentManager);
        this.context = context;
        this.onLoads = onLoads;
        this.select = select;
    }

    @Override
    public Fragment getItem(int position) {
        return ReadFragment.newInstance(onLoads.get(position), select);
    }

    @Override
    public int getCount() {
        return onLoads.size();
    }

    @Override
    public int getItemPosition(Object object) {
        notifyDataSetChanged();
        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        FragmentManager manager = ((Fragment) object).getFragmentManager();
        FragmentTransaction trans = manager.beginTransaction();
        trans.remove((Fragment) object);
        trans.commit();
        super.destroyItem(container, position, object);
    }
}
