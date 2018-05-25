package polytechnice.si3.ihm.android;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

@SuppressWarnings("unchecked")
public class MapsActivity extends FragmentActivity {

    private boolean allGranted = false;
    private GoogleMap googleMap;
    private LatLng selectedLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        getLocationPermission();

        Button btnSelectLocation = findViewById(R.id.btn_SelectLocation);

        btnSelectLocation.setOnClickListener(view ->{
            Intent intent = new Intent();
            intent.putExtra("location", selectedLatLng);  // insert your extras here
            setResult(RESULT_OK, intent);
            finish();
        });

    }

    private void getDeviceLocation() {
        FusedLocationProviderClient locationProvider = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (allGranted) {
                Task location = locationProvider.getLastLocation();
                location.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Location currentLocation = (Location) task.getResult();
                        selectedLatLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLatLng, 18));
                    } else {
                        Toast.makeText(this, "unable to get current location", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (SecurityException ignored) {
            getLocationPermission();
        }
    }

    private void initMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(googleMap -> {
            this.googleMap = googleMap;
            try {
                if (allGranted) {
                    getDeviceLocation();

                    googleMap.setMyLocationEnabled(true);
                    googleMap.setOnMapLongClickListener(latLng -> {
                        googleMap.clear();
                        googleMap.addMarker(new MarkerOptions().position(latLng));
                        selectedLatLng = latLng;
                    });
                }
            } catch (SecurityException ignored) {
                getLocationPermission();
            }
        });
    }

    private void getLocationPermission() {

        boolean accessToFineLoaction = checkSelfPermission(ACCESS_FINE_LOCATION) == PERMISSION_GRANTED;
        boolean accessToCoarseLocation = checkSelfPermission(ACCESS_COARSE_LOCATION) == PERMISSION_GRANTED;

        allGranted = accessToFineLoaction && accessToCoarseLocation;

        if (!allGranted)
            requestPermissions(new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, 1);
        else
            initMap();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 1) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PERMISSION_GRANTED) {
                        allGranted = false;
                        return;
                    }
                }
                allGranted = true;
                initMap();
            }
        }
    }
}
