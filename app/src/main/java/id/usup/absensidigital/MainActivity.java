package id.usup.absensidigital;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    //constanta
    private static final String TAG="MainActivity";
    private static final int DEVICE_ID_REQUEST =01;
    private static final int PERM_COARSE_LOCATION=200;
    private static final long UPDATE_INTERVAL = 2 * 1000;  /* 10 secs */
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 999;
    // Location updates intervals in
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters


    private TextClock mTextClock;
    private TextView mImei;
    private TextView mNoPhone;

    //telpone manager
    private TelephonyManager telephonyManager;
    private TextView mLantitude;
    private TextView mLongitude;
    private Location mLastLocation;

    // google api client
    private GoogleApiClient mGoogleApiClient;

    //location request
    LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextClock = findViewById(R.id.textClock);

        //textview
        mImei = findViewById(R.id.textView_imei);
        mNoPhone = findViewById(R.id.textView_nohanphone);
        mLantitude = findViewById(R.id.textView_lantitude);
        mLongitude = findViewById(R.id.textView_longitude);

        //call method
        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();

        }

        getDeviceId();



    }

    public void getDeviceId() {
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    DEVICE_ID_REQUEST);
            return;
        }else {

            final String deviceId = telephonyManager.getDeviceId();
            final String noPhone = telephonyManager.getLine1Number();
            mImei.setText(deviceId);
            mNoPhone.setText(noPhone);
            Log.i(TAG,"Your imei: "+deviceId+" your phone number: "+noPhone);
        }
    }


    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, String.format("onRequestPermissionsResult permsRequestCode=%s permissions=%s grantResults=%s",
                permsRequestCode, Arrays.asList(permissions), Arrays.asList(grantResults)));

        switch (permsRequestCode){
            case PERM_COARSE_LOCATION:
                boolean locationAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (locationAccepted) {
                    Log.i(TAG, "ACCESS_COARSE_LOCATION permission granted");
                    realGetLocation();
                } else {
                    Log.w(TAG, "ACCESS_COARSE_LOCATION permission declined");
                    AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                    alertDialog.setTitle("Insufficient Permission");
                    alertDialog.setMessage("Please allow location access");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
                break;
        }
    }

    private void realGetLocation() {
// TODO: lakukan pengambilan data location: mFusedLocationClient.getLastLocation()...
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERM_COARSE_LOCATION);
            // The callback method gets the result of the request.

        }else {

            mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);
            Log.d(TAG,"ini REal location");
            if (mLastLocation != null){
                double latitude = mLastLocation.getLatitude();
                double longitude = mLastLocation.getLongitude();

                mLantitude.setText(String.valueOf(latitude));
                mLongitude.setText(String.valueOf(longitude));
            }
        }
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        Log.i(TAG, "Api client: " + mGoogleApiClient);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        realGetLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }
    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FATEST_INTERVAL);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        Log.i(TAG, "Reuslt code: " + resultCode);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;

    }
}
