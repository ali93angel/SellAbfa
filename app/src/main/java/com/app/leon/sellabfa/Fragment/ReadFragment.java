package com.app.leon.sellabfa.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.app.leon.sellabfa.BaseItem.BaseFragment;
import com.app.leon.sellabfa.Models.DbTables.OnLoad;
import com.app.leon.sellabfa.Models.Enums.BundleEnum;
import com.app.leon.sellabfa.R;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ReadFragment extends BaseFragment {
    @BindView(R.id.textViewAbBaha)
    TextView textViewAbBaha;
    @BindView(R.id.textViewMasraf)
    TextView textViewMasraf;
    @BindView(R.id.textViewFarei)
    TextView textViewFarei;
    @BindView(R.id.textViewAsli)
    TextView textViewAsli;
    @BindView(R.id.textViewAddress)
    TextView textViewAddress;
    @BindView(R.id.textViewName)
    TextView textViewName;
    @BindView(R.id.textViewRadif)
    TextView textViewRadif;
    @BindView(R.id.textViewEshterakOrQeraat)
    TextView textViewEshterakOrQeraat;
    @BindView(R.id.textViewKarbari)
    TextView textViewKarbari;
    @BindView(R.id.textViewCounterSerial)
    TextView textViewCounterSerial;
    @BindView(R.id.textViewForoush)
    TextView textViewForoush;
    int select;
    Unbinder unbinder;
    private OnLoad onLoad;
    private Context context;
    private View view, viewFocus;

    public ReadFragment() {

    }

    public static ReadFragment newInstance(OnLoad onLoad, int select) {
        ReadFragment fragment = new ReadFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BundleEnum.ON_OFFLOAD.getValue(), new Gson().toJson(onLoad));
        bundle.putInt(BundleEnum.ESHTERAK_OR_QERAAT.getValue(), select);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.read_fragment, parent, false);
        unbinder = ButterKnife.bind(this, view);
        if (getArguments() != null) {
            String jsonBundle = getArguments().getString(BundleEnum.ON_OFFLOAD.getValue());
            select = getArguments().getInt(BundleEnum.ESHTERAK_OR_QERAAT.getValue());
            onLoad = new Gson().fromJson(jsonBundle, OnLoad.class);
        }
        return view;
    }

    @Override
    public void initialize() {
        initializeTextView();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        view = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context = null;
    }

    private void initializeTextView() {
        if (select == 0) {
            if (onLoad.eshterak != null)
                textViewEshterakOrQeraat.setText(onLoad.eshterak);
        } else {
            if (onLoad.qeraatCode != null)
                textViewEshterakOrQeraat.setText(onLoad.qeraatCode);
        }
        if (onLoad.address != null)
            textViewAddress.setText(onLoad.address);
        if (onLoad.firstName != null && onLoad.sureName != null)
            textViewName.setText(onLoad.firstName + " " + onLoad.sureName);
        if (onLoad.radif != null)
            textViewRadif.setText(String.valueOf(onLoad.radif));
        if (onLoad.counterSerial != null)
            textViewCounterSerial.setText(onLoad.counterSerial);
        if (onLoad.karbariForoosh != null)
            textViewForoush.setText(onLoad.karbariForoosh);
        if (onLoad.karbariMasraf != null)
            textViewMasraf.setText(onLoad.karbariMasraf);
        textViewAsli.setText(String.valueOf(onLoad.ahadAsli));
        textViewFarei.setText(String.valueOf(onLoad.ahadFari));
        textViewMasraf.setText(String.valueOf(onLoad.ahadMasraf));
        textViewAbBaha.setText(String.valueOf(onLoad.ahadAbBaha));
    }
}
