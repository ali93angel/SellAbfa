package com.app.leon.sellabfa.Activities;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.Button;

import com.app.leon.sellabfa.BaseItem.BaseActivity;
import com.app.leon.sellabfa.Infrastructure.IAbfaService;
import com.app.leon.sellabfa.Models.DbTables.OnLoad;
import com.app.leon.sellabfa.Models.Enums.OffloadStateEnum;
import com.app.leon.sellabfa.Models.Enums.ProgressType;
import com.app.leon.sellabfa.Models.Enums.SharedReferenceKeys;
import com.app.leon.sellabfa.Models.Enums.SharedReferenceNames;
import com.app.leon.sellabfa.Models.InterCommunation.Location;
import com.app.leon.sellabfa.Models.ViewModels.UiElementInActivity;
import com.app.leon.sellabfa.R;
import com.app.leon.sellabfa.Utils.HttpClientWrapper;
import com.app.leon.sellabfa.Utils.ICallback;
import com.app.leon.sellabfa.Utils.NetworkHelper;
import com.app.leon.sellabfa.Utils.SharedPreferenceManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Retrofit;

public class UploadActivity extends BaseActivity
        implements ICallback<List<String>> {
    List<OnLoad> onLoads;
    @BindView(R.id.buttonUpload)
    Button buttonUpload;
    SharedPreferenceManager sharedPreferenceManager;
    Context context;
    List<Location> locations = new ArrayList<>();

    @Override
    protected UiElementInActivity getUiElementsInActivity() {
        UiElementInActivity uiElementInActivity = new UiElementInActivity();
        uiElementInActivity.setContentViewId(R.layout.upload_activity);
        context = this;
        return uiElementInActivity;
    }

    @Override
    protected void initialize() {
        ButterKnife.bind(this);
        sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext(), SharedReferenceNames.ACCOUNT.getValue());
        setButtonUploadOnClickListener();
    }

    void setButtonUploadOnClickListener() {
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onLoads = new ArrayList<OnLoad>();
                onLoads = OnLoad.find(OnLoad.class, "OFF_LOAD_STATE_ID = ?",
                        String.valueOf(OffloadStateEnum.INSERTED.getValue()));
                for (OnLoad onLoad : onLoads) {
                    locations.add(new Location(onLoad.idCustom, onLoad.d1, onLoad.d2, onLoad.l1,
                            onLoad.l2, onLoad.latitude, onLoad.latitude));
                }
                sendLocation();
            }
        });
    }

    void sendLocation() {
        String token = sharedPreferenceManager.getStringData(SharedReferenceKeys.TOKEN.getValue());
        Retrofit retrofit = NetworkHelper.getInstance(true, token);
        final IAbfaService location = retrofit.create(IAbfaService.class);
        Call<List<String>> call = location.counterPositions(locations);
        HttpClientWrapper.callHttpAsync(call, UploadActivity.this, context, ProgressType.SHOW.getValue());
    }

    @Override
    public void execute(List<String> strings) {
        for (String s : strings) {
            for (OnLoad onLoad : onLoads) {
                if (onLoad.idCustom.equals(s)) {
                    onLoad.offLoadStateId = OffloadStateEnum.SENT.getValue();
                    onLoad.save();
                }
            }
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Snackbar.make(buttonUpload, getString(R.string.side_bar), Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }
}
