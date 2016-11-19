package com.coding4fun.gpa;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by coding4fun on 09-Oct-16.
 */

public class Map extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    LatLng currentLocation;
    FloatingActionButton FAB_addMarker;
    SharedPreferences prefs;
    SharedPreferences.Editor e;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initPrefs();
        View v = inflater.inflate(R.layout.map, container, false);
        GoogleApiAvailability gAPI = GoogleApiAvailability.getInstance();
        int isAvailable = gAPI.isGooglePlayServicesAvailable(getContext());
        if (isAvailable == ConnectionResult.SUCCESS) { //everything is OK
            initMap(v);
        } else if (gAPI.isUserResolvableError(isAvailable)) { //there is an error, but the user can do something about it
            initResolvableError(v, gAPI, isAvailable);
        } else { //there is an error, and the user can't do something about it...
            initError(v);
        }
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //code here
        //Log.e("MAP","ViewCreated");
    }

    private void initPrefs() {
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        e = prefs.edit();
    }

    private void initResolvableError(View v, final GoogleApiAvailability gAPI, final int isAvailable) {
        (v.findViewById(R.id.map_resolvableErrorLayout)).setVisibility(View.VISIBLE);
        ((Button) v.findViewById(R.id.map_resolvableErrorBTN)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gAPI.showErrorDialogFragment(getActivity(), isAvailable, 99);
            }
        });
    }

    private void initError(View v) {
        (v.findViewById(R.id.map_ErrorLayout)).setVisibility(View.VISIBLE);
    }

    private void initMap(View v) {
        (v.findViewById(R.id.map_fragment)).setVisibility(View.VISIBLE);
        FAB_addMarker = (FloatingActionButton) v.findViewById(R.id.fab_addMarker);
        SupportMapFragment mf = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        mf.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.setMyLocationEnabled(true);
        setMapListeners();
        //getUniMarkerIfExistsThenSetBounds();
        mGoogleApiClient = new GoogleApiClient.Builder(getContext()).addApi(LocationServices.API)
                .addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(getContext(), "Connected", Toast.LENGTH_SHORT).show();
        getCurrentLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getContext(), "Connection suspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getContext(), "Connection failed!", Toast.LENGTH_SHORT).show();
    }

    void getUniMarkerIfExistsThenSetBounds(){
        if(prefs.getBoolean("uniMarker",false)){
            double lat = (double) prefs.getFloat("uniLat",0f);
            double lng = (double) prefs.getFloat("uniLng",0f);
            LatLng uni = new LatLng(lat,lng);
            addMarkerOnUni(uni);
            LatLngBounds b = new LatLngBounds(uni,currentLocation);
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(b,99));
        }
    }

    void goToLocation(double lat, double lng, float zoom, boolean animate){
        LatLng ll = new LatLng(lat,lng);
        CameraUpdate c = null;
        if(zoom < 0) c = CameraUpdateFactory.newLatLng(ll);
        else c = CameraUpdateFactory.newLatLngZoom(ll,zoom);
        if(animate) mMap.animateCamera(c);
        else mMap.moveCamera(c);
    }

    void goToLocation(LatLng ll, float zoom, boolean animate){
        goToLocation(ll.latitude,ll.longitude,zoom,animate);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 66:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.e("GPA","requesting location permission : DONE :)");
                    getCurrentLocation();
                } else {
                    Toast.makeText(getContext(), "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    void getCurrentLocation(){
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Log.e("GPA","requesting location permission");
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},66);
            return;
        }
        Log.e("GPA","getting location");
        Location current = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(current != null){
            Log.e("GPA","current location found");
            Toast.makeText(getContext(), "current location found", Toast.LENGTH_SHORT).show();
            currentLocation = new LatLng(current.getLatitude(),current.getLongitude());
            //goToLocation(currentLocation,15,true);
            getUniMarkerIfExistsThenSetBounds();
        }
    }

    private void addMarkerOnUni(LatLng ll) {
        MarkerOptions ma = new MarkerOptions().position(ll).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        mMap.addMarker(ma);
        e.putBoolean("uniMarker",true);
        e.putFloat("uniLat",(float)ll.latitude);
        e.putFloat("uniLng",(float)ll.longitude);
        e.commit();
    }

    private void setMapListeners() {
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng ll) {
                zoomFABin(ll);
            }
        });
        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
            @Override
            public void onCameraMoveStarted(int i) {
                if(FAB_addMarker.getVisibility() == View.VISIBLE) zoomFABout();
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(FAB_addMarker.getVisibility() == View.VISIBLE) zoomFABout();
            }
        });
    }

    private void zoomFABin (final LatLng ll) {
        FAB_addMarker.setVisibility(View.VISIBLE);
        Animation aa = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_in_fab);
        FAB_addMarker.startAnimation(aa);
        FAB_addMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMarkerOnUni(ll);
            }
        });
    }

    private void zoomFABout () {
        Animation a = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_out_fab);
        FAB_addMarker.startAnimation(a);
        FAB_addMarker.setVisibility(View.INVISIBLE);
    }


}