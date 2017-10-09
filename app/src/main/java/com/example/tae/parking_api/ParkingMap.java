package com.example.tae.parking_api;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.tae.parking_api.allMarkers.allMarkersPresenter;
import com.example.tae.parking_api.allMarkers.iAllMarkersMvpView;
import com.example.tae.parking_api.model.Parking_Model;
import com.example.tae.parking_api.services.AppDataManger;
import com.example.tae.parking_api.utils.rx.AppSchedulerProvider;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;


public class ParkingMap extends AppCompatActivity implements OnMapReadyCallback,iAllMarkersMvpView {

    private GoogleMap mMap;
    allMarkersPresenter<iAllMarkersMvpView> allMarkersMvpViewallMarkersPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parking_map);
        allMarkersMvpViewallMarkersPresenter = new allMarkersPresenter<>
                (new AppDataManger()
                        , new AppSchedulerProvider()
                        , new CompositeDisposable());
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        allMarkersMvpViewallMarkersPresenter.onAttach(this);
        allMarkersMvpViewallMarkersPresenter.onViewPrepared();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
    }

    @Override
    public void onFetchDataCompleted(List<Parking_Model> parking_model) {
        Log.i("on fetch called", "Markers loaded");
        Parking_Model p = new Parking_Model();
        mMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter(parking_model));
        for (int i = 0; i < parking_model.size(); i++) {

            p = parking_model.get(i);
            String lat = p.getLat();
            String lon = p.getLng();
            double dLat = Double.parseDouble(lat);
            double dLong = Double.parseDouble(lon);
            LatLng location = new LatLng(dLat, dLong);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14));
            if (p.getIsReserved()) {
                mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(String.valueOf(i))
                        .snippet(p.getCostPerMinute())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
            } else {
                mMap.addMarker(new MarkerOptions()
                        .position(location)
                        .title(String.valueOf(i))
                        .snippet(p.getCostPerMinute())
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

            }
            Log.i("TESSSSSSTING NAME ", p.getName());

        }
    }

    @Override
    public void showLoading() {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void onError(String message) {
        Log.i("error", message);
    }

    @Override
    public void showMessage(String message) {

    }

    @Override
    public boolean isNetworkConnected() {
        return false;
    }

    public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
        private final View view;
        private final List<Parking_Model> parkingModel;
        public MarkerInfoWindowAdapter(List<Parking_Model> parkingModel) {
            view = LayoutInflater.from(MyApplication.getContext()).inflate(R.layout.custom_layout,null);
             this.parkingModel=parkingModel;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            TextView tvTitle = (TextView)view.findViewById(R.id.title_tv);
            tvTitle.setText(parkingModel.get(Integer.parseInt(marker.getTitle())).getName());
            TextView tvPrice= (TextView)view.findViewById(R.id.price_tv);
            tvPrice.setText(parkingModel.get(Integer.parseInt(marker.getTitle())).getCostPerMinute()+ " per min");
            TextView tvReserved = (TextView)view.findViewById(R.id.reserved_tv);
            tvReserved.setText(parkingModel.get(Integer.parseInt(marker.getTitle())).getIsReserved().toString());
            Log.i("testing snippet layout", parkingModel.get(Integer.parseInt(marker.getTitle())).getIsReserved().toString());
            return view;
        }
        }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if(id == R.id.action_refresh){
            mMap.clear();
            allMarkersMvpViewallMarkersPresenter.onViewPreparedLocal();
        }

        return super.onOptionsItemSelected(item);
    }
    }
