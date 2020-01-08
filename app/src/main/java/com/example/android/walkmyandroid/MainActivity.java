/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.walkmyandroid;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Location mLastLocation;
    private static final int REQUEST_LOCATION_PERMISSION=1;
    private TextView mLocationTextView;
    private FusedLocationProviderClient mFusedLocationClient;

    private final String TAG = "wut";


    private EditText txtMobile;
    private EditText txtMessage;
    private Button btnSms;
    private SmsManager smgr;

    private String Result="meh";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.SEND_SMS},
                1);



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLocationTextView = (TextView) findViewById(R.id.textview_location);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);




        setContentView(R.layout.activity_main);
        txtMobile = (EditText)findViewById(R.id.mblTxt);
        btnSms = (Button)findViewById(R.id.btnSend);
        btnSms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getLocation();

                    try {

                        smgr = SmsManager.getDefault();

                        Toast.makeText(MainActivity.this, Result, Toast.LENGTH_SHORT).show();


                            smgr.sendTextMessage(txtMobile.getText().toString(), null, Result, null, null);

                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "SMS Failed to Send, Please try again", Toast.LENGTH_SHORT).show();
                    }
                }

        });



    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(
                    new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                mLastLocation = location;




                                Geocoder geocoder = new Geocoder(MainActivity.this,
                                        Locale.getDefault());
                                Location loc = location;

                                List<Address> addresses = null;
                                String resultMessage = "";

                                try {
                                    addresses = geocoder.getFromLocation(
                                            location.getLatitude(),
                                            location.getLongitude(),
                                            1);
                                }catch (IOException ioException) {
                                    // Catch network or other I/O problems
                                    resultMessage = MainActivity.this
                                            .getString(R.string.service_not_available);
                                    Log.e(TAG, resultMessage, ioException);
                                }catch (IllegalArgumentException illegalArgumentException) {
                                    // Catch invalid latitude or longitude values
                                    resultMessage = MainActivity.this
                                            .getString(R.string.invalid_lat_long_used);
                                    Log.e(TAG, resultMessage + ". " +
                                            "Latitude = " + location.getLatitude() +
                                            ", Longitude = " +
                                            location.getLongitude(), illegalArgumentException);
                                }
                                if (addresses == null || addresses.size() == 0) {
                                    if (resultMessage.isEmpty()) {
                                        resultMessage = MainActivity.this
                                                .getString(R.string.no_address_found);
                                        Log.e(TAG, resultMessage);
                                    }
                                }else {
                                    // If an address is found, read it into resultMessage
                                    Address address = addresses.get(0);
                                    ArrayList<String> addressParts = new ArrayList<>();

                                    // Fetch the address lines using getAddressLine,
                                    // join them, and send them to the thread
                                    for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                                        addressParts.add(address.getAddressLine(i));
                                    }

                                    resultMessage = TextUtils.join("\n", addressParts);
                                }

                                  Result=resultMessage+"\nTe pup!";

                            } else {
                                Result="meh";
                            }
                        }
                    });






        }
    }






    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSION:
                // If the permission is granted, get the location,
                // otherwise, show a Toast
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    Toast.makeText(this,
                            R.string.location_permission_denied,
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


}
