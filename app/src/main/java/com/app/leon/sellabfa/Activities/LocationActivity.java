package com.app.leon.sellabfa.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.app.leon.sellabfa.Adapters.SpinnerGisAdapter;
import com.app.leon.sellabfa.BaseItem.BaseActivity;
import com.app.leon.sellabfa.Models.DbTables.OnLoad;
import com.app.leon.sellabfa.Models.Enums.BundleEnum;
import com.app.leon.sellabfa.Models.Enums.OffloadStateEnum;
import com.app.leon.sellabfa.Models.Enums.SharedReferenceKeys;
import com.app.leon.sellabfa.Models.Enums.SharedReferenceNames;
import com.app.leon.sellabfa.Models.ViewModels.SpinnerDataModel;
import com.app.leon.sellabfa.Models.ViewModels.UiElementInActivity;
import com.app.leon.sellabfa.R;
import com.app.leon.sellabfa.Utils.GeoTracker;
import com.app.leon.sellabfa.Utils.ICallback;
import com.app.leon.sellabfa.Utils.IGeoTracker;
import com.app.leon.sellabfa.Utils.SharedPreferenceManager;
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.ArcGISRuntimeException;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.layers.ArcGISTiledLayer;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.layers.Layer;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.LayerList;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.DrawStatus;
import com.esri.arcgisruntime.mapping.view.DrawStatusChangedEvent;
import com.esri.arcgisruntime.mapping.view.DrawStatusChangedListener;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.LocationDisplay;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocationActivity extends BaseActivity
        implements ICallback<String> {

    private final double SCALE = 700;
    Context context;
    SharedPreferenceManager sharedPreferenceManager;
    String customId;
    OnLoad onLoad;
    @BindView(R.id.baseMapSpinner)
    Spinner mBaseMapSpinner;
    @BindView(R.id.mapViewLayout)
    MapView mMapView;
    @BindView(R.id.searchView)
    SearchView searchView;
    @BindView(R.id.progressBarMapLoading)
    ProgressBar progressBarMapLoading;
    LinearLayout confirmWrapper, addOrCancel;
    EditText d1, d2, l1, l2, r1, r2;
    Button cancel, ok, addCounter, cancelSelect;
    Point mapPoint = null;
    String eshterak;
    private android.location.Location lastLocation;
    private IGeoTracker geoTracker;
    private LocationDisplay mLocationDisplay;
    private ArcGISMap map;
    //private Basemap openStreetBasemap;
    private Basemap tswBoundaryBasemap;
    private LayerList mOperationalLayers;
    private ArcGISTiledLayer tswBoundaryTiledLayer;
    private Layer streetLayer, parcelLayer;
    private FeatureLayer counterLayer, parcelLayerGolestan;
    private ServiceFeatureTable counterFeatureTable, parcelFeatureTableGolestan;
    private ArcGISFeature counterIdentifiedFeature, parcelIdentifiedFeatureGolestan;
    private boolean isCounterFeatureSelected = false, isGolestanParcelSelected;

    @Override
    protected UiElementInActivity getUiElementsInActivity() {
        UiElementInActivity uiElementInActivity = new UiElementInActivity();
        uiElementInActivity.setContentViewId(R.layout.location_activity);
        ArcGISRuntimeEnvironment.setLicense("runtimelite,1000,rud8277465837,none,8SH93PJPXMH2NERL1236");
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getBundleExtra(BundleEnum.DATA.getValue());
            String jsonBundle = bundle.getString(BundleEnum.ON_OFFLOAD.getValue());
            onLoad = new Gson().fromJson(jsonBundle, OnLoad.class);
            eshterak = onLoad.eshterak;
            customId = onLoad.idCustom;
        }
        GpsEnabled();
        context = this;
        geoTracker = new GeoTracker("LocationActivity", this);
        if (geoTracker.checkPlayServices()) {
            geoTracker.buildGoogleApiClient();
            geoTracker.createLocationRequest();
        }
        geoTracker.displayLocation();
        mLocationDisplay = mMapView.getLocationDisplay();
        if (mLocationDisplay == null) {
            Log.e("loc display", " is null");
        }
        setAutoPan();
        return uiElementInActivity;
    }

    @Override
    protected void initialize() {
        ButterKnife.bind(this);
        initializeMap();
        initializeChangeBaseMapSpinner();
        sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext(), SharedReferenceNames.ACCOUNT.getValue());
    }

    public void execute(String s) {
        onLoad.offLoadStateId = OffloadStateEnum.SENT.getValue();
        onLoad.save();
    }

    private void GpsEnabled() {
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        Log.e("GPS IS:", enabled + "");
        if (!enabled) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setCancelable(false);
            alertDialog.setTitle(getString(R.string.setting_gps));
            alertDialog.setMessage(getString(R.string.gps_question));
            alertDialog.setPositiveButton(getString(R.string.setting_), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            alertDialog.setNegativeButton(getString(R.string.close_app), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    finishAffinity();
                }
            });
            alertDialog.show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.resume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    protected void initializeMap() {
        initializeViewElements();

        tswBoundaryTiledLayer = new ArcGISTiledLayer(getString(R.string.tsw_boundary));
        tswBoundaryBasemap = new Basemap(tswBoundaryTiledLayer);
        map = new ArcGISMap(tswBoundaryBasemap);

        initializeServiceFeatureTable();
        initializeSubLayers();

        mOperationalLayers = map.getOperationalLayers();
        addSubLayers();
        // set the map to be displayed in this view
        mMapView.setMap(map);
        mapDrawingStateChangeListener();
        // enable magnifier
        mMapView.setMagnifierEnabled(true);
        // allow magnifier to pan near the edge of the map bounds
        mMapView.setCanMagnifierPanMap(true);
        mapOnTouchListener();
        onOkClickListener();
        onCancelClickListener();
        //onCancelSelectListener();
    }

    private void initializeViewElements() {
//        mMapView = findViewById(R.id.mapViewLayout);
//        progressBarMapLoading = findViewById(R.id.progressBarMapLoading);
//        searchView = findViewById(R.id.searchView);
//        d1.setFilters(new InputFilter[]{new InputFilterMinMax("0", "30")});
//        d2.setFilters(new InputFilter[]{new InputFilterMinMax("0", "90")});
//        l1.setFilters(new InputFilter[]{new InputFilterMinMax("0", "30")});
//        l2.setFilters(new InputFilter[]{new InputFilterMinMax("0", "90")});
//        r1.setFilters(new InputFilter[]{new InputFilterMinMax("0", "90")});
//        r2.setFilters(new InputFilter[]{new InputFilterMinMax("0", "90")});
        initializeSearchView(eshterak);
    }

    protected void initializeSearchView(CharSequence eshterak) {
        searchView.setQueryHint(getString(R.string.search));
        searchView.setQuery(eshterak, false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                queryAndSelectFeature(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void initializeServiceFeatureTable() {
        counterFeatureTable = new ServiceFeatureTable(getString(R.string.counter_feature_service));
        counterFeatureTable.setFeatureRequestMode(ServiceFeatureTable.FeatureRequestMode.ON_INTERACTION_CACHE);

        //parcelFeatureTableGolestan=new ServiceFeatureTable(getString(R.string.parcel_feature_service_golestan));
        //parcelFeatureTableGolestan.setFeatureRequestMode(ServiceFeatureTable.FeatureRequestMode.ON_INTERACTION_CACHE);
    }

    private void initializeSubLayers() {
        streetLayer = new ArcGISTiledLayer(getString(R.string.street));
        parcelLayer = new ArcGISTiledLayer(getString(R.string.parcel));

        counterLayer = new FeatureLayer(counterFeatureTable);
        counterLayer.setSelectionColor(Color.CYAN);
        counterLayer.setSelectionWidth(6);

       /* parcelLayerGolestan =new FeatureLayer(parcelFeatureTableGolestan);
        parcelLayerGolestan.setSelectionColor(Color.BLUE);
        parcelLayerGolestan.setSelectionWidth(6);*/

    }

    private void addSubLayers() {
        mOperationalLayers.add(streetLayer);
        mOperationalLayers.add(parcelLayer);
        mOperationalLayers.add(counterLayer);
        //mOperationalLayers.add(parcelLayerGolestan);
    }

    private void mapDrawingStateChangeListener() {
        mMapView.addDrawStatusChangedListener(new DrawStatusChangedListener() {
            @Override
            public void drawStatusChanged(DrawStatusChangedEvent drawStatusChangedEvent) {
                if (drawStatusChangedEvent.getDrawStatus() == DrawStatus.IN_PROGRESS) {
                    progressBarMapLoading.setVisibility(View.VISIBLE);
                } else if (drawStatusChangedEvent.getDrawStatus() == DrawStatus.COMPLETED) {
                    progressBarMapLoading.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void mapOnTouchListener() {
        try {
            mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(getApplicationContext(), mMapView) {
                @Override
                public boolean onSingleTapConfirmed(MotionEvent e) {
                  /*  if(!isGolestanParcelSelected) {
                        selectGolestanParcel(e);
                    }
                    else {*/
                    fillPreAddParams(e);
//                    }
                    return super.onSingleTapConfirmed(e);
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    updateFeature(e);
                    super.onLongPress(e);
                }
            });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "بنظر میرسد این فیچر از به روز رسانی پشتیبانی نمیکند", Toast.LENGTH_SHORT).show();
        }

    }

    private void applyEditsToServer() {
        final ListenableFuture<List<FeatureEditResult>> applyEditsFuture =
                ((ServiceFeatureTable) (counterLayer).getFeatureTable()).applyEditsAsync();
        applyEditsFuture.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    // get results of edit
                    List<FeatureEditResult> featureEditResultsList = applyEditsFuture.get();
                    if (!featureEditResultsList.get(0).hasCompletedWithErrors()) {
                        Toast.makeText(getApplicationContext(), "تغییرات مورد نظر شما اعمال شد. ObjectID: " + featureEditResultsList.get(0).getObjectId(), Toast.LENGTH_SHORT).show();
                    }
                } catch (InterruptedException | ExecutionException e) {
                    Log.e(getResources().getString(R.string.app_name), "به روز رسانی ناموفق: " + e.getMessage());
                }
            }
        });
    }

    private void initializeChangeBaseMapSpinner() {
//        mBaseMapSpinner = findViewById(R.id.baseMapSpinner);
        addBaseMapGisSpinner();
    }

    private void addBaseMapGisSpinner() {
        ArrayList<SpinnerDataModel> list = new ArrayList<>();
        list.add(new SpinnerDataModel(getString(R.string.local_gis_basemap), R.drawable.locationdisplaydisabled));
        list.add(new SpinnerDataModel(getString(R.string.osm), R.drawable.locationdisplayon));

        SpinnerGisAdapter adapter = new SpinnerGisAdapter(this, R.layout.gis_pan_mode_spinner_layout, R.id.txt, list);
        mBaseMapSpinner.setAdapter(adapter);
        mBaseMapSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        map.setBasemap(tswBoundaryBasemap);
                        break;
                    case 1:
                        map.setBasemap(Basemap.createOpenStreetMap());
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void updateFeature(MotionEvent e) {
        if (!isCounterFeatureSelected) {
            android.graphics.Point screenCoordinate = new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY()));
            double tolerance = 20;
            //Identify Layers to find features
            final ListenableFuture<IdentifyLayerResult> identifyFuture = mMapView.identifyLayerAsync(counterLayer, screenCoordinate, tolerance, false, 1);
            identifyFuture.addDoneListener(new Runnable() {
                @Override
                public void run() {
                    try {
                        // call get on the future to get the result
                        IdentifyLayerResult layerResult = identifyFuture.get();
                        List<GeoElement> resultGeoElements = layerResult.getElements();

                        //Debug.waitForDebugger();
                        if (resultGeoElements.size() > 0) {
                            if (resultGeoElements.get(0) instanceof ArcGISFeature) {
                                counterIdentifiedFeature = (ArcGISFeature) resultGeoElements.get(0);
                                //Select the identified feature
                                (counterLayer).selectFeature(counterIdentifiedFeature);
                                isCounterFeatureSelected = true;
                                Toast.makeText(getApplicationContext(), "فیچر انتخاب شد  ،برای اعمال تغییرات اطلاعات مکانی روی نقشه تپ کنید", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "هیچ فیچری انتخاب نشده ، برای انتخاب تپ کنید", Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        Log.e(getResources().getString(R.string.app_name), "به روز رسانی ناموفق: " + e.getMessage());
                    }
                }
            });
        } else {
            Point movedPoint = mMapView.screenToLocation(new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY())));
            final Point normalizedPoint = (Point) GeometryEngine.normalizeCentralMeridian(movedPoint);
            counterIdentifiedFeature.addDoneLoadingListener(new Runnable() {
                @Override
                public void run() {
                    //Debug.waitForDebugger();
                    boolean canIEdit = counterIdentifiedFeature.canEditAttachments();
                    boolean canUpdateGeometr = counterIdentifiedFeature.canUpdateGeometry();
                    if (!canUpdateGeometr) {
                        Toast.makeText(getApplicationContext(), "این فیچر قابلیت به روز رسانی ندارد", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        counterIdentifiedFeature.setGeometry(normalizedPoint);
                        final ListenableFuture<Void> updateFuture = counterLayer.getFeatureTable().updateFeatureAsync(counterIdentifiedFeature);
                        updateFuture.addDoneListener(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    // track the update
                                    updateFuture.get();
                                    // apply edits once the update has completed
                                    if (updateFuture.isDone()) {
                                        applyEditsToServer();
                                        counterLayer.clearSelection();
                                        isCounterFeatureSelected = false;
                                    } else {
                                        Log.e(getResources().getString(R.string.app_name), "به روز رسانی ناموفق");
                                    }
                                } catch (InterruptedException | ExecutionException e) {
                                    Log.e(getResources().getString(R.string.app_name), "علت به روز رسانی ناموفق: " + e.getMessage());
                                }
                            }
                        });
                    }
                }
            });
            counterIdentifiedFeature.loadAsync();
        }
    }

    private void selectGolestanParcel(MotionEvent e) {
        if (!isGolestanParcelSelected) {
            android.graphics.Point screenCoordinate = new android.graphics.Point(Math.round(e.getX()), Math.round(e.getY()));
            double tolerance = 20;
            //Identify Layers to find features
            final ListenableFuture<IdentifyLayerResult> identifyFuture = mMapView.identifyLayerAsync(parcelLayerGolestan, screenCoordinate, tolerance, false, 1);
            identifyFuture.addDoneListener(new Runnable() {
                @Override
                public void run() {
                    try {
                        // call get on the future to get the result
                        IdentifyLayerResult layerResult = identifyFuture.get();
                        List<GeoElement> resultGeoElements = layerResult.getElements();

                        //Debug.waitForDebugger();
                        if (resultGeoElements.size() > 0) {
                            if (resultGeoElements.get(0) instanceof ArcGISFeature) {
                                parcelIdentifiedFeatureGolestan = (ArcGISFeature) resultGeoElements.get(0);
                                //Select the identified feature
                                (parcelLayerGolestan).selectFeature(parcelIdentifiedFeatureGolestan);
                                isGolestanParcelSelected = true;
                                addOrCancel.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(getApplicationContext(), "هیچ فیچری انتخاب نشده ، برای انتخاب تپ کنید", Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        Log.e(getResources().getString(R.string.app_name), "انتخاب عارضه ناموفق: " + e.getMessage());
                    }
                }
            });
        }
    }


    private void addCounterFeature(MotionEvent e) {
        if (isCounterFeatureSelected) {
            return;
        }
        android.graphics.Point screenPoint = new android.graphics.Point((int) e.getX(), (int) e.getY());
        // convert this to a map point
        Point mapPoint = mMapView.screenToLocation(screenPoint);

        // check features can be added, based on edit capabilities
        // create the attributes for the feature
        java.util.Map<String, Object> attributes = new HashMap<>();
        attributes.put("Eshterak_Code", "12345"); // Coded Values: [1: Manatee] etc...
        attributes.put("Address", "address"); // Coded Values: [0: No] , [1: Yes]
        attributes.put("Usage_", "1234");
        attributes.put("Customer_Name", "12345678");
        //attributes.put("Status ", "");
        //attributes.put("City_Name ", "چهاردانگه");
        attributes.put("C1", 1);
        attributes.put("C2", 0);
        attributes.put("C3", 2);
        //attributes.put("C4", 1);
        //attributes.put("X", mapPoint.getX());
        //attributes.put("Y", mapPoint.getY());
        attributes.put("ESHTERAK_CODE_NEW", "12345678");

        // Create a new feature from the attributes and an existing point geometry, and then add the feature
        Feature addedFeature = counterFeatureTable.createFeature(attributes, mapPoint);
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

    private void fillPreAddParams(MotionEvent e) {
        if (confirmWrapper.getVisibility() == View.VISIBLE || isCounterFeatureSelected /*|| !isGolestanParcelSelected*/) {
            return;
        }
        //addOrCancel.setVisibility(View.GONE);
        confirmWrapper.setVisibility(View.VISIBLE);
        android.graphics.Point screenPoint = new android.graphics.Point((int) e.getX(), (int) e.getY());
        // convert this to a map point
        mapPoint = mMapView.screenToLocation(screenPoint);
    }

    private Feature makeAddFeature() {
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext(), SharedReferenceNames.ACCOUNT.getValue());
        String userCode = sharedPreferenceManager.getStringData(SharedReferenceKeys.USERNAME.getValue());
        Point wgs84Point = projectToWgs84(mapPoint);
        int _d1, _d2, _l1, _l2, _r1, _r2;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateandTime = sdf.format(new Date());
        _d1 = Integer.parseInt(d1.getText().toString());
        _d2 = Integer.parseInt(d2.getText().toString());
        _l1 = Integer.parseInt(l1.getText().toString());
        _l2 = Integer.parseInt(l2.getText().toString());
        _r1 = Integer.parseInt(r1.getText().toString());
        _r2 = Integer.parseInt(r2.getText().toString());
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
        attributes.put("User_Code", userCode.substring(4, 6));
        attributes.put("Date_Time ", currentDateandTime);
        attributes.put("X", wgs84Point.getX());
        attributes.put("Y", wgs84Point.getY());
        attributes.put("Description", "");

        // Create a new feature from the attributes and an existing point geometry, and then add the feature
        Feature addedFeature = counterFeatureTable.createFeature(attributes, mapPoint);
        return addedFeature;
    }

    private void addCounterFeature() {
        if (isCounterFeatureSelected) {
            return;
        }
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
                } finally {
                    confirmWrapper.setVisibility(View.GONE);
                    clearEditTexts();
                }
            }
        });

    }

    private void queryAndSelectFeature(final CharSequence eshterak) {
        counterLayer.clearSelection();

        // create objects required to do a selection with a query
        QueryParameters query = new QueryParameters();
        //make search case insensitive
        query.setWhereClause("upper(Eshterak_Code) LIKE '%" + eshterak + "%'");//1203102450 for test

        final ListenableFuture<FeatureQueryResult> future = counterFeatureTable.queryFeaturesAsync(query);
        // add done loading listener to fire when the selection returns
        future.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    // call get on the future to get the result
                    FeatureQueryResult result = future.get();

                    // check there are some results
                    if (result.iterator().hasNext()) {
                        // get the extend of the first feature in the result to zoom to with the default scale
                        Feature feature = result.iterator().next();
                        Envelope envelope = feature.getGeometry().getExtent();
                        mMapView.setViewpointGeometryAsync(envelope, 200);
                        Geometry geometry = feature.getGeometry();
                        mMapView.setViewpointGeometryAsync(geometry);
                        mMapView.setViewpointScaleAsync(SCALE);
                        //Select the feature
                        counterLayer.selectFeature(feature);
                        isCounterFeatureSelected = true;
                        counterIdentifiedFeature = (ArcGISFeature) feature;
                    } else {
                        setAutoPan();
                        Toast.makeText(getApplicationContext(), "جستجوی اشتراک: " + eshterak + " میسر نشد", Toast.LENGTH_SHORT);
                        Log.e("gis", "eshterak not found");
                    }
                } catch (Exception e) {
                    setAutoPan();
                    Log.e(getResources().getString(R.string.app_name), "جستجوی اشتراک: " + eshterak + " میسر نشد" + e.getCause());
                }
            }
        });
    }

    private void setAutoPan() {
        try {
            mLocationDisplay.setAutoPanMode(LocationDisplay.AutoPanMode.RECENTER);
            if (!mLocationDisplay.isStarted()) {
                mLocationDisplay.startAsync();
            }
        } catch (Exception e) {
            Log.e(Tag.CREATOR.toString(), e.getMessage());
        }
    }

    //
    private void onOkClickListener() {
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (d1.getText().toString().length() > 0 && d2.getText().toString().length() > 0 &&
                        l1.getText().toString().length() > 0 && l2.getText().toString().length() > 0) {
                    addCounterFeature();
                } else {
                    Toast.makeText(getApplicationContext(), "لطفا فاصله ها را با دقت بیشتری وارد فرمایید", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void onCancelClickListener() {
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mapPoint = null;
                isCounterFeatureSelected = false;
                confirmWrapper.setVisibility(View.GONE);
                clearEditTexts();
            }
        });
    }

    private void onCancelSelectListener() {
        cancelSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                parcelLayerGolestan.clearSelection();
                parcelIdentifiedFeatureGolestan = null;
                isGolestanParcelSelected = false;
                addOrCancel.setVisibility(View.GONE);
            }
        });
    }

    private void clearEditTexts() {
        d1.setText("");
        d2.setText("");
        l1.setText("");
        l2.setText("");
        r1.setText("");
        r2.setText("");
    }

    private Point projectToWgs84(Point point) {
        Point wgs84Point = (Point) GeometryEngine.project(point, SpatialReferences.getWgs84());
        return wgs84Point;
    }
}
