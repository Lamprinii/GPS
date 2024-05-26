package com.unipi.lzerva;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    LocationManager locationManager;
    private static final int LOCATION_CODE = 123;

    float oldspeed;
    float newspeed=0;
    double acl=0; //accelaration
    TextView speed;
    TextView accelaration;
    SQLiteDatabase database;

    float lon = 151;
    float lat = -34;

    String noAccelaration;
    String noSpeed;

    long oldtime;

    LocationListener locationListener2 =
            location -> { oldspeed = newspeed;
            long time = System.currentTimeMillis()/1000;
            newspeed = location.getSpeed();
            speed.setText(String.valueOf(newspeed));
                acl=(newspeed-oldspeed)/(time - oldtime);
                oldtime = time;
                accelaration.setText(String.valueOf(acl));
                if (acl == -3.33)
                {
                    database.execSQL("Insert or ignore into EmergancyBreak values(?,?,?,?)",new String[]{String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()),String.valueOf(acl) ,String.valueOf(System.currentTimeMillis())});
                    showMessage("Emergency breaking", "Drive Safely");
                }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==LOCATION_CODE){
            if (ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        0, 0, locationListener2);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        speed = findViewById(R.id.speed);
        accelaration = findViewById(R.id.accelaration);

        database = openOrCreateDatabase("myDB.db",MODE_PRIVATE,null);
        database.execSQL("Create table if not exists EmergancyBreak(" +
                "Latitude NUMBER," +
                "Longitude NUMBER," +
                "Accelaration NUMBER," +
                "timestamp TEXT PRIMARY KEY)");
        database.execSQL("Insert or ignore into EmergancyBreak values(-34, 151, 154, 'test')");



        noAccelaration = getString(R.string.no_accelaration);
        noSpeed = getString(R.string.no_speed);

    }

    public void openGPS(View view) {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},LOCATION_CODE);
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                0, 0, locationListener2);
    }

    public void gpsoff(View view) {
        accelaration.setText(noAccelaration);
        speed.setText(noSpeed);
        locationManager.removeUpdates(locationListener2);
    }

    public void maps(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }
    private void showMessage(String title, String message){
        new AlertDialog.Builder(this)
                .setCancelable(true)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}