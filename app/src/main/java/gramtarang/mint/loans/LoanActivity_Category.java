package gramtarang.mint.loans;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Spinner;

import gramtarang.mint.R;

public class LoanActivity_Category extends AppCompatActivity {

    Spinner bank,category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_category);

        bank = findViewById(R.id.sp_bank);
        category = findViewById(R.id.sp_cat);
    }
}