package gramtarang.mint.aadhaarPay;

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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;



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
import gramtarang.mint.agent_login.activity_WelcomeScreen;
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

/*AadhaarPay activity
*contains every functionality about adhaar pay transaction
* */
public class AadhaarPay extends AppCompatActivity implements LogOutTimer.LogOutListener {
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

    //
    SharedPreferences preferences;
    public static final String mypreference = "mypref";
    private static final String TAG = "AadhaarPay";
    String selected_bank_name;
    String selected_bank_id;
    String latitude;
    String longitude;
    String string_adhaarNumber;
    String string_name;
    String string_phoneNumber;
    String string_amount;
    String message;
    String status;
    String status_code;
    String pidDataXML;
    String pidOptions;
    String agentId;
    String balance;
    String androidId;
    String bank_RRN;
    String transaction_type;
    String merchant_transid;
    String timestamp;
    String amount;
    String transaction_status;
    String fpTransId;
    //parameters- getting from RD service of FingerPrint device
    public static String ci;
    private static String dc;
    public static String errInfo;
    public static String fCount;
    public static String fType ;
    public static String hmac;
    public static String iCount ;
    public static String iType ;
    public static String mc ;
    public static String mi;
    public static String nmPoints;
    public static String qScore;
    public static String rdsID;
    public static String rdsVer ;
    public static String format;
    public static String errcode;
    public static String dpId;
    public static String SessionKey;
    public static String PidDatatype;
    public static String Piddata;
    public static String pCount;
    public static String pType;

    public static String pidData_json;
    public static Activity activity;
    OkHttpClient client;
    ImageView backbtn;
    Button btn_submit;
    EditText et_adhaar;
    EditText et_Amount;
    EditText et_Phone;
    EditText et_Name;
    ConnectionClass connectionClass;
    AutoCompleteTextView bank_autofill;

    ArrayList<String> arryList_bankNames = new ArrayList<String>();
    ArrayList<String> arryList_bankIIN = new ArrayList<String>();

    LoadingDialog loadingDialog = new LoadingDialog(AadhaarPay.this);
    boolean isValidAadhar,isValidBankName,isValidName,isValidAmount,isValidPhone;
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

    //This method working as auto scaling of ui by density
    public void adjustFontScale(Configuration configuration) {

        configuration.fontScale = (float) 1.0;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        getBaseContext().getResources().updateConfiguration(configuration, metrics);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        newConfig.densityDpi= (int) (metrics.density * 160f);
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = newConfig.densityDpi * metrics.density;
        getBaseContext().getResources().updateConfiguration(newConfig, metrics);

    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adjustFontScale(getResources().getConfiguration());
        onConfigurationChanged(getResources().getConfiguration());
        setContentView(R.layout.activity_aadhaar_pay);

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        client = new OkHttpClient();
        //@param- pidOptions, this data is given by RD service
        pidOptions = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <PidOptions ver=\"1.0\"><Opts fCount=\"1\" fType=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\"\n" +
                "otp=\"\" wadh=\"\" posh=\"UNKNOWN\"></Opts><Demo></Demo><CustOpts><Param name=\"\" value=\'\'/></CustOpts></PidOptions>";
        backbtn = findViewById(R.id.backimg);
        et_adhaar = findViewById(R.id.aadhaar);
        et_Amount = findViewById(R.id.totamount);
        et_Name = findViewById(R.id.entered_name);
        et_Phone = findViewById(R.id.enteredphone);
        //
        preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        latitude=preferences.getString("Latitude","No name defined");
        longitude=preferences.getString("Longitude","No name defined");
        androidId=preferences.getString("AndroidId","No name defined");

        bank_autofill = findViewById(R.id.bank_auto);
        final Utils utils=new Utils();
        final SQLQueries query=new SQLQueries();

        arryList_bankNames =query.getBankNames();
        arryList_bankIIN =query.getBankIIN();

        utils.AutoCompleteTV_BankId(AadhaarPay.this, bank_autofill, arryList_bankNames, arryList_bankIIN,TAG);
        btn_submit = findViewById(R.id.submitbtn);
        btn_submit.setEnabled(true);
        //back button
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), activity_Aeps_HomeScreen.class);
                startActivity(intent);
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckNetwork.isInternetAvailable(AadhaarPay.this)) //returns true if internet available
                {
                    btn_submit.setEnabled(false);
                    selected_bank_id= utils.AutoCompleteTV_BankId(AadhaarPay.this, bank_autofill, arryList_bankNames, arryList_bankIIN,TAG);
                    selected_bank_name=utils.AutoCompleteTV_BankName(AadhaarPay.this, bank_autofill, arryList_bankNames, arryList_bankIIN,TAG);

                    string_adhaarNumber = et_adhaar.getText().toString().trim();
                    string_name = et_Name.getText().toString().trim();
                    string_phoneNumber = et_Phone.getText().toString().trim();
                    string_amount = et_Amount.getText().toString().trim();

                    isValidAadhar=utils.isValidAadhaar(string_adhaarNumber);
                    isValidName=utils.isValidName(string_name);
                    isValidPhone=utils.isValidPhone(string_phoneNumber);
                    isValidAmount=utils.isValidAmount(string_amount);
                    isValidBankName= query.isValidBankName(selected_bank_name);

                    if (isValidAadhar && isValidPhone && isValidName  && isValidAmount ) {
                        try {
                            Matra_capture(pidOptions);
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Fingerprint Device not connected.", Toast.LENGTH_LONG).show();
                       btn_submit.setEnabled(true);
                        }
                    }
                    if(!isValidAadhar){
                        et_adhaar.setError("Enter Valid Aadhaar Number");
                        btn_submit.setEnabled(true);
                    }
                    if(!isValidName){
                        et_Name.setError("Enter Valid Name");
                        btn_submit.setEnabled(true);
                    }
                    if(!isValidPhone){
                        et_Phone.setError("Enter Valid Phone Number");
                        btn_submit.setEnabled(true);
                    }
                    if(!isValidAmount){
                        et_Amount.setError("Amount should be in range between INR100 to INR10000");
                        btn_submit.setEnabled(true);
                    }
                } else {
                    Toast.makeText(AadhaarPay.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                    btn_submit.setEnabled(true);
                }
            }
        });
    }

    //Matra_capture method is use for calling RD service of mantra device
    public String Matra_capture(String pidOptions) {
        Log.d(TAG, "capture: this is a log For capture :-" + pidOptions);
        Intent intentCapture = new Intent("in.gov.uidai.rdservice.fp.CAPTURE");
        intentCapture.putExtra("PID_OPTIONS", pidOptions);
        startActivityForResult(intentCapture, 1);
        return pidDataXML;
   }

   /*Inside MantraCapture after firing of startActivityResult, onActivityResult will be fire,
   * according to the request code it will perform the operations*/

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            assert data != null;
            pidDataXML = data.getStringExtra("PID_DATA");
            if(pidDataXML==null){
               Toast.makeText(AadhaarPay.this,"Fingerprint not captured.Try again.",Toast.LENGTH_SHORT).show();
                btn_submit.setEnabled(true);
            }
            if (pidDataXML != null) {
                XmlPullParserFactory pullParserFactory;
                try {
                    pullParserFactory = XmlPullParserFactory.newInstance();
                    XmlPullParser parser = pullParserFactory.newPullParser();
                    InputStream is = new ByteArrayInputStream(pidDataXML.getBytes(StandardCharsets.UTF_8));
                    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    parser.setInput(is, null);
                    processParsing(parser);
                } catch (XmlPullParserException | IOException e) {
                    e.printStackTrace();
                }
            }
        }}

        /*processParsing method ,
        XML data, getting from rd service starts parsing here
        * */
    public void processParsing(XmlPullParser parser) throws IOException, XmlPullParserException {

        int eventType = parser.getEventType();
        CaptureResponse captureResponse = null;
        while(eventType!= XmlPullParser.END_DOCUMENT){
            String eltName = null;

            switch (eventType){
                case XmlPullParser.START_TAG:
                    eltName = parser.getName();
                    if ("PidData".equals(eltName)){
                        captureResponse = new CaptureResponse();
                    }else if (captureResponse != null){
                        if ("DeviceInfo".equals(eltName)){
                            captureResponse.dpID = parser.getAttributeValue(null,"dpId");
                            captureResponse.rdsID = parser.getAttributeValue(null,"rdsId");
                            captureResponse.rdsVer = parser.getAttributeValue(null,"rdsVer");
                            captureResponse.dc = parser.getAttributeValue(null,"dc");
                            captureResponse.mi=parser.getAttributeValue(null,"mi");
                            captureResponse.mc = parser.getAttributeValue(null,"mc");

                            dpId = captureResponse.dpID;
                            rdsID = captureResponse.rdsID;
                            rdsVer = captureResponse.rdsVer;
                            dc = captureResponse.dc;
                            mi = captureResponse.mi;
                            mc = captureResponse.mc;

                        } else if ("Resp".equals(eltName)) {
                            captureResponse.errCode = parser.getAttributeValue(null,"errCode");
                            captureResponse.errInfo = parser.getAttributeValue(null,"errInfo");
                            captureResponse.fType = parser.getAttributeValue(null,"fType");
                            captureResponse.fCount = parser.getAttributeValue(null,"fCount");
                            captureResponse.iCount = parser.getAttributeValue(null,"iCount");
                            captureResponse.iType = parser.getAttributeValue(null,"iType");
                            captureResponse.format = parser.getAttributeValue(null,"format");
                            captureResponse.nmPoints = parser.getAttributeValue(null,"nmPoints");
                            captureResponse.qScore = parser.getAttributeValue(null,"qScore");
                            captureResponse.pCount = parser.getAttributeValue(null,"qScore");
                            captureResponse.pType = parser.getAttributeValue(null,"qScore");

                            errcode = captureResponse.errCode;
                            errInfo = captureResponse.errInfo;
                            fType = captureResponse.fType;
                            fCount = captureResponse.fCount;
                            format = captureResponse.format;
                            nmPoints = captureResponse.nmPoints;
                            qScore = captureResponse.qScore;
                            iCount = captureResponse.iCount;
                            iType = captureResponse.iType;
                            pCount = captureResponse.pCount;
                            pType = captureResponse.pType;

                        } else if ("Skey".equals(eltName)) {

                            captureResponse.ci = parser.getAttributeValue(null, "ci");
                            captureResponse.sessionKey = parser.nextText();
                            ci = captureResponse.ci;
                            SessionKey = captureResponse.sessionKey;

                        } else if ("Hmac".equals(eltName)) {
                            captureResponse.hmac = parser.nextText();
                            hmac = captureResponse.hmac;

                        } else if ("Data".equals(eltName)) {
                            captureResponse.PidDatatype = parser.getAttributeValue(null, "type");
                            captureResponse.Piddata = parser.nextText();
                            PidDatatype = captureResponse.PidDatatype;
                            Piddata = captureResponse.Piddata;
                        }
                    }
                    break;
            }
            eventType = parser.next();
        }
        ConvertToJSONformat();
    }

    // After XML data parsing the required data are converting to JSON format
    private void ConvertToJSONformat() {
        String msgStr = "";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("errcode",errcode);
            jsonObject.put("errInfo",errInfo);
            jsonObject.put("fCount",fCount);
            jsonObject.put("fType",fType);
            jsonObject.put("iCount",iCount);
            jsonObject.put("iType",iType);
            jsonObject.put("pCount",pCount);
            jsonObject.put("pType",pType);
            jsonObject.put("nmPoints",nmPoints);
            jsonObject.put("qScore",qScore);
            jsonObject.put("dpID",dpId);
            jsonObject.put("rdsID",rdsID);
            jsonObject.put("rdsVer",rdsVer);
            jsonObject.put("dc",dc);
            jsonObject.put("mi",mi);
            jsonObject.put("mc",mc);
            jsonObject.put("ci",ci);
            jsonObject.put("sessionKey",SessionKey);
            jsonObject.put("hmac",hmac);
            jsonObject.put("PidDatatype",PidDatatype);
            jsonObject.put("Piddata",Piddata);

            pidData_json = jsonObject.toString();

            new AdaarPay_apiCall().execute();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*AdhaarPa_apicall class
    * here the the requirment data are feeding to the adharrPay api which is present in our AWS server(API CALL using okHTTP3)*/
    class AdaarPay_apiCall extends AsyncTask<Request, Void, String> {

        @Override
        protected String doInBackground(Request... requests) {
            loadingDialog.startLoading();


            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            //THe piddata in JSON format are used as a BODY
            RequestBody body = RequestBody.create(JSON, pidData_json);

            Request request = new Request.Builder()
                    .url("http://mintserver.gramtarang.org:8080/aadhaarPay/merchant/pay")
                    .addHeader("AdhaarNumber", string_adhaarNumber)
                    .addHeader("bankId",selected_bank_id)
                    .addHeader("MobileNumber", string_phoneNumber)
                    .addHeader("name", string_name)
                    .addHeader("amount", string_amount)
                    .addHeader("imeiNumber",androidId)
                    .addHeader("Latitude", latitude)
                    .addHeader("Longitude", longitude)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    loadingDialog.dismissDialog();
                    btn_submit.setEnabled(true);
                    Toast.makeText(AadhaarPay.this,"Your transaction Failed.Please Try Again", Toast.LENGTH_SHORT).show();


                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    assert response.body() != null;

                    //data getting from server
                    String resStr = response.body().string();
                    if(resStr!= null)
                    {
                        loadingDialog.dismissDialog();
                        try {
                            // Response JSON data start parsing
                            JSONObject jsonResponse=new JSONObject(resStr);
                            message=jsonResponse.getString("message");
                            status=jsonResponse.getString("status");
                            status_code=jsonResponse.getString("statusCode");
                            String data=jsonResponse.getString("data");
                            JSONObject jsonData=new JSONObject(data);
                            amount=jsonData.getString("transactionAmount");
                            transaction_status=jsonData.getString("transactionStatus");
                            balance=jsonData.getString("balanceAmount");
                            bank_RRN=jsonData.getString("bankRRN");
                            transaction_type=jsonData.getString("transactionType");
                            merchant_transid=jsonData.getString("merchantTxnId");
                            timestamp=jsonData.getString("requestTransactionTime");
                            message=jsonData.getString("message");
                            fpTransId=jsonData.getString("fpTransactionId");

                        } catch (JSONException | NullPointerException e) {
                            DialogActivity.DialogCaller.showDialog(AadhaarPay.this,"Transaction Failed",message+"."+"This transaction cannot be completed.Please try after sometime\"",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });

                        }

                        // intent to next Activity with the required data
                        Intent intent=new Intent(AadhaarPay.this, AadhaarPay_Receipt.class);
                        intent.putExtra("trans_amount", string_amount);
                        intent.putExtra("balance",balance);
                        intent.putExtra("merchant_transid",merchant_transid);
                        intent.putExtra("timestamp",timestamp);
                        intent.putExtra("aadhaar", string_adhaarNumber);
                        intent.putExtra("bank_name",selected_bank_name);
                        intent.putExtra("agent_id",agentId);
                        intent.putExtra("rrn_no",bank_RRN);
                        intent.putExtra("custName", string_name);
                        intent.putExtra("message",message);
                        intent.putExtra("fpTransId",fpTransId);
                        intent.putExtra("status",status);
                        intent.putExtra("status_code",status_code);
                        intent.putExtra("transaction_type",transaction_type);
                        startActivity(intent); }
                    else{
                        Toast.makeText(AadhaarPay.this,"You are not getting any Response From Bank !! ",Toast.LENGTH_SHORT).show();

                    }
                }
            });

            return null;
        }
    }
}






