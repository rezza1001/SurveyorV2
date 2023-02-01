package com.wadaro.surveyor.ui.assignment.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.wadaro.surveyor.R;

import java.util.ArrayList;
import java.util.Objects;


public class MapFragment extends Fragment implements GoogleMap.OnMarkerClickListener { // implements OnMapReadyCallback {

    private MapView mMapView;
    private GoogleMap mGoogleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_map, container, false);

        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(getActivity(), "Membutuhkan Izin Lokasi", Toast.LENGTH_SHORT).show();
            } else {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);
            }
        } else {
            setUpMapIfNeeded(v);
        }
        return v;
    }

    private void setUpMapIfNeeded(View v) {

        if (mGoogleMap == null) {
            mMapView = v.findViewById(R.id.mvMap);
            mMapView.onCreate(getArguments());
            mMapView.onResume();

            try {
                MapsInitializer.initialize(getActivity());
                mMapView.getMapAsync(gmap -> {
                    mGoogleMap = gmap;
                    mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                    //fill markers
                    //add marker listener

                    // latitude and longitude
                    double latitude = -6.284310;
                    double longitude = 106.727430;

                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(latitude, longitude)).zoom(10).build();
                    mGoogleMap.animateCamera(CameraUpdateFactory
                            .newCameraPosition(cameraPosition));

                });

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    private void makersMaker(double longitude, double latitude, String name){
        if (mGoogleMap == null){
            return;
        }
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_marker_blue);
        if (name.equals("Demo 2")){
            bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_marker_orange);
        }
        else if (name.equals("Demo 3")){
            bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_marker_green);
        }
        else if (name.equals("Demo 4")){
            bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_marker_grey);
        }
        Marker marker1 = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)));
        marker1.setIcon(BitmapDescriptorFactory.fromBitmap(bm));


    }

    @Override
    public void onStart() {
        super.onStart();
        Objects.requireNonNull(getActivity()).registerReceiver(receiver,new IntentFilter("LOAD_DATA"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Objects.requireNonNull(getActivity()).unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (mGoogleMap == null){
                return;
            }
            mGoogleMap.clear();
            ArrayList<String> data = intent.getStringArrayListExtra("data");
            assert data != null;
            for (String sData : data){
                double longitude = Double.parseDouble(sData.split("::")[2]);
                double latitude = Double.parseDouble(sData.split("::")[1]);
                String demo = sData.split("::")[0];
                makersMaker(longitude, latitude, "Demo "+ demo);
            }
        }
    };


}
