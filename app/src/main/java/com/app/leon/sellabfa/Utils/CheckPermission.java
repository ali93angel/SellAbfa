package com.app.leon.sellabfa.Utils;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.app.leon.sellabfa.Models.Enums.DialogType;
import com.app.leon.sellabfa.R;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.util.ArrayList;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by Leon on 1/7/2018.
 */

public class CheckPermission {
    public static void finishAffinity() {
        throw new RuntimeException("Stub!");
    }

    public static boolean GpsEnabled(final Context context) {
        LocationManager service = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        Log.e("GPS IS:", enabled + "");
        if (!enabled) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setCancelable(false);
            alertDialog.setTitle("تنظیمات جی پی اس");
            // Setting Dialog Message
            alertDialog.setMessage("مکان یابی شما غیر فعال است ،آیا مایلید به قسمت تنظیمات مکان یابی منتقل شوید");
            alertDialog.setPositiveButton("تنظیمات", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(intent);
                }
            });

            // on pressing cancel button
            alertDialog.setNegativeButton("بستن برنامه", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finishAffinity();
                }
            });
            alertDialog.show();
        }
        return enabled;
    }

    public void manage_permissions(final Context context) {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(context, "مجوز ها داده شده", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(context, "مجوز رد شد \n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                forceClose(context);
            }
        };

        new TedPermission(context)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("جهت استفاده بهتر از برنامه مجوز های پیشنهادی را قبول فرمایید")
                .setDeniedMessage("در صورت رد این مجوز قادر با استفاده از این دستگاه نخواهید بود" + "\n" +
                        "لطفا با فشار دادن دکمه" + " " + "اعطای دسترسی" + " " + "و سپس در بخش " + " دسترسی ها" + " " + " با این مجوز هاموافقت نمایید")
                .setGotoSettingButtonText("اعطای دسترسی")
                .setPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_NETWORK_STATE,
                        Manifest.permission.RECORD_AUDIO,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.INTERNET,
                        Manifest.permission.CALL_PHONE
                        //,Manifest.permission.READ_LOGS
                )
                .check();
    }

    private void forceClose(Context context) {
        new CustomDialog(DialogType.Red, context,
                context.getString(R.string.permission_not_completed),
                context.getString(R.string.dear_user),
                context.getString(R.string.call_operator),
                context.getString(R.string.force_close));
        finishAffinity();
    }
}
