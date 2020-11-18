package gramtarang.mint.utils;

import android.os.AsyncTask;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SQLQueries extends AppCompatActivity {

    ConnectionClass connectionClass;

    boolean isValidBankName,
            isValidOTP,
            isValidLoanApplicationId;

    String[] arr2=new String[100];
    String[] arr=new String[100];

    List<String> al = new ArrayList<String>();

    String agentid,lastlogin,response_String,
            db_otp,role,mAgentId,
            agentPhn,branchcode;
    OkHttpClient httpClient;
    public String  getBankId(String bankname){
        try {


            Class.forName("com.mysql.jdbc.Driver");
            connectionClass=new ConnectionClass();
            Connection con = connectionClass.CONN();
            if (con == null) {

            }
            else {
                Statement statement = con.createStatement();

                ResultSet resultSet = statement.executeQuery("SELECT branchcode FROM apgvbbanks where banklocation='"+bankname+"'");
                while(resultSet.next()){
                    branchcode=resultSet.getString("branchcode");
                }



            }  }



        catch(NullPointerException | SQLException | ClassNotFoundException e)

        {
            e.printStackTrace();

        }
        return branchcode;
    }

    public String  getRole(String agentid){
        try {


            Class.forName("com.mysql.jdbc.Driver");
            connectionClass=new ConnectionClass();
            Connection con = connectionClass.CONN();
            if (con == null) {

            }
            else {
                Statement statement = con.createStatement();

                ResultSet resultSet = statement.executeQuery("SELECT role FROM bankmitra where id='"+agentid+"'");
                while(resultSet.next()){
                    role=resultSet.getString("role");
                }



            }  }



        catch(NullPointerException | SQLException | ClassNotFoundException e)

        {
            e.printStackTrace();

        }
        return role;
    }

    //String[] ManagerAgents=new String[100];

    ArrayList<String> ManagerAgents = new ArrayList<String>();
    public ArrayList<String> getUnderAreaMAgents(String managerid){
        try {


            Class.forName("com.mysql.jdbc.Driver");
            connectionClass=new ConnectionClass();
            Connection con = connectionClass.CONN();
            if (con == null) {

            }
            else {
                Statement statement = con.createStatement();

                ResultSet resultSet = statement.executeQuery("SELECT id FROM bankmitra where area_manager_id='"+managerid+"'");
                while(resultSet.next()){
                    mAgentId=resultSet.getString("id");
                    ManagerAgents.add(mAgentId);
                }



            }  }



        catch(NullPointerException | SQLException | ClassNotFoundException e)

        {
            e.printStackTrace();

        }
        return ManagerAgents;
    }

    ArrayList<String> loanList = new ArrayList<String>();
    String loanId;
    public ArrayList<String> getLoanList(String agentid){
        try {


            Class.forName("com.mysql.jdbc.Driver");
            connectionClass=new ConnectionClass();
            Connection con = connectionClass.CONN();
            if (con == null) {

            }
            else {
                Statement statement = con.createStatement();

                ResultSet resultSet = statement.executeQuery("SELECT uniqueId FROM loans where bankmitra_id='"+agentid+"'");
                while(resultSet.next()){
                    loanId=resultSet.getString("uniqueId");
                    loanList.add(loanId);
                }



            }  }



        catch(NullPointerException | SQLException | ClassNotFoundException e)

        {
            e.printStackTrace();

        }
        return loanList;
    }

    ArrayList<String> loanDetails = new ArrayList<String>();
    //String loanId;
    public ArrayList<String> getLoanDetails(String loanid){
        try {


            Class.forName("com.mysql.jdbc.Driver");
            connectionClass=new ConnectionClass();
            Connection con = connectionClass.CONN();
            if (con == null) {

            }
            else {
                Statement statement = con.createStatement();

                ResultSet resultSet = statement.executeQuery("SELECT * FROM loans where uniqueId='"+loanid+"'");
                while(resultSet.next()){
                    loanDetails.add(resultSet.getString("uniqueId"));
                    loanDetails.add(resultSet.getString("beneficiary_name"));
                    loanDetails.add(resultSet.getString("beneficiary_phn"));
                    loanDetails.add(resultSet.getString("beneficiary_accno"));
                    loanDetails.add(resultSet.getString("NearestAPGVBBank"));
                    loanDetails.add(resultSet.getString("beneficiary_lineofactivity"));
                    loanDetails.add(resultSet.getString("beneficiary_fatherhusband"));
                    loanDetails.add(resultSet.getString("beneficiary_dob"));
                    loanDetails.add(resultSet.getString("beneficiary_aadhaarno"));
                    loanDetails.add(resultSet.getString("beneficiary_resaddress"));
                    loanDetails.add(resultSet.getString("beneficiary_businessname"));
                    loanDetails.add(resultSet.getString("beneficiary_businessaddress"));
                    loanDetails.add(resultSet.getString("business_proname"));
                    loanDetails.add(resultSet.getString("beneficiary_businessexistence"));
                    loanDetails.add(resultSet.getString("beneficiary_education"));
                    loanDetails.add(resultSet.getString("beneficiary_category"));
                    loanDetails.add(resultSet.getString("beneficiary_family_child"));
                    loanDetails.add(resultSet.getString("beneficiary_family_adult"));
                    loanDetails.add(resultSet.getString("beneficiary_sustenance"));
                    loanDetails.add(resultSet.getString("beneficiary_purpose"));
                    loanDetails.add(resultSet.getString("beneficiary_termloan"));
                    loanDetails.add(resultSet.getString("beneficiary_tenor"));
                    loanDetails.add(resultSet.getString("existing_apgvb_loan"));
                    loanDetails.add(resultSet.getString("existing_otherbank_loans"));
                    loanDetails.add(resultSet.getString("own_property"));


                }



            }  }



        catch(NullPointerException | SQLException | ClassNotFoundException e)

        {
            e.printStackTrace();

        }
        return loanDetails;
    }


    public String getlastlogin(String androidId){
        try {


            Class.forName("com.mysql.jdbc.Driver");
            connectionClass=new ConnectionClass();
            Connection con = connectionClass.CONN();
            if (con == null) {

            }
            else {
                Statement statement = con.createStatement();

                ResultSet resultSet = statement.executeQuery("SELECT timestamp FROM login_logs where status='Success' AND androidid='"+androidId+"' ORDER BY timestamp DESC LIMIT 1 OFFSET 1");
                while(resultSet.next()){
                    lastlogin=resultSet.getString("timestamp");
                }



            }  }



        catch(NullPointerException | SQLException | ClassNotFoundException e)

        {
            e.printStackTrace();

        }
        return lastlogin;
    }
    public void insertloanapplication(int i,String uniqueID,
                                      String bank_mitraname,
                                      String bankmitra_id,
                                      String bankmitra_contactno,
                                      String beneficiary_name,
                                      String beneficiary_phn,
                                      String beneficiary_accno,
                                      String nearestapgvb,
                                      String beneficiary_lineofactivity,
                                      String beneficiary_father_husband,
                                      String beneficiary_dob,
                                      String beneficiary_aad,
                                      String beneficiary_pan,
                                      String beneficiary_resadd,
                                      String beneficiary_busname,
                                      String beneficiary_busadd,
                                      String pro_name,
                                      String business_existence,
                                      String beneficiary_education,
                                      String beneficiary_category,
                                      String beneficiary_family_adult,String beneficiary_family_child,
                                      String beneficiary_sustenance,
                                      String beneficiary_purpose,
                                      String beneficiary_termloan,
                                      String beneficiary_tenor,
                                      String beneficiary_detailsofother, String beneficiary_detailsofother_2,
                                      String own_pro,
                                      String Status,
                                      String isRead,
                                      String remark,String iddetails){
        try {


            Class.forName("com.mysql.jdbc.Driver");
            connectionClass=new ConnectionClass();
            Connection con = connectionClass.CONN();
            if (con == null) {


            }
            else {

                PreparedStatement statement=con.prepareStatement("INSERT INTO loans " + " VALUES (" +
                        "'" + i + "' ,null,'"+uniqueID+"' ,'" +bank_mitraname+ "'," +
                        "'" +bankmitra_id+ "','" +bankmitra_contactno+ "','" +beneficiary_name + "'," +
                        "'" + beneficiary_phn+ "','" +beneficiary_accno + "' ,'" + nearestapgvb+ "'," +
                        "'" + beneficiary_lineofactivity+ "' ,'" + beneficiary_father_husband + "'," +
                        "'" + beneficiary_dob + "','" +beneficiary_aad + "','" +  beneficiary_pan + "'," +
                        "'" + beneficiary_resadd + "','" + beneficiary_busname + "',' " +beneficiary_busadd +  "' " +
                        ",' " +pro_name +  "' ,' " +business_existence+  "',' " +beneficiary_education+  "',' "
                        +beneficiary_category+  "',' " +beneficiary_family_adult +  "','"+beneficiary_family_child+"','"+beneficiary_sustenance+"',' " +beneficiary_purpose +  "' ,10000,' " + beneficiary_tenor+  "' ,' " + beneficiary_detailsofother +  "',' " + beneficiary_detailsofother_2 +  "',' " + own_pro +
                        "',' " +Status +  "',' " +  isRead +  "',' " + remark +  "' , null,null,null,null)");
                statement.executeUpdate();




            }  }



        catch(NullPointerException | SQLException | ClassNotFoundException e)

        {


            e.printStackTrace();

        }
    }
public String getAgentPhone(String androidId){
    try {


        Class.forName("com.mysql.jdbc.Driver");
        connectionClass=new ConnectionClass();
        Connection con = connectionClass.CONN();
        if (con == null) {

        }
        else {
            Statement statement = con.createStatement();

            ResultSet resultSet = statement.executeQuery("select contact_number from agent WHERE androidid='" + androidId + "'");
            while(resultSet.next()){
                agentPhn=resultSet.getString("contact_number");
            }



        }  }



    catch(NullPointerException | SQLException | ClassNotFoundException e)

    {
        e.printStackTrace();

    }
    return agentPhn;
}
public boolean validateOTP(String enteredotp,String androidId){
    try {


        Class.forName("com.mysql.jdbc.Driver");
        connectionClass=new ConnectionClass();
        Connection con = connectionClass.CONN();
        if (con == null) {

        }
        else {
            Statement statement = con.createStatement();

            ResultSet resultSet = statement.executeQuery("select otp from otp WHERE androidid='" + androidId + "'");
            while(resultSet.next()){
                db_otp=resultSet.getString("otp");
            }
            if(enteredotp.equals(db_otp)){
                isValidOTP=true;
            }
            else{
                isValidOTP=false;
            }


        }  }



    catch(NullPointerException | SQLException | ClassNotFoundException e)

    {
        e.printStackTrace();

    }
    return isValidOTP;
}

public void updateOTP(String generated_pin,String androidId){



    try {


        Class.forName("com.mysql.jdbc.Driver");
        connectionClass=new ConnectionClass();
        Connection con = connectionClass.CONN();
        if (con == null) {

        }
        else {
            PreparedStatement statement = con.prepareStatement("UPDATE otp SET otp='" + generated_pin + "' WHERE androidid='" + androidId + "'");

           statement.executeUpdate();

        }  }



    catch(NullPointerException | SQLException | ClassNotFoundException e)

    {
        e.printStackTrace();

    }
    db_otp=generated_pin;

}

    public String[] getAgentDetails(String androidId){
        try {


            Class.forName("com.mysql.jdbc.Driver");
            connectionClass=new ConnectionClass();
            Connection con = connectionClass.CONN();
            if (con == null) {

            }
            else {
                Statement statement = con.createStatement();

                ResultSet resultSet = statement.executeQuery("SELECT * FROM bankmitra WHERE androidId='"+androidId +"'");

                while (resultSet.next()) {
                    al.add(resultSet.getString("id"));
                    al.add(resultSet.getString("name"));
                    al.add(resultSet.getString("contact_no"));
                    al.add(resultSet.getString("email"));
                    arr=al.toArray(arr);
                }
            }  }



        catch(NullPointerException | SQLException | ClassNotFoundException e)

        {


        }
        return arr;

    }

    public String[] getLoanApplicarionDetails(String applicationId){
        try {


            Class.forName("com.mysql.jdbc.Driver");
            connectionClass=new ConnectionClass();
            Connection con = connectionClass.CONN();
            if (con == null) {

            }
            else {
                Statement statement = con.createStatement();

                ResultSet resultSet = statement.executeQuery("SELECT * FROM loans WHERE UniqueID='"+applicationId +"'");

                while (resultSet.next()) {
                    al.add(resultSet.getString("UniqueID"));
                    al.add(resultSet.getString("BeneficiaryName"));
                    al.add(resultSet.getString("MobileNumber"));
                    al.add(resultSet.getString("Email"));
                    al.add(resultSet.getString("AadhaarNumber"));
                    al.add(resultSet.getString("NearestAPGVBBank"));
                    al.add(resultSet.getString("Gender"));
                    al.add(resultSet.getString("TypeofLoan"));
                    al.add(resultSet.getString("Address"));
                    al.add(resultSet.getString("District"));
                    al.add(resultSet.getString("state"));
                    al.add(resultSet.getString("pincode"));
                    al.add(resultSet.getString("LoanAmount"));
                    al.add(resultSet.getString("Occupation"));
                    al.add(resultSet.getString("Purpose"));
                    al.add(resultSet.getString("Tenure"));
                    al.add(resultSet.getString("RePaymentPeriod"));

                    arr=al.toArray(arr);
                }
            }  }



        catch(NullPointerException | SQLException | ClassNotFoundException e)

        {


        }
        return arr;

    }

public void inserttranslogs(int i,String androidId,String lati,String longi,String custName,String trans_id,String status,String status_code,String message,String transtype,String timestamp,String fpTransId,String rrn_no,String agentid,String transaction_amount,String available_balance){
    try {


        Class.forName("com.mysql.jdbc.Driver");
        connectionClass=new ConnectionClass();
        Connection con = connectionClass.CONN();
        if (con == null) {


        }
        else {
            Statement statement = con.createStatement();
            PreparedStatement statement2=con.prepareStatement("INSERT INTO trans_logs " + " VALUES ('" + i + "' ,'" + androidId + "' ,'" +lati+ "','" +longi+ "','" +custName+ "','" +trans_id + "','" + status+ "','" +status_code + "' ,'" + message + "','" + transtype+ "' ,'" + timestamp + "','" + fpTransId + "','" + rrn_no + "','" + agentid + "','" + transaction_amount + "','" + available_balance + "',null,0)");
            statement2.executeUpdate();




        }  }



    catch(NullPointerException | SQLException | ClassNotFoundException e)

    {


        e.printStackTrace();

    }
}
    public void insertloanlogs(int i,String loanid,String agentname,String gender,String beneficiaryName,String address,String occupation,String mobilenumber,String aadhaarnumber,String bank,String amount,String purpose,String tenure,String repayment,String typeofloan,String timeStamp, String status,int status2){
        try {


            Class.forName("com.mysql.jdbc.Driver");
            connectionClass=new ConnectionClass();
            Connection con = connectionClass.CONN();
            if (con == null) {


            }
            else {
                Statement statement1 = con.createStatement();
                PreparedStatement statementl=con.prepareStatement("INSERT INTO loans " + " VALUES ('" + i+ "' ,'" + loanid+ "' ,'" + agentname+ "' ,'" +gender+ "','" +beneficiaryName+ "','" +address + "','" + occupation+ "' ,'" + mobilenumber + "','" + aadhaarnumber + "','" + bank + "','" + amount + "','" + purpose + "','"+tenure+"','"+repayment+"','"+typeofloan+"','"+status+"',null,'"+status2+"')");
                statementl.executeUpdate();




            }  }



        catch(NullPointerException | SQLException | ClassNotFoundException e)

        {


            e.printStackTrace();

        }
    }
    public void insertbeneficiaryforotp(int i,String benfName,String benfPhn,String benfOtp){
        try {


            Class.forName("com.mysql.jdbc.Driver");
            connectionClass=new ConnectionClass();
            Connection con = connectionClass.CONN();
            if (con == null) {


            }
            else {
                Statement statement1 = con.createStatement();
                PreparedStatement statementl=con.prepareStatement("INSERT INTO loan_otp " + " VALUES ('" + i+ "' ,'" + benfName+ "' ,'" +benfPhn+ "','" +benfOtp+ "')");
                statementl.executeUpdate();




            }  }



        catch(NullPointerException | SQLException | ClassNotFoundException e)

        {


            e.printStackTrace();

        }
    }
public String getAgentPhn(){

    return agentPhn;
}
    public boolean isValidBankName(String selectedbank) {
        isValidBankName=true;
    /*    try {


            Class.forName("com.mysql.jdbc.Driver");
            connectionClass=new ConnectionClass();
            Connection con = connectionClass.CONN();
            if (con == null) {

            }
            else {
                Statement statement = con.createStatement();

                ResultSet resultSet = statement.executeQuery("SELECT * FROM bankslist");

                while (resultSet.next()) {

                    bankNames = resultSet.getString("bankname");
                    banks_arr.add(bankNames);
                    arr2=banks_arr.toArray(arr2);



                }
                Log.d("SQLQueries","Bank Array is:"+arr2);
                for(int j=0;j<arr2.length;j++) {
                    if (arr2[j].equals(selectedbank)) {
                        isValidBankName = true;
                        break;
                    } else {
                        isValidBankName = false;

                    }
                }



            }  }



        catch(NullPointerException | SQLException | ClassNotFoundException e)

        {

            e.printStackTrace();

        }*/
        return isValidBankName;
    }

    String db_username,db_androidid,getagentId,getAgentName,bankNames,bankIIN;
    ArrayList<String> banks_arr = new ArrayList<String>();
    ArrayList<String> banksID_arr = new ArrayList<String>();
    public ArrayList<String> getBankNames() {
        Utils utils=new Utils();
                //httpClient = utils.createAuthenticatedClient(username, password);
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
                                    banks_arr.add(jresponse.getString("bankname"));
                                    banksID_arr.add(jresponse.getString("iinno"));
                                }

                                Log.d("SQL QUERIES","BANK NAMES"+banks_arr+banksID_arr);
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



        Log.d("SQL QUERIES","BANK NAMES"+banks_arr+banksID_arr);
        return banks_arr;
    }
    public ArrayList<String> getBankNames2(String username,String password) {
        Utils utils=new Utils();
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
                            banks_arr.add(jresponse.getString("bankname"));
                            banksID_arr.add(jresponse.getString("iinno"));
                        }

                        Log.d("SQL QUERIES","BANK NAMES"+banks_arr+banksID_arr);
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



        Log.d("SQL QUERIES","BANK NAMES"+banks_arr+banksID_arr);
        return banks_arr;
    }


    public ArrayList<String> getBankIIN() {
        try {


            Class.forName("com.mysql.jdbc.Driver");
            connectionClass=new ConnectionClass();
            Connection con = connectionClass.CONN();
            if (con == null) {

            }
            else {
                Statement statement = con.createStatement();

                ResultSet resultSet = statement.executeQuery("SELECT * FROM bankslist");

                while (resultSet.next()) {

                    bankIIN= resultSet.getString("iinno");
                   banksID_arr.add(bankIIN);




                }




            }  }



        catch(NullPointerException | SQLException | ClassNotFoundException e)

        {

            e.printStackTrace();

        }
        return banksID_arr;
    }


    ArrayList<String> apgvbBranch_arr = new ArrayList<String>();
    String apgvbBranch;
    public ArrayList<String> getApgvbBankBranch() {
        try {


            Class.forName("com.mysql.jdbc.Driver");
            connectionClass=new ConnectionClass();
            Connection con = connectionClass.CONN();
            if (con == null) {

            }
            else {
                Statement statement = con.createStatement();

                ResultSet resultSet = statement.executeQuery("SELECT * FROM apgvbbanks");

                while (resultSet.next()) {

                    apgvbBranch= resultSet.getString("banklocation");
                    apgvbBranch_arr.add(apgvbBranch);




                }




            }  }



        catch(NullPointerException | SQLException | ClassNotFoundException e)

        {

            e.printStackTrace();

        }
        return apgvbBranch_arr;
    }

    public String GetAgentName(String androidId) {
        try {


            Class.forName("com.mysql.jdbc.Driver");
            connectionClass=new ConnectionClass();
            Connection con = connectionClass.CONN();
            if (con == null) {


            }
            else {
                Statement statement = con.createStatement();

                ResultSet resultSet = statement.executeQuery("SELECT * FROM bankmitra where androidId='"+androidId+"'");

                while (resultSet.next()) {

                   getAgentName = resultSet.getString("name");


                }



            }  }



        catch(NullPointerException | SQLException | ClassNotFoundException e)

        {

            e.printStackTrace();

        }
        return getAgentName;
    }
   /* public String getLastLogin(String androidId) {
        try {


            Class.forName("com.mysql.jdbc.Driver");
            connectionClass=new ConnectionClass();
            Connection con = connectionClass.CONN();
            if (con == null) {
            } else {
                Statement statement=con.createStatement();
                ResultSet resultSet=statement.executeQuery("select timestamp from login_logs where android='"+androidId+"' AND status='"+"Success"+"' order by timestamp desc LIMIT 1,1 ");
                while(resultSet.next()){
                   lastlogin=resultSet.getString("timestamp");
                }
            }  }



        catch(NullPointerException | SQLException | ClassNotFoundException e)

        {


            e.printStackTrace();

        }
        return lastlogin;
    }*/
    public String GetagentId(String androidId) {
        try {


            Class.forName("com.mysql.jdbc.Driver");
            connectionClass=new ConnectionClass();
            Connection con = connectionClass.CONN();
            if (con == null) {
            } else {
                Statement statement=con.createStatement();
                ResultSet resultSet=statement.executeQuery("select * from agent where andid='"+androidId+"'");
                while(resultSet.next()){
                    getagentId=resultSet.getString("agent_id");
                }
            }  }



        catch(NullPointerException | SQLException | ClassNotFoundException e)

        {


            e.printStackTrace();

        }
        return getagentId;
    }

    public void insert_login_log(int i,String db_agentid,String username,String androidId,String latitude,String longitude,String timestamp,String login_status) {
        try {


            Class.forName("com.mysql.jdbc.Driver");
            connectionClass=new ConnectionClass();
            Connection con = connectionClass.CONN();
            if (con == null) {
            } else {



                PreparedStatement statement4=con.prepareStatement("INSERT INTO login_logs " + " VALUES ('" + i+ "','" + db_agentid + "' ,'" +username+ "','" +androidId+ "','" +latitude+ "','" +longitude + "',null,'" + login_status+ "')");
                statement4.executeUpdate();




            }  }



        catch(NullPointerException | SQLException | ClassNotFoundException e)

        {

            e.printStackTrace();

        }
    }

    public boolean isRegistered(String username, String androidId) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connectionClass=new ConnectionClass();
            Connection con = connectionClass.CONN();
            if (con == null) {


            }
            else {

                Statement statement = con.createStatement();

                ResultSet resultSet = statement.executeQuery("SELECT * FROM bankmitra where id='"+username+"'");

                while (resultSet.next()) {
                    db_username=resultSet.getString("id");
                    db_androidid=resultSet.getString("androidId");

                }

                if(db_username.equals(username)&&db_androidid.equals(androidId)){
                    isRegistered=true;

                }
                else{
                   isRegistered=false;

                }




            }  }



        catch(NullPointerException |ClassNotFoundException | SQLException e)

        {


        }
        return isRegistered;
    }

    String app_latest_version;
    boolean isRegistered;
    public String getApp_latest_version() {
        try {

            int status=1;
            Class.forName("com.mysql.jdbc.Driver");
            connectionClass=new ConnectionClass();
            Connection con = connectionClass.CONN();
            if (con == null) {
                Log.d("TAG","No Internet Connection");
            }
            else {
                Statement statement=con.createStatement();
                ResultSet resultSet=statement.executeQuery("select * from version where active='"+status+"'");
                while(resultSet.next()){
                    app_latest_version=resultSet.getString("version_number");

                }
            }  }
        catch(NullPointerException | SQLException | ClassNotFoundException e)

        {
            e.printStackTrace();

        }
        return app_latest_version;
    }

    public void setApp_latest_version(String app_latest_version) {
        this.app_latest_version = app_latest_version;
    }
}
