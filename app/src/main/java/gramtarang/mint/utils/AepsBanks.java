package gramtarang.mint.utils;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AepsBanks {
    OkHttpClient httpClient;
    Utils utils=new Utils();
    String response_String;
    ArrayList<String> arryList_bankName = new ArrayList<String>();
    ArrayList<String> arrayList_bankNumber = new ArrayList<String>();
    public void getAepsBankNames(String username,String password){
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

    }}

}
