package com.example.mathieu.geolocalisationquest;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.GsonGoogleHttpClientSpiceService;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private LocationManager locationManager;
    private String locationProvider;
    LocationListener locationListener;
    private Double Lat;
    private Double Long;
    public final static int REQUEST_LOCATION_PERMISSION = 1;

    private String apiKey;
    private TextView textViewCurrent;
    private TextView textViewForecast;
    private TextView textViewToday;
    private ListView listView;
    private ImageView gifTextView;

    ProgressDialog dialog;
    protected SpiceManager spiceManager = new SpiceManager(GsonGoogleHttpClientSpiceService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;

        apiKey = "0889ec61413e84e9fab64f44c38791d0";

        gifTextView = (ImageView) findViewById(R.id.gifTextViewResult);
        textViewCurrent = (TextView)findViewById(R.id.textViewCurrent);
        textViewForecast = (TextView)findViewById(R.id.textViewForecast);
        textViewForecast.setVisibility(View.INVISIBLE);
        textViewToday = (TextView)findViewById(R.id.textViewToday);
        textViewToday.setVisibility(View.INVISIBLE);
        listView = (ListView)findViewById(R.id.listViewForecast);

        locationManager = (LocationManager) this.getSystemService(MainActivity.LOCATION_SERVICE);


         locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {

                // Called when a new location is found by the network location provider.
                Lat = location.getLatitude();
                Long = location.getLongitude();
                Toast.makeText(MainActivity.this, "New location found Lat : " + Lat + " Long : " + Long, Toast.LENGTH_SHORT).show();
                performRequest();
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {


                String locationProvider = LocationManager.GPS_PROVIDER;


                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                            }, 1);

                }


                Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);
            }


            public void onProviderDisabled(String provider) {


            }
        };


        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck2 = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);


        if (permissionCheck
                != PackageManager.PERMISSION_GRANTED && permissionCheck2 != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) && ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                Toast.makeText(MainActivity.this, "Accepte !!!", Toast.LENGTH_LONG).show();

            } else {


                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

            }
        }





        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    private void performRequest() {

        ForecastWeatherRequest requestForecast = new ForecastWeatherRequest(Lat, Long, apiKey);
        spiceManager.execute(requestForecast, new ForecastWeatherRequestListener());

        CurrentWeatherModelRequest requestCurrent = new CurrentWeatherModelRequest(Lat, Long, apiKey);
        spiceManager.execute(requestCurrent, new CurrentWeatherRequestListener());
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 20, locationListener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 20, locationListener);

        spiceManager.start(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);

        spiceManager.shouldStop();
    }

    private class ForecastWeatherRequestListener implements RequestListener<Forecast> {

        @Override
        public void onRequestFailure(SpiceException e) {
            //update your UI
        }

        @Override
        public void onRequestSuccess(Forecast forecastWeatherModel) {
            textViewForecast.setVisibility(View.VISIBLE);
            final ArrayList<List> weatherList = (ArrayList<List>) forecastWeatherModel.getList();
            final WeatherAdapter weatherAdapter = new WeatherAdapter(MainActivity.this, weatherList);
            listView.setAdapter(weatherAdapter);

        }


    }

    private class CurrentWeatherRequestListener implements RequestListener<WeatherModel> {

        private String city;

        @Override
        public void onRequestFailure(SpiceException e) {
            //update your UI
        }

        @Override
        public void onRequestSuccess(WeatherModel currentWeatherModel) {

            textViewToday.setVisibility(View.VISIBLE);
            int windSpeed = (int) (currentWeatherModel.getWind().getSpeed() * 3.6);

            if (currentWeatherModel.getName().length() > 0) {
                city = currentWeatherModel.getName();
                textViewCurrent.setText("Ville : " + city + "\n" + "Vitesse du vent : " + windSpeed + " Km/h");
                if (windSpeed > 10) {
                    gifTextView.setBackgroundResource(R.drawable.lightwind);
                } else if (windSpeed > 30) {
                    gifTextView.setBackgroundResource(R.drawable.hardwind);
                } else {
                    gifTextView.setBackgroundResource(R.drawable.moderatewind);
                }
            } else {
                textViewCurrent.setText("Vitesse du vent : " + windSpeed + " Km/h");
                if (windSpeed > 10) {
                    gifTextView.setBackgroundResource(R.drawable.lightwind);
                } else if (windSpeed > 30) {
                    gifTextView.setBackgroundResource(R.drawable.hardwind);
                } else {
                    gifTextView.setBackgroundResource(R.drawable.moderatewind);
                }

            }
        }


    }

}

