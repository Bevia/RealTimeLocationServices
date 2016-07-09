package com.corebaseit.realtimelocationservices;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.corebaseit.realtimelocationservices.adapters.LocationAdapter;
import com.corebaseit.realtimelocationservices.constants.Constants;
import com.corebaseit.realtimelocationservices.models.DistanceModel;
import com.corebaseit.realtimelocationservices.models.LocationModel;
import com.corebaseit.realtimelocationservices.rest.RestLocations;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity {

    private final InternetConnectivityCheker internetConnectivityCheker =
            new InternetConnectivityCheker();

    String PROVIDER = LocationManager.NETWORK_PROVIDER;
    LocationTracker lTracker;

    private int REQUEST_LOCATION = 2;
    protected LocationManager locationConnectivityManager;

    private LocationAdapter type_contact_list_inner_image_adapter;
    private List<LocationModel> locationModels;
    private List<String> names;
    private List<DistanceModel> distanceModels;
    private ListView lview;

    private int theSize;
    private String MAIN_JSON_URL;
    private boolean mState;
    private boolean TAG_PROVIDER_ENABLE;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.city)
    TextView myCity;

    @BindView(R.id.address)
    TextView myAddress;

    @BindView(R.id.country)
    TextView myCountry;

    @BindView(R.id.cp) // this is the Postal Code
            TextView myCP;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("  Location Services");

        mState = false;// setting state for ubicacion menu

        invalidateOptionsMenu(); // now onCreateOptionsMenu(...) is called again

        String slugStringNegoapps = Constants.LOCATION_JSON_URL;
        MAIN_JSON_URL = slugStringNegoapps;

        requestData(MAIN_JSON_URL);

    }

    private void requestData(String uri) {

        StringRequest request = new StringRequest(Request.Method.POST, uri,

                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (response.isEmpty() || response == null) {
                            Toast.makeText(MainActivity.this, "no se ha podido conectar al servidor...", Toast.LENGTH_LONG).show();
                        } else {
                            locationModels = RestLocations.parseLocationFeed(response); // response is the JSON!
                            try {
                                updateDisplay();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError ex) {

                    }
                }
        ) {
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    protected void updateDisplay() throws IOException {

        if (locationModels != null) {

            theSize = locationModels.size();
            names = new ArrayList<>();
            reCallNames();

        } else {
            Log.d("TESTING", "Error");
        }
    }

    private void reCallNames() throws IOException {

        //make sure we DO have the names in that show up in the list
        //these names are the names to the right of the image in the list!!
        if (names == null || names.size() < 1) {

            Log.d("COORDINATES", "the size is the names array is: " + names);

            theloop:
            for (int look = 0; look < theSize; look++) {
                final LocationModel model = locationModels.get(look);
                model.getCity();
                names.add(model.getCity());

                if (names.size() > 0) {
                    mainUpdateDisplay();
                    break theloop;
                }
            }

        } else {

            mainUpdateDisplay();
        }
    }

    //This is my comparator method use to arrange the distances from smaller to larger!!
    Comparator<DistanceModel> compareByDistance = new Comparator<DistanceModel>() {
        @Override
        public int compare(DistanceModel a, DistanceModel b) {
            return Double.compare(a.getDistance(), b.getDistance());
        }
    };

    private void mainUpdateDisplay() throws IOException {

        if (names.size() < 1) {
            reCallNames();
        } else {

            distanceModels = new ArrayList<>();

            for (int look = 0; look < this.theSize; look++) {
                final LocationModel model = locationModels.get(look);

                model.setDistance(0.00);
                showLocationTrackingUpdates(model.getLatitude(), model.getLongitude());

                //populate the new distanceModel ArrayList<>() with the new calculated values!
                DistanceModel dist = new DistanceModel();

                dist.setLongitude(model.getLongitude());
                dist.setLatitude(model.getLatitude());
                dist.setAddress(model.getAddress());
                dist.setDistance(model.getDistance());
                dist.setCity(model.getCity());

                distanceModels.add(dist);
            }

            //Here it goes, new List of type double:
            List<Double> distancesModels = new ArrayList<>();

            if (TAG_PROVIDER_ENABLE = true) {

                Collections.sort(distanceModels, compareByDistance);
                for (DistanceModel compareByDistance : distanceModels) {
                    distancesModels.add(compareByDistance.getDistance());
                }
            }

            lview = (ListView) findViewById(R.id.tab5_inner_list);

            /** Updating parsed JSON data into ListView */

            if (type_contact_list_inner_image_adapter == null) {

                Log.d("KM_UPDATING", "Is it updating?" + "adapter is null");

                type_contact_list_inner_image_adapter = new LocationAdapter(
                        this, R.layout.locations_item_layout, distanceModels);

                lview.setAdapter(type_contact_list_inner_image_adapter);

            } else {

                //update only dataset, retain index of listview!!
                type_contact_list_inner_image_adapter.clear();
                type_contact_list_inner_image_adapter.addAll(distanceModels);
                type_contact_list_inner_image_adapter.notifyDataSetChanged();

                Log.d("KM_UPDATING", "Is it updating?" + "adapter is NOT null");
            }
        }
    }

    private void checkForPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {

            Log.d("DISTANCE", "onCreate: " + " permission has been granted, continue as usual ");
            lTracker = new LocationTracker(this);
            lTracker.locationManager.requestLocationUpdates(PROVIDER, 0, 0, myLocationTrackerManager);
        }
    }

    private void getDistanceInKm(double lat, double lg, double latJSON, double lgJSON) {

        LatLng point1 = new LatLng(lat, lg);  //this is where you currently are at...
        LatLng point2 = new LatLng(latJSON, lgJSON); //These are the values given by the JSON!

        Log.d("MY_PLACE", "Where am I? " + point1);
        MyCurrentAddress(lat, lg); //Give me my address and other useful data!

        //Call the distanceBetween method:
        distanceBetween(point1, point2);

        theSize = locationModels.size();

        for (int look = 0; look < theSize; look++) {

            final LocationModel model = locationModels.get(look);
            model.setDistance(distanceBetween(point1, point2));

        }
    }

    private void showLocationTrackingUpdates(double latFromJSON, double longFromJSON) {

        if (lTracker.canGetLocation) {

            if (lTracker.getLocation() == null) {

                //in case we use the emulator...
                Log.d("DISTANCE", "nulo");

            } else {

                lTracker.getLocation();
                getDistanceInKm(lTracker.getLatitude(), lTracker.getLongitude(), latFromJSON, longFromJSON);

            }
        } else {

            //Toast.makeText(Tab5_ContactInnerListActivity.this, "Waiting for results...", Toast.LENGTH_SHORT).show();
            Log.d("DISTANCE", "Unable to find ");
        }
    }


    public static Double distanceBetween(LatLng point1, LatLng point2) {
        if (point1 == null || point2 == null) {
            return null;
        }

        return SphericalUtil.computeDistanceBetween(point1, point2);
    }

    private LocationListener myLocationTrackerManager = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            try {
                reCallNames();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(MainActivity.this, "The Location Service is disable!", Toast.LENGTH_LONG).show();
            TAG_PROVIDER_ENABLE = false;
            try {
                reCallNames();
            } catch (IOException e) {
                e.printStackTrace();
            }
            toolbar.setSubtitle("   Real-Time Location Tracking: OFF");
            invalidateOptionsMenu(); // now onCreateOptionsMenu(...) is called again
            mState = true;// setting state for ubicacion menu
        }

        @Override
        public void onProviderEnabled(String provider) {
            TAG_PROVIDER_ENABLE = true;
            invalidateOptionsMenu(); // now onCreateOptionsMenu(...) is called again
            toolbar.setSubtitle("   Real-Time Location Tracking: ON");
            try {
                reCallNames();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mState = false;// setting state for ubicacion menu
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    };

    private void removeTrackingUpdates() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }

        //removes tracking updates:
        lTracker.locationManager.removeUpdates(myLocationTrackerManager);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // We can now safely use the API we requested access to
                Log.d("DISTANCE", "onRequestPermissionsResult: " + " permission has been granted, continue as usual ");
                lTracker = new LocationTracker(this);

            } else {
                Log.d("DISTANCE", "onRequestPermissionsResult: " + "  Permission was denied or request was cancelled ");
            }
        }
    }

    private void checkStatusOfNetwoksConnectivity() {

        locationConnectivityManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (locationConnectivityManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationConnectivityManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            TAG_PROVIDER_ENABLE = true;
            toolbar.setSubtitle("   Real-Time Location Tracking: ON");

        } else {

            TAG_PROVIDER_ENABLE = false;
            toolbar.setSubtitle("   Real-Time Location Tracking: OFF");
            //Toast.makeText(MainActivity.this, "¡El servicio de ubicación está desactivado!", Toast.LENGTH_LONG).show();

            showNoInternetConnectionAlertDialog(MainActivity.this);
        }
    }

    private void MyCurrentAddress(double myLat, double myLong) {

        if (internetConnectivityCheker.isOnline(this)) {

            Geocoder geocoder;
            List<Address> addresses = null;
            geocoder = new Geocoder(this, Locale.getDefault());

            try {
                // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                addresses = geocoder.getFromLocation(myLat, myLong, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();

            myCity.setText(city);
            myAddress.setText(address);
            myCP.setText(postalCode);
            myCP.setText(postalCode);
            myCountry.setText(country);

            /*Log.d("MY_PLACE", "Where am I? " + city + "  " + address + "  " + postalCode + "  " + country);*/

        } else {

            Toast.makeText(MainActivity.this, "There seems to be no conection to the internet!", Toast.LENGTH_LONG).show();
        }
    }

    public void showNoInternetConnectionAlertDialog(Context context) {
        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Locations Services is disable!")
                .setContentText("\n\n¿Do you want to enable it?")
                .setConfirmText("OK")
                .setCancelText("Cancel")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        MainActivity.this.startActivity(intent);
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkForPermissions();
        checkStatusOfNetwoksConnectivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        removeTrackingUpdates();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem settingsMenuItem = menu.findItem(R.id.ubicacion);
        SpannableString s = new SpannableString(settingsMenuItem.getTitle());
        s.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.indigo_900)), 0, s.length(), 0);
        settingsMenuItem.setTitle(s);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // inflate menu from xml
        getMenuInflater().inflate(R.menu.ubicacion, menu);

        if (mState == false) {
            for (int i = 0; i < menu.size(); i++)
                menu.getItem(i).setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.ubicacion) {

            Toast.makeText(MainActivity.this, "Accediendo a Ubicación...", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(
                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            MainActivity.this.startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}