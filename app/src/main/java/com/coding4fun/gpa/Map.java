package com.coding4fun.gpa;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by coding4fun on 09-Oct-16.
 */

public class Map extends Fragment implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMyLocationButtonClickListener, LocationListener {

    private final int REQUEST_CHECK_SETTINGS = 88;
    private final int REQUEST_CHECK_PERMISSION = 66;
    private final int REQUEST_CHECK_FINE_PERMISSION_TO_ENABLE_MY_LOCATION_BUTTON = 64;
    private final int REQUEST_CHECK_FINE_PERMISSION_TO_GET_LOCATION_UPDATES = 62;
    private final int REQUEST_GOOGLE_PLAY_SERVICES = 44;
    GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    LatLng currentLocation;
    Marker uniMarker;
    FloatingActionButton FAB_addMarker;
    View v, errorLayout, resolvableErrorLayout, map;
    TextView errorTV;
    SharedPreferences prefs;
    SharedPreferences.Editor e;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.map, container, false);
        errorLayout = v.findViewById(R.id.map_ErrorLayout);
        resolvableErrorLayout = v.findViewById(R.id.map_resolvableErrorLayout);
        map = v.findViewById(R.id.map_fragment);
        errorTV = (TextView) v.findViewById(R.id.map_ErrorTV);
        setHasOptionsMenu(true);    //assign menu only for this fragment
        initPrefs();
        GoogleApiAvailability gAPI = GoogleApiAvailability.getInstance();
        int isAvailable = gAPI.isGooglePlayServicesAvailable(getContext());
        if (isAvailable == ConnectionResult.SUCCESS) { //everything is OK
            //initMap(); // TODO: checkLocationSettings();
            Log.e("GPA", "GooglePlayServices are Available");
            initGoogleApiClient();
        } else if (gAPI.isUserResolvableError(isAvailable)) { //there is an error, but the user can do something about it
            Log.e("GPA", "GooglePlayServices are not available, but can be fixed");
            initResolvableError(v, gAPI, isAvailable);
        } else { //there is an error, and the user can't do something about it...
            Log.e("GPA", "GooglePlayServices are not available!");
            initError("Google Play Services are NOT available!");
        }
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //code here
        //Log.e("MAP","ViewCreated");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.map_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.normal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.satellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.hybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.terrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            case R.id.map_menu_scale:
                scale2();
                return true;
            case R.id.map_menu_Where_am_I:
                showCurrentLocation();
                //onMyLocationButtonClick();
                return true;
            case R.id.map_menu_night_mode:
                if (item.isChecked()) {
                    item.setChecked(false);
                    mMap.setMapStyle(null); // set default theme
                } else {
                    item.setChecked(true);
                    mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(),R.raw.night_mode_map)); //set night mode style
                }
                return true;
            default:
                return false;
        }
    }

    private void initPrefs() {
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        e = prefs.edit();
    }

    private void initResolvableError(View v, final GoogleApiAvailability gAPI, final int isAvailable) {
        if (map.getVisibility() == View.VISIBLE) map.setVisibility(View.GONE);
        if (resolvableErrorLayout.getVisibility() == View.VISIBLE)
            resolvableErrorLayout.setVisibility(View.GONE);
        resolvableErrorLayout.setVisibility(View.VISIBLE);
        ((Button) v.findViewById(R.id.map_resolvableErrorBTN)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gAPI.showErrorDialogFragment(getActivity(), isAvailable, REQUEST_GOOGLE_PLAY_SERVICES); //TODO: handle this (requestCode && onResult)
            }
        });
    }

    private void initError(String errorMSG) {
        if (resolvableErrorLayout.getVisibility() == View.VISIBLE)
            resolvableErrorLayout.setVisibility(View.GONE);
        if (map.getVisibility() == View.VISIBLE) map.setVisibility(View.GONE);
        errorLayout.setVisibility(View.VISIBLE);
        errorTV.setText("Opsss! Google Maps can't be shown!\n" + errorMSG);
    }

    private void initMap() {
        Log.e("GPA", "initializing map");
        if (resolvableErrorLayout.getVisibility() == View.VISIBLE)
            resolvableErrorLayout.setVisibility(View.GONE);
        if (errorLayout.getVisibility() == View.VISIBLE) errorLayout.setVisibility(View.GONE);
        map.setVisibility(View.VISIBLE);
        FAB_addMarker = (FloatingActionButton) v.findViewById(R.id.fab_addMarker);
        SupportMapFragment mf = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        mf.getMapAsync(this);
    }

    private void initGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext()).addApi(LocationServices.API)
                .addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();
    }

    void checkLocationSettings() {
        Log.e("GPA", "checking location settings");
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                //final LocationSettingsStates codes = locationSettingsResult.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.e("GPA", "location is ON");
                        initMap();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            Log.e("GPA", "location is OFF. Trying to turn on...");
                            status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e1) {
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.e("GPA", "location is OFF (Unavailable)");
                        initError("Location Settings issue!");
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //final LocationSettingsStates status = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {

            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.e("GPA", "location settings was off, but now it is on");
                        initMap();
                        break;
                    case Activity.RESULT_CANCELED:
                        initError("Location Settings issue");
                        break;
                    default:
                        break;
                }
                break;

            case REQUEST_GOOGLE_PLAY_SERVICES:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.e("GPA", "Google Play Services were NOT available, but now they are :)");
                        initMap();
                        break;
                    default:
                        initError("Google Play Services are NOT available!");
                        break;
                }
                break;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.e("GPA", "map ready");
        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(),R.raw.night_mode_map)); //set night mode style
        //mMap.setMyLocationEnabled(true);
        setMapListeners();
        mMap.setOnMyLocationButtonClickListener(this);
        //getUniMarkerIfExistsThenSetBounds();
        enableMyLocation();
        //getCurrentLocation(true);

        getUniMarkerIfExists();
        startLocationUpdates();
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CHECK_FINE_PERMISSION_TO_ENABLE_MY_LOCATION_BUTTON);
        } else if (mMap != null) {
            mMap.setMyLocationEnabled(true);
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CHECK_FINE_PERMISSION_TO_GET_LOCATION_UPDATES);
            return;
        }
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
    }

    private void stopLocationUpdates() {
        if(mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = new LatLng(location.getLatitude(),location.getLongitude());
        Toast.makeText(getContext(), "current location is available :)", Toast.LENGTH_SHORT).show();
        Log.e("GPA","current location is available :)\nLat: "+location.getLatitude()+" Lon: "+location.getLongitude());
        stopLocationUpdates();
        scale2();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Toast.makeText(getContext(), "Connected", Toast.LENGTH_SHORT).show();
        Log.e("GPA","connected");
        checkLocationSettings();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(getContext(), "Connection suspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(getContext(), "Connection failed!", Toast.LENGTH_SHORT).show();
        initError("Connection failed! Maybe internet access is required!");
    }

    void getUniMarkerIfExistsThenSetBounds(Boolean setBounds){
        if(prefs.getBoolean("uniMarker",false)){
            double lat = (double) prefs.getFloat("uniLat",0f);
            double lng = (double) prefs.getFloat("uniLng",0f);
            LatLng uni = new LatLng(lat,lng);
            addMarkerOnUni(uni);
            if(setBounds && currentLocation != null){
                LatLngBounds b = new LatLngBounds(uni,currentLocation);
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(b,99));
            }
        }
    }

    void getUniMarkerIfExists(){
        if(prefs.getBoolean("uniMarker",false)){
            double lat = (double) prefs.getFloat("uniLat",0f);
            double lng = (double) prefs.getFloat("uniLng",0f);
            LatLng uni = new LatLng(lat,lng);
            addMarkerOnUni(uni);
        }
    }
    void scale2(){
        if(uniMarker != null && currentLocation != null){
            //LatLngBounds b = new LatLngBounds(uniMarker.getPosition(),currentLocation);
            LatLngBounds.Builder b = new LatLngBounds.Builder();
            b.include(currentLocation).include(uniMarker.getPosition());
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(b.build(),99));
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
            case REQUEST_CHECK_PERMISSION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.e("GPA","requesting location permission : DONE :)");
                    getCurrentLocation(true);
                } else {
                    Toast.makeText(getContext(), "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CHECK_FINE_PERMISSION_TO_ENABLE_MY_LOCATION_BUTTON:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.e("GPA","requesting FINE location permission : DONE :)");
                    enableMyLocation();
                } else {
                    Toast.makeText(getContext(), "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
                break;
            case REQUEST_CHECK_FINE_PERMISSION_TO_GET_LOCATION_UPDATES:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.e("GPA","requesting FINE location permission : DONE :)\nNow let's start getting location updates");
                    startLocationUpdates();
                } else {
                    Toast.makeText(getContext(), "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    void getCurrentLocation(boolean setBounds){
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            Log.e("GPA","requesting location permission");
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_CHECK_PERMISSION);
            return;
        }
        Log.e("GPA","getting location");
        Log.e("GPA","mGoogleApiClient : " + ((mGoogleApiClient==null)?"null":"not null"));
        Location current = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(current != null){
            Log.e("GPA","current location found");
            Toast.makeText(getContext(), "current location found", Toast.LENGTH_SHORT).show();
            currentLocation = new LatLng(current.getLatitude(),current.getLongitude());
            //goToLocation(currentLocation,15,true);
        }
        if(setBounds) getUniMarkerIfExistsThenSetBounds(setBounds);
    }

    void scale(){
        getCurrentLocation(true);
    }

    void showCurrentLocation(){
        getCurrentLocation(false);
        if(currentLocation != null) goToLocation(currentLocation,15,true);
        else Toast.makeText(getContext(), "Can't get current location!", Toast.LENGTH_SHORT).show();
    }

    private void addMarkerOnUni(LatLng ll) {
        if(uniMarker != null) uniMarker.remove();
        MarkerOptions ma = new MarkerOptions().position(ll).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        uniMarker = mMap.addMarker(ma);
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
                zoomFABout();
            }
        });
    }

    private void zoomFABout () {
        Animation a = AnimationUtils.loadAnimation(getContext(), R.anim.zoom_out_fab);
        FAB_addMarker.startAnimation(a);
        FAB_addMarker.setVisibility(View.INVISIBLE);
    }

    float getDistance (LatLng ll1, LatLng ll2) {
        float[] d = new float[1];
        Location.distanceBetween(ll1.latitude, ll1.longitude, ll2.latitude, ll2.longitude, d);
        return d[0];
    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onStop() {
        super.onStop();
        stopLocationUpdates();
    }
}