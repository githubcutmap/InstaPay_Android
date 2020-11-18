package gramtarang.mint.aeps;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
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
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import gramtarang.mint.R;
import gramtarang.mint.agent_login.activity_Login;
import gramtarang.mint.utils.CaptureResponse;
import gramtarang.mint.utils.CheckNetwork;

import gramtarang.mint.utils.DialogActivity;

import gramtarang.mint.utils.LogOutTimer;

import gramtarang.mint.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/*activity_Aeps_BalanceEnquiry
 * this activity responsible for BALANCE enquiry transaction , like collection all the required details from customer and
 * send to the bank Api collect the response from server then send the data to the next screen*/
public class activity_Aeps_BalanceEnquiry extends AppCompatActivity implements LogOutTimer.LogOutListener {
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
    private static final String TAG = "AepsBalanceEnquiry";
    EditText aadhaar, name, phonenumber;
    Button submit;
    ImageView backbtn;
    AlertDialog alertDialog;

    boolean isValidAadhar, isValidBankName;
    String[] deviceList = new String[]{"Mantra", "StarTeck"};
    AutoCompleteTextView bank_autofill;
    boolean isValidName, isValidPhone;
    String username,password,selected_bank_id, selected_bank_name, en_aadhaar, en_name, en_phn, latitude, longitude, response_String, androidId, banks, selected_bank, banks_no, pidDataXML, message, status, status_code, pidOptions, data, transaction_status, balance, bank_RRN, transaction_type, merchant_transid, timestamp, fpTransId, agentId;

    //bellow parameters are coming from RD service of fingerPrint device
    public String ci;
    private String dc;
    public String errInfo;
    public String fCount;
    public String fType;
    public String hmac;
    public  String iCount;
    public String iType;
    public String mc;
    public String mi;
    public String nmPoints;
    public String qScore;
    public String rdsID;
    public String rdsVer;
    public String format;
    public String errcode;
    public String dpId;
    public String SessionKey;
    public String PidDatatype;
    public String Piddata;
    public String pCount;
    public String pType;
    public String pidData_json;
    OkHttpClient client;
    OkHttpClient httpClient;
    ArrayList<String> arryList_bankName = new ArrayList<String>();
    ArrayList<String> arrayList_bankNumber = new ArrayList<String>();


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
        newConfig.densityDpi = (int) (metrics.density * 160f);

        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = newConfig.densityDpi * metrics.density;
        getBaseContext().getResources().updateConfiguration(newConfig, metrics);

    }

    @SuppressLint("NewApi")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        adjustFontScale(getResources().getConfiguration());
        onConfigurationChanged(getResources().getConfiguration());
        setContentView(R.layout.activity_aeps_bal);





        Utils util = new Utils();

        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        bank_autofill = findViewById(R.id.bank_auto);

        client = new OkHttpClient();
        //pidOptions parameter's data given by RD service of fingerPrint device
        pidOptions = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <PidOptions ver=\"1.0\"><Opts fCount=\"1\" fType=\"0\" format=\"0\" pidVer=\"2.0\" timeout=\"10000\"\n" +
                "otp=\"\" wadh=\"\" posh=\"UNKNOWN\"></Opts><Demo></Demo><CustOpts><Param name=\"\" value=\'\'/></CustOpts></PidOptions>";
        aadhaar = findViewById(R.id.aadhar_no);
        name = findViewById(R.id.name);
        phonenumber = findViewById(R.id.phonenumber);
        submit = findViewById(R.id.submit);
        backbtn = findViewById(R.id.backimg);
        preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        latitude = preferences.getString("Latitude", "No name defined");
        longitude = preferences.getString("Longitude", "No name defined");
        agentId=preferences.getString("AgentId","No name defined");
        username=preferences.getString("Username","No name defined");
        password=preferences.getString("Password","No name defined");
        androidId= Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        // androidId = preferences.getString("AndroidId", "No name defined");
        // latitude="21.22";
        //  longitude="112.2";
        // androidId= Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);


        //bankNames are getting from SQL database for validation purpose
        /*SQLQueries getbanknames = new SQLQueries();
        SQLQueries bankvalidation = new SQLQueries();
        arryList_bankName = getbanknames.getBankNames();
        arrayList_bankNumber = getbanknames.getBankIIN();*/

        new apiCall_getBanks().execute();

        //AUto complete of bank id
       //      //back button
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),activity_Aeps_HomeScreen.class);
                startActivity(intent);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title = "Balance Enquiry";
                // message = "Under Development";
               /* Utils util = new Utils();
                util.dialog(activity_Aeps_BalanceEnquiry.this,title,message);*/

                if (CheckNetwork.isInternetAvailable(activity_Aeps_BalanceEnquiry.this)) //returns true if internet available
                {

                    selected_bank_id = util.AutoCompleteTV_BankId(activity_Aeps_BalanceEnquiry.this, bank_autofill, arryList_bankName, arrayList_bankNumber, TAG);
                    selected_bank_name = util.AutoCompleteTV_BankName(activity_Aeps_BalanceEnquiry.this, bank_autofill, arryList_bankName, arrayList_bankNumber, TAG);
                    en_aadhaar = aadhaar.getText().toString().trim();
                    en_name = name.getText().toString().trim();
                    en_phn = phonenumber.getText().toString().trim();
                    isValidAadhar = util.isValidAadhaar(en_aadhaar);
                    isValidName = util.isValidName(en_name);
                    isValidPhone = util.isValidPhone(en_phn);
                    //isValidBankName = bankvalidation.isValidBankName(selected_bank_name);
                    Log.d(TAG, "Validations are:" + isValidPhone + isValidBankName + isValidAadhar + isValidName);
                    if (isValidAadhar && isValidPhone && isValidName) {
                        try {
                            //   new  ().execute();
                           capture(pidOptions);
                           //  fingerprintDataConvertingToJson();
                      // new apiCall_BalanceEnquiry().execute();
                        } catch (Exception e) {
                            // Toast.makeText(getApplicationContext(), "Fingerprint Device not connected.", Toast.LENGTH_LONG).show();
                        }
                    }
                    if (!isValidAadhar) {
                        aadhaar.setError("Enter Valid Aadhaar Number");
                    }
                    if (!isValidName) {
                        name.setError("Enter Valid Name");
                    }
                    if (!isValidPhone) {
                        phonenumber.setError("Enter Valid Phone Number");
                    }
                } else {
                    Toast.makeText(activity_Aeps_BalanceEnquiry.this, "No Internet Connection", Toast.LENGTH_LONG).show();

                }
            }

        });

    }

     class apiCall_getBanks extends AsyncTask<Request, Void, String> {
        @Override
        protected String doInBackground(Request... requests) {
            httpClient = utils.createAuthenticatedClient(username, password);
            okhttp3.Request request = new Request.Builder()
                    .url("https://aepsapi.gramtarang.org:8008/mint/aeps/getBanks")
                    .addHeader("Accept", "*/*")
                    .get()
                    .build();
            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    assert response.body() != null;
                    //the response we are getting from api
                    response_String = response.body().string();
                    if (response_String != null) {
                        Log.d("TAG","Response is+"+response_String.toString());
                        JSONArray jsonResponse = null;
                        try {
                            jsonResponse = new JSONArray(response_String);
                            for(int j = 0; j < jsonResponse.length(); j++){
                                JSONObject jresponse = jsonResponse.getJSONObject(j);
                                arryList_bankName.add(jresponse.getString("bankname"));
                                arrayList_bankNumber.add(jresponse.getString("iinno"));
                            }

                            Log.d("TAG","BANK NAMES"+arryList_bankName+arrayList_bankNumber);
                            setAutoCompleteTV();

                            //   setText(tv_dateofrelease,dateofrelease,tv_version,latest_app_version);

                            //Log.d("TAG","SAME CLASS"+latest_app_version+dateofrelease+androidId+latitude+longitude);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        catch (NullPointerException e) {
                        }
                    }

                }
            });

            return null;
        }

    }
    public void setAutoCompleteTV(){
        runOnUiThread(new Runnable() {
            public void run() {
                Log.d(TAG, "setauto complete: " + arrayList_bankNumber + arryList_bankName);
                utils.AutoCompleteTV_BankId(activity_Aeps_BalanceEnquiry .this,bank_autofill,arryList_bankName,arrayList_bankNumber,TAG);
            }
        });
     }

    //RD service api calling of mantra Fingerprint device
    public String capture(String pidOptions) {
        Log.d(TAG, "capture: this is a log For capture :-" + pidOptions);
        Intent intentCapture = new Intent("in.gov.uidai.rdservice.fp.CAPTURE");
        //in.gov.uidai.rdservice.fp.CAPTURE
        intentCapture.putExtra("PID_OPTIONS", pidOptions);
        startActivityForResult(intentCapture, 1);
        // intentCapture.getExtra("PID_OPTIONS");
        return pidDataXML;
    }

    //StartTek_capture method is RD service api call of StartTech fingerprint device
    public String StartTek_capture(String pidOptions) {
        Intent intentCapture = new Intent("in.gov.uidai.rdservice.fp.CAPTURE");
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intentCapture, PackageManager.MATCH_DEFAULT_ONLY);
        boolean isIntentSafe = activities.size() > 0;
        intentCapture.putExtra("PID_OPTIONS", pidOptions);
        startActivityForResult(intentCapture, 2);
        if (!isIntentSafe) {
        }
        return pidDataXML;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            assert data != null;
            pidDataXML = data.getStringExtra("PID_DATA");

            //if fingerPrint data doesn't captured then it will go for this condition
            // where we are showing an alert dialog
            if (pidDataXML == null) {
                DialogActivity.DialogCaller.showDialog(activity_Aeps_BalanceEnquiry.this, "Alert", "Fingerprint data not captured.\nPlease try again.", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
            }
            if (pidDataXML != null) {

                try {
                    this.sendDatatoServer(pidDataXML);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //XML data parsing starts from here

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
            } else {
                Toast.makeText(activity_Aeps_BalanceEnquiry.this, "Please connect Fingerprint device", Toast.LENGTH_SHORT).show();


            }
        }
    }

    //processParsing method, inside this method the xml data coming from rd service going for parsing purpose
    public void processParsing(XmlPullParser parser) throws IOException, XmlPullParserException {

        int eventType = parser.getEventType();
        CaptureResponse currentPlayer = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String eltName = null;

            switch (eventType) {
                case XmlPullParser.START_TAG:
                    eltName = parser.getName();
                    Log.d(TAG, "processParsing: eltName " + eltName);

                    if ("PidData".equals(eltName)) {
                        //currentPlayer = new com.jaitejapp.mintnewapplication.CaptureResponse();
                        currentPlayer = new CaptureResponse();
                        //captureResponses.add(currentPlayer);
                        Log.d(TAG, "processParsing: currentPlayer" + currentPlayer);

                    } else if (currentPlayer != null) {
                        if ("DeviceInfo".equals(eltName)) {
                            currentPlayer.dpID = parser.getAttributeValue(null, "dpId");
                            currentPlayer.rdsID = parser.getAttributeValue(null, "rdsId");
                            currentPlayer.rdsVer = parser.getAttributeValue(null, "rdsVer");
                            currentPlayer.dc = parser.getAttributeValue(null, "dc");
                            currentPlayer.mi = parser.getAttributeValue(null, "mi");
                            currentPlayer.mc = parser.getAttributeValue(null, "mc");

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
                            currentPlayer.errCode = parser.getAttributeValue(null, "errCode");
                            currentPlayer.errInfo = parser.getAttributeValue(null, "errInfo");
                            currentPlayer.fType = parser.getAttributeValue(null, "fType");
                            currentPlayer.fCount = parser.getAttributeValue(null, "fCount");
                            currentPlayer.iCount = parser.getAttributeValue(null, "iCount");
                            currentPlayer.iType = parser.getAttributeValue(null, "iType");
                            currentPlayer.format = parser.getAttributeValue(null, "format");
                            currentPlayer.nmPoints = parser.getAttributeValue(null, "nmPoints");
                            currentPlayer.qScore = parser.getAttributeValue(null, "qScore");
                            currentPlayer.pCount = parser.getAttributeValue(null, "qScore");
                            currentPlayer.pType = parser.getAttributeValue(null, "qScore");

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

                            Log.d(TAG, "processParsing: piddata and pidDatatype is ::::::::::::::::::::" + PidDatatype + Piddata);

                            // Log.d(TAG, "processParsing: PidData and Data Type is :" + pidDataType + "\n" + "pidData:" + pidData);
                        }
                    }
                    break;
            }
            eventType = parser.next();
        }

        fingerprintDataConvertingToJson();
        //  new apiCall_BalanceEnquiry().execute();

    }

    private void fingerprintDataConvertingToJson() {
        String msgStr = "";

        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("errcode", errcode);
            jsonObject.put("errInfo", errInfo);
            jsonObject.put("fCount", fCount);
            jsonObject.put("fType", fType);
            jsonObject.put("iCount", iCount);
            jsonObject.put("iType", iType);
            jsonObject.put("pCount", pCount);
            jsonObject.put("pType", pType);
            jsonObject.put("nmPoints", nmPoints);
            jsonObject.put("qScore", qScore);
            jsonObject.put("dpID", dpId);
            jsonObject.put("rdsID", rdsID);
            jsonObject.put("rdsVer", rdsVer);
            jsonObject.put("dc", dc);
            jsonObject.put("mi", mi);
            jsonObject.put("mc", mc);
            jsonObject.put("ci", ci);
            jsonObject.put("sessionKey", SessionKey);
            jsonObject.put("hmac", hmac);
            jsonObject.put("PidDatatype", PidDatatype);
            jsonObject.put("Piddata", Piddata);
/*
       jsonObject.put("errcode", "errcode1");
            jsonObject.put("errInfo", "errInfo1");
            jsonObject.put("fCount", "fCount1");
            jsonObject.put("fType", "fType1");
            jsonObject.put("iCount", "iCount1");
            jsonObject.put("iType", "iType1");
            jsonObject.put("pCount", "pCount1");
            jsonObject.put("pType", "pType1");
            jsonObject.put("nmPoints", "nmPoints1");
            jsonObject.put("qScore", "qScor1e");
            jsonObject.put("dpID", "dpI1d");
            jsonObject.put("rdsID", "rds1ID");
            jsonObject.put("rdsVer", "rdsVer");
            jsonObject.put("dc", "dc1");
            jsonObject.put("mi", "mi1");
            jsonObject.put("mc", "mc");
            jsonObject.put("ci", "c1i");
            jsonObject.put("sessionKey", "SessionK1ey");
            jsonObject.put("hmac", "hma1c");
            jsonObject.put("PidDatatype", "PidDatat1ype");
            jsonObject.put("Piddata", "Pidda1ta");*/
            pidData_json = jsonObject.toString();
            new apiCall_BalanceEnquiry().execute();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    Utils utils=new Utils();

    class apiCall_BalanceEnquiry extends AsyncTask<Request, Void, String> {
        @Override
        protected String doInBackground(Request... requests) {
            Log.d("TAG","IN API CALL");
            //Loading dialog in the time of api calling
//            loadingDialog.startLoading();

            //   MediaType JSON = MediaType.parse("application/json");
            //the json building using fingerprint data are used here as body of the api
            // RequestBody body = RequestBody.create(JSON, pidData_json);


           httpClient = utils.createAuthenticatedClient(username, password);
         //   httpClient = utils.createAuthenticatedClient("1010", "Test@123");
Log.d("TAG","Message is"+username+password);
          /*  RequestBody formBody = new FormBody.Builder()
                    .add("AdhaarNumber",en_aadhaar)
                    .add("imeiNumber", androidId)
                    .add("Piddata", Piddata)
                    .add("sessionKey",SessionKey)
                    .add("rdsVer",rdsVer)
                    .add("rdsID",rdsID)
                    .add("qScore",qScore)
                    .add("pType",pType)
                    .add("pCount",pCount)
                    .add("nmPoints",nmPoints)
                    .add("mi",mi)
                    .add("mc",mc)
                    .add("lattitude",latitude)
                    .add("longitude",longitude)
                    .add("iType",iType)
                    .add("iCount",iCount)
                    .add("hmac",hmac)
                    .add("fType",fType)
                    .add("fCount",fCount)
                    .add("errInfo",errInfo)
                    .add("errCode",errcode)
                    .add("dpID",dpId)
                    .add("dc",dc)
                    .add("ci",ci)
                    .add("PidDatatype", PidDatatype)
                    .add("Bankid", selected_bank_id)
                    .add("phnumber",en_phn)
                    .add("name",en_name)*/

                    /*.add("phnumber",en_phn)
                    .add("name",en_name)
                    .add("AdhaarNumber","9178874025")
                    .add("imeiNumber"," androidId")
                    .add("Piddata", "Piddata")
                    .add("sessionKey","SessionKey")
                    .add("rdsVer","rdsVer")
                    .add("rdsID","rdsID")
                    .add("qScore","qScore")
                    .add("pType","pType")
                    .add("pCount","pCount")
                    .add("nmPoints","nmPoints")
                    .add("mi","mi")
                    .add("mc","mc")
                    .add("lattitude","11.212")
                    .add("longitude","13.988")
                    .add("iType","iType")
                    .add("iCount","iCount")
                    .add("hmac","hmac")
                    .add("fType","fType")
                    .add("fCount","fCount")
                    .add("errInfo","errInfo")
                    .add("errCode","errcode")
                    .add("dpID","dpId")
                    .add("dc","dc")
                    .add("ci","ci")
                    .add("PidDatatype", "PidDatatype")
                    .add("Bankid", selected_bank_id)
                    .build();*/
            MediaType JSON = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(JSON, pidData_json);
            Request request = new Request.Builder()
                    .url("https://aepsapi.gramtarang.org:8008/mint/aeps/ipbalanceenquiry")
                    .addHeader("AdhaarNumber", en_aadhaar)
                    .addHeader("Bankid", selected_bank_id)
                    .addHeader("phnumber", en_phn)
                    .addHeader("name", en_name)
                    .addHeader("imeiNumber", androidId)
                    .addHeader("latitude", latitude)
                    .addHeader("longitude", longitude)
                    .addHeader("Accept", "*/*")
                   /* .addHeader("AdhaarNumber", "123456781190")
                     .addHeader("Bankid", "1234")
                     .addHeader("phnumber", "7896541230")
                     .addHeader("name", "megapower")
                     .addHeader("imeiNumber", "1234567890")
                     .addHeader("latitude", "123.1")
                     .addHeader("longitude", "145.2")*/

                    .post(body)
                    .build();
            httpClient.newCall(request).enqueue(new Callback() {
                @Override

                //of the api calling got failed then it will go for onFailure,inside this we have added one alertDialog
                public void onFailure(Call call, IOException e) {
                    Log.d("TAG","IN API CALL FAILURE"+e);
                    //loadingDialog.dismissDialog();
                    //  Toast.makeText(activity_Aeps_BalanceEnquiry.this, "Server not Connected.", Toast.LENGTH_SHORT).show();
                }

                //if API call got success then it will go for this onResponse also where we are collection
                //the response as well
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    assert response.body() != null;
                    //the response we are getting from api
                    response_String = response.body().string();

                    //  Toast.makeText(activity_Aeps_BalanceEnquiry.this, response_String, Toast.LENGTH_SHORT).show();

                    if (response_String != null) {
                        // loadingDialog.dismissDialog();
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(response_String);
                            // message = jsonResponse.getString("message");
                            status = jsonResponse.getString("status");
                            status_code = jsonResponse.getString("statuscode");
                            bank_RRN = jsonResponse.getString("ipay_uuid");
                            data = jsonResponse.getString("data");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            // the data we are getting from api in json format so
                            // we are parsing that data from here
                            JSONObject jsonData = new JSONObject(data);
                            transaction_status = jsonData.getString("status");
                            balance = jsonData.getString("balance");
                            //
                            //  transaction_type = jsonData.getString("sp_key");
                            fpTransId = jsonData.getString("opr_id");
                            merchant_transid = jsonData.getString("ipay_id");
                            timestamp = jsonData.getString("timestamp");
                        } catch (JSONException e) {

                        } catch (NullPointerException e) {
                        }
                        //moving to the next screen after getting the response also
                        // we are sending the require data through intent


                        Intent intent = new Intent(activity_Aeps_BalanceEnquiry.this, activity_Aeps_BalanceEnq_Receipt.class);
                        intent.putExtra("balance", balance);
                        intent.putExtra("merchant_transid", merchant_transid);
                        intent.putExtra("timestamp", timestamp);
                        intent.putExtra("aadhaar", en_aadhaar);
                        intent.putExtra("bank_name", selected_bank_name);
                        intent.putExtra("agent_id", agentId);
                        intent.putExtra("rrn_no", bank_RRN);
                        intent.putExtra("custName", en_name);
                        intent.putExtra("message", message);
                        intent.putExtra("fpTransId", fpTransId);
                        intent.putExtra("message", message);
                        intent.putExtra("status", status);
                        intent.putExtra("status_code", status_code);
                        intent.putExtra("transaction_type", transaction_type);
                        startActivity(intent);

                    } else {
                        Toast.makeText(activity_Aeps_BalanceEnquiry.this, "You are not getting any Response From Bank !! ", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            return null;
        }
    }
    public void sendDatatoServer(String op1) throws IOException {

        HttpUrl.Builder httpBuilder = HttpUrl.parse("https://aepsapi.gramtarang.org:8008/mint/aeps/printPXML").newBuilder();
        httpBuilder.addQueryParameter("pxml",op1);
        Request request = new Request.Builder().url(httpBuilder.build()).build();
        Response response = httpClient.newCall(request).execute();

    }
    public void responseCallback()

    {


    }
}


