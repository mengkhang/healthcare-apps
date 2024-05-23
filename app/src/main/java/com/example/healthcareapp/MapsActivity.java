package com.example.healthcareapp;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.healthcareapp.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

        // Add a marker in Sydney and move the camera
        LatLng ampang = new LatLng(3.1446174702680825, 101.76272440454855);
        mMap.addMarker(new MarkerOptions().position(ampang).title("Ampang hospital"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(ampang));

        LatLng cheras = new LatLng(3.0697382519831793, 101.77359669736173);
        mMap.addMarker(new MarkerOptions().position(cheras).title("Cheras hospital"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(cheras)); // copy this three line of code to add few location

        LatLng subangJaya = new LatLng(3.059118220993984, 101.58425205154798);
        mMap.addMarker(new MarkerOptions().position(subangJaya).title("Subang hospital"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(subangJaya));

        LatLng segambut = new LatLng(3.185575926623935, 101.66214310140946);
        mMap.addMarker(new MarkerOptions().position(segambut).title("Segambut Clinic"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(segambut));

        LatLng bangsar = new LatLng(3.1267307308130765, 101.66916540706234);
        mMap.addMarker(new MarkerOptions().position(bangsar).title("Bangsar Hospital"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(bangsar));

        LatLng sentul = new LatLng(3.214414078073587, 101.68212373737987);
        mMap.addMarker(new MarkerOptions().position(sentul).title("Sentul CLinic"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sentul));

        LatLng kajang = new LatLng(3.214414078073587, 101.68212373737987);
        mMap.addMarker(new MarkerOptions().position(kajang).title("Kajang Hospital"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(kajang));

        // Set the initial camera position with a desired zoom level
        float zoomLevel = 10.0f; // You can set the desired zoom level here
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ampang, zoomLevel));
    }
}