package gramtarang.mint.utils;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import gramtarang.mint.R;
import gramtarang.mint.aeps.activity_Aeps_HomeScreen;
import gramtarang.mint.agent_login.Dashboard;
import gramtarang.mint.agent_login.activity_Login;
import gramtarang.mint.api.MobileSMSAPI;
import gramtarang.mint.loans.LoanActuivity_ViewApplication;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginVerification extends AppCompatActivity {
    OkHttpClient client;
    EditText edit_otp;
    TextView timer,otp_type;
    Button submit,resend_btn;
    String bankmitra_id,jsonString,entered_otp,androidId,login_status,latitude,longitude,username,agentId,timestamp,agentphn,agentemail,agentname,generated_pin;
    SharedPreferences preferences;boolean isValidOtp;String verification_type;
    public static final String mypreference = "mypref";
    String TAG = "LoginVerification";
    int i;
    Utils utils=new Utils();
    int counter = 0;
    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Click again to exit.", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
                Intent intent=new Intent(getApplicationContext(), activity_Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }, 2000);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_verification);
        client = new OkHttpClient();
        edit_otp=findViewById(R.id.user_entered_otp);
        submit=findViewById(R.id.verify);
        timer = findViewById(R.id.timer);
        resend_btn = findViewById(R.id.resend_btn);
        otp_type = findViewById(R.id.type);
        preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        androidId= Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        latitude=preferences.getString("Latitude","No name defined");
        longitude=preferences.getString("Longitude","No name defined");
        bankmitra_id=preferences.getString("BankMitraId","No name defined");
        agentphn=preferences.getString("AgentPhone","No name defined");;
        agentemail=preferences.getString("AgentEmail","No name defined");
        agentname=preferences.getString("AgentName","No name defined");
        verification_type=preferences.getString("VerificationMethod","No name defined");
        generated_pin=preferences.getString("LoginOTP","No name defined");
Log.d("TAG","WQQQ"+verification_type+generated_pin+agentphn+agentemail);
        if (verification_type.equals("OTP")) {
            otp_type.setText(agentphn);
        } else {
            otp_type.setText(agentemail);
        }
        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        timestamp = s.format(new Date());

        timer();
        new OTPReceiver().setEditText_otp(edit_otp);
        edit_otp.addTextChangedListener(otpTextWatcher);
        resend_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                counter++;
                if(counter<=3){
                    resend_btn.setEnabled(false);
                    resend_btn.setBackground(getDrawable(R.color.silver_colour));
                    generated_pin=utils.getOTPString();
                   // SQLQueries update_otp=new SQLQueries();
                   // update_otp.updateOTP(generated_pin,androidId);
                  //  Log.d(TAG,"Agent email is:"+agentemail);
                    MobileSMSAPI sms=new MobileSMSAPI();
                    Log.d(TAG,"Agent Phn is:"+agentphn);
                    if(verification_type.equals("OTP")){
                        sms.sendSms1(generated_pin,agentphn,agentname);
                    }
                    else{
                        utils.sendOTPMail(generated_pin,agentemail,agentname);
                    }


                    timer();
                }
                else{
                    timer.setText("Please Login After Some time");
                    resend_btn.setEnabled(false);
                    resend_btn.setBackground(getDrawable(R.drawable.disabled_button));

                }

            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit.setEnabled(false);
                entered_otp=edit_otp.getText().toString().trim();
                //utils.getprogressDialog(LoginVerification.this,"Please Wait","Verification of OTP in Progress..");
if(entered_otp.equals(generated_pin)){
    isValidOtp=true;
}
else{
    isValidOtp=false;
}

                if(isValidOtp){
                    login_status="Success";
                    new apiCall_insertloginlog().execute() ;
                    Intent intent=new Intent(LoginVerification.this, Dashboard.class);
                    startActivity(intent);
                }
                else{
                    login_status="Failed";
                    new apiCall_insertloginlog().execute() ;
                    Toast.makeText(LoginVerification.this,"Incorrect OTP",Toast.LENGTH_SHORT);
                    edit_otp.setError("Enter Valid OTP");
//                    utils.dismissProgressDialog();
                    submit.setEnabled(true);
                }
            }
        });

        /*requestPermissions();
        new OTPReceiver().setEditText_otp(edit_otp);




        Log.d("TAG","OTP VERIFICATION:"+verification_type+generated_pin);
        if(agentphn.equals("No name defined")){
            Intent intent = new Intent(this, LoginVerification.class);
            this.startActivity(intent);
            this.finishAffinity();
        }
        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        timestamp = s.format(new Date());
        SQLQueries query=new SQLQueries();
        timer();

        if (verification_type=="OTP") {
            otp_type.setText(agentphn);
        } else {
            otp_type.setText(agentemail);
        }


        edit_otp.addTextChangedListener(otpTextWatcher);




        resend_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                counter++;
                if(counter<=3){
                    resend_btn.setEnabled(false);
                    resend_btn.setBackground(getDrawable(R.color.silver_colour));

                    generated_pin=utils.getOTPString();
                    SQLQueries update_otp=new SQLQueries();
                    update_otp.updateOTP(generated_pin,androidId);
                    Log.d(TAG,"Agent email is:"+agentemail);
                    MobileSMSAPI sms=new MobileSMSAPI();
                    Log.d(TAG,"Agent Phn is:"+agentphn);
                    if(verification_type.equals("OTP")){
                        sms.sendSms1(generated_pin,agentphn,agentname);
                    }
                    else{
                        utils.sendOTPMail(generated_pin,agentemail,agentname);
                    }


                    timer();
                }
                else{
                    timer.setText("Please Login After Some time");
                    resend_btn.setEnabled(false);
                    resend_btn.setBackground(getDrawable(R.drawable.disabled_button));

                }

            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit.setEnabled(false);
                entered_otp=edit_otp.getText().toString().trim();
                //utils.getprogressDialog(LoginVerification.this,"Please Wait","Verification of OTP in Progress..");

                isValidOtp= query.validateOTP(entered_otp,androidId);

                if(isValidOtp){
                    login_status="Success";
                    insertlog();
                    Intent intent=new Intent(LoginVerification.this, activity_Aeps_HomeScreen.class);
                    startActivity(intent);
                }
                else{
                    login_status="Failed";
                    insertlog();
                    Toast.makeText(LoginVerification.this,"Incorrect OTP",Toast.LENGTH_SHORT);
                    edit_otp.setError("Enter Valid OTP");
//                    utils.dismissProgressDialog();
                    submit.setEnabled(true);
                }
            }
        });*/



    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(LoginVerification.this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(LoginVerification.this,new String[]{
                    Manifest.permission.RECEIVE_SMS
            },100);
        }


    }

    public TextWatcher otpTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @SuppressLint("NewApi")
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            entered_otp=edit_otp.getText().toString().trim();
            if(entered_otp.length() == 6){
                submit.setEnabled(true);
                submit.setBackground(getDrawable(R.drawable.button));


            }

        }

        @Override
        public void afterTextChanged(Editable editable) {


        }
    };


    public void insertlog(){
        SQLQueries insert_login_logs=new SQLQueries();
        insert_login_logs.insert_login_log(i,agentId,username,androidId,latitude,longitude,timestamp,login_status);



    }

    public void timer(){
        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText("Resend OTP in: " + millisUntilFinished / 1000+" Seconds");
            }


            @SuppressLint("NewApi")
            public void onFinish() {
                timer.setText("");
                resend_btn.setEnabled(true);
                resend_btn.setBackground(getDrawable(R.drawable.button));

            }
        }.start();
    }
    class apiCall_insertloginlog extends AsyncTask<Request, Void, String> {
        @SuppressLint("HardwareIds")
        @Override
        protected String doInBackground(Request... requests) {
            JSONObject jsonObject = new JSONObject();
            try {
                SharedPreferences preferences;
                String mypreference = "mypref";
                preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
                androidId= preferences.getString("AndroidId","No name defined");
                latitude=preferences.getString("Latitude","No name defined");
                longitude=preferences.getString("Longitude","No name defined");
                agentId=preferences.getString("AgentId","No name defined");
                agentphn=preferences.getString("AgentPhone","No name defined");;
                agentemail=preferences.getString("AgentEmail","No name defined");
                agentname=preferences.getString("AgentName","No name defined");
                verification_type=preferences.getString("VerificationMethod","No name defined");
                generated_pin=preferences.getString("LoginOTP","No name defined");

                Log.d("RAF","Message"+agentId+androidId+latitude+longitude+login_status);
                jsonObject.put("s_id", null);
                jsonObject.put("agentid",agentId);
                jsonObject.put("androidid", androidId);
                jsonObject.put("latitude",latitude.toString());
                jsonObject.put("longitude",longitude);
                jsonObject.put("timestamp",null);
                jsonObject.put("loginstatus",login_status);
                jsonString = jsonObject.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            MediaType JSON = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(JSON, jsonString);
            Request request = new Request.Builder()
                    .url("http://bankmgr.gramtarang.org:8081/mint/im/loginlogs")
                    .addHeader("Accept", "*/*")
                    .post(body)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override

                //of the api calling got failed then it will go for onFailure,inside this we have added one alertDialog
                public void onFailure(Call call, IOException e) {
                }

                //if API call got success then it will go for this onResponse also where we are collection
                //the response as well
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    assert response.body() != null;
                }

            });


            return null;
        }

    }
}