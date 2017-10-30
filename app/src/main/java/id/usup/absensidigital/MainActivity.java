package id.usup.absensidigital;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.TextClock;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    //constanta
    private static final String TAG="MainActivity";
    private static final int DEVICE_ID_REQUEST =01;
    private static final int PERM_COARSE_LOCATION=200;


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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextClock = (TextClock) findViewById(R.id.textClock);

        //textview
        mImei = (TextView) findViewById(R.id.textView_imei);
        mNoPhone = (TextView) findViewById(R.id.textView_nohanphone);
        mLantitude = (TextView)findViewById(R.id.textView_lantitude);
        mLongitude = (TextView)findViewById(R.id.textView_longitude);

        //call method
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


            return;
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
}
