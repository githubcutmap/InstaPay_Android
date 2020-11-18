package gramtarang.mint.agent_login;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import gramtarang.mint.R;
import gramtarang.mint.aeps.activity_Aeps_BalanceEnq_Receipt;
import gramtarang.mint.aeps.activity_Aeps_BalanceEnquiry;
import gramtarang.mint.api.DBApi;
import gramtarang.mint.utils.DialogActivity;
import gramtarang.mint.utils.LocationTrack;

import gramtarang.mint.utils.Utils;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/*acitivity_WelcomeScreen activity is containing the popup screen when the application
 * is opened*/
public class activity_WelcomeScreen extends AppCompatActivity {
    OkHttpClient client;
    ImageView img_mintLogo;
    TextView app_name;
    String agent_id,response_String,jsonString,areamanagerid;
    String agent_name;
    String agent_aadhar;
    String agent_phone;
    String agent_mail;
    String agent_pan;
    String agent_llt;
    String ag_details[];
    String app_version = "Version:Beta V 0.4";
    String dateofrelease;
    String latest_app_version;
    String androidId;
    String latitude;
    String longitude;
    String agentId;
    boolean isLatestVersion, isLocationgranted;
    SharedPreferences preferences;
    public static final String mypreference = "mypref";
    LocationTrack locationTrack;boolean isRooted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        img_mintLogo = (ImageView) findViewById(R.id.logo);
        app_name = findViewById(R.id.app_name);

        Animation myanim = AnimationUtils.loadAnimation(this, R.anim.logoanimation);
        img_mintLogo.startAnimation(myanim);
        app_name.startAnimation(myanim);
        client = new OkHttpClient();

        locationTrack = new LocationTrack(activity_WelcomeScreen.this);
        if (locationTrack.canGetLocation()) {
            longitude = String.valueOf(locationTrack.getLongitude());
            latitude = String.valueOf(locationTrack.getLatitude());

        }
        else {
            locationTrack.showSettingsAlert();
        }
        new apiCall_getversion().execute();
        //   androidId= Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);


        Utils util=new Utils();
        isRooted=util.isDeviceRooted();
        if(isRooted){
            DialogActivity.DialogCaller.showDialog(activity_WelcomeScreen.this,"Alert","App can't run on rooted devices.",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishAffinity();
                }
            });
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            isLocationgranted=true;
        }
        if (!isConnected()) {
            DialogActivity.DialogCaller.showDialog(activity_WelcomeScreen.this,"Alert","No Internet Connection",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
        }
        try {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                DialogActivity.DialogCaller.showDialog(activity_WelcomeScreen.this,"Alert","Unable retrieve the location.\nPlease enable location permission and restart the app.",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(0);
                    }
                });
            }

            if(isLocationgranted  && latitude.equals("0.0") && longitude.equals("0.0")){
                Intent intent = new Intent(this,activity_WelcomeScreen.class);
                this.startActivity(intent);
                this.finishAffinity();
            }

        }
        catch (NullPointerException e){
            e.printStackTrace();
        }
    }
    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        DialogActivity.DialogCaller.showDialog(getApplicationContext(),"Alert","No Internet Connection",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
                finish();
            }
        }, 4000);
    }
    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception", e.getMessage());
        }
        return connected;
    }
/*Utils utils=new Utils();
    OkHttpClient httpClient = utils.createAuthenticatedClient("1010", "Test@123");*/
    class apiCall_getversion extends AsyncTask<Request, Void, String> {
        @Override
        protected String doInBackground(Request... requests) {
            okhttp3.Request request = new Request.Builder()
                    .url("https://aepsapi.gramtarang.org:8008/mint/im/version")
                    .addHeader("Accept", "*/*")
                    .get()
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    DialogActivity.DialogCaller.showDialog(activity_WelcomeScreen.this,"Alert","Server Unreachable.",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAffinity();
                        }
                    });
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    assert response.body() != null;
                    //the response we are getting from api
                    response_String = response.body().string();
                    if (response_String != null) {
                        Log.d("TAG","Response is+"+response_String.toString());
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(response_String);
                            latest_app_version= jsonResponse.getString("version_number");
                            dateofrelease = jsonResponse.getString("date_of_release");
                            Log.d("TAG","SAME CLASS"+latest_app_version+dateofrelease);
                            //   setText(tv_dateofrelease,dateofrelease,tv_version,latest_app_version);

                            //Log.d("TAG","SAME CLASS"+latest_app_version+dateofrelease+androidId+latitude+longitude);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        catch (NullPointerException e) {
                        }
                        if(!getString(R.string.app_version).equals(latest_app_version)){
                            Log.d("TAG","Errorrrrr"+getString(R.string.app_version)+latest_app_version);
                            activity_WelcomeScreen.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    DialogActivity.DialogCaller.showDialog(activity_WelcomeScreen.this,"Alert","Outdated Version.\nPlease Contact Administrator.",new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });
                                }
                            });
                            /*   */
                        }
                        else{
                            isLatestVersion=true;
                            if(isLatestVersion && !isRooted&&isConnected() && !latitude.equals("0.0") && !longitude.equals("0.0") ){
                                activity_WelcomeScreen.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        move();
                                    }
                                });

                            }
                        }



                    }

                }
            });

            return null;
        }

    }
    public void move(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("Latitude",latitude);
                editor.putString("Longitude",longitude);
                editor.commit();
                Intent i = new Intent(activity_WelcomeScreen.this, activity_Login.class);
                startActivity(i);
                finish();
            }
        }, 2000);
    }
}

