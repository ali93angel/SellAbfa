package com.app.leon.sellabfa.Activities;

import android.content.Context;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.app.leon.sellabfa.Adapters.SpinnerGisAdapter;
import com.app.leon.sellabfa.Fragment.AddPointFragment;
import com.app.leon.sellabfa.Models.DbTables.OnLoad;
import com.app.leon.sellabfa.Models.Enums.BundleEnum;
import com.app.leon.sellabfa.Models.ViewModels.SpinnerDataModel;
import com.app.leon.sellabfa.R;
import com.app.leon.sellabfa.Utils.IGeoTracker;
import com.app.leon.sellabfa.Utils.SharedPreferenceManager;
import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LocationActivity_1 extends AppCompatActivity {
    private final double SCALE = 700;
    Context context;
    SharedPreferenceManager sharedPreferenceManager;
    String customId;
    OnLoad onLoad;
    @BindView(R.id.searchView)
    SearchView searchView;
    @BindView(R.id.baseMapSpinner)
    Spinner mBaseMapSpinner;
    @BindView(R.id.mapViewLayout)
    MapView mMapView;
    @BindView(R.id.progressBarMapLoading)
    ProgressBar progressBarMapLoading;
    @BindView(R.id.buttonNext)
    Button buttonNext;
    @BindView(R.id.buttonPrevious)
    Button buttonPrevious;
    Point mapPoint = null;
    String eshterak;
    ArrayList<OnLoad> onLoads;
    int currentPage;
    private android.location.Location lastLocation;
    private IGeoTracker geoTracker;
    private LocationDisplay mLocationDisplay;
    private ArcGISMap map;
    //private Basemap openStreetBasemap;
    private Basemap tswBoundaryBasemap;
    private LayerList mOperationalLayers;
    private ArcGISTiledLayer tswBoundaryTiledLayer;
    private Layer streetLayer, parcelLayer;
    private FeatureLayer counterLayer;
    private ServiceFeatureTable counterFeatureTable, parcelFeatureTableGolestan;
    private ArcGISFeature counterIdentifiedFeature, parcelIdentifiedFeatureGolestan;
    private boolean isCounterFeatureSelected = false, isGolestanParcelSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_content);
        ArcGISRuntimeEnvironment.setLicense("runtimelite,1000,rud8277465837,none,8SH93PJPXMH2NERL1236");
        context = this;
        if (getIntent().getExtras() != null) {
            onLoads = ReadActivity.getInstance().getOnLoads();
            currentPage = getIntent().getIntExtra(BundleEnum.CURRENT_PAGE.getValue(), 0);
        }
        ButterKnife.bind(this);
        initialize();
    }

    void initialize() {
        initializeSearchView(eshterak);
        initializeMap();
        addBaseMapGisSpinner();
        buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPage + 1 < onLoads.size())
                    currentPage++;
                searchView.setQuery(onLoads.get(currentPage).eshterak, true);
            }
        });
        buttonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentPage > 0)
                    currentPage--;
                searchView.setQuery(onLoads.get(currentPage).eshterak, true);
            }
        });
    }

    protected void initializeSearchView(CharSequence eshterak) {
        searchView.setQueryHint(getString(R.string.search));
        searchView.setQuery(eshterak, true);
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

    protected void initializeMap() {
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
        //onCancelSelectListener();
    }

    private void initializeServiceFeatureTable() {
        counterFeatureTable = new ServiceFeatureTable(getString(R.string.counter_feature_service));
        counterFeatureTable.setFeatureRequestMode(ServiceFeatureTable.FeatureRequestMode.ON_INTERACTION_CACHE);
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
                    boolean canUpdateGeometer = counterIdentifiedFeature.canUpdateGeometry();
                    if (!canUpdateGeometer) {
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

    private void fillPreAddParams(MotionEvent e) {
        if (isCounterFeatureSelected)
            return;
        android.graphics.Point screenPoint = new android.graphics.Point((int) e.getX(), (int) e.getY());
        // convert this to a map point
        mapPoint = mMapView.screenToLocation(screenPoint);
        FragmentManager fragmentManager = getSupportFragmentManager();
        AddPointFragment addPointFragment = AddPointFragment.newInstance(onLoads.get(currentPage).eshterak);
        addPointFragment.show(fragmentManager, "نقطه");
    }

    public Point getMapPoint() {
        if (mapPoint == null)
            Log.e("status", "point is empty");
        return mapPoint;
    }

    public ServiceFeatureTable getCounterFeatureTable() {
        return counterFeatureTable;
    }
}
