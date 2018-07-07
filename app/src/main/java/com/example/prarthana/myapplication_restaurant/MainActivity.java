package com.example.prarthana.myapplication_restaurant;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, LocationListener, Serializable {
   static  String TAG= MainActivity.class.getSimpleName();
    static ArrayList<Results> results;
    static ResultsRecyclerAdapter resultsRecyclerAdapter;
    static String latitude="28.7";
    static String longitude="77.1";
    static String radius="500";

    final String TAG2 = "GPS";
    private final static int ALL_PERMISSIONS_RESULT = 101;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;


    LocationManager locationManager;
    Location loc;
    ArrayList<String> permissions = new ArrayList<>();
    ArrayList<String> permissionsToRequest;
    ArrayList<String> permissionsRejected = new ArrayList<>();
    boolean isGPS = false;
    boolean isNetwork = false;
    boolean canGetLocation = true;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.e(TAG,"OnSave");
        outState.putString("value","1");
        outState.putSerializable("results",results);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.e(TAG,"OnRestore");
        super.onRestoreInstanceState(savedInstanceState);
        String value = (String)savedInstanceState.get("value");
        results= (ArrayList<Results>) savedInstanceState.getSerializable("results");
        resultsRecyclerAdapter.addAll(results);
        resultsRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.refresh){
            new SyncTask_GET().execute();
        }
        else if (id == R.id.settings) {
            Intent intent=new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(intent);
        }
        else if(id==R.id.my_location){
            locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
            isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissionsToRequest = findUnAskedPermissions(permissions);

            if (!isGPS && !isNetwork) {
                Log.d(TAG2, "Connection off");
                showSettingsAlert();
                getLastLocation();
            } else {
                Log.d(TAG2, "Connection on");
                // check permissions
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (permissionsToRequest.size() > 0) {
                        requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                                ALL_PERMISSIONS_RESULT);
                        Log.d(TAG2, "Permission requests");
                        canGetLocation = false;
                    }
                }

                // get location
                getLocation();
            }

            //used permission in manifest
        }
        return true;
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG2, "onLocationChanged");
        updateUI(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String s) {
        getLocation();
    }

    @Override
    public void onProviderDisabled(String s) {
        if (locationManager != null) {
            locationManager.removeUpdates(this);
        }
    }

    private void getLocation() {
        try {
            if (canGetLocation) {
                Log.d(TAG2, "Can get location");
                if (isGPS) {
                    // from GPS
                    Log.d(TAG2, "GPS on");
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (loc != null)
                            updateUI(loc);
                    }
                } else if (isNetwork) {
                    // from Network Provider
                    Log.d(TAG2, "NETWORK_PROVIDER on");
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (loc != null)
                            updateUI(loc);
                    }
                } else {
                    loc.setLatitude(0);
                    loc.setLongitude(0);
                    updateUI(loc);
                }
            } else {
                Log.d(TAG2, "Can't get location");
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void getLastLocation() {
        try {
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(provider);
            Log.d(TAG2, provider);
            Log.d(TAG2, location == null ? "NO LastLocation" : location.toString());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canAskPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                Log.d(TAG2, "onRequestPermissionsResult");
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(
                                                        new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                } else {
                    Log.d(TAG2, "No rejected permissions.");
                    canGetLocation = true;
                    getLocation();
                }
                break;
        }
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS is not Enabled!");
        alertDialog.setMessage("Do you want to turn on GPS?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }


    private void updateUI(Location loc) {
        Log.d(TAG2, "updateUI");
        latitude=Double.toString(loc.getLatitude());
        longitude=Double.toString(loc.getLongitude());
        new SyncTask_GET().execute();


//        tvLatitude.setText(Double.toString(loc.getLatitude()));
//        tvLongitude.setText(Double.toString(loc.getLongitude()));
//        tvTime.setText(DateFormat.getTimeInstance().format(loc.getTime()));
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (locationManager != null) {
//            locationManager.removeUpdates(this);
//        }
//    }
// this is of location

    //permission in manifest "<uses-permission android:name="android.permission.INTERNET"/>"
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        results = new ArrayList<>();
        resultsRecyclerAdapter = new ResultsRecyclerAdapter(this,results);

        //in activity_main.xml
        RecyclerView recyclerView=findViewById(R.id.news_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(resultsRecyclerAdapter);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (savedInstanceState == null) {
            if (preferences.getString("latitude", null) != null) {
                latitude=preferences.getString("latitude", "28.7");
                new SyncTask_GET().execute(latitude);
            }

            if (preferences.getString("longitude", null) != null) {
                longitude=preferences.getString("longitude", "77.1");
                new SyncTask_GET().execute(longitude);
            }

            if (preferences.getString("radius", null) != null) {
                radius=preferences.getString("radius", "500");
                new SyncTask_GET().execute(radius);
            }

        }
        preferences.registerOnSharedPreferenceChangeListener(this);


    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equalsIgnoreCase("latitude")){
         new SyncTask_GET().execute(sharedPreferences.getString(key,"28.7"));
    }
        else  if(key.equalsIgnoreCase("longitude")){
            new SyncTask_GET().execute(sharedPreferences.getString(key,"77.1"));
        }
        else  if(key.equalsIgnoreCase("radius")){
            new SyncTask_GET().execute(sharedPreferences.getString(key,"500"));
        }
}

    public class SyncTask_GET extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... strings) {
            HttpsURLConnection urlConnection = null;
            BufferedReader reader = null;
            String JsonStr = null;
            try {
                URL url = new URL("https://maps.googleapis.com/maps/api/place/textsearch/json?type=restaurants&location="+ latitude+","+longitude+"&radius="+radius+ "&key=AIzaSyBlKjM8zpQHrNRYf_BiqFLJ0BlQfwPuFHE");
                urlConnection = (HttpsURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                // if there is ssl handshake exception
                //urlConnection.setSSLSocketFactory(buildSslSocketFactory(MainActivity.this));
                urlConnection.connect();


                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                JsonStr = buffer.toString();
                Log.d("API Response", JsonStr);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }
            return JsonStr;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            results.clear();
            if (s != null) {
                final String OWM_RESULTS= "results";
              //  final String OWM_SOURCE = "source";
                final String OWM_ADDRESS = "formatted_address";
                final String OWM_NAME = "name";
                final String OWM_RATING = "rating";
                final String OWM_OPENINGHOURS="opening_hours";
                final String OWM_URL_TO_ICON="icon";



                try {
                    JSONObject forecastJson = new JSONObject(s);

                 //   String mSource = forecastJson.getString(OWM_SOURCE);
                    JSONArray newsArray = forecastJson.getJSONArray(OWM_RESULTS);



                    for (int i = 0; i < newsArray.length(); i++) {
                        // These are the values that will be collected.
                        String address;
                        String name;
                        String rating;
                        String isopen = null;
                        String icon;
                        //JSONArray openingHours;

                        Results result = new Results();

                        // Get the JSON object representing the day
                        JSONObject article = newsArray.getJSONObject(i);

                        address = article.getString(OWM_ADDRESS);
                        name = article.getString(OWM_NAME);
                        try{
                            rating = article.getString(OWM_RATING);
                        }
                        catch (Exception e){
                            rating="unknown";
                        }


                        //openingHours=article.getJSONArray(OWM_OPENINGHOURS);
                        icon=article.getString(OWM_URL_TO_ICON);

                        final String OWM_OPEN="open_now";

//                        for(int k=0;k<openingHours.length();k++){
//
//
//                            JSONObject infoAboutTime = openingHours.getJSONObject(k);
//
//                            isopen = infoAboutTime.getString(OWM_OPEN);
//                        }
                        result.setAddress(address);
 //                       result.setIsopen(isopen);
                        result.setName(name);
                        result.setRating(rating);
                        result.setIcon(icon);


                        results.add(result);
                    }
                    resultsRecyclerAdapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage(), e);
                    e.printStackTrace();

                }
            }
        }

    }
}
