package com.example.devansh.gpsalarm;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity {

    private static final int minTime = 1000 * 30;
    private static final int minDistance = 50;

    EditText latitude, longitude;
    TextView cLatitude, cLongitude;
    Button startUpdates, stopUpdates;

    LocationManager locationManager;

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            cLatitude.setText(String.valueOf(round(location.getLatitude(), 4)));
            cLongitude.setText(String.valueOf(round(location.getLongitude(), 4)));

            Toast.makeText(getApplicationContext(), "Location changed", Toast.LENGTH_LONG).show();

            if (round(Double.valueOf(latitude.getText().toString()), 4) ==
                Double.valueOf(cLatitude.getText().toString()) &&
                round(Double.valueOf(longitude.getText().toString()), 4) ==
                Double.valueOf(longitude.getText().toString())) {

                notifyUser();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "Turn ON Location from Settings", Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latitude = findViewById(R.id.editText);
        longitude = findViewById(R.id.editText2);

        cLatitude = findViewById(R.id.textView5);
        cLongitude = findViewById(R.id.textView6);

        startUpdates = findViewById(R.id.button);
        stopUpdates = findViewById(R.id.button2);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        startUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocationUpdates();
            }
        });
        stopUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "This button is useless right now !!!", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
            }, 10);
            return;
        }

        if (!checkInput()) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, locationListener);
        } else {
            if (TextUtils.isEmpty(latitude.getText())) {
                latitude.setError("Latitude cannot be empty");
            } else {
                longitude.setError("Longitude cannot be empty");
            }
        }
    }

    public void notifyUser() {
        Toast.makeText(getApplicationContext(), "Location reached !!!", Toast.LENGTH_LONG).show();
    }

    public boolean checkInput() {
        return TextUtils.isEmpty(latitude.getText()) || TextUtils.isEmpty(longitude.getText());
    }

    public double round(double value, int numberOfDigitsAfterDecimalPoint) {
        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(numberOfDigitsAfterDecimalPoint,
                BigDecimal.ROUND_HALF_UP);
        return bigDecimal.doubleValue();
    }
}
