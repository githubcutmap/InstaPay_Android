package gramtarang.mint.aeps;

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
import gramtarang.mint.agent_login.activity_Login;
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

/*activity_Aeps_Deposit
in this class , all the functionality related to Deposit features
* */
public class activity_Aeps_Deposit extends AppCompatActivity implements LogOutTimer.LogOutListener {
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
    SharedPreferences preferences;
    public static final String mypreference = "mypref";
    private static final String TAG = "AepsDeposit";
    String selected_bank_name;
    String selected_bank_id;
    String latitude;
    String longitude;
    String adharrNumber_string;
    String name_string;
    String phone_string;
    String amount_string;
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
    //bellow parameter are used for rd service for fingerprint device
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

    public static String piddata_json;
    public static Activity activity;
    OkHttpClient client;
    Button btn_submit;
    ImageView backbtn;
    EditText et_adhaar;
    EditText et_amount;
    EditText et_phone;
    EditText et_name;
    ConnectionClass connectionClass;
    AutoCompleteTextView bank_autofill;

    ArrayList<String> arrayList_bankName = new ArrayList<String>();
    ArrayList<String> arrayList_bankIIN = new ArrayList<String>();

    LoadingDialog loadingDialog = new LoadingDialog(activity_Aeps_Deposit.this);
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
        setContentView(R.layout.activity__aeps__deposit);

        //location permission
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        client = new OkHttpClient();
        //pidOptions data given by RD service of mantra fingerprint device
        pidOptions = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <PidOptions ver=\"1.0\"><Opts fCount=\"1\" fType=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\"\n" +
                "otp=\"\" wadh=\"\" posh=\"UNKNOWN\"></Opts><Demo></Demo><CustOpts><Param name=\"\" value=\'\'/></CustOpts></PidOptions>";

        et_adhaar = findViewById(R.id.aadhaar);
        et_amount = findViewById(R.id.totamount);
        et_name = findViewById(R.id.entered_name);
        et_phone = findViewById(R.id.enteredphone);
        backbtn = findViewById(R.id.backimg);
        preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        latitude=preferences.getString("Latitude","No name defined");
        longitude=preferences.getString("Longitude","No name defined");
        androidId=preferences.getString("AndroidId","No name defined");

        bank_autofill = findViewById(R.id.bank_auto);
        //data retrive from SQL
        final Utils utils=new Utils();
        final SQLQueries query=new SQLQueries();
        arrayList_bankName =query.getBankNames();
        arrayList_bankIIN =query.getBankIIN();
        utils.AutoCompleteTV_BankId(activity_Aeps_Deposit.this, bank_autofill, arrayList_bankName, arrayList_bankIIN,TAG);

        btn_submit = findViewById(R.id.submitbtn);
        //back button
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),activity_Aeps_HomeScreen.class);
                startActivity(intent);
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckNetwork.isInternetAvailable(activity_Aeps_Deposit.this)) //returns true if internet available
                {
                    selected_bank_id= utils.AutoCompleteTV_BankId(activity_Aeps_Deposit.this, bank_autofill, arrayList_bankName, arrayList_bankIIN,TAG);
                    selected_bank_name=utils.AutoCompleteTV_BankName(activity_Aeps_Deposit.this, bank_autofill, arrayList_bankName, arrayList_bankIIN,TAG);

                    adharrNumber_string = et_adhaar.getText().toString().trim();
                    name_string = et_name.getText().toString().trim();
                    phone_string = et_phone.getText().toString().trim();
                    amount_string = et_amount.getText().toString().trim();

                    isValidAadhar=utils.isValidAadhaar(adharrNumber_string);
                    isValidName=utils.isValidName(name_string);
                    isValidPhone=utils.isValidPhone(phone_string);
                    isValidBankName= query.isValidBankName(selected_bank_name);
                    if (isValidAadhar && isValidPhone && isValidName ) {
                        try {
                            //Rd service api call
                            Matra_capture(pidOptions);
                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Fingerprint Device not connected.", Toast.LENGTH_LONG).show();
                        }
                    }
                    if(!isValidAadhar){
                        et_adhaar.setError("Enter Valid Aadhaar Number");
                    }
                    if(!isValidName){
                        et_name.setError("Enter Valid Name");
                    }
                    if(!isValidPhone){
                        et_phone.setError("Enter Valid Phone Number");
                    }
                } else {
                    Toast.makeText(activity_Aeps_Deposit.this, "No Internet Connection", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //Matra_capture in this method the rd service's api call is happening
    public String Matra_capture(String pidOptions) {
        Log.d(TAG, "capture: this is a log For capture :-" + pidOptions);
        Intent intentCapture = new Intent("in.gov.uidai.rdservice.fp.CAPTURE");
        intentCapture.putExtra("PID_OPTIONS", pidOptions);
        startActivityForResult(intentCapture, 1);
        return pidDataXML;


    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//......USER CODE
        Log.d(TAG, "onActivityResult: ______OnActivityResult Called");
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            assert data != null;
            pidDataXML = data.getStringExtra("PID_DATA");
            Log.d(TAG, "onActivityResult: PidDataXml:" + pidDataXML);
            if(pidDataXML==null){
                DialogActivity.DialogCaller.showDialog(activity_Aeps_Deposit.this, "Alert", "Fingerprint data not captured.\nPlease try again.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
            }
            if (pidDataXML != null) {
                //tv.setText(pidDataXML);
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
    public void processParsing(XmlPullParser parser) throws IOException, XmlPullParserException {

        int eventType = parser.getEventType();
        //com.jaitejapp.mintnewapplication.CaptureResponse currentPlayer = null;

        CaptureResponse currentPlayer = null;
        while(eventType!= XmlPullParser.END_DOCUMENT){
            String eltName = null;

            switch (eventType){
                case XmlPullParser.START_TAG:
                    eltName = parser.getName();
                    Log.d(TAG, "processParsing: eltName "+eltName);

                    if ("PidData".equals(eltName)){
                        //  currentPlayer = new  com.jaitejapp.mintnewapplication.CaptureResponse();
                        currentPlayer = new CaptureResponse();
                        //captureResponses.add(currentPlayer);
                        Log.d(TAG, "processParsing: currentPlayer"+currentPlayer);

                    }else if (currentPlayer != null){
                        if ("DeviceInfo".equals(eltName)){
                            currentPlayer.dpID = parser.getAttributeValue(null,"dpId");
                            currentPlayer.rdsID = parser.getAttributeValue(null,"rdsId");
                            currentPlayer.rdsVer = parser.getAttributeValue(null,"rdsVer");
                            currentPlayer.dc = parser.getAttributeValue(null,"dc");
                            currentPlayer.mi=parser.getAttributeValue(null,"mi");
                            currentPlayer.mc = parser.getAttributeValue(null,"mc");

                            dpId = currentPlayer.dpID;
                            rdsID = currentPlayer.rdsID;
                            rdsVer = currentPlayer.rdsVer;
                            dc = currentPlayer.dc;
                            mi = currentPlayer.mi;
                            mc = currentPlayer.mc;
                            //captureResponses.add(dpId,rdsID);

                            //captureResponses.add(dpId);


                            //Log.d(TAG, "processParsing dpId is:"+ dpId +""+"rdsId: "+rdsID+""+"rdsVer:"+rdsVer+""+"dc:"+dc+""+"Mi:"+mi+""+"mc:"+mc);

                        } else if ("Resp".equals(eltName)) {
                            currentPlayer.errCode = parser.getAttributeValue(null,"errCode");
                            currentPlayer.errInfo = parser.getAttributeValue(null,"errInfo");
                            currentPlayer.fType = parser.getAttributeValue(null,"fType");
                            currentPlayer.fCount = parser.getAttributeValue(null,"fCount");
                            currentPlayer.iCount = parser.getAttributeValue(null,"iCount");
                            currentPlayer.iType = parser.getAttributeValue(null,"iType");
                            currentPlayer.format = parser.getAttributeValue(null,"format");
                            currentPlayer.nmPoints = parser.getAttributeValue(null,"nmPoints");
                            currentPlayer.qScore = parser.getAttributeValue(null,"qScore");
                            currentPlayer.pCount = parser.getAttributeValue(null,"qScore");
                            currentPlayer.pType = parser.getAttributeValue(null,"qScore");

                            errcode = currentPlayer.errCode;
                            errInfo = currentPlayer.errInfo;
                            fType = currentPlayer.fType;
                            fCount = currentPlayer.fCount;
                            format = currentPlayer.format;
                            nmPoints = currentPlayer.nmPoints;
                            qScore = currentPlayer.qScore;
                            iCount = currentPlayer.iCount;
                            iType = currentPlayer.iType;
                            pCount = currentPlayer.pCount;
                            pType = currentPlayer.pType;


                            // Log.d(TAG, "processParsing: errorCode:"+errcode+""+"errorInfo"+errInfo+""+"fType:"
                            //    +fType+""+"fCount:"+fCount+""+"format:"+format+""+"nmPoints:"+nmPoints+""+"qScore:"+qScore);

                        } else if ("Skey".equals(eltName)) {

                            currentPlayer.ci = parser.getAttributeValue(null, "ci");
                            currentPlayer.sessionKey = parser.nextText();

                            ci = currentPlayer.ci;
                            SessionKey = currentPlayer.sessionKey;
                            // Log.d(TAG, "processParsing: Skey :" + SessionKey + "/n" + "ci:" + ci);

                        } else if ("Hmac".equals(eltName)) {
                            currentPlayer.hmac = parser.nextText();

                            hmac = currentPlayer.hmac;
                            // Log.d(TAG, "processParsing: Hmac is:" + hmac);

                        } else if ("Data".equals(eltName)) {
                            currentPlayer.PidDatatype = parser.getAttributeValue(null, "type");
                            currentPlayer.Piddata = parser.nextText();

                            PidDatatype = currentPlayer.PidDatatype;
                            Piddata = currentPlayer.Piddata;

                            Log.d(TAG, "processParsing: piddata and pidDatatype is ::::::::::::::::::::"+PidDatatype+Piddata);

                            // Log.d(TAG, "processParsing: PidData and Data Type is :" + pidDataType + "\n" + "pidData:" + pidData);
                        }
                    }
                    break;
            }
            eventType = parser.next();
        }
        cashDepositRequest();

    }

    private void cashDepositRequest() {
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

            Log.d(TAG, "cashWithdrawRequest: Json is :"+jsonObject);

            piddata_json = jsonObject.toString();

            //tv.setText(is);

            Log.d(TAG, "cashWithdrawRequest: The is :"+ piddata_json);

            new Networks().execute();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    class Networks extends AsyncTask<Request, Void, String> {
        @Override
        protected String doInBackground(Request... requests) {
            //  Toast.makeText(MainActivity.this,"Network call",Toast.LENGTH_LONG);
            loadingDialog.startLoading();


            Log.d(TAG, "doInBackground: --------------------");
            Response response ;
            // final String responseBody=response.body().string();
            MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody body = RequestBody.create(JSON, piddata_json);
            Log.d(TAG, "doInBackground: sample body  "+ piddata_json);
       /*RequestBody formBody = new FormBody.Builder
               .add("JSONbody",is)
               .build();*/
            Log.d(TAG, "doInBackground: formbody"+body);
            Request request = new Request.Builder()
                    .url("http://mintserver.gramtarang.org:8080/aeps/deposit") //http://15.206.223.162:8080/piddata
                    .addHeader("AdhaarNumber", adharrNumber_string)
                    .addHeader("Bankid",selected_bank_id)
                    .addHeader("phnumber", phone_string)
                    .addHeader("name", name_string)
                    .addHeader("amount", amount_string)
                    .addHeader("imeiNumber",androidId)
                    .addHeader("Latitude", latitude)
                    .addHeader("Longitude", longitude)
                    .post(body)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(TAG, "onFailure: "+call+e);
                    loadingDialog.dismissDialog();
                    Toast.makeText(activity_Aeps_Deposit.this,"Your transaction Failed.Please Try Again", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    assert response.body() != null;
                    String resStr = response.body().string();
                    if(resStr!= null)

                    {
                        loadingDialog.dismissDialog();
                        Log.d(TAG,"Response resstring is:"+resStr);
                        try {
                            JSONObject jsonResponse=new JSONObject(resStr);
                            message=jsonResponse.getString("message");
                            status=jsonResponse.getString("status");
                            status_code=jsonResponse.getString("statusCode");
                            String data=jsonResponse.getString("data");
                            Log.d(TAG,"Message is:"+message+",Status Code is:"+status_code+",Data is:"+data);

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
                            Log.d(TAG,"Entered Amount is :"+amount+",Transaction Status is:"+transaction_status+",Balance is"+balance+",Bank RRN is:"+bank_RRN+
                                    ",Transaction Type is:"+transaction_type+",Merchant Transaction ID is:"+merchant_transid);


                        } catch (JSONException | NullPointerException e) {
                            DialogActivity.DialogCaller.showDialog(activity_Aeps_Deposit.this,"Transaction Failed",message+"."+"This transaction cannot be completed.Please try after sometime\"",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });

                        }



                        Intent intent=new Intent(activity_Aeps_Deposit.this, activity_Aeps_Deposit_Receipt.class);
                        intent.putExtra("trans_amount", amount_string);
                        intent.putExtra("balance",balance);
                        intent.putExtra("merchant_transid",merchant_transid);
                        intent.putExtra("timestamp",timestamp);
                        intent.putExtra("aadhaar", adharrNumber_string);
                        intent.putExtra("bank_name",selected_bank_name);
                        intent.putExtra("agent_id",agentId);
                        intent.putExtra("rrn_no",bank_RRN);
                        intent.putExtra("custName", name_string);
                        intent.putExtra("message",message);
                        intent.putExtra("fpTransId",fpTransId);
                        intent.putExtra("status",status);
                        intent.putExtra("status_code",status_code);
                        intent.putExtra("transaction_type",transaction_type);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(activity_Aeps_Deposit.this,"You are not getting any Response From Bank !! ",Toast.LENGTH_SHORT).show();


                    }





                }
            });

            // Log.d(TAG, "doInBackground: response"+response);
            String resStr = null;

            Log.d(TAG, "doInBackground: Response____ "+resStr);


            return resStr;
        }
    }
}






