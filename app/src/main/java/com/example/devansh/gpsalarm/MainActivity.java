package com.example.devansh.gpsalarm;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity {

    private static final int minTime = 5 * 1000;
    private static final int minDistance = 0;

    EditText latitude, longitude;
    TextView cLatitude, cLongitude;
    Button startUpdates, stopUpdates, stopAlarm;

    LocationManager locationManager;

    Uri uri;

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            cLatitude.setText(String.valueOf(round(location.getLatitude())));
            cLongitude.setText(String.valueOf(round(location.getLongitude())));

            Toast.makeText(getApplicationContext(), "Location changed", Toast.LENGTH_SHORT).show();

            // Fix for negative numbers
            if (round(Double.valueOf(latitude.getText().toString())) == Double.valueOf(cLatitude.getText().toString()) &&
                    round(Double.valueOf(longitude.getText().toString())) == Double.valueOf(longitude.getText().toString())) {

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
        stopAlarm = findViewById(R.id.button3);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        stopAlarm.setEnabled(false);

        latitude.setFilters(new InputFilter[] {
                new DigitsKeyListener(Boolean.TRUE, Boolean.TRUE) {
                    int beforeDecimal = 2, afterDecimal = 4;

                    @Override
                    public CharSequence filter(CharSequence source, int start, int end,
                                               Spanned dest, int dstart, int dend) {
                        String temp = latitude.getText() + source.toString();

                        if (temp.equals(".")) {
                            return "0.";
                        } else if (temp.equals("0")) {
                            return "";
                        } else if (temp.equals("-")) {
                            beforeDecimal++;
                            return "-";
                        } else if (!temp.contains(".")) {
                            // no decimal point placed yet
                            if (temp.length() > beforeDecimal) {
                                return "";
                            }
                        } else {
                            temp = temp.substring(temp.indexOf(".") + 1);
                            if (temp.length() > afterDecimal) {
                                return "";
                            }
                        }

                        return super.filter(source, start, end, dest, dstart, dend);
                    }
                }
        });

        longitude.setFilters(new InputFilter[] {
                new DigitsKeyListener(Boolean.TRUE, Boolean.TRUE) {
                    int beforeDecimal = 3, afterDecimal = 4;

                    @Override
                    public CharSequence filter(CharSequence source, int start, int end,
                                               Spanned dest, int dstart, int dend) {
                        String temp = longitude.getText() + source.toString();

                        if (temp.equals(".")) {
                            return "0.";
                        } else if (temp.equals("0")) {
                            return "";
                        } else if (temp.equals("-")) {
                            beforeDecimal++;
                            return "-";
                        } else if (!temp.contains(".")) {
                            // no decimal point placed yet
                            if (temp.length() > beforeDecimal) {
                                return "";
                            }
                        } else {
                            temp = temp.substring(temp.indexOf(".") + 1);
                            if (temp.length() > afterDecimal) {
                                return "";
                            }
                        }

                        return super.filter(source, start, end, dest, dstart, dend);
                    }
                }
        });

        startUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startLocationUpdates();
            }
        });
        stopUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopLocationUpdates();
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

    public void stopLocationUpdates() {
        locationManager.removeUpdates(locationListener);
        Toast.makeText(getApplicationContext(), "Location updates stopped", Toast.LENGTH_LONG).show();
    }

    public void notifyUser() {
        final Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);

        ringtone.play();
        stopAlarm.setEnabled(true);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                stopAlarm(ringtone);
            }
        }, 10 * 1000);

        stopAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAlarm(ringtone);
            }
        });
    }

    public void stopAlarm(Ringtone ringtone) {
        if(ringtone.isPlaying()) {
            ringtone.stop();
            stopAlarm.setEnabled(false);

            stopLocationUpdates();
        }
    }

    public boolean checkInput() {
        return TextUtils.isEmpty(latitude.getText()) || TextUtils.isEmpty(longitude.getText());
    }

    public double round(double value) {
        BigDecimal bigDecimal = new BigDecimal(value);
        bigDecimal = bigDecimal.setScale(4,
                BigDecimal.ROUND_HALF_UP);
        return bigDecimal.doubleValue();
    }
}
