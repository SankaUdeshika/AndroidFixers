package lk.sankaudeshika.androidfixers;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.Arrays;

public class ShopLocatoinActivity extends AppCompatActivity {

    private LatLng selectedDestination;
    private GoogleMap googleMap1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shop_locatoin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Initialize Places API
        String apiKey = "AIzaSyBHBpJkSj-glfrU1YGPqRhpvZU7j-w0SM0";
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), apiKey);
        }

//
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        if (autocompleteFragment == null) {
            autocompleteFragment = new AutocompleteSupportFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.autocomplete_fragment, autocompleteFragment);
            transaction.commit();
        }

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG,Place.Field.ADDRESS));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // Handle place selection
                double latitude = place.getLatLng().latitude;
                double longitude = place.getLatLng().longitude;
                 selectedDestination = new LatLng(latitude,longitude);
                String destinationAddress = place.getAddress();

                Log.i("TAG", "Place: " + place.getAddress());
                Log.i("TAG", "Latitude: " + latitude + ", Longitude: " + longitude);

                setMarker(googleMap1);



            }

            @Override
            public void onError(@NonNull Status status) {
                // Handle errors
                Log.i("TAG", "An error occurred: " + status);
            }
        });

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), "AIzaSyCcKUDBoGKWzsLRjDGTagVbelHArRo-w-8");

        }




        PlacesClient placesClient = Places.createClient(ShopLocatoinActivity.this);
        Toast.makeText(ShopLocatoinActivity.this, "Google Places API Initialized", Toast.LENGTH_SHORT).show();

        SupportMapFragment supportMapFragment = new SupportMapFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.frameLayout,supportMapFragment);
        fragmentTransaction.commit();


        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {

                googleMap1 = googleMap;




            }
        });

    }
    private void setMarker(GoogleMap googleMap1){
        if(selectedDestination != null){
            googleMap1.addMarker(new MarkerOptions().position(selectedDestination).title("Marker").icon(BitmapDescriptorFactory.fromResource(R.drawable.map)));
            googleMap1.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(selectedDestination).zoom(15).build()));
        }
    }

}


