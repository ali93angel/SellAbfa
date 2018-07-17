package com.app.leon.sellabfa.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.leon.sellabfa.R;

import java.util.List;

/**
 * Created by Leon on 12/4/2017.
 */


public class NavigationCustomAdapter extends ArrayAdapter<NavigationCustomAdapter.DrawerItem> {
    private Context context;
    private List<DrawerItem> drawerItemList;
    private int layoutResID;

    public NavigationCustomAdapter(Context context, int layoutResourceID,
                                   List<DrawerItem> listItems) {
        super(context, layoutResourceID, listItems);
        this.context = context;
        this.drawerItemList = listItems;
        this.layoutResID = layoutResourceID;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Nullable
    @Override
    public DrawerItem getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public int getPosition(DrawerItem item) {
        return super.getPosition(item);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        DrawerItemHolder drawerHolder;
        convertView = null;
        DrawerItem dItem;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        if (position == 0) {
            drawerHolder = new DrawerItemHolder();
            convertView = inflater.inflate(R.layout.item_navigation_, parent, false);
            drawerHolder.imageViewIcon = convertView.findViewById(R.id.imageViewIcon);
            dItem = this.drawerItemList.get(position);
            drawerHolder.imageViewIcon.setImageDrawable(convertView.getResources().getDrawable(
                    dItem.getImgResID()));
        } else {
            drawerHolder = new DrawerItemHolder();
            convertView = inflater.inflate(layoutResID, parent, false);
            drawerHolder.textViewTitle = convertView
                    .findViewById(R.id.textViewTitle);
            if (position == 4)
                drawerHolder.textViewTitle.setTextColor(context.getResources().getColor(R.color.red4));
            drawerHolder.imageViewIcon = convertView.findViewById(R.id.imageViewIcon);
            dItem = this.drawerItemList.get(position);
            drawerHolder.imageViewIcon.setImageDrawable(convertView.getResources().getDrawable(
                    dItem.getImgResID()));
            drawerHolder.textViewTitle.setText(dItem.getItemName());
        }
        convertView.setTag(drawerHolder);
        return convertView;
    }

    private static class DrawerItemHolder {
        TextView textViewTitle;
        ImageView imageViewIcon;
    }

    public static class DrawerItem {

        String ItemName;
        int imgResID;

        public DrawerItem(String itemName, int imgResID) {
            super();
            ItemName = itemName;
            this.imgResID = imgResID;
        }

        String getItemName() {
            return ItemName;
        }

        void setItemName(String itemName) {
            ItemName = itemName;
        }

        int getImgResID() {
            return imgResID;
        }

        void setImgResID(int imgResID) {
            this.imgResID = imgResID;
        }

    }
}
