package gramtarang.mint.agent_login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import gramtarang.mint.R;
import gramtarang.mint.aeps.activity_Aeps_HomeScreen;
import gramtarang.mint.loans.LoanActivity_MainScreen;
import gramtarang.mint.pan.PanCard;
import gramtarang.mint.utils.DialogActivity;

public class Dashboard extends AppCompatActivity {
    SharedPreferences preferences;
    public static final String mypreference = "mypref";
    String agentname;
    int aeps,bbps,loan,pan,card;
    ImageView imaeps,imbbps,impan,imcard,imloan;
    LinearLayout llaeps,llbbps,llpan,llcard,llloan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        agentname=preferences.getString("AgentName","No name defined");
        aeps=preferences.getInt("aeps",0);
        pan=preferences.getInt("pan",0);
        bbps=preferences.getInt("bbps",0);
        loan=preferences.getInt("loan",0);
        card=preferences.getInt("card",0);
        //   Log.d("TAG","Permissions:"+aeps+pan+loan+bbps+card);
        imaeps=findViewById(R.id.aeps);
        impan=findViewById(R.id.pan);
        imloan=findViewById(R.id.loan);
        imcard=findViewById(R.id.card);
        imbbps=findViewById(R.id.bbps);

        llaeps=findViewById(R.id.ll_aeps);
        llbbps=findViewById(R.id.ll_bbps);
        llpan=findViewById(R.id.ll_pan);
        llcard=findViewById(R.id.ll_card);
        llloan=findViewById(R.id.ll_loan);


/*        if(aeps == 1){
            llaeps.setVisibility(View.VISIBLE);
        }
        if(pan == 1){
            llpan.setVisibility(View.VISIBLE);
        }
        if(bbps == 1){
            llbbps.setVisibility(View.VISIBLE);
        }
        if(loan == 1){
            llloan.setVisibility(View.VISIBLE);
        }
        if(card == 1){
            llcard.setVisibility(View.VISIBLE);
        }*/


        imaeps.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View v) {
                                          if(aeps==1){
                                              Intent intent=new Intent(Dashboard.this, activity_Aeps_HomeScreen.class);
                                              startActivity(intent);
                                          }
                                          else{
                                              DialogActivity.DialogCaller.showDialog(Dashboard.this,"Alert","This feature is not available for your login.",new DialogInterface.OnClickListener() {
                                                  @Override
                                                  public void onClick(DialogInterface dialog, int which) {
                                                      dialog.dismiss();
                                                  }
                                              });
                                          }
                                      }
                                  }
        );
        impan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pan==1){
                    Intent intent=new Intent(Dashboard.this, PanCard.class);
                    startActivity(intent);
                }
                else{
                    DialogActivity.DialogCaller.showDialog(Dashboard.this,"Alert","This feature is not available for your login.",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });
        imbbps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bbps==1){
                    Intent intent=new Intent(Dashboard.this, activity_Aeps_HomeScreen.class);
                    startActivity(intent);
                }
                else{
                    DialogActivity.DialogCaller.showDialog(Dashboard.this,"Alert","This feature is not available for your login.",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });
        imloan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loan==1){
                    Intent intent=new Intent(Dashboard.this, LoanActivity_MainScreen.class);
                    startActivity(intent);
                }
                else{
                    DialogActivity.DialogCaller.showDialog(Dashboard.this,"Alert","This feature is not available for your login.",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });
        imcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(card==1){
                    Intent intent=new Intent(Dashboard.this, activity_Aeps_HomeScreen.class);
                    startActivity(intent);
                }
                else{
                    DialogActivity.DialogCaller.showDialog(Dashboard.this,"Alert","This feature is not available for your login.",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                }
            }
        });
    }
}