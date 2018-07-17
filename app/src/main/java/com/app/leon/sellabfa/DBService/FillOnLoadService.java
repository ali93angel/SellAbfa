package com.app.leon.sellabfa.DBService;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.app.leon.sellabfa.Models.DbTables.OnLoad;
import com.app.leon.sellabfa.Models.Enums.DialogType;
import com.app.leon.sellabfa.Models.InterCommunation.OnLoadParams;
import com.app.leon.sellabfa.R;
import com.app.leon.sellabfa.Utils.CustomDialog;
import com.orm.SugarRecord;
import com.orm.SugarTransactionHelper;

import java.util.ArrayList;

public class FillOnLoadService extends AsyncTask<String, String, String> {
    Context context;
    ProgressDialog progressDialog;
    ArrayList<OnLoadParams> onLoadsParams = new ArrayList<>();
    ArrayList<OnLoad> onLoads = new ArrayList<>();

    public FillOnLoadService(Context context, ArrayList<OnLoadParams> onLoadParams) {
        this.context = context;
        this.onLoadsParams = onLoadParams;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.show();
        progressDialog.setCancelable(false);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        new CustomDialog(DialogType.Green, context, s, context.getString(R.string.dear_user),
                context.getString(R.string.attention), context.getString(R.string.accepted));
        progressDialog.dismiss();
    }

    @Override
    protected String doInBackground(String... strings) {
        String message;
        try {
            for (OnLoadParams onLoadParams : onLoadsParams) {
                OnLoad onLoad = new OnLoad(onLoadParams);
                onLoads.add(onLoad);
            }
            SugarTransactionHelper.doInTransaction(new SugarTransactionHelper.Callback() {
                @Override
                public void manipulateInTransaction() {
                    SugarRecord.saveInTx(onLoads);
                }
            });
            message = "تعداد" + String.valueOf(onLoads.size()) + "اشتراک بارگیری شد.";
        } catch (Exception e) {
            message = e.toString();
            Log.e("message ", message);
            return message;
        }
        return message;
    }
}
