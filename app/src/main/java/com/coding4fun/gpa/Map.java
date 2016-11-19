package com.coding4fun.gpa;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

/**
 * Created by coding4fun on 09-Oct-16.
 */

public class Map extends Fragment implements OnMapReadyCallback {

    GoogleMap mMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map, container, false);
        GoogleApiAvailability gAPI = GoogleApiAvailability.getInstance();
        int isAvailable = gAPI.isGooglePlayServicesAvailable(getContext());
        if(isAvailable == ConnectionResult.SUCCESS){ //everything is OK
            initMap(v);
        }
        else if(gAPI.isUserResolvableError(isAvailable)){ //there is an error, but the user can do something about it
            initResolvableError(v,gAPI,isAvailable);
        }
        else{ //there is an error, and the user can't do something about it...
            initError(v);
        }
        return v;
    }

    private void initResolvableError(View v, final GoogleApiAvailability gAPI, final int isAvailable) {
        (v.findViewById(R.id.map_resolvableErrorLayout)).setVisibility(View.VISIBLE);
        ((Button)v.findViewById(R.id.map_resolvableErrorBTN)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gAPI.showErrorDialogFragment(getActivity(),isAvailable,99);
            }
        });
    }

    private void initError(View v){
        (v.findViewById(R.id.map_ErrorLayout)).setVisibility(View.VISIBLE);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //code here
        //Log.e("MAP","ViewCreated");
    }

    private void initMap(View v) {
        (v.findViewById(R.id.map_fragment)).setVisibility(View.VISIBLE);
        SupportMapFragment mf = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment);
        mf.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //mMap.setMyLocationEnabled(true);
    }

}