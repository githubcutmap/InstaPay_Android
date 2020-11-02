package gramtarang.mint.agent_login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import gramtarang.mint.R;
import gramtarang.mint.aeps.activity_Aeps_HomeScreen;
import gramtarang.mint.utils.SQLQueries;


public class activity_AgentsProfile extends AppCompatActivity {
    SharedPreferences preferences;
    public static final String mypreference = "mypref";

    private static final String TAG = "AgentProfile";
    ImageView backbtn;
    TextView agent_id,agent_name,agent_aadhar,agent_phone,agent_mail,agent_pan,agent_llt;
    String ag_details[],androidId;
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
        agent_pan = findViewById(R.id.agent_pan);
        agent_llt = findViewById(R.id.agent_llt);
        preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        androidId=preferences.getString("AndroidId","No name defined");
        SQLQueries query=new SQLQueries();
        ag_details= query.getAgentDetails(androidId);
        agent_id.setText(ag_details[0]);
        agent_name.setText(ag_details[1]+" "+ag_details[2]);
        agent_aadhar.setText(ag_details[3]);
        agent_phone.setText(ag_details[4]);
        agent_mail.setText(ag_details[5]);
        agent_pan.setText(ag_details[6]);
        agent_llt.setText(ag_details[7]);

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
