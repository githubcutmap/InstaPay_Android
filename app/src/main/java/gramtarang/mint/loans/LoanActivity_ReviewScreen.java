package gramtarang.mint.loans;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Button;
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


import java.text.SimpleDateFormat;
import java.util.Date;

public class LoanActivity_ReviewScreen extends AppCompatActivity implements LogOutTimer.LogOutListener {
    private String TAG="LOAN_review";
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
    SharedPreferences preferences,preferences2;
    int i=0;
    OkHttpClient client;

    public static final String mypreference = "Loanpreferences";

    public static final String mypreference2 = "mypref";


    TextView bId,
            bIddetails,
            bName,
            bphone,
            bBank,
            bGender,
            bAddress,
            bLoanAmount,
            bOccupation,
            bLoanPurpose;


    String beneficiaryUniqueId,beneficiary_unitName,beneficiary_unitAddress,beneficiaryotherLoans_2,
            beneficiaryIddetails,beneficiary_adult,beneficiary_child,beneficiaryincome,
            beneficiaryexpenses,beneficiaryotherexp,beneficiarynetsurplus,beneficiaryEducation,
            beneficiarysustenance,beneficiarybusinessperiod,beneficiarycategory,
            agentName,agentId,agent_contact,beneficiaryotherLoans,
            beneficiaryName,beneficiaryAccNumber,beneficiaryPan,beneficiary_father_husband,
            beneficiaryPhone,beneficiaryProName,beneficiaryDOB,
            beneficiaryAadhaar,latitude,longitude,
            beneficiaryBank,
            beneficiaryGender,
            beneficiaryloanType,
            beneficiaryAddressLane,
            beneficiaryLoanAmount,
            beneficiaryOccupation,
            beneficiaryLoanPurpose,
            beneficiaryTenure,
            beneficiaryRepaymentPeriod,beneficiaryOwnProp,
            timestamp,regionalOffice,
            response_String,data_json;

    Button cancel,confirm;
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
                Intent intent = new Intent(getApplicationContext(), activity_Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan__review_screen);
        preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        preferences2 = getSharedPreferences(mypreference2, Context.MODE_PRIVATE);
        agentName = preferences2.getString("AgentName", " Null");
        latitude = preferences2.getString("Latitude", "No name defined");
        longitude = preferences2.getString("Longitude", "No name defined");
        agentId = preferences2.getString("BankMitraId", " Null");
        agent_contact = preferences2.getString("AgentPhone", " Null");
        beneficiaryUniqueId = preferences.getString("BeneficiaryUniqueId2", "Null");
        beneficiaryName = preferences.getString("BeneficiaryName", "Null");
        beneficiaryAccNumber = preferences.getString("BeneficiaryAccNumber", "Null");
        beneficiaryPhone = preferences.getString("BeneficiaryPhone", "Null");
        beneficiaryAadhaar = preferences.getString("BeneficiaryAadhaar", "Null");
        beneficiaryBank = preferences.getString("BeneficiaryBank", "Null");
        beneficiaryPan = preferences.getString("BeneficiaryPan", "Null");
        beneficiaryGender = preferences.getString("BeneficiaryGender", "Null");
        beneficiaryloanType = preferences.getString("LoanType", "Null");
        beneficiary_unitName = preferences.getString("UnitName", "Null");
        beneficiary_unitAddress = preferences.getString("UnitAddress", "Null");
        beneficiaryAddressLane = preferences.getString("ResidentialAddress", "Null");
        beneficiaryLoanAmount = preferences.getString("LoanAmount", "Null");
        beneficiary_father_husband = preferences.getString("FatherHusbandName", "Null");
        beneficiaryOccupation = preferences.getString("LineOfActivity", "Null");
        beneficiaryLoanPurpose = preferences.getString("LoanPurpose", "Null");
        beneficiary_adult = preferences.getString("BeneficiaryAdult", "Null");
        beneficiary_child = preferences.getString("BeneficiaryChild", "Null");
        beneficiaryEducation = preferences.getString("BeneficiaryEducation", "Null");
        beneficiaryexpenses = preferences.getString("BeneficiaryExpenses", "Null");
        beneficiaryotherexp = preferences.getString("BeneficiaryOtherExpenses", "Null");
        beneficiaryincome = preferences.getString("BeneficiaryIncome", "Null");
        beneficiarysustenance = preferences.getString("BeneficiarySustenance", "Null");
        beneficiarynetsurplus = preferences.getString("BeneficiaryNetSurplus", "Null");
        beneficiarybusinessperiod = preferences.getString("BeneficiaryBusinessPeriod", "Null");
        beneficiarycategory = preferences.getString("BeneficiaryCategory", "Null");
        beneficiaryOwnProp = preferences.getString("OwnProperty", "Null");
        beneficiaryIddetails = preferences.getString("BeneficiaryIdDetails", "Null");
        beneficiaryDOB = preferences.getString("BeneficiaryDOB", "Null");
        beneficiaryProName = preferences.getString("BeneficiaryProName", "Null");
        beneficiaryotherLoans = preferences.getString("BeneficiaryLoanDetailsAPGVB", "Null");
        beneficiaryotherLoans_2 = preferences.getString("BeneficiaryLoanDetailsOthers", "Null");
        regionalOffice = preferences.getString("RegionalOffice", "Null");
        beneficiaryTenure = "24";
        beneficiaryIddetails = preferences.getString("BeneficiaryIdDetails", "Null");

        bId = findViewById(R.id.beneficiary_id);
        bName = findViewById(R.id.beneficiary_name);
        bphone = findViewById(R.id.beneficiary_phone);
        bBank = findViewById(R.id.beneficiary_bank);
        bGender = findViewById(R.id.beneficiary_gender);
        bAddress = findViewById(R.id.beneficiary_address);
        bLoanAmount = findViewById(R.id.beneficiary_loan_amount);
        bOccupation = findViewById(R.id.beneficiary_occupation);
        bLoanPurpose = findViewById(R.id.beneficiary_loan_purpose);


        cancel = findViewById(R.id.cancel);
        confirm = findViewById(R.id.confirm);

        bId.setText(beneficiaryUniqueId);
        bName.setText(beneficiaryName);
        bphone.setText(beneficiaryPhone);

        bBank.setText(beneficiaryBank);
        bGender.setText(beneficiaryGender);

        bLoanAmount.setText(beneficiaryLoanAmount);
        bOccupation.setText(beneficiaryOccupation);
        bLoanPurpose.setText(beneficiaryLoanPurpose);


        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        timestamp = s.format(new Date());
        client = new OkHttpClient();


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                new apiCall_loanRegister().execute();
                Log.d("TAG", "after apicall try");
                Log.d("TAG sharedpref", beneficiaryUniqueId + agentName + agentId + agent_contact + beneficiaryName +
                        beneficiaryPhone + beneficiaryAccNumber + beneficiaryOccupation + beneficiary_father_husband + beneficiaryDOB +
                        beneficiaryAadhaar + beneficiaryPan + beneficiaryAddressLane + beneficiary_unitName + beneficiary_unitAddress +
                        beneficiaryProName + beneficiarybusinessperiod + beneficiaryEducation + beneficiarycategory + beneficiary_adult +
                        beneficiary_child + beneficiarysustenance + beneficiaryLoanPurpose + beneficiaryLoanAmount + beneficiaryTenure +
                        beneficiaryotherLoans + beneficiaryotherLoans_2 + beneficiaryOwnProp + beneficiaryIddetails + beneficiaryBank + regionalOffice
                );
                Log.d("TAG", "Response is+");
                 MobileSMSAPI sendSMS=new MobileSMSAPI();
                 sendSMS.sendloanconfirmation(beneficiaryName,beneficiaryPhone,beneficiaryUniqueId);
                Intent intent = new Intent(LoanActivity_ReviewScreen.this, LoanActivity_FinalScreen.class);
                startActivity(intent);





               /* SQLQueries insertlog=new SQLQueries();
                insertlog.insertloanapplication(i, beneficiaryUniqueId,agentName, agentId,agent_contact,beneficiaryName,
                        beneficiaryPhone,beneficiaryAccNumber,beneficiaryOccupation,beneficiary_father_husband,beneficiaryDOB,
                        beneficiaryAadhaar, beneficiaryPan,beneficiaryAddressLane,beneficiary_unitName, beneficiary_unitAddress,
                        beneficiaryProName, beneficiarybusinessperiod,beneficiaryEducation,beneficiarycategory,beneficiary_adult,
                        beneficiary_child,beneficiarysustenance,beneficiaryLoanPurpose,beneficiaryLoanAmount,beneficiaryTenure,
                        beneficiaryotherLoans,beneficiaryotherLoans_2,beneficiaryOwnProp,"null",beneficiaryIddetails,"null",
                        "null","null","null", 0,"null",beneficiaryBank,0
                       );*/
               /* SQLQueries insertlog=new SQLQueries();

                insertlog.insertloanlogs(i,beneficiaryUniqueId,agentName,beneficiaryGender,
                        beneficiaryName,beneficiaryAddressLane,beneficiaryOccupation,beneficiaryPhone,beneficiaryAadhaar,beneficiaryBank,
                        beneficiaryLoanAmount,beneficiaryLoanPurpose,beneficiaryTenure,beneficiaryRepaymentPeriod,
                        beneficiaryloanType,timestamp,"0",0);
                Log.d("Status","Success");*/

            }
        });





            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(LoanActivity_ReviewScreen.this, LoanActivity_FinalScreen.class);
                    startActivity(intent);
                }
            });

        }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.id_cb_tandc:
                if (checked)
                    confirm.setEnabled(true);
                else
                    confirm.setEnabled(false);
                    break;

        }
    }



    class apiCall_loanRegister extends AsyncTask<Request, Void, String> {

        @Override
        protected String doInBackground(Request... requests) {
            JSONObject jsonObject = new JSONObject();
            try {
                Log.d("TAG","Inside apicall try");
                jsonObject.put("uniqueid",beneficiaryUniqueId);
                jsonObject.put("bankmitra_name",agentName);
                jsonObject.put("latitude",latitude);
                jsonObject.put("longitude",longitude);
                jsonObject.put("bankmitra_id",agentId);
                jsonObject.put("bankmitra_contactno",agent_contact);
                jsonObject.put("beneficiary_name", beneficiaryName);
                jsonObject.put("beneficiary_phn", beneficiaryPhone);
                jsonObject.put("beneficiary_accno",beneficiaryAccNumber);
                jsonObject.put("beneficiary_lineofactivity",beneficiaryOccupation);
                jsonObject.put("beneficiary_fatherhusband", beneficiary_father_husband);
                jsonObject.put("beneficiary_dob",beneficiaryDOB);
                jsonObject.put("beneficiary_aadhaarno", beneficiaryAadhaar);
                jsonObject.put("beneficiary_pancard", beneficiaryPan);
                jsonObject.put("beneficiary_resaddress", beneficiaryAddressLane);
                jsonObject.put("beneficiary_businessname", beneficiary_unitName);
                jsonObject.put("beneficiary_businessaddress",beneficiary_unitAddress);
                jsonObject.put("business_proname",beneficiaryProName);
                jsonObject.put("beneficiary_businessexistence",beneficiarybusinessperiod);
                jsonObject.put("beneficiary_education", beneficiaryEducation);
                jsonObject.put("beneficiary_category",    beneficiarycategory);
                jsonObject.put("beneficiary_family_child",beneficiary_adult);
                jsonObject.put("beneficiary_family_adult",beneficiary_child);
                jsonObject.put("beneficiary_sustenance",beneficiarysustenance);
                jsonObject.put("beneficiary_purpose",  beneficiaryLoanPurpose);
                jsonObject.put("beneficiary_termloan",beneficiaryLoanAmount);
                jsonObject.put("beneficiary_tenor",  beneficiaryTenure);
                jsonObject.put("existing_apgvb_loan", beneficiaryotherLoans);
                jsonObject.put("existing_otherbank_loans",beneficiaryotherLoans_2);
                jsonObject.put("own_property", beneficiaryOwnProp);
                jsonObject.put("remark",  null);
                jsonObject.put("id_details",   beneficiaryIddetails);
                jsonObject.put("conductedon",  null);
                jsonObject.put("conductedby",  null);
                jsonObject.put("observation",  null);
                jsonObject.put("status",0);
                jsonObject.put("postingDate", null);
                jsonObject.put("nearest_apgvb_bank",  beneficiaryBank);
                jsonObject.put("is_read",0);
                jsonObject.put("ro",regionalOffice);
                jsonObject.put("armgrlatitude","0");
                jsonObject.put("armgrlongitude","0");
                jsonObject.put("armgrstatus",0);
                data_json = jsonObject.toString();
                /*
         {
        "uniqueid": "APGVB/test5/test5",
        "bankmitra_name": "Pagoti Vijaykumar",
        "bankmitra_id": "2323",
        "bankmitra_contactno": "9999999999",
        "beneficiary_name": "test",
        "beneficiary_phn": "9999999999",
        "beneficiary_accno": "9999999999",
        "beneficiary_lineofactivity": "test",
        "beneficiary_fatherhusband": "test",
        "beneficiary_dob": "01/01/1111",
        "beneficiary_aadhaarno": "999999999999",
        "beneficiary_pancard": "",
        "beneficiary_resaddress": "test",
        "beneficiary_businessname": "test",
        "beneficiary_businessaddress": " testL",
        "business_proname": " test",
        "beneficiary_businessexistence": " 2Years2Months",
        "beneficiary_education": " ILLETERATE",
        "beneficiary_category": " OBC",
        "beneficiary_family_child": " 0",
        "beneficiary_family_adult": "3",
        "beneficiary_sustenance": "10000",
        "beneficiary_purpose": " BUSINESS",
        "beneficiary_termloan": "10000",
        "beneficiary_tenor": " 24",
        "existing_apgvb_loan": " ",
        "existing_otherbank_loans": " ",
        "own_property": " Yes",
        "remark": null,
        "id_details": null,
        "conductedon": null,
        "conductedby": null,
        "observation": null,
        "status":0,
        "postingDate": null,
        "nearestAPGVBBank": "Saravakota",
        "is_Read":0
    }
                 */

            } catch (JSONException e) {
                e.printStackTrace();
            }
            MediaType JSON = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(JSON, data_json);
            okhttp3.Request request = new Request.Builder()
                    .url("http://mintserver.gramtarang.org:8080/mint/loans/registration")
                    .addHeader("Accept", "*/*")
                    .post(body)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    assert response.body() != null;
                    //the response we are getting from api
                    response_String = response.body().string();
                    if (response_String != null) {
                        Log.d("TAG","Success");
                        Log.d("TAG","Success Response is: "+response_String.toString());

                    } else {
                        Log.d("TAG","failed");
                        }
                }
            });

            return null;
        }


    }



}