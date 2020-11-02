package gramtarang.mint.utils;

//import androidx.appcompat.app.AlertDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;

import gramtarang.mint.R;


/*Common LoadingDialog for some class*/
public class LoadingDialog {
     Activity activity;
     AlertDialog alertDialog;

    public LoadingDialog(Activity myActivity){

        activity = myActivity;

    };

    public void startLoading(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        builder.setView(layoutInflater.inflate(R.layout.custom_dialog,null));
        builder.setCancelable(false);

        alertDialog = builder.create();
        alertDialog.show();

    };
    public void dismissDialog(){
        alertDialog.dismiss();
    };
}

