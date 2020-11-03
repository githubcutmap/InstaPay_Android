package gramtarang.mint.loans;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import gramtarang.mint.R;
import gramtarang.mint.aeps.activity_Aeps_HomeScreen;
import gramtarang.mint.agent_login.activity_Login;
import gramtarang.mint.api.MobileSMSAPI;
import gramtarang.mint.utils.CaptureResponse;
import gramtarang.mint.utils.CheckNetwork;
import gramtarang.mint.utils.ConnectionClass;
import gramtarang.mint.utils.DialogActivity;
import gramtarang.mint.utils.LoadingDialog;
import gramtarang.mint.utils.LogOutTimer;
import gramtarang.mint.utils.SQLQueries;
import gramtarang.mint.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;



import java.util.ArrayList;

public class LoanActivity_PrimaryScreen extends AppCompatActivity implements LogOutTimer.LogOutListener {
    private String TAG="LOAN_primary";
    @Override
    protected void onStart() {
        super.onStart();
        LogOutTimer.startLogoutTimer(this, this);
        Log.e(TAG, "OnStart () &&& Starting timer");
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        LogOutTimer.startLogoutTimer(this, this);
        Log.e(TAG, "User interacting with screen");
    }


    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG, "onPause()");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.e(TAG, "onResume()");
    }

    /**
     * Performing idle time logout
     */
    @Override
    public void doLogout() {
        // Toast.makeText(getApplicationContext(),"Session Expired",Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(getApplicationContext(), activity_Login.class);
        startActivity(intent);
    }
EditText et_dob,et_proname,et_beneficiaryname,et_aadhaar,et_pan,et_phn,et_enteredotp,et_accountno,et_unitaddress,et_resaddress,et_resvpo,et_resdist,et_respin;
Button verify_otp;
ImageView backbtn;
TextView send_otp;int i;
int isSelectSendOTP = 0;
    OkHttpClient client;
boolean isValidAadhaar,isValidEmail,isValidPhone,isValidOTP;
Spinner sp_gender,sp_unitname,sp_lineactivity;
boolean isValidDOB;
    AutoCompleteTextView bank_autofill;
String response_String,beneficiaryId2,branchcode,trim_branch,beneficiarydob,pro_name,accountno,unitname,unitaddress,resaddress,vpo,distt,pin,lineactivity,loan_id_generation1,load_id_generation2,selected_bank_id,
        businessexistance,selected_apgvb_branch,selected_bank_name,generated_otp,beneficiary_name,beneficiary_aadhaar,beneficiary_pan,beneficiary_phone,
        beneficiary_uniqueId,loan_type,gender,entered_otp;
    ArrayList<String> apgvbBranch_arr = new ArrayList<String>();
    ArrayList<String> apgvbBranchID_arr = new ArrayList<String>();
SQLQueries query=new SQLQueries();
    public static final String mypreference = "Loanpreferences";SharedPreferences preferences;
    boolean doubleBackToExitPressedOnce = false;
    private int selected_index;

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
                Intent intent = new Intent(getApplicationContext(), activity_Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan__primary_screen);
        et_beneficiaryname=findViewById(R.id.beneficiary_name);
        et_aadhaar=findViewById(R.id.aadhaar_number);
        et_pan = findViewById(R.id.pan_number);
        et_phn=findViewById(R.id.phone_number);
        send_otp=findViewById(R.id.sendOtp);
        et_dob=findViewById(R.id.dob);
        verify_otp=findViewById(R.id.verifyOtp);
        sp_gender=findViewById(R.id.gender);
        et_enteredotp=findViewById(R.id.enteredotp);
        bank_autofill=findViewById(R.id.near_bank_auto);
        backbtn = findViewById(R.id.backimg);
        et_accountno=findViewById(R.id.accno);
        sp_unitname=findViewById(R.id.unitname);
        et_unitaddress=findViewById(R.id.unitaddress);
        et_resaddress=findViewById(R.id.resaddress);
        et_resvpo = findViewById(R.id.et_add_villagepo);
        et_resdist = findViewById(R.id.et_add_district);
        et_respin = findViewById(R.id.et_add_pincode);
        sp_lineactivity=findViewById(R.id.occupation);
        et_proname=findViewById(R.id.pro_name);
        et_beneficiaryname.addTextChangedListener(LoanPrimaryTextWatcher);
        et_phn.addTextChangedListener(LoanPrimaryTextWatcher);
        et_pan.addTextChangedListener(LoanPrimaryTextWatcher);
      //  et_email.addTextChangedListener(LoanPrimaryTextWatcher);
        et_enteredotp.addTextChangedListener(LoanPrimaryTextWatcher);
        et_accountno.addTextChangedListener(LoanPrimaryTextWatcher);
        et_unitaddress.addTextChangedListener(LoanPrimaryTextWatcher);
        et_resaddress.addTextChangedListener(LoanPrimaryTextWatcher);
        et_resvpo.addTextChangedListener(LoanPrimaryTextWatcher);
        et_resdist.addTextChangedListener(LoanPrimaryTextWatcher);
        et_respin.addTextChangedListener(LoanPrimaryTextWatcher);

        et_dob.addTextChangedListener(LoanPrimaryTextWatcher);


    new apiCall_getloanbanks().execute();
    Utils utils=new Utils();
    //SQLQueries getbanknames=new SQLQueries();
    //apgvbBranch_arr=getbanknames.getApgvbBankBranch();
    utils.AutoCompleteTV_ApgvbBranch(LoanActivity_PrimaryScreen.this,bank_autofill,apgvbBranch_arr,"LoanActivity_Primary");
//back button
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoanActivity_MainScreen.class);
                startActivity(intent);
            }
        });
        send_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSelectSendOTP = 1;
                beneficiary_name=et_beneficiaryname.getText().toString().trim();
                beneficiary_aadhaar=et_aadhaar.getText().toString().trim();
                accountno=et_accountno.getText().toString().trim();
                beneficiarydob=et_dob.getText().toString().trim();
                beneficiary_pan = et_pan.getText().toString().trim();
                pro_name=et_proname.getText().toString().trim();
                beneficiary_phone=et_phn.getText().toString().trim();
                gender=sp_gender.getSelectedItem().toString().trim();
                unitname=sp_unitname.getSelectedItem().toString().trim();
                unitaddress=et_unitaddress.getText().toString().trim();
                vpo = et_resvpo.getText().toString().trim();
                distt = et_resdist.getText().toString().trim();
                pin = et_respin.getText().toString().trim();
                resaddress=et_resaddress.getText().toString().trim();
                lineactivity=sp_lineactivity.getSelectedItem().toString().trim();

                selected_index = utils.AutoCompleteTV_ApgvbBranch(LoanActivity_PrimaryScreen.this,bank_autofill,apgvbBranch_arr,"LoanActivity_Primary");
                Log.d("TAG", "Selected Index:" + selected_index);
                selected_apgvb_branch = apgvbBranch_arr.get(selected_index);
                Log.d("TAG", "Selected Branch:" + selected_apgvb_branch);

                //selected_apgvb_branch= utils.AutoCompleteTV_ApgvbBranch(LoanActivity_PrimaryScreen.this,bank_autofill,apgvbBranch_arr,apgvbBranchID_arr,"LoanActivity_Primary");
                selected_apgvb_branch=selected_apgvb_branch.replaceAll("\\s", "");
                isValidPhone=utils.isValidPhone(beneficiary_phone);
               isValidAadhaar=utils.isValidAadhaar(beneficiary_aadhaar);
              // isValidEmail=utils.isValidEmail(beneficiary_email);
                isValidDOB=utils.isValidDOB(beneficiarydob);

               // branchcode=query.getBankId(selected_apgvb_branch);
                branchcode = apgvbBranchID_arr.get(selected_index);
                Log.d("Branchcode","branch-code: "+branchcode);
                generated_otp = utils.getOTPString();
                try {
                    trim_branch = selected_apgvb_branch.trim().substring(0, 4);
                }catch (NullPointerException e){
                    bank_autofill.setError("Select Branch");
                }
                /*if(loan_type.equals("Home Loan")){
                    loan_id_generation1="HOME";
                }
               if(loan_type.equals("Mudra Loan")){
                    loan_id_generation1="MUDR";
                }
                if(loan_type.equals("PM SVANidhi Loan")){
                    loan_id_generation1="PMSV";
                }*/
                String flagid=utils.getOTPString();
                beneficiary_uniqueId="apgvb"+"/"+"mudra"+"/"+trim_branch.toLowerCase()+"/"+flagid;
                beneficiaryId2="APGVB"+"/"+branchcode+"/"+flagid;
                Log.d("TAG","ID is"+beneficiary_uniqueId);
                if(!gender.equals("Gender") &&isValidDOB &&pro_name!=null && beneficiary_name!=null && selected_apgvb_branch!=null && beneficiary_phone.length()==10 &&isValidPhone && isValidAadhaar && accountno.length()==11 &&
                        !unitname.equals("Unit Name") && unitaddress!=null && resaddress!=null && vpo!=null && distt!=null && pin!=null && !lineactivity.equals("Line of Activity")) {
                    SQLQueries BenfDetailsforOTP=new SQLQueries();
                    Log.d("OTP","generated otp"+generated_otp);
                    BenfDetailsforOTP.insertbeneficiaryforotp(i,beneficiary_name,beneficiary_phone,generated_otp);
                    MobileSMSAPI sms = new MobileSMSAPI();
                    sms.sendloanverification(beneficiary_name,beneficiary_phone,generated_otp);
                  //  sms.sendSms1(generated_otp, beneficiary_phone, beneficiary_name);
                    timer();
                }
                if (beneficiary_phone == null){
                    et_phn.setError("Enter Phone Number");
                }
                if(gender.equals("Gender")){
                    sp_gender.getPrompt();
                }
                if(unitname.equals("Unit Name")){
                    sp_unitname.getPrompt();
                }
                if(lineactivity.equals("Line of Activity")){
                    sp_lineactivity.getPrompt();
                }
                if (beneficiarydob == null){
                    et_dob.setError("Enter Date of Birth");
                }
                if (pro_name == null){
                    et_proname.setError("Enter Propreitor Name");
                }
                if(beneficiary_name == null){
                    et_beneficiaryname.setError("Enter Beneficiary Name");
                }
                if(selected_apgvb_branch==null){
                    bank_autofill.setError("Enter Bank Name");
                }
                /*if(beneficiary_pan == null){
                    et_pan.setError("Enter PAN");
                }*/
                if(beneficiary_pan.length()!=0){
                if(beneficiary_pan.length() != 10){
                    et_pan.setError("Enter Valid PAN");
                }}
if(beneficiary_aadhaar.length()!=0){
                if(!isValidAadhaar){
                    et_aadhaar.setError("Enter Valid Aadhaar Number");
                }

}else{
    et_aadhaar.setError("Enter Aadhaar Number");
}
if(gender.equals("Gender")){
    Toast.makeText(LoanActivity_PrimaryScreen.this,"Select Gender",Toast.LENGTH_SHORT).show();
}
if(accountno.length()!=0){
                if(accountno.length()!=11){
                    et_accountno.setError("Enter Valid Account Number");
                }
}else{
    et_accountno.setError("Enter Account Number");
}
if(beneficiary_phone.length()!=0){
                if(!isValidPhone){
                    et_phn.setError("Enter Valid Phone Number");
                }}else {
    et_phn.setError("Enter Phone Number");
}
                if(unitname.equals("Unit Name")){
                    Toast.makeText(LoanActivity_PrimaryScreen.this,"Select Unit",Toast.LENGTH_SHORT).show();
                }
                if(lineactivity.equals("Line of Activity")){
                    Toast.makeText(LoanActivity_PrimaryScreen.this,"Select Line of Activity",Toast.LENGTH_SHORT).show();
                }
                if(unitaddress.length()==0){
                    et_unitaddress.setError("Enter Unit Address");
                }
                if(vpo.length()==0){
                    et_resvpo.setError("Enter Residential Address VPO");
                }
                if(distt.length()==0){
                    et_resdist.setError("Enter Residential Address Distt.");
                }
                if(pin.length()==0){
                    et_respin.setError("Enter Residential Address PINCODE");
                }
                if(resaddress.length()==0){
                    et_resaddress.setError("Enter Residential Address");
                }
                if(beneficiarydob.length()!=0){
                if(!isValidDOB){
                   et_dob.setError("Enter Valid Date of Birth");
                }}
                else{
                    et_dob.setError("Enter Date of Birth");
                }
                if(pro_name.length()==0){
                    et_proname.setError("Enter Proprietor Name");
                }
                if(beneficiary_name.length()==0){
                    et_beneficiaryname.setError("Enter Applicant Name");
                }

              /*  else{
                    LoanActivity_PrimaryScreen.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoanActivity_PrimaryScreen.this,"Enter Valid Details.",Toast.LENGTH_SHORT).show();
                        }
                    });

                }*/
            }
        });

        verify_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               entered_otp=et_enteredotp.getText().toString().trim();
               if(entered_otp.equals(generated_otp)){
                   preferences = getSharedPreferences(mypreference,
                           Context.MODE_PRIVATE);
                   SharedPreferences.Editor editor =preferences.edit();
                   editor.putString("BeneficiaryUniqueId",beneficiary_uniqueId.toLowerCase());
                   editor.putString("BeneficiaryUniqueId2",beneficiaryId2);
                   editor.putString("BeneficiaryDOB",beneficiarydob);
                   editor.putString("BeneficiaryProName",pro_name);
                   editor.putString("BeneficiaryName", beneficiary_name);
                   editor.putString("BeneficiaryAccNumber",accountno);
                   editor.putString("BeneficiaryPhone", beneficiary_phone);
                   editor.putString("BeneficiaryAadhaar", beneficiary_aadhaar);
                   editor.putString("BeneficiaryPan",beneficiary_pan);
                   editor.putString("BeneficiaryBank", selected_apgvb_branch);
                   editor.putString("BeneficiaryGender", gender);
                   editor.putString("LoanType",loan_type);
                   editor.putString("UnitName",unitname);
                   editor.putString("UnitAddress",unitaddress);
                   editor.putString("ResidentialAddress",resaddress+" "+ vpo+" "+distt+" "+pin);
                   editor.putString("LineOfActivity",lineactivity);

                   editor.commit();
                   Intent intent=new Intent(LoanActivity_PrimaryScreen.this,LoanActivity_SecondaryScreen.class);
                   startActivity(intent);
               }
               else{
                   et_enteredotp.setError("Invaid OTP");
               }

               if (entered_otp.length() < 6){
                   et_enteredotp.setError("Invalid OTP");
               }

               if (isSelectSendOTP == 0){
                   et_enteredotp.setError("Please click on Send OTP");
               }
            }
        });

    }

    public TextWatcher LoanPrimaryTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @SuppressLint("NewApi")
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            beneficiary_name=et_beneficiaryname.getText().toString().trim();
            beneficiary_phone=et_phn.getText().toString().trim();
            pro_name=et_proname.getText().toString().trim();
            beneficiarydob=et_dob.getText().toString().trim();

            entered_otp=et_enteredotp.getText().toString().trim();
            accountno=et_accountno.getText().toString().trim();

            unitaddress=et_unitaddress.getText().toString().trim();
            resaddress=et_resaddress.getText().toString().trim();

            if(beneficiary_name!=null &&beneficiarydob!=null &&pro_name!=null  &&beneficiary_name!=null  && beneficiary_phone.length() == 10 &&
                    entered_otp.length() == 6 && accountno.length() == 11 && unitaddress != null && resaddress != null){

                verify_otp.setEnabled(true);
                verify_otp.setBackground(getDrawable(R.drawable.button));



            }

        }

        @Override
        public void afterTextChanged(Editable editable) {


        }
    };

    class apiCall_getloanbanks extends AsyncTask<Request, Void, String> {

        @Override
        protected String doInBackground(Request... requests) {
            client=new OkHttpClient();
            Request request = new Request.Builder()
                    .url("http://bankmgr.gramtarang.org:8081/mint/loans/getBanks")
                    //.addHeader("Accept", "/")
                    .get()
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override

                //of the api calling got failed then it will go for onFailure,inside this we have added one alertDialog
                public void onFailure(Call call, IOException e) {
                    // Toast.makeText(activity_Login.this,"Agent not registered.\nPlease Contact Administrator"+androidId,Toast.LENGTH_SHORT).show();

                }

                //if API call got success then it will go for this onResponse also where we are collection
                //the response as well
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    assert response.body() != null;
                    response_String = response.body().string();
                    if (response_String != null) {
                        JSONArray jsonResponse = null;
                        //Log.d("TAG", "Response:" + response_String);
                        try {
                            ArrayList<String> array1 = new ArrayList<String>();
                            ArrayList<String> array2 = new ArrayList<String>();
                            jsonResponse = new JSONArray(response_String);
                            for(int j = 0; j < jsonResponse.length(); j++){
                                JSONObject jresponse = jsonResponse.getJSONObject(j);
                                apgvbBranch_arr.add(jresponse.getString("banklocation"));
                                apgvbBranchID_arr.add(jresponse.getString("branchcode"));
                            }
                            //Log.d("TAG", "Response Banks:" + array1+ array2);
                            Log.d("TAG", "Response Banks:" + apgvbBranch_arr+ apgvbBranchID_arr);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                }

            });


            return null;
        }

    }

    public void timer(){
        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                send_otp.setEnabled(false);
                send_otp.setText("Re-send OTP in: " + millisUntilFinished / 1000+" Seconds");
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            public void onFinish() {
                send_otp.setText("Re-send");
                send_otp.setEnabled(true);


            }
        }.start();
    }
}