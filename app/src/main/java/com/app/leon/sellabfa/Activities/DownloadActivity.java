package com.app.leon.sellabfa.Activities;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.app.leon.sellabfa.BaseItem.BaseActivity;
import com.app.leon.sellabfa.BuildConfig;
import com.app.leon.sellabfa.DBService.FillOnLoadService;
import com.app.leon.sellabfa.Infrastructure.IAbfaService;
import com.app.leon.sellabfa.Models.DbTables.OnLoad;
import com.app.leon.sellabfa.Models.Enums.ProgressType;
import com.app.leon.sellabfa.Models.Enums.SharedReferenceKeys;
import com.app.leon.sellabfa.Models.Enums.SharedReferenceNames;
import com.app.leon.sellabfa.Models.InterCommunation.OnLoadParams;
import com.app.leon.sellabfa.Models.ViewModels.UiElementInActivity;
import com.app.leon.sellabfa.R;
import com.app.leon.sellabfa.Utils.HttpClientWrapper;
import com.app.leon.sellabfa.Utils.ICallback;
import com.app.leon.sellabfa.Utils.NetworkHelper;
import com.app.leon.sellabfa.Utils.SharedPreferenceManager;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Retrofit;

public class DownloadActivity extends BaseActivity implements ICallback<ArrayList<OnLoadParams>> {
    SharedPreferenceManager sharedPreferenceManager;
    @BindView(R.id.buttonDownload)
    Button buttonDownload;
    Context context;

    @Override
    protected UiElementInActivity getUiElementsInActivity() {
        UiElementInActivity uiElementInActivity = new UiElementInActivity();
        uiElementInActivity.setContentViewId(R.layout.download_activity);
        return uiElementInActivity;
    }

    @Override
    protected void initialize() {
        ButterKnife.bind(this);
        context = this;
        sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext(), SharedReferenceNames.ACCOUNT.getValue());
        setOnButtonDownloadClickListener();
        Log.e("Size 2", String.valueOf(OnLoad.count(OnLoad.class)));
    }

    void setOnButtonDownloadClickListener() {
        buttonDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                download();
            }
        });
    }

    void download() {
        String token = sharedPreferenceManager.getStringData(SharedReferenceKeys.TOKEN.getValue());
        Retrofit retrofit = NetworkHelper.getInstance(true, token);
        final IAbfaService getDate = retrofit.create(IAbfaService.class);
        Call<ArrayList<OnLoadParams>> call = getDate.download(BuildConfig.VERSION_CODE);
//        Call<ArrayList<OnLoadParams>> call = getDate.download();
        HttpClientWrapper.callHttpAsync(call, DownloadActivity.this, context, ProgressType.SHOW.getValue());
    }

    @Override
    public void execute(ArrayList<OnLoadParams> onLoads) {
        new FillOnLoadService(context, onLoads).execute();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Snackbar.make(buttonDownload, getString(R.string.side_bar), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }
}
