package com.app.leon.sellabfa.Activities;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.app.leon.sellabfa.Adapters.ViewPagerAdapterRead;
import com.app.leon.sellabfa.BaseItem.BaseActivity;
import com.app.leon.sellabfa.Models.DbTables.OnLoad;
import com.app.leon.sellabfa.Models.Enums.BundleEnum;
import com.app.leon.sellabfa.Models.Enums.SharedReferenceKeys;
import com.app.leon.sellabfa.Models.Enums.SharedReferenceNames;
import com.app.leon.sellabfa.Models.ViewModels.UiElementInActivity;
import com.app.leon.sellabfa.R;
import com.app.leon.sellabfa.Utils.FontManager;
import com.app.leon.sellabfa.Utils.SharedPreferenceManager;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReadActivity extends BaseActivity {
    private static ReadActivity instance;
    @BindView(R.id.viewPagerRead)
    public ViewPager viewPager;
    SharedPreferenceManager sharedPreferenceManager;
    int select = 0;
    int pageNumber = 0;
    private ViewPagerAdapterRead viewPagerAdapterRead;
    private ArrayList<OnLoad> onLoads;

    public static ReadActivity getInstance() {
        return instance;
    }

    @Override
    protected UiElementInActivity getUiElementsInActivity() {
        UiElementInActivity uiElementInActivity = new UiElementInActivity();
        uiElementInActivity.setContentViewId(R.layout.read_activity);
        sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext(), SharedReferenceNames.ESHTERAK_OR_QERAAT.getValue());
        instance = this;
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getBundleExtra(BundleEnum.DATA.getValue());
            select = Integer.valueOf(bundle.getString(BundleEnum.ESHTERAK_OR_QERAAT.getValue()));
            sharedPreferenceManager.putData(SharedReferenceKeys.ESHTERAK_OR_QERAAT.getValue(), bundle.getString(BundleEnum.ESHTERAK_OR_QERAAT.getValue()));
            pageNumber = bundle.getInt(BundleEnum.CURRENT_PAGE.getValue());
        } else if (sharedPreferenceManager.getStringData(SharedReferenceKeys.ESHTERAK_OR_QERAAT.getValue()).length() > 0) {
            select = Integer.valueOf(sharedPreferenceManager.getStringData(SharedReferenceKeys.ESHTERAK_OR_QERAAT.getValue()));
        }
        return uiElementInActivity;
    }

    @Override
    protected void initialize() {
        ButterKnife.bind(this);
        new FillReadFragment(this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        if (select == 1)
            menuInflater.inflate(R.menu.menu_main_eshterak, menu);
        else
            menuInflater.inflate(R.menu.menu_main_qeraat, menu);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString spanString = new SpannableString(menu.getItem(i).getTitle().toString());
            int end = spanString.length();
            spanString.setSpan(new RelativeSizeSpan(1.5f), 0, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            FontManager fontManager = new FontManager(getApplicationContext());
            fontManager.setFont(spanString);
            item.setTitle(spanString);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (onLoads.size() < 1) {
            return false;
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        Intent intent;
        Bundle bundle = new Bundle();
        switch (id) {
            case R.id.location_menu:
                intent = new Intent(getApplicationContext(), LocationActivity_1.class);
                intent.putExtra(BundleEnum.CURRENT_PAGE.getValue(), viewPager.getCurrentItem());
                startActivity(intent);
                break;
            case R.id.qeraat_menu:
                sharedPreferenceManager.putData(SharedReferenceKeys.ESHTERAK_OR_QERAAT.getValue(), "1");
                intent = new Intent(getApplicationContext(), ReadActivity.class);
                bundle.putInt(BundleEnum.CURRENT_PAGE.getValue(), viewPager.getCurrentItem());
                bundle.putString(BundleEnum.ESHTERAK_OR_QERAAT.getValue(), "1");
                intent.putExtra(BundleEnum.DATA.getValue(), bundle);
                startActivity(intent);
                break;
            case R.id.eshterak_menu:
                sharedPreferenceManager.putData(SharedReferenceKeys.ESHTERAK_OR_QERAAT.getValue(), "0");
                intent = new Intent(getApplicationContext(), ReadActivity.class);
                bundle.putInt(BundleEnum.CURRENT_PAGE.getValue(), viewPager.getCurrentItem());
                bundle.putString(BundleEnum.ESHTERAK_OR_QERAAT.getValue(), "0");
                intent.putExtra(BundleEnum.DATA.getValue(), bundle);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Snackbar.make(viewPager, getString(R.string.side_bar), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    public ArrayList<OnLoad> getOnLoads() {
        return onLoads;
    }

    class FillReadFragment extends AsyncTask<String, String, String> {
        ProgressDialog progressDialog;
        Context context;

        FillReadFragment(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... strings) {
            onLoads = (ArrayList<OnLoad>) OnLoad.listAll(OnLoad.class);
            if (onLoads != null && onLoads.size() > 0) {
                viewPagerAdapterRead = new ViewPagerAdapterRead(getSupportFragmentManager(), context, onLoads, select);
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.show();
            progressDialog.setCancelable(false);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            viewPager.setAdapter(viewPagerAdapterRead);
            viewPager.setCurrentItem(pageNumber);
            progressDialog.dismiss();
            super.onPostExecute(s);
        }
    }
}
