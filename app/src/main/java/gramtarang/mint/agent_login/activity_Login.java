package gramtarang.mint.agent_login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.res.FontResourcesParserCompat;


import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;
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
import gramtarang.mint.api.DBApi;
import gramtarang.mint.api.MobileSMSAPI;

import gramtarang.mint.utils.CaptureResponse;
import gramtarang.mint.utils.ConnectionClass;
import gramtarang.mint.utils.DialogActivity;
import gramtarang.mint.utils.LogOutTimer;
import gramtarang.mint.utils.LoginVerification;
import gramtarang.mint.utils.SQLQueries;
import gramtarang.mint.utils.Utils;
import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.FormBody;
import okhttp3.Route;


/*activity_Login activity
 * enable the merchant to Login into MINT App*/
public class activity_Login extends AppCompatActivity implements LogOutTimer.LogOutListener {
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
        Intent intent=new Intent(activity_Login.this,activity_WelcomeScreen.class);
        startActivity(intent);
    }
    Utils utils = new Utils();
    private Timer timer;
    public final String mypreference = "mypref";
    private final String TAG = "Login_Activity";

    SharedPreferences preferences;
    String verification_type,latest_app_version;
    int role;
    CoordinatorLayout coordinatorLayout;

    String agentname,bankmitraid,agentPassword;
    String generated_pin;
    String agentphn;
    String agentemail;
    String latitude;
    String longitude;

    String username;

    String androidId;
    String appversion;
    String dateofrelease;
    String selected_method;
    String response_String;
    String jsonString;
    String timestamp,areamanager_id;
    EditText et_userName, et_loginOptions,et_pass;
    ConnectionClass connectionClass;
    int selected_option = 1, i;
    boolean isValidUsername,isphnregistered,isemailregistered;
    TextView tv_version, tv_dateofrelease, tv_androidId;
    Button btn_login;
    int aeps,loan,bbps,pan,card;
    ImageView btn_loginOptions;
    OkHttpClient client,httpClient;
    boolean doubleBackToExitPressedOnce = false;
    MobileSMSAPI sms = new MobileSMSAPI();
    //This method working as auto scaling of ui by density
    /*public void adjustFontScale(Configuration configuration) {
        configuration.fontScale = (float) 1.0;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        getBaseContext().getResources().updateConfiguration(configuration, metrics);
    }

    @SuppressLint("NewApi")
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
*/

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onConfigurationChanged(getResources().getConfiguration());
        //adjustFontScale(getResources().getConfiguration());
        setContentView(R.layout.activity_login);
        preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        androidId= Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        latitude = preferences.getString("Latitude", "No name defined");
        longitude = preferences.getString("Longitude", "No name defined");
        //androidId = preferences.getString("AndroidId", "No name defined");
        appversion = preferences.getString("AppVersion", "No name defined");
        dateofrelease = preferences.getString("DateofRelease", "No name defined");
        Log.d("TAG", "Login:" + latitude + longitude + androidId + appversion + dateofrelease);
        // Initialization of all the UI component
        et_userName = findViewById(R.id.username);
        et_pass = findViewById(R.id.password);
        //  btn_loginOptions = findViewById(R.id.right_arrow);
        btn_login = findViewById(R.id.login_button);
        btn_login.setEnabled(true);
        tv_version = findViewById(R.id.version);
        tv_dateofrelease = findViewById(R.id.dateofr);
        tv_androidId = findViewById(R.id.andid);
        //  et_loginOptions = findViewById(R.id.select);
        tv_version.setText(R.string.app_version);
        tv_dateofrelease.setText(R.string.dateofrelease);
        client = new OkHttpClient();

        Utils util = new Utils();


        tv_androidId.setText(androidId);
        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        timestamp = s.format(new Date());

        //Login Button clicked
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    username = et_userName.getText().toString(); } catch (NullPointerException e) {
                    et_userName.setError("Enter Username");

                }
                try{
                    agentPassword = et_pass.getText().toString();}catch (NullPointerException e){
                    et_pass.setError("Enter Password");
                }
                isValidUsername = util.isValidName(username);

                if (!isValidUsername) {
                    et_userName.setError("Enter Valid Username");
                }
                else{
                    new apiCall_getagentdetails().execute();
                }


             /*   if (selected_option != 1) {


                } else {

                    Toast.makeText(activity_Login.this, "Please Select Login Option ", Toast.LENGTH_LONG).show();
                    btn_login.setEnabled(true);
                }*/
            }
        });
    }




    //On back button it will automatically logout or brings to user into login screen
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Click again to Close the app.", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
                finish();
            }
        }, 2000);
    }

    // necessary data are saving into SQL database
    public void insertlog() {
       /* SQLQueries insert_login_logs = new SQLQueries();
        insert_login_logs.insert_login_log(i, agentId, username, androidId, latitude, longitude, timestamp, login_status);

*/
    }

/*    private static OkHttpClient createAuthenticatedClient(final String api_username,
                                                          final String api_password) {
        // build client with authentication information.
        OkHttpClient httpClient = new OkHttpClient.Builder().authenticator(new Authenticator() {
            public Request authenticate(Route route, Response response) throws IOException {
                String credential = Credentials.basic(api_username, api_password);
                return response.request().newBuilder().header("Authorization", credential).build();
            }
        }).build();
        return httpClient;
    }

    OkHttpClient httpClient = createAuthenticatedClient("apiadminuser", "CuTm@_GtiDs+3#2020@!");*/


//String en_flag= BCrypt.hashpw("Test@123", BCrypt.gensalt(12));




    class apiCall_getagentdetails extends AsyncTask<Request, Void, String> {

        @Override
        protected String doInBackground(Request... requests) {
            JSONObject jsonObject = new JSONObject();
            //  Log.d("TAG","EN_FLAG"+en_flag);
            try {
                httpClient = utils.createAuthenticatedClient(username,agentPassword);
                System.out.println("USERNAME "+username+"PASSWORD "+agentPassword);
                jsonObject.put("id",username);
                jsonObject.put("password",agentPassword);
                jsonString = jsonObject.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            MediaType JSON = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(JSON, jsonString);
            Request request = new Request.Builder()
                    .url("https://aepsapi.gramtarang.org:8008/mint/loans/getagentdetails")
                    .addHeader("Accept", "*/*")
                    // .addHeader("Authorization","Basic MTAxMDpUZXN0QDEyMw==")
                    .post(body)
                    .build();
            httpClient.newCall(request).enqueue(new Callback() {
                @Override

                //of the api calling got failed then it will go for onFailure,inside this we have added one alertDialog
                public void onFailure(Call call, IOException e) {
                    Log.d("TAG", "response onfailure"+e );
                    //Snackbar.make(coordinatorLayout, "Agent not registered.\\nPlease Contact Administrator", Snackbar.LENGTH_LONG).setAction("action",null).show();
                   /* DialogActivity.DialogCaller.showDialog(activity_Login.this,"Login Failed","Server Unreachable.\nPlease contact administrator.",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });*/
                    // Toast.makeText(activity_Login.this,"Agent not registered.\nPlease Contact Administrator"+androidId,Toast.LENGTH_SHORT).show();

                }

                //if API call got success then it will go for this onResponse also where we are collection
                //the response as well
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    assert response.body() != null;
                    response_String = response.body().string();
                    System.out.println("RESPONSE IS"+response_String);
                    if (response_String != null) {
                        JSONObject jsonResponse = null;

                        try {
                            jsonResponse = new JSONObject(response_String);
                            JSONArray llist1 = jsonResponse.getJSONArray("llist1");
                            agentemail = llist1.getJSONObject(0).getString("email");
                            agentphn = llist1.getJSONObject(0).getString("contact_no");
                            agentname = llist1.getJSONObject(0).getString("name");
                            bankmitraid=llist1.getJSONObject(0).getString("id");
                            areamanager_id=llist1.getJSONObject(0).getString("area_manager_id");
                            role=llist1.getJSONObject(0).getInt("role");
                            aeps=llist1.getJSONObject(0).getInt("aeps");
                            pan=llist1.getJSONObject(0).getInt("pan");
                            bbps=llist1.getJSONObject(0).getInt("bbps");
                            card=llist1.getJSONObject(0).getInt("card");
                            loan=llist1.getJSONObject(0).getInt("loan");
                            Log.d("TAG","AMID:"+areamanager_id+"Roles:"+aeps+pan+card+loan+bbps);
                            activity_Login.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //Snackbar.make(coordinatorLayout, "success", Snackbar.LENGTH_LONG).setAction("action",null).show();

                                    method(agentemail,agentname,agentphn,bankmitraid,areamanager_id,aeps,pan,bbps,loan,card,isphnregistered,isemailregistered);
                                }
                            });

                        } catch (JSONException e) {
                            Log.d("TAG", "Login Failed" );
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    DialogActivity.DialogCaller.showDialog(activity_Login.this,"Login Failed","Agent not registered.\nPlease contact administrator."+androidId,new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            });

                            /*activity_Login.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(activity_Login.this,"Agent not registered",Toast.LENGTH_SHORT).show();
                                }
                            });*/

                        }

                        Log.d("TAG", "Response Email:" + agentemail + agentname + agentphn);
                    }

                }

            });


            return null;
        }

    }
    public void method(String email,String name,String phn,String bankmitraid,String areamanagerid,int aeps,int pan,int bbps,int loan,int card,boolean isphnregistered,boolean isemailregistered){
        // utils.getprogressDialog(activity_Login.this, "Logging in", "Please Wait");
        verification_type = "OTP";
//            btn_login.setEnabled(false);
        generated_pin = utils.getOTPString();
        Log.d("TAG","pin: "+generated_pin);
        sms.sendSms1(generated_pin, phn, name);
        try {
            username = et_userName.getText().toString();
            Log.d("TAG","Entered"+username);
        } catch (NullPointerException e) {
            et_userName.setError("Enter Username");

        }
        androidId= Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        Log.d("TAG","Sample"+agentphn);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("AndroidId",androidId);
        editor.putString("Username",username);
        editor.putString("Password",agentPassword);
        editor.putString("AgentName", name);
        editor.putString("AgentEmail", email);
        editor.putString("AgentPhone", phn);
        editor.putString("BankMitraId", bankmitraid);
        editor.putString("VerificationMethod", verification_type);
        editor.putString("LoginOTP", generated_pin);
        editor.putString("AreaManagerId", areamanagerid);
        editor.putInt("Role", role);
        editor.putInt("aeps",aeps);
        editor.putInt("pan",pan);
        editor.putInt("loan",loan);
        editor.putInt("bbps",bbps);
        editor.putInt("card",card);
        editor.commit();

        Intent i = new Intent(activity_Login.this, LoginVerification.class);
        startActivity(i);

    }

}

