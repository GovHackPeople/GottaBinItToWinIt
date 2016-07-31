package org.govhack.gottabinittowinit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class SetupActivity extends AppCompatActivity implements PlaceSelectionListener, OnMapReadyCallback {
    private static final String TAG = "SetupActivity";

    GoogleMap mMap;
    Place mPlace;
    SupportMapFragment mMapFragment;
    PlaceAutocompleteFragment mPlaceAutoCompleteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        // Setup toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Setup Google Maps fragment
        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googleMap);
        mMapFragment.getMapAsync(this);

        // Setup Google Places for address selection
        mPlaceAutoCompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.address_autocomplete);
        mPlaceAutoCompleteFragment.setOnPlaceSelectedListener(this);

        // Limit results to address locations
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .build();
        mPlaceAutoCompleteFragment.setFilter(typeFilter);

        // Create a boundary to limit potential address results to City of Melbourne area
        mPlaceAutoCompleteFragment.setBoundsBias(new LatLngBounds(
                new LatLng(-37.858320, 144.885292),
                new LatLng(-37.766644, 145.014725)));


    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        // Setup camera to position on Melbourne
        CameraUpdate melbourneCamera =
                CameraUpdateFactory.newLatLngZoom(Constants.MELBOURNE_LOCATION, 14);
        map.moveCamera(melbourneCamera);
    }

    @Override
    public void onPlaceSelected(Place place) {
        mPlace = place;

        // Add marker to map for address and zoom to it
        if (mMap != null) {
            mMap.clear();
            mMap.addMarker(new MarkerOptions()
                    .position(place.getLatLng())
                    .title(place.getAddress().toString()));

            CameraUpdate placeCamera =
                    CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 17);
            mMap.animateCamera(placeCamera);
        }

        // Make continue button accessible
        toggleButton();
    }

    @Override
    public void onError(Status status) {
        // TODO: Error handling for place selection
    }

    private void saveAddressPreference(Place place) {
        SharedPreferences prefs = getSharedPreferences(Constants.PREFERENCES_ID, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.PREFERENCES_KEY_ADDRESS_SETUP, place.getAddress().toString());
        editor.commit();
        Log.v(TAG, "Address saved: " + place.getAddress().toString());
    }

    private void toggleButton() {
        Button button = (Button) findViewById(R.id.continueButton);
        if (button.getVisibility() == View.INVISIBLE) {
            button.setVisibility(View.VISIBLE);
            button.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Save address to preferences
                    saveAddressPreference(mPlace);

                    // Go to MainActivity
                    Intent i = new Intent(SetupActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            });
        }
    }
}
