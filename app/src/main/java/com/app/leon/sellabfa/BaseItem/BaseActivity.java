package com.app.leon.sellabfa.BaseItem;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.app.leon.sellabfa.Activities.DownloadActivity;
import com.app.leon.sellabfa.Activities.ReadActivity;
import com.app.leon.sellabfa.Activities.UploadActivity;
import com.app.leon.sellabfa.Adapters.NavigationCustomAdapter;
import com.app.leon.sellabfa.Models.ViewModels.UiElementInActivity;
import com.app.leon.sellabfa.R;
import com.app.leon.sellabfa.Utils.FontManager;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public DrawerLayout drawer;
    @BindView(R.id.toolbar)
    public Toolbar toolbar;
    Typeface typeface;
    NavigationCustomAdapter adapter;
    List<NavigationCustomAdapter.DrawerItem> dataList;
    private ListView drawerList;
    private UiElementInActivity uiElementInActivity;
    private FontManager fontManager;

    public void setFont(ViewGroup viewTree, Context context) {
        initializeTypeface();
        Stack<ViewGroup> stackOfViewGroup = new Stack<ViewGroup>();
        stackOfViewGroup.push(viewTree);
        while (!stackOfViewGroup.isEmpty()) {
            ViewGroup tree = stackOfViewGroup.pop();
            for (int i = 0; i < tree.getChildCount(); i++) {
                View child = tree.getChildAt(i);

                if (child instanceof ViewGroup) {
                    stackOfViewGroup.push((ViewGroup) child);
                } else if (child instanceof Button) {
                    ((Button) child).setTypeface(typeface);
                } else if (child instanceof EditText) {
                    ((EditText) child).setTypeface(typeface);
                } else if (child instanceof TextView) {
                    ((TextView) child).setTypeface(typeface);
                } else if (child instanceof ListView) {
                    TextView textView = (TextView) ((ListView) child).getChildAt(0);
                    textView.setTypeface(typeface);
                    textView = (TextView) ((ListView) child).getChildAt(2);
                    textView.setTypeface(typeface);
                }
            }
        }
    }

    void initializeTypeface() {
        typeface = Typeface.createFromAsset(getApplicationContext().getAssets(), "font/BYekan_3.ttf");
    }

    protected abstract UiElementInActivity getUiElementsInActivity();

    protected abstract void initialize();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        super.onCreate(savedInstanceState);
        uiElementInActivity = getUiElementsInActivity();
        overridePendingTransition(R.anim.slide_up_info, R.anim.no_change);
        setContentView(uiElementInActivity.getContentViewId());
        initializeBase();
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                drawer.openDrawer(Gravity.RIGHT);
            }
        });
        initialize();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.logout, menu);
        return true;
    }

    void setOnDrawerItemClick() {
        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adpterView, View view, int position,
                                    long id) {
                if (position == 1) {
                    Intent intent = new Intent(getApplicationContext(), DownloadActivity.class);
                    startActivity(intent);
                    finish();
                } else if (position == 2) {
                    Intent intent = new Intent(getApplicationContext(), UploadActivity.class);
                    startActivity(intent);
                    finish();
                } else if (position == 3) {
                    Intent intent = new Intent(getApplicationContext(), ReadActivity.class);
                    startActivity(intent);
                    finish();
                } else if (position == 4) {
                    finishAffinity();
                }
                drawer.closeDrawer(GravityCompat.START);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initializeBase() {
        initializeTypeface();
        ButterKnife.bind(this);
        drawer = findViewById(R.id.drawer_layout);
        dataList = new ArrayList<>();
        drawer = findViewById(R.id.drawer_layout);
        drawerList = findViewById(R.id.left_drawer);
        fillDrawerListView();
        setOnDrawerItemClick();
        fontManager = new FontManager(getApplicationContext());
        fontManager.setFont(this.drawer);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dataList = null;
        adapter = null;
        typeface = null;
    }

    void fillDrawerListView() {
        dataList.add(new NavigationCustomAdapter.DrawerItem("", R.drawable.img_menu_logo));
        dataList.add(new NavigationCustomAdapter.DrawerItem(getString(R.string.download), R.drawable.img_download_information));
        dataList.add(new NavigationCustomAdapter.DrawerItem(getString(R.string.upload), R.drawable.img_data_upload_information));
        dataList.add(new NavigationCustomAdapter.DrawerItem(getString(R.string.reading), R.drawable.img_readings));
        dataList.add(new NavigationCustomAdapter.DrawerItem(getString(R.string.exit), R.drawable.img_exit));
        adapter = new NavigationCustomAdapter(this, R.layout.item_navigation, dataList);
        drawerList.setAdapter(adapter);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }
}
