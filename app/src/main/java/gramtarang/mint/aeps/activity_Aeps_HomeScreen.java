package gramtarang.mint.aeps;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import gramtarang.mint.R;
import gramtarang.mint.agent_login.activity_AgentsProfile;
import gramtarang.mint.agent_login.activity_Login;
import gramtarang.mint.loans.LoanActivity_MainScreen;
import gramtarang.mint.utils.ConnectionClass;
import gramtarang.mint.utils.LogOutTimer;
import gramtarang.mint.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/*activity_Aeps_HomeScreen activity is the base activity of all transactions
*/
public class activity_Aeps_HomeScreen extends AppCompatActivity implements LogOutTimer.LogOutListener {
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

    private static final String TAG ="MenuScreen" ;
    String androidId,lastlogin_time,response_String, bankmitra_id,jsonString,agentname;
    OkHttpClient client;
    TextView menu_timestamp,textMessage;
    TextView time;
    TextView agent_name;
    TextView transName;
    ImageView aadhaarPay,aepsBalance,aepsWithdraw,aepsDeposit,ministatement,eodreport,accOpen,loan,rrnStatus,billPayments,card,logout;


    Switch transSwitch;
    String agent_firstname,agent_lastname,title,message;
    ConnectionClass connectionClass;
    private static final int REQUEST_CODE = 101;
    SharedPreferences preferences;
    public static final String mypreference = "mypref";
    Utils utils = new Utils();

    //This method working as auto scaling of ui by density
/*    public void adjustFontScale(Configuration configuration) {

        configuration.fontScale = (float) 1.0;
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        metrics.scaledDensity = configuration.fontScale * metrics.density;
        getBaseContext().getResources().updateConfiguration(configuration, metrics);
    }*/
   /* //This method working as auto scaling of ui by density
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

    }*/

    //logout, Profile on the option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    //Options in the menuOption this onOptionMenueItemSelected method enable to click the items are
    //present inside option menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_dropdown_myacc:
                Intent intent = new Intent(getApplicationContext(), activity_AgentsProfile.class);
                startActivity(intent);
                return true;
            case R.id.action_dropdown_logout:
                Intent intent2 = new Intent(getApplicationContext(), activity_Login.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent2);
                return true;
            case R.id.action_dropdown_eodreport:
                title = "End Of Day Report";
                message = "  This function is not available in your login please try after some time or contact your admin";
                utils.dialog(activity_Aeps_HomeScreen.this,title,message);
                return true;
            case R.id.action_dropdown_aboutmint:
                title = "About MINT";
                message = "  This function is not available in your login please try after some time or contact your admin";
                utils.dialog(activity_Aeps_HomeScreen.this,title,message);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

 /*  boolean doubleBackToExitPressedOnce = false;
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
                Intent intent=new Intent(getApplicationContext(),activity_Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }, 2000);
    }*/


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //adjustFontScale(getResources().getConfiguration());
        onConfigurationChanged(getResources().getConfiguration());
        setContentView(R.layout.activity_menu_screen);
client=new OkHttpClient();
        preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        androidId=preferences.getString("AndroidId","No name defined");
        bankmitra_id =preferences.getString("AgentId","No name defined");
        agentname=preferences.getString("AgentName","No name defined");
        menu_timestamp=findViewById(R.id.menu_timestamp);
        textMessage = findViewById(R.id.textMessage);
        agent_name = findViewById(R.id.agent_name);
        agent_name.setText(agentname);
        aepsBalance=findViewById(R.id.aeps_balenquiry);
        aepsWithdraw=findViewById(R.id.aeps_withdraw);
      // ministatement = findViewById(R.id.image_ministatement);
        aepsDeposit = findViewById(R.id.deposit);
      //  eodreport=findViewById(R.id.eod);
        ministatement=findViewById(R.id.image_ministatement);
        aadhaarPay=findViewById(R.id.aadhaar_pay);
      //  loan=findViewById(R.id.loan);
       // transName = findViewById(R.id.trans_name);
        rrnStatus = findViewById(R.id.rrnstatus);
        //billPayments = findViewById(R.id.billPayments);
       // card = findViewById(R.id.card);
        logout=findViewById(R.id.logout);


        ministatement.setEnabled(true);
        aepsWithdraw.setEnabled(true);
        aepsBalance.setEnabled(true);
        aadhaarPay.setEnabled(true);
//        loan.setEnabled(true);
new apiCall_getlastlogin().execute();
        //APICalling apiCalling = new APICalling();
        //ArrayList arrayList = apiCalling.getagentdetails("2323","b912df01ef572d57");
        //Log.d("TAG","agents"+arrayList);

        //SQLQueries getAgentName=new SQLQueries();
        //agent_firstname=getAgentName.GetAgentName(androidId);
//        agent_name.setText((CharSequence) arrayList);
        //lastlogin_time=getAgentName.getlastlogin(androidId);
        Log.d("TAG","Last Login Time is:"+lastlogin_time);
      //  try{menu_timestamp.setText(lastlogin_time.substring(8,10)+"-"+lastlogin_time.substring(5,7)+"-"+lastlogin_time.substring(0,4)+" "+lastlogin_time.substring(11,16));}  catch (Exception e){menu_timestamp.setText("--");}


logout.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getApplicationContext(),activity_Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
});

/*String time;
time=getlAgentName.getLastLogin(androidId);
menu_timeaststamp.setText(time);*/



        //this is the toogle button between card and AEPs transaction
       /* BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_aeps:
                        Toast.makeText(getApplicationContext(), "Aeps", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_card:
                        Toast.makeText(getApplicationContext(), "Card", Toast.LENGTH_SHORT).show();
                        title = "Card";
                        message = "This function is not available in your login please try after some time or contact your admin";
                        dialog(title,message);
                        break;
                    case R.id.action_billPayment:
                        Toast.makeText(getApplicationContext(), "Bill Payment", Toast.LENGTH_SHORT).show();
                        title = "Bill Payment";
                        message = "This function is not available in your login please try after some time or contact your admin";
                        dialog(title,message);
                        break;
                    case R.id.action_loan:
                        Toast.makeText(getApplicationContext(), "Loan", Toast.LENGTH_SHORT).show();
                        title = "Loan";
                        Intent intent = new Intent(getApplicationContext(), LoanActivity_PrimaryScreen.class);
                        startActivity(intent);
                        break;
                }
                return true;
            }
        });*/


        //this button navigating to AEPs balance enquiry screen
        aepsBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*title = "AEPS Balance Enquiry";
                message = "  This function is not available in your login please try after some time or contact your admin";
                utils.dialog(activity_Aeps_HomeScreen.this,title,message);*/
                //aepsBalance.setEnabled(false);
                Intent intent = new Intent(activity_Aeps_HomeScreen.this, activity_Aeps_BalanceEnquiry.class);
                startActivity(intent);

            }
        });
        //this button onclick listener navigating to the user to  AEPS withdraw screen
        aepsWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*title = "AEPS Withdraw";
                message = "  This function is not available in your login please try after some time or contact your admin";
                utils.dialog(activity_Aeps_HomeScreen.this,title,message);
                aepsWithdraw.setEnabled(false);*/
                Intent intent = new Intent(activity_Aeps_HomeScreen.this, activity_Aeps_Withdraw.class);
                startActivity(intent);
            }
        });

//This button onclickListener directly navigating the user to the AEPS ministatement screen
        ministatement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*title = "Mini Statement";
                message = "  This function is not available in your login please try after some time or contact your admin";
                utils.dialog(activity_Aeps_HomeScreen.this,title,message);
                ministatement.setEnabled(false);*/

                Intent intent = new Intent(activity_Aeps_HomeScreen.this, activity_Aeps_Ministatement.class);
                startActivity(intent);
            }
        });
//This adaarPay button onClickLlistener directly navigate the user to AdhaarPay screen
        aadhaarPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = "Aadhaar Pay";
                message = "  This function is not available in your login please try after some time or contact your admin";
                utils.dialog(activity_Aeps_HomeScreen.this,title,message);
              /*  aadhaarPay.setEnabled(false);
                Intent intent = new Intent(activity_Aeps_HomeScreen.this, AadhaarPay.class);
                startActivity(intent);*/
              /*  title = "Account Opening";
                message = "  This function is not available in your login please try after some time or contact your admin";
                dialog(title,message);*/
            }
        });

        //This eodreport Image button navigate the user directly to EOD report screen
       /* eodreport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = "End Of Day Report";
                message = "  This function is not available in your login please try after some time or contact your admin";
                dialog(title,message);
            }
        });*/

       aepsDeposit.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               title = "AEPS Deposit";
               message = "  This function is not available in your login please try after some time or contact your admin";
               utils.dialog(activity_Aeps_HomeScreen.this,title,message);

           }
       });

        rrnStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = "RRN Status";
                message = "  This function is not available in your login please try after some time or contact your admin";
                utils.dialog(activity_Aeps_HomeScreen.this,title,message);
            }
        });



        Utils gethour=new Utils();
        String hour = gethour.gethour();
        textMessage.setText(hour+"!");
    }
    class apiCall_getlastlogin extends AsyncTask<Request, Void, String> {
        @Override
        protected String doInBackground(Request... requests) {
            JSONObject jsonObject = new JSONObject();
            try {
                Log.d("TAG","ANDROID"+androidId);
                jsonObject.put("androidid", androidId);
                jsonObject.put("loginstatus", "Success");
                jsonString = jsonObject.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            MediaType JSON = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(JSON, jsonString);
            Request request = new Request.Builder()
                    .url("http://mintserver.gramtarang.org:8080/mint/im/getagentlastlogin")
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
                    response_String = response.body().string();
                    if (response_String != null) {
                        JSONObject jsonResponse = null;
                        try {
                            jsonResponse = new JSONObject(response_String);
                            JSONArray llist1 = jsonResponse.getJSONArray("getagentlastlogin");
                            lastlogin_time = llist1.getJSONObject(0).getString("timestamp");
                            Log.d("TAG","Last Login is:"+lastlogin_time);
                            menu_timestamp.setText(lastlogin_time.substring(8,10)+"-"+lastlogin_time.substring(5,7)+"-"+lastlogin_time.substring(0,4)+" "+lastlogin_time.substring(11,16));



                        } catch (Exception e) {
                            e.printStackTrace();
                        }





                    }
                }

            });


            return null;
        }

    }
}