package gramtarang.mint.agent_login;

import androidx.fragment.app.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import gramtarang.mint.R;
import gramtarang.mint.utils.DialogActivity;
import gramtarang.mint.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WalletFragment extends Fragment {
    OkHttpClient httpClient;
    Utils utils;
TextView tv_totalwd,tv_totalms,tv_walletamount,tv_totalbe;
 Context con;
    SharedPreferences preferences;
    public static final String mypreference = "mypref";
    String totalWithdrawCount, totalMinistatementCount,agentPhn,agentId,password,totalBECount,walletAmount;
    public WalletFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View v= inflater.inflate(R.layout.fragment_wallet_info, container, false);
        tv_totalwd=v.findViewById(R.id.id_totalwd);
        tv_totalms=v.findViewById(R.id.id_totalms);
        tv_totalbe=v.findViewById(R.id.id_totalbe);
        tv_walletamount=v.findViewById(R.id.tv_walletamount);
        preferences = getActivity().getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        agentPhn=preferences.getString("AgentPhone","No name defined");
        agentId=preferences.getString("Username","No name defined");
        password=preferences.getString("Password","No name defined");
        new apiCall_getagentdetails().execute();
        return v;
    }

    class apiCall_getagentdetails extends AsyncTask<Request, Void, String> {
        String response_String,jsonString;


        ProgressDialog dialog=new ProgressDialog(getActivity(),ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Loading,Please wait....");
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(Request... requests) {
            JSONObject jsonObject = new JSONObject();
            httpClient = utils.createAuthenticatedClient(agentId,password);
            //  Log.d("TAG","EN_FLAG"+en_flag);
            //1.TOTAL WITHDRAW COUNT
            try {

                jsonObject.put("status","SUCCESS");
                jsonObject.put("accountno",agentPhn);
                jsonString = jsonObject.toString();

            } catch (Exception e) {
                e.printStackTrace();
            }
            MediaType JSON = MediaType.parse("application/json");
            RequestBody body = RequestBody.create(JSON, jsonString);
            Request request = new Request.Builder()
                    .url("https://aepsapi.gramtarang.org:8008/mint/aeps/countWithdrawalByAccountNoandStatus")
                    .addHeader("Accept", "*/*")
                    // .addHeader("Authorization","Basic MTAxMDpUZXN0QDEyMw==")
                    .post(body)
                    .build();
            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                //of the api calling got failed then it will go for onFailure,inside this we have added one alertDialog
                public void onFailure(Call call, IOException e) {
                    Log.d("TAG", "response onfailure"+e);


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
                        try{
                            jsonResponse = new JSONObject(response_String);
                            totalWithdrawCount =jsonResponse.getString("count");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_totalwd.setText(totalWithdrawCount);
                            }
                        });
                        //tv_totalwd.setText(count);
                        Log.d("TAG", "Exception Caught "+ totalWithdrawCount);

                    }

                }

            });
            //2.TOTAL MINISTATEMENT COUNT
            Request request2 = new Request.Builder()
                    .url("https://aepsapi.gramtarang.org:8008/mint/aeps/countWithdrawalByMSAccountNoandStatus")
                    .addHeader("Accept", "*/*")
                    // .addHeader("Authorization","Basic MTAxMDpUZXN0QDEyMw==")
                    .post(body)
                    .build();
            httpClient.newCall(request2).enqueue(new Callback() {
                @Override
                //of the api calling got failed then it will go for onFailure,inside this we have added one alertDialog
                public void onFailure(Call call, IOException e) {
                    Log.d("TAG", "response onfailure"+e);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogActivity.DialogCaller.showDialog(getActivity(),"Alert","Invalid Credentials.\nPlease Contact Administrator",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    });


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
                        try{
                            jsonResponse = new JSONObject(response_String);
                            totalMinistatementCount =jsonResponse.getString("count");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_totalms.setText(totalMinistatementCount);
                            }
                        });
                        //tv_totalwd.setText(count);
                        Log.d("TAG", "Exception Caught "+ totalWithdrawCount);

                    }

                }

            });
           //3.TOTAL BE COUNT
            Request request3 = new Request.Builder()
                    .url("https://aepsapi.gramtarang.org:8008/mint/aeps/countWithdrawalByBEAccountNoandStatus")
                    .addHeader("Accept", "*/*")
                    // .addHeader("Authorization","Basic MTAxMDpUZXN0QDEyMw==")
                    .post(body)
                    .build();
            httpClient.newCall(request3).enqueue(new Callback() {
                @Override
                //of the api calling got failed then it will go for onFailure,inside this we have added one alertDialog
                public void onFailure(Call call, IOException e) {
                    Log.d("TAG", "response onfailure"+e);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogActivity.DialogCaller.showDialog(getActivity(),"Alert","Invalid Credentials.\nPlease Contact Administrator",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    });


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
                        try{
                            jsonResponse = new JSONObject(response_String);
                            totalBECount=jsonResponse.getString("count");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_totalbe.setText(totalBECount);
                            }
                        });
                        //tv_totalwd.setText(count);
                        Log.d("TAG", "Exception Caught "+ totalBECount);

                    }

                }

            });
            //4.Wallet Amount
            Request request4= new Request.Builder()
                    .url("https://aepsapi.gramtarang.org:8008/mint/aeps/SumTotWithdrawalByAccountNoandStatus")
                    .addHeader("Accept", "*/*")
                    // .addHeader("Authorization","Basic MTAxMDpUZXN0QDEyMw==")
                    .post(body)
                    .build();
            httpClient.newCall(request4).enqueue(new Callback() {
                @Override
                //of the api calling got failed then it will go for onFailure,inside this we have added one alertDialog
                public void onFailure(Call call, IOException e) {
                    Log.d("TAG", "response onfailure"+e);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            DialogActivity.DialogCaller.showDialog(getActivity(),"Alert","Invalid Credentials.\nPlease Contact Administrator",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                        }
                    });


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
                        try{
                            jsonResponse = new JSONObject(response_String);
                            walletAmount=jsonResponse.getString("sum");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_walletamount.setText(walletAmount);
                            }
                        });
                        //tv_totalwd.setText(count);
                        Log.d("TAG", "Exception Caught "+ totalBECount);

                    }

                }

            });
            return null;
        }


    }

}