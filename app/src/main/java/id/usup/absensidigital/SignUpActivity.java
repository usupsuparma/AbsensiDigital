package id.usup.absensidigital;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.usup.absensidigital.api.ApiRequestPegawai;
import id.usup.absensidigital.model.Value;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SignUpActivity extends AppCompatActivity {
    private static final String URL = "http://10.140.140.116/absensi/";
//    private static final String URL = "http://192.168.43.239/absensi/";
    //constant
    private static final String TAG = "Sign Up Activity";
    private static final int DEVICE_ID_REQUEST = 101;

    private RadioButton radioSexButton;
    private ProgressDialog progress;


    @BindView(R.id.edittext_signup_nip)
    EditText mNIP;
    @BindView(R.id.edittext_signup_name)
    EditText mName;
    @BindView(R.id.edittext_signup_address)
    EditText mAddress;
    @BindView(R.id.edittext_signup_imei)
    EditText mImei;
    @BindView(R.id.edittext_signup_nohp)
    EditText mNoHp;
    @BindView(R.id.radiogroup_signup_gender)
    RadioGroup mGender;

    @OnClick(R.id.button_signup_save)
    void sendDataPegawai() {
        final String nameCheck = mName.getText().toString();
        final char first = nameCheck.charAt(0);
        if (mNIP.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Please Insert NIP", Toast.LENGTH_SHORT).show();
        } else if (mName.getText().toString().equals("") || first == 1) {
            Toast.makeText(getApplicationContext(), "Please Insert NAME", Toast.LENGTH_SHORT).show();
        } else if (mAddress.getText().toString().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please Insert Address", Toast.LENGTH_SHORT).show();
        } else if (first == 1) {
            Toast.makeText(getApplicationContext(), "Please Insert First Letter mush be Character", Toast.LENGTH_SHORT).show();

        } else {
            //create progress dialog
            final String loading = getString(R.string.progress_dialog_hint);
            progress = new ProgressDialog(this);
            progress.setCancelable(false);
            progress.setMessage(loading);
            progress.show();

            //call edit text
            final String nip = mNIP.getText().toString();
            final String name = mName.getText().toString();
            final String address = mAddress.getText().toString();
            final String imei = mImei.getText().toString();
            final String noHp = mNoHp.getText().toString();

            int idSelectedGender = mGender.getCheckedRadioButtonId();
            //search id radio button
            radioSexButton = findViewById(idSelectedGender);
            final String gender = radioSexButton.getText().toString();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            ApiRequestPegawai api = retrofit.create(ApiRequestPegawai.class);
            Call<Value> call = api.sendDataPegawai(nip, name, address, gender, noHp, imei);
            call.enqueue(new Callback<Value>() {
                @Override
                public void onResponse(Call<Value> call, Response<Value> response) {
                    final String value = response.body().getmValue();
                    Log.d(TAG, "value: " + value);
                    final String message = response.body().getmMessage();
                    Log.d(TAG, "message: " + message);

                    if (value.equals("1")) {
                        Log.d(TAG, "value dapat 1");
                        Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
                        progress.dismiss();
                    } else {
                        Toast.makeText(SignUpActivity.this, message, Toast.LENGTH_SHORT).show();
                        progress.dismiss();
                    }
                }

                @Override
                public void onFailure(Call<Value> call, Throwable t) {
                    progress.dismiss();
                    Toast.makeText(SignUpActivity.this, "Network Error!", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "error jaringan " + call);

                }
            });
        }


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        //method
        getId();


    }

    private void getId() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_PHONE_STATE},
                    DEVICE_ID_REQUEST);
            return;
        } else {

            final String deviceId = telephonyManager.getDeviceId();
            final String noPhone = telephonyManager.getLine1Number();
            mImei.setText(deviceId);
            mImei.setKeyListener(null);
            mNoHp.setText(noPhone);
            mNoHp.setKeyListener(null);
            Log.i(TAG, "Your imei: " + deviceId + " your phone number: " + noPhone);
        }
    }
}

