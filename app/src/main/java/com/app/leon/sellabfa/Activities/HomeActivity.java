package com.app.leon.sellabfa.Activities;

import android.Manifest;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.leon.sellabfa.BaseItem.BaseActivity;
import com.app.leon.sellabfa.Infrastructure.DifferentCompanyManager;
import com.app.leon.sellabfa.Models.Enums.DialogType;
import com.app.leon.sellabfa.Models.Enums.SharedReferenceNames;
import com.app.leon.sellabfa.Models.ViewModels.UiElementInActivity;
import com.app.leon.sellabfa.R;
import com.app.leon.sellabfa.Utils.CustomDialog;
import com.app.leon.sellabfa.Utils.SharedPreferenceManager;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.app.leon.sellabfa.Infrastructure.DifferentCompanyManager.getActiveCompanyName;

public class HomeActivity extends BaseActivity {

    SharedPreferenceManager sharedPreferenceManager;
    @BindView(R.id.imageViewDownload)
    ImageView imageViewDownload;
    @BindView(R.id.imageViewExit)
    ImageView imageViewExit;
    @BindView(R.id.imageViewUpload)
    ImageView imageViewUpload;
    @BindView(R.id.imageViewRead)
    ImageView imageViewRead;
    @BindView(R.id.textViewFooter)
    TextView textViewFooter;

    @Override
    protected UiElementInActivity getUiElementsInActivity() {
        UiElementInActivity uiElementInActivity = new UiElementInActivity();
        uiElementInActivity.setContentViewId(R.layout.home_activity);
        return uiElementInActivity;
    }

    @Override
    protected void initialize() {
        ButterKnife.bind(this);
        manage_M_permissions();


        initializeImageViewListener();
        textViewFooter.setText(DifferentCompanyManager.getCompanyName(getActiveCompanyName()));
        sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext(), SharedReferenceNames.ACCOUNT.getValue());
//        sharedPreferenceManager.put(SharedReferenceKeys.ESHTERAK_OR_QERAAT.getValue(), "1");
//        sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext());
//        Log.e("shared", String.valueOf(sharedPreferenceManager.getStringData(SharedReferenceKeys.TOKEN.getValue())));
    }

    void initializeImageViewListener() {
        imageViewExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
            }
        });
        imageViewDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DownloadActivity.class);
                startActivity(intent);
                finish();
            }
        });
        imageViewUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), UploadActivity.class);
                startActivity(intent);
                finish();
            }
        });
        imageViewRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ReadActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void manage_M_permissions() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(getApplicationContext(), "مجوز ها داده شده", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(getApplicationContext(), "مجوز رد شد \n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                forceClose();
            }
        };

        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("جهت استفاده بهتر از برنامه مجوز های پیشنهادی را قبول فرمایید")
                .setDeniedMessage("در صورت رد این مجوز قادر با استفاده از این دستگاه نخواهید بود" + "\n" +
                        "لطفا با فشار دادن دکمه" + " " + "اعطای دسترسی" + " " + "و سپس در بخش " + " دسترسی ها" + " " + " با این مجوز هاموافقت نمایید")
                .setGotoSettingButtonText("اعطای دسترسی")
                .setPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET
//                        Manifest.permission.CAMERA,
//                        Manifest.permission.RECORD_AUDIO,
//                        Manifest.permission.CALL_PHONE,
                        //,Manifest.permission.READ_LOGS
                )
                .check();
    }

    private void forceClose() {
        new CustomDialog(DialogType.Red, getApplicationContext(),
                getApplicationContext().getString(R.string.permission_not_completed),
                getApplicationContext().getString(R.string.dear_user),
                getApplicationContext().getString(R.string.call_operator),
                getApplicationContext().getString(R.string.force_close));
        finishAffinity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageViewDownload.setImageDrawable(null);
        imageViewExit.setImageDrawable(null);
        imageViewUpload.setImageDrawable(null);
        imageViewRead.setImageDrawable(null);
        sharedPreferenceManager = null;
    }
}
