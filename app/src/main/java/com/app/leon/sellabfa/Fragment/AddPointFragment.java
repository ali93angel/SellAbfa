package com.app.leon.sellabfa.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;

import com.app.leon.sellabfa.Activities.LocationActivity_1;
import com.app.leon.sellabfa.Models.Enums.SharedReferenceKeys;
import com.app.leon.sellabfa.Models.Enums.SharedReferenceNames;
import com.app.leon.sellabfa.R;
import com.app.leon.sellabfa.Utils.SharedPreferenceManager;
import com.esri.arcgisruntime.ArcGISRuntimeException;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AddPointFragment extends DialogFragment {
    private static final String ARG_PARAM1 = "param1";
    Unbinder unbinder;
    Context context;
    View view;
    @BindView(R.id.buttonSubmit)
    Button buttonSubmit;
    @BindView(R.id.buttonCancel)
    Button buttonCancel;
    @BindView(R.id.fragmentFrameLayout)
    FrameLayout frameLayout;
    @BindView(R.id.editText1)
    EditText editText1;
    @BindView(R.id.editText2)
    EditText editText2;
    @BindView(R.id.editText3)
    EditText editText3;
    @BindView(R.id.editText4)
    EditText editText4;
    @BindView(R.id.editText5)
    EditText editText5;
    @BindView(R.id.editText6)
    EditText editText6;
    @BindView(R.id.linearLayout1)
    LinearLayout linearLayout1;
    @BindView(R.id.linearLayout2)
    LinearLayout linearLayout2;
    @BindView(R.id.linearLayout3)
    LinearLayout linearLayout3;
    @BindView(R.id.linearLayout4)
    LinearLayout linearLayout4;
    @BindView(R.id.linearLayout5)
    LinearLayout linearLayout5;
    @BindView(R.id.linearLayout6)
    LinearLayout linearLayout6;
    // TODO: Rename and change types of parameters
    private String eshterak;
    private Point mapPoint;
    private ServiceFeatureTable counterFeatureTable;

    public AddPointFragment() {
        // Required empty public constructor
    }

    public static AddPointFragment newInstance(String eshterak) {
        AddPointFragment fragment = new AddPointFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, eshterak);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            eshterak = getArguments().getString(ARG_PARAM1);
            mapPoint = ((LocationActivity_1) (getActivity())).getMapPoint();
            counterFeatureTable = ((LocationActivity_1) (getActivity())).getCounterFeatureTable();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        view = inflater.inflate(R.layout.add_point_fragment, container, false);
        unbinder = ButterKnife.bind(this, view);
        initialize();
        return view;
    }

    void initialize() {
        setOnButtonSubmitClickListener();
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        setEditText1OnFocusChangeListener();
        setEditText2OnFocusChangeListener();
        setEditText3OnFocusChangeListener();
        setEditText4OnFocusChangeListener();
        setEditText5OnFocusChangeListener();
        setEditText6OnFocusChangeListener();
    }

    private void addCounterFeature() {
        Feature addedFeature = makeAddFeature();
        if (addedFeature == null) {
            return;
        }
        //Debug.waitForDebugger();
        final ListenableFuture<Void> addFeatureFuture = counterFeatureTable.addFeatureAsync(addedFeature);
        addFeatureFuture.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    // check the result of the future to find out if/when the addFeatureAsync call succeeded - exception will be
                    // thrown if the edit failed
                    addFeatureFuture.get();

                    // if using an ArcGISFeatureTable, call getAddedFeaturesCountAsync to check the total number of features
                    // that have been added since last sync

                    // if dealing with ServiceFeatureTable, apply edits after making updates; if editing locally, then edits can
                    // be synchronized at some point using the SyncGeodatabaseTask.
                    if (counterFeatureTable instanceof ServiceFeatureTable) {
                        ServiceFeatureTable serviceFeatureTable = counterFeatureTable;
                        // apply the edits
                        final ListenableFuture<List<FeatureEditResult>> applyEditsFuture = serviceFeatureTable.applyEditsAsync();
                        applyEditsFuture.addDoneListener(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    final List<FeatureEditResult> featureEditResults = applyEditsFuture.get();
                                    // if required, can check the edits applied in this operation
                                    Log.e("arc success", String.format("Number of edits: %d", featureEditResults.size()));
                                } catch (InterruptedException | ExecutionException e) {
                                    //Debug.waitForDebugger();
                                    Log.e("error", e.getMessage());
                                }
                            }
                        });
                    }

                } catch (InterruptedException | ExecutionException e) {
                    // executionException may contain an ArcGISRuntimeException with edit error information.
                    if (e.getCause() instanceof ArcGISRuntimeException) {
                        //Debug.waitForDebugger();
                        ArcGISRuntimeException agsEx = (ArcGISRuntimeException) e.getCause();
                        Log.e("error", String.format("Add Feature Error %d\n=%s", agsEx.getErrorCode(), agsEx.getMessage()));
                        Log.e("add feature additional", ((ArcGISRuntimeException) e.getCause()).getAdditionalMessage());
                    } else {
                        //Debug.waitForDebugger();
                        Log.e("error", e.getMessage());
                    }
                }
            }
        });
    }

    private Feature makeAddFeature() {
        Point wgs84Point = projectToWgs84(mapPoint);
        int _d1, _d2, _l1, _l2, _r1, _r2;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateAndTime = sdf.format(new Date());
        _d1 = Integer.parseInt(editText2.getText().toString());
        _d2 = Integer.parseInt(editText1.getText().toString());
        _l1 = Integer.parseInt(editText4.getText().toString());
        _l2 = Integer.parseInt(editText3.getText().toString());
        _r1 = Integer.parseInt(editText6.getText().toString());
        _r2 = Integer.parseInt(editText5.getText().toString());
        // check features can be added, based on edit capabilities
        // create the attributes for the feature
        java.util.Map<String, Object> attributes = new HashMap<>();
        attributes.put("Eshterak_Code", eshterak);
        attributes.put("ESHTERAK_CODE_NEW", eshterak);
        attributes.put("City_Name ", "");
        attributes.put("C1", _d1);
        attributes.put("C2", _d2);
        attributes.put("C3", _l1);
        attributes.put("C4", _l2);
        attributes.put("C5", _r1);
        attributes.put("C6", _r2);
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(getActivity(),
                SharedReferenceNames.ACCOUNT.getValue());
        String userCode = sharedPreferenceManager.getStringData(SharedReferenceKeys.USERNAME.getValue());
        Log.d("user ", userCode.substring(4, 6));
        attributes.put("User_Code", Integer.valueOf(userCode.substring(4, 6)));
        attributes.put("Date_Time ", currentDateAndTime);
        attributes.put("X", wgs84Point.getX());
        attributes.put("Y", wgs84Point.getY());
        attributes.put("Description", "");

        // Create a new feature from the attributes and an existing point geometry, and then add the feature
        Feature addedFeature = counterFeatureTable.createFeature(attributes, mapPoint);
        return addedFeature;
    }

    private Point projectToWgs84(Point point) {
        Point wgs84Point = (Point) GeometryEngine.project(point, SpatialReferences.getWgs84());
        return wgs84Point;
    }

    void setEditText1OnFocusChangeListener() {
        editText1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    linearLayout1.setBackground(getResources().getDrawable(R.drawable.border_orange_white));
                    editText1.setBackground(getResources().getDrawable(R.drawable.border_white_2));
                    editText1.setTextColor(getResources().getColor(R.color.orange2));
                } else {
                    linearLayout1.setBackground(getResources().getDrawable(R.drawable.border_gray_2));
                    editText1.setBackground(getResources().getDrawable(R.drawable.border_gray_3));
                    editText1.setTextColor(getResources().getColor(R.color.gray2));
                }
            }
        });
    }

    void setEditText2OnFocusChangeListener() {
        editText2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    linearLayout2.setBackground(getResources().getDrawable(R.drawable.border_orange_white));
                    editText2.setBackground(getResources().getDrawable(R.drawable.border_white_2));
                    editText2.setTextColor(getResources().getColor(R.color.orange2));
                } else {
                    linearLayout2.setBackground(getResources().getDrawable(R.drawable.border_gray_2));
                    editText2.setBackground(getResources().getDrawable(R.drawable.border_gray_3));
                    editText2.setTextColor(getResources().getColor(R.color.gray2));
                }
            }
        });
    }

    void setEditText3OnFocusChangeListener() {
        editText3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    linearLayout3.setBackground(getResources().getDrawable(R.drawable.border_orange_white));
                    editText3.setBackground(getResources().getDrawable(R.drawable.border_white_2));
                    editText3.setTextColor(getResources().getColor(R.color.orange2));
                } else {
                    linearLayout3.setBackground(getResources().getDrawable(R.drawable.border_gray_2));
                    editText3.setBackground(getResources().getDrawable(R.drawable.border_gray_3));
                    editText3.setTextColor(getResources().getColor(R.color.gray2));
                }
            }
        });
    }

    void setEditText4OnFocusChangeListener() {
        editText4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    linearLayout4.setBackground(getResources().getDrawable(R.drawable.border_orange_white));
                    editText4.setBackground(getResources().getDrawable(R.drawable.border_white_2));
                    editText4.setTextColor(getResources().getColor(R.color.orange2));
                } else {
                    linearLayout4.setBackground(getResources().getDrawable(R.drawable.border_gray_2));
                    editText4.setBackground(getResources().getDrawable(R.drawable.border_gray_3));
                    editText4.setTextColor(getResources().getColor(R.color.gray2));
                }
            }
        });
    }

    void setEditText5OnFocusChangeListener() {
        editText5.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    linearLayout5.setBackground(getResources().getDrawable(R.drawable.border_orange_white));
                    editText5.setBackground(getResources().getDrawable(R.drawable.border_white_2));
                    editText5.setTextColor(getResources().getColor(R.color.orange2));
                } else {
                    linearLayout5.setBackground(getResources().getDrawable(R.drawable.border_gray_2));
                    editText5.setBackground(getResources().getDrawable(R.drawable.border_gray_3));
                    editText5.setTextColor(getResources().getColor(R.color.gray2));
                }
            }
        });
    }

    void setEditText6OnFocusChangeListener() {
        editText6.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    linearLayout6.setBackground(getResources().getDrawable(R.drawable.border_orange_white));
                    editText6.setBackground(getResources().getDrawable(R.drawable.border_white_2));
                    editText6.setTextColor(getResources().getColor(R.color.orange2));
                } else {
                    linearLayout6.setBackground(getResources().getDrawable(R.drawable.border_gray_2));
                    editText6.setBackground(getResources().getDrawable(R.drawable.border_gray_3));
                    editText6.setTextColor(getResources().getColor(R.color.gray2));
                }
            }
        });
    }

    void setOnButtonSubmitClickListener() {
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText1.getText().toString().length() > 0 && editText2.getText().toString().length() > 0 &&
                        editText3.getText().toString().length() > 0 && editText4.getText().toString().length() > 0 &&
                        editText5.getText().toString().length() > 0 && editText6.getText().toString().length() > 0
                ) {
                    addCounterFeature();
                    dismiss();
                } else {
                    Toast.makeText(getContext(), "لطفا فاصله ها را با دقت بیشتری وارد فرمایید", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        super.onResume();
    }
}
