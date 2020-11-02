package gramtarang.mint.aadhaarPay;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

import gramtarang.mint.R;
import gramtarang.mint.aeps.activity_Aeps_HomeScreen;
import gramtarang.mint.agent_login.activity_Login;
import gramtarang.mint.api.MobileSMSAPI;
import gramtarang.mint.utils.ConnectionClass;
import gramtarang.mint.utils.LogOutTimer;
import gramtarang.mint.utils.SQLQueries;


/*AdhaarPay_Receipt activity
* this activity contains the data coming after the AdhaarPAy transaction*/
public class AadhaarPay_Receipt extends AppCompatActivity implements LogOutTimer.LogOutListener {
    private String TAG="Aadharpay";
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
    TextView tv_timestamp,tv_bal,tv_bankname,tv_aadhaarnumber,tv_transid,tv_agentid,tv_rrnno, tv_customerName, tv_descriptionMessage,tv_transamount;
    String agent_phone_number,agent_name,transtype="AADHAAR PAY",latitude,longitude,ipAddress,timestamp,available_balance,bankName,aadhaar_number,trans_id,agentid,rrn_no,custName,message,trans_amount,macAddress,androidId,fpTransId,status,status_code,transaction_type="AdhaarPay";
    Button btn_back;
    ConnectionClass connectionClass;int i;

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

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        adjustFontScale(getResources().getConfiguration());
        onConfigurationChanged(getResources().getConfiguration());
        setContentView(R.layout.activity_aadhaar_pay__receipt);


        tv_aadhaarnumber=findViewById(R.id.aadhaar_number);
        tv_bal=findViewById(R.id.available_balance);
        tv_bankname=findViewById(R.id.bank_name);
        tv_timestamp=findViewById(R.id.timestamp);
        tv_transid=findViewById(R.id.merchant_transid);
        tv_agentid=findViewById(R.id.agent_id);
        tv_rrnno=findViewById(R.id.rrn_no);
        tv_transamount=findViewById(R.id.trans_amount);
        tv_descriptionMessage =findViewById(R.id.tex_message);
        tv_customerName =findViewById(R.id.customer_name);
        btn_back =findViewById(R.id.back);

        //data retrive from sharedPreference
        preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        latitude=preferences.getString("Latitude","No name defined");
        longitude=preferences.getString("Longitude","No name defined");
        androidId=preferences.getString("AndroidId","No name defined");
        agent_phone_number=preferences.getString("AgentPhn","No name defined");
        agent_name=preferences.getString("AgentName","No name defined");
        Intent intent = getIntent();

        SimpleDateFormat s = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        timestamp = s.format(new Date());
        // data retrive from AdhaarPay.java
        available_balance= intent.getStringExtra("balance");
        bankName = intent.getStringExtra("bank_name");
        aadhaar_number = intent.getStringExtra("aadhaar");
        trans_id = intent.getStringExtra("merchant_transid");
        rrn_no=intent.getStringExtra("rrn_no");
        custName=intent.getStringExtra("custName");
        message=intent.getStringExtra("message");
        trans_amount=intent.getStringExtra("trans_amount");
        fpTransId=intent.getStringExtra("fpTransId");
        status=intent.getStringExtra("status");
        status_code=intent.getStringExtra("status_code");
        transaction_type=intent.getStringExtra("transaction_type");

        //Data retrive from SQL dataBase
        SQLQueries getAgentId=new SQLQueries();
       // agentid=getAgentId.getAgentID(androidId);
        SQLQueries insertintodb=new SQLQueries();
        insertintodb.inserttranslogs(i,androidId,latitude,longitude,custName,trans_id,status,status_code,message,transtype,timestamp,fpTransId,rrn_no,agentid,trans_amount,available_balance);
        MobileSMSAPI sendmsg=new MobileSMSAPI();
        sendmsg.sendtransmsg(agent_phone_number,agent_name,message,transtype);
        tv_bankname.setText(bankName);
        if(rrn_no==null){
            tv_rrnno.setText("Not Applicable");
        }
        else{
            tv_rrnno.setText(rrn_no);
        }
        if(trans_id==null){
            tv_transid.setText("Not Applicable");
        }
        else{
            tv_transid.setText(fpTransId);
        }
        if(available_balance==null){
            tv_bal.setText("Not Applicable");
        }
        else{
            tv_bal.setText(available_balance);

        }
        tv_agentid.setText(agentid);
        tv_descriptionMessage.setText(message);
        tv_customerName.setText(custName);
        tv_aadhaarnumber.setText("XXXX" + " " + "XXXX" + " " + aadhaar_number.charAt(8) + aadhaar_number.charAt(9) + aadhaar_number.charAt(10) + aadhaar_number.charAt(11));
        tv_timestamp.setText(timestamp);
        tv_transamount.setText(trans_amount);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AadhaarPay_Receipt.this, activity_Aeps_HomeScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}
