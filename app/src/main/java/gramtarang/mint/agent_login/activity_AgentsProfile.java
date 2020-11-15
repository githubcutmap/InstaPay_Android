package gramtarang.mint.agent_login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import gramtarang.mint.R;
import gramtarang.mint.aeps.activity_Aeps_HomeScreen;


public class activity_AgentsProfile extends AppCompatActivity {
    SharedPreferences preferences;
    public static final String mypreference = "mypref";

    private static final String TAG = "AgentProfile";
    ImageView backbtn;
    TextView agent_id,agent_name,agent_aadhar,agent_phone,agent_mail,agent_areamgr,agent_areamgr_id;
    String ag_details[],androidId,agentName,agentEmail,agentPhone,agentId,areamanager,areamanagerId, agentAadhaar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_profile);

        backbtn = findViewById(R.id.backimg);
        agent_id = findViewById(R.id.agent_id);
        agent_name = findViewById(R.id.agent_name);
        agent_aadhar = findViewById(R.id.agent_aadhar);
        agent_phone = findViewById(R.id.agent_phone);
        agent_mail = findViewById(R.id.agent_mail);
        agent_areamgr=findViewById(R.id.agent_areamgr);
        agent_areamgr_id=findViewById(R.id.agent_areamgr_id);

        preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        androidId=preferences.getString("AndroidId","No name defined");

        agentName=preferences.getString("AgentName","No name defined");
        agentEmail=preferences.getString("AgentEmail","No name defined");
        agentPhone=preferences.getString("AgentPhone","No name defined");
        agentId=preferences.getString("Username","No name defined");
        areamanager=preferences.getString("AreaManagerName","No name defined");
        areamanagerId=preferences.getString("AreaManagerId","No name defined");
        agentAadhaar=preferences.getString("AgentAadhaar","No name defined");
try{
    agent_id.setText(agentId);
    agent_name.setText(agentName);
    agent_mail.setText(agentEmail);
    agent_phone.setText(agentPhone);
    agent_aadhar.setText(agentAadhaar);
    agent_areamgr_id.setText(areamanagerId);
    agent_areamgr.setText(areamanager);
}catch (Exception e){
    e.printStackTrace();
}
Log.d("TAG","Responses are:"+androidId+agentPhone+agentEmail+agentName+agentId+areamanager+areamanagerId);
        //back button
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), activity_Aeps_HomeScreen.class);
                startActivity(intent);
            }
        });
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
                Intent intent=new Intent(getApplicationContext(),activity_Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }, 2000);
    }
}
