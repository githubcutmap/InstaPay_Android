package gramtarang.mint.loans;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;

import gramtarang.mint.R;
import gramtarang.mint.aeps.activity_Aeps_HomeScreen;
import gramtarang.mint.agent_login.Dashboard;
import gramtarang.mint.agent_login.activity_Login;
import gramtarang.mint.loans.areamgr.loan_viewapp;
import gramtarang.mint.utils.LocationTrack;
import gramtarang.mint.utils.LogOutTimer;

import gramtarang.mint.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LoanActivity_SearchViewApplication extends AppCompatActivity implements LogOutTimer.LogOutListener {
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

    EditText etUserEnteredSearchID;
    Button search;
    boolean doubleBackToExitPressedOnce = false;
    TextView test;
    String jsonString,response_String,username,password;
    OkHttpClient client;
    LinearLayout ll_buttons;
    ImageView backbtn,proof1,proof2,proof3;
    TextView bId,
            bmId,
            bmName,
            bmphone,
            bName,
            bphone,
            bBank,
            bOccupation,
            bfh,
            bdob,
            bAadhaar,
            bAddress,
            bbusinessname,
            bbusinessaddress,
            bproname,
            bbusinessexistence,
            beducation,
            bcategory,
            bfamily,
            bsustenance,
            bLoanPurpose,
            bLoanAmount,
            bTenure,
            bExistanceLoanapgvb,
            bExistanceLoanOthers,
            bOwnProperty,
            appStatus,
            bEmail,
            bGender,
            bLoanType,
            bRepaymentPeriod;
    String userSearchID,
            applicationArr[],TAG="View Application";
    TableLayout tableView;
    LocationTrack locationTrack;
    private int shortAnimationDuration;
    private Animator currentAnimator;
    Bitmap bmp,bmp2,bmp3;
    SharedPreferences preferences;
    public static final String mypreference = "Loanpreferences";
    public static final String userpreference = "mypref";

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
                Intent intent = new Intent(getApplicationContext(), activity_Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        }, 2000);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan__searchview_application);
        backbtn = findViewById(R.id.backimg);
        etUserEnteredSearchID = findViewById(R.id.search_id);
        search = findViewById(R.id.search_btn);


        bId = findViewById(R.id.beneficiary_id);
        bmId = findViewById(R.id.bankmitra_id);
        bmName = findViewById(R.id.bankmitra_name);
        bmphone = findViewById(R.id.bankmitra_co);
        bName = findViewById(R.id.beneficiary_name);
        bphone = findViewById(R.id.beneficiary_phone);
        bBank = findViewById(R.id.beneficiary_bank);
        bOccupation = findViewById(R.id.beneficiary_occupation); //lineofactivity == occupation
        bfh = findViewById(R.id.beneficiary_fh);
        bdob = findViewById(R.id.beneficiary_dob);
        bAadhaar = findViewById(R.id.beneficiary_aadhaar);
        bAddress = findViewById(R.id.beneficiary_address);
        bbusinessname =findViewById(R.id.beneficiary_bname);
        bbusinessaddress=findViewById(R.id.beneficiary_badd);
        bproname=findViewById(R.id.beneficiary_bpro);
        bbusinessexistence=findViewById(R.id.beneficiary_bep);
        beducation=findViewById(R.id.beneficiary_edu);
        bcategory=findViewById(R.id.beneficiary_cat);
        bfamily=findViewById(R.id.beneficiary_fca);
        bsustenance=findViewById(R.id.beneficiary_sus);
        bLoanPurpose = findViewById(R.id.beneficiary_loan_purpose);
        bLoanAmount = findViewById(R.id.beneficiary_loan_amount);
        bTenure = findViewById(R.id.beneficiary_tenure);
        bExistanceLoanapgvb = findViewById(R.id.beneficiary_ela);
        bExistanceLoanOthers= findViewById(R.id.beneficiary_elo);
        bOwnProperty= findViewById(R.id.beneficiary_op);
        appStatus= findViewById(R.id.beneficiary_status);
        bRepaymentPeriod = findViewById(R.id.beneficiary_repayment_period);
        bLoanType = findViewById(R.id.beneficiary_loan_type);
        bEmail = findViewById(R.id.beneficiary_email);
        bGender = findViewById(R.id.beneficiary_gender);
        tableView = findViewById(R.id.content_tl);
        proof1 = findViewById(R.id.thumb__1);
        proof2 = findViewById(R.id.thumb__2);
        proof3 = findViewById(R.id.thumb__3);



        etUserEnteredSearchID.addTextChangedListener(LoanViewApplicationTextWatcher);
        search.addTextChangedListener(LoanViewApplicationTextWatcher);

//back button
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoanActivity_MainScreen.class);
                startActivity(intent);
            }
        });



        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userSearchID =  etUserEnteredSearchID.getText().toString().trim();
                Log.d(TAG,"application ID: "+userSearchID);

                if(userSearchID.length() == 17){
                    //SQLQueries applicationDetails = new SQLQueries();
                    //applicationArr = applicationDetails.getLoanApplicarionDetails(userSearchID);
                    client=new OkHttpClient();
                    api_getAppdetails(userSearchID);


                }
                else{
                    etUserEnteredSearchID.setError("Invalid Application ID");
                }
            }
        });

    }

    private void zoomImageFromThumb(final View thumbView, Bitmap asd) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) findViewById(
                R.id.expanded_image1);
        //
        // expandedImageView.setImageBitmap(imageResId);
        //expandedImageView.setImageBitmap(imgurl);

        try  {
            expandedImageView.setImageBitmap(asd);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.container)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(expandedImageView,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(shortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentAnimator = null;
            }
        });
        set.start();
        currentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentAnimator != null) {
                    currentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(shortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        currentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        currentAnimator = null;
                    }
                });
                set.start();
                currentAnimator = set;
            }
        });
    }



    private void api_getAppdetails(String id){
        Utils utils = new Utils();
        preferences = getSharedPreferences(userpreference, Context.MODE_PRIVATE);
        username=preferences.getString("Username","No name defined");
        password=preferences.getString("Password","No name defined");
        Log.d("User + Pass: ",username+password);
        OkHttpClient httpClient = utils.createAuthenticatedClient(username, password);
        ArrayList<String> appdetails = new ArrayList<String>();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uniqueid", id);
            jsonString = jsonObject.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        MediaType JSON = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(JSON, jsonString);
        okhttp3.Request request = new Request.Builder()
                .url("https://aepsapi.gramtarang.org:8008/mint/loans/getLoansapp")
                .addHeader("Accept", "*/*")
                .post(body)
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
                    JSONObject jsonResponse = null;
                    try {
                        jsonResponse = new JSONObject(response_String);
                        JSONArray llist1 = jsonResponse.getJSONArray("llist2");
                        for(int i = 0; i < llist1.length(); i++){

                            String bankmitra_name = llist1.getJSONObject(i).getString("bankmitra_name");
                            String bankmitra_id = llist1.getJSONObject(i).getString("bankmitra_id");
                            String bankmitra_contactno = llist1.getJSONObject(i).getString("bankmitra_contactno");
                            String beneficiary_name = llist1.getJSONObject(i).getString("beneficiary_name");
                            String beneficiary_phn = llist1.getJSONObject(i).getString("beneficiary_phn");
                            String beneficiary_accno = llist1.getJSONObject(i).getString("beneficiary_accno");
                            //nearestapgvb bank
                            String nearestapgvbbank = llist1.getJSONObject(i).getString("nearestapgvbbank");

                            String beneficiary_lineofactivity = llist1.getJSONObject(i).getString("beneficiary_lineofactivity");
                            String beneficiary_fatherhusband = llist1.getJSONObject(i).getString("beneficiary_fatherhusband");
                            String beneficiary_dob = llist1.getJSONObject(i).getString("beneficiary_dob");
                            String beneficiary_aadhaarno = llist1.getJSONObject(i).getString("beneficiary_aadhaarno");
                            String beneficiary_resaddress = llist1.getJSONObject(i).getString("beneficiary_resaddress");
                            String beneficiary_businessname = llist1.getJSONObject(i).getString("beneficiary_businessname");
                            String beneficiary_businessaddress = llist1.getJSONObject(i).getString("beneficiary_businessaddress");
                            String business_proname = llist1.getJSONObject(i).getString("business_proname");
                            String beneficiary_businessexistence = llist1.getJSONObject(i).getString("beneficiary_businessexistence");
                            String beneficiary_education = llist1.getJSONObject(i).getString("beneficiary_education");
                            String beneficiary_category = llist1.getJSONObject(i).getString("beneficiary_category");
                            String beneficiary_family= llist1.getJSONObject(i).getString("beneficiary_family_adult")+" Adult "+llist1.getJSONObject(i).getString("beneficiary_family_child")+" Children";
                            String beneficiary_sustenance = llist1.getJSONObject(i).getString("beneficiary_sustenance");
                            String beneficiary_purpose = llist1.getJSONObject(i).getString("beneficiary_purpose");
                            String beneficiary_termloan = llist1.getJSONObject(i).getString("beneficiary_termloan");
                            String beneficiary_tenor = llist1.getJSONObject(i).getString("beneficiary_tenor");
                            String existing_apgvb_loan = llist1.getJSONObject(i).getString("existing_apgvb_loan");
                            String existing_otherbank_loans = llist1.getJSONObject(i).getString("existing_otherbank_loans");
                            String own_property = llist1.getJSONObject(i).getString("own_property");
                            String status = llist1.getJSONObject(i).getString("status");
                            String beneficiary_pancard = llist1.getJSONObject(i).getString("beneficiary_pancard");
                            String armgrstatus = llist1.getJSONObject(i).getString("armgrstatus");
                            String beneficiary_latitude = llist1.getJSONObject(i).getString("latitude");
                            String beneficiary_longitude = llist1.getJSONObject(i).getString("longitude");


                            appdetails.add(id);
                            appdetails.add(bankmitra_name);
                            appdetails.add(bankmitra_id);
                            appdetails.add(bankmitra_contactno);
                            appdetails.add(beneficiary_name);
                            appdetails.add(beneficiary_phn);
                            appdetails.add(beneficiary_accno);
                            appdetails.add(beneficiary_lineofactivity);
                            appdetails.add(beneficiary_fatherhusband);
                            appdetails.add(beneficiary_dob);
                            appdetails.add(beneficiary_aadhaarno);
                            appdetails.add(beneficiary_pancard);
                            appdetails.add(beneficiary_resaddress);
                            appdetails.add(beneficiary_businessname);
                            appdetails.add(beneficiary_businessaddress);
                            appdetails.add(business_proname);
                            appdetails.add(beneficiary_businessexistence);
                            appdetails.add(beneficiary_education);
                            appdetails.add(beneficiary_category);
                            appdetails.add(beneficiary_family);
                            appdetails.add(beneficiary_sustenance);
                            appdetails.add(beneficiary_purpose);
                            appdetails.add(beneficiary_termloan);
                            appdetails.add(beneficiary_tenor);
                            appdetails.add(existing_apgvb_loan);
                            appdetails.add(existing_otherbank_loans);
                            appdetails.add(own_property);
                            appdetails.add(status);
                            appdetails.add(nearestapgvbbank);
                            appdetails.add(armgrstatus);
                            appdetails.add(beneficiary_latitude);
                            appdetails.add(beneficiary_longitude);




                        }
                        runOnUiThread(new Runnable() {
                            public void run() {
                                fillDetails(appdetails);
                            }
                        });

                        Log.d("TAG","SAME CLAdddSS"+appdetails);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    catch (NullPointerException e) {
                    }
                } else {
                }
            }
        });
    }

    private void fillDetails(ArrayList<String> appdetails) {
        tableView.setVisibility(View.VISIBLE);
        applicationArr = appdetails.toArray(new String[0]);
        Log.d(TAG,"application Arr: "+appdetails);
        //bId.setText(applicationArr[0]);
        //bName.setText(applicationArr[1]);
        //bphone.setText(applicationArr[2]);
        /*bEmail.setText(applicationArr[3]);
        bAadhaar.setText(applicationArr[4]);
        bBank.setText(applicationArr[5]);
        bGender.setText(applicationArr[6]);
        bLoanType.setText(applicationArr[7]);
        bAddress.setText(applicationArr[8]+" "+applicationArr[9]+" "+applicationArr[10]+" -"+applicationArr[11]);
        bLoanAmount.setText(applicationArr[12]);
        bOccupation.setText(applicationArr[13]);
        bLoanPurpose.setText(applicationArr[14]);
        bTenure.setText(applicationArr[15]);
        bRepaymentPeriod.setText(applicationArr[16]);*/

        bId.setText(applicationArr[0]);
        bmId.setText(applicationArr[2]);
        bmName.setText(applicationArr[1]);
        bmphone.setText(applicationArr[3]);
        bName.setText(applicationArr[4]);
        bphone.setText(applicationArr[5]);
        bBank.setText(applicationArr[28]);
        bOccupation.setText(applicationArr[7]);
        bfh.setText(applicationArr[8]);
        bdob.setText(applicationArr[9]);
        bAadhaar.setText(applicationArr[10]);
        bAddress.setText(applicationArr[12]);
        bbusinessname.setText(applicationArr[13]);
        bbusinessaddress.setText(applicationArr[14]);
        bproname.setText(applicationArr[15]);
        bbusinessexistence.setText(applicationArr[16]);
        beducation.setText(applicationArr[17]);
        bcategory.setText(applicationArr[18]);
        bfamily.setText(applicationArr[19]);
        bsustenance.setText(applicationArr[20]);
        bLoanPurpose.setText(applicationArr[21]);
        bLoanAmount.setText(applicationArr[22]);
        bTenure.setText(applicationArr[23]);
        bExistanceLoanapgvb.setText(applicationArr[24]);
        bExistanceLoanOthers.setText(applicationArr[25]);
        bOwnProperty.setText(applicationArr[26]);
        if(applicationArr[29].matches("0")) {
            appStatus.setText("Pending");
        }if(applicationArr[29].matches("2")) {
            appStatus.setText("Rejected By Area Manager");
        }if(applicationArr[29].matches("1")) {
            appStatus.setText("Accepted By Area Manager");
        }
        setproof(applicationArr[28],applicationArr[0]);

    }

    private void setproof(String bankname,String id){

        preferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE);
        username=preferences.getString("Username","No name defined");
        password=preferences.getString("Password","No name defined");

        Log.d("Setproof",bankname+"  if"+id);
        //https://bankmgr.gramtarang.org/webapp/uploads/ADH/ADH_apgvb_mudra_akku_582494.jpg
        //https://bankmgr.gramtarang.org/webapp/uploads/ADH/ADH_apgvb_mudra_tekk_787825.jpg
        String code = bankname.toLowerCase().substring(0,4)+"_"+id.substring(11,17);
        Log.d("Setproof code",code);

        Authenticator.setDefault (new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password.toCharArray());
            }
        });

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    URL url = new URL("http://bankmgr.gramtarang.org:8081/mint/doc/downloadadh?fname=ADH_apgvb_mudra_"+code+".jpg");
                    Log.d("Setproof url","http://bankmgr.gramtarang.org:8081/mint/doc/downloadadh?fname=ADH_apgvb_mudra_"+code+".jpg");
                    bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    proof1.setImageBitmap(bmp);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        Thread thread2 = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {

                    URL url2 = new URL("http://bankmgr.gramtarang.org:8081/mint/doc/downloadbp?fname=BP_apgvb_mudra_"+code+".jpg");
                    Log.d("Setproof bp url","http://bankmgr.gramtarang.org:8081/mint/doc/downloadbp?fname=BP_apgvb_mudra_"+code+".jpg");
                    bmp2 = BitmapFactory.decodeStream(url2.openConnection().getInputStream());
                    proof2.setImageBitmap(bmp2);



                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread2.start();

        Thread thread3 = new Thread(new Runnable() {

            @Override
            public void run() {
                try  {
                    URL url3 = new URL("http://bankmgr.gramtarang.org:8081/mint/doc/downloadpd?fname=PD_apgvb_mudra_"+code+".jpg");
                    Log.d("Setproof bp url","http://bankmgr.gramtarang.org:8081/mint/doc/downloadpd?fname=PD_apgvb_mudra_"+code+".jpg");
                    bmp3 = BitmapFactory.decodeStream(url3.openConnection().getInputStream());
                    proof3.setImageBitmap(bmp3);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread3.start();

        proof1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomImageFromThumb(proof1,bmp);
            }
        });
        proof2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomImageFromThumb(proof2,bmp2);
            }
        });
        proof3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                zoomImageFromThumb(proof3,bmp3);
            }
        });
    }

    public TextWatcher LoanViewApplicationTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            userSearchID =  etUserEnteredSearchID.getText().toString().trim();
            if(userSearchID.length() == 17){
                search.setEnabled(true);
            }




        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

}

