package com.example.mohamedabdelazizhamad.whatsapp.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mohamedabdelazizhamad.whatsapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private Toolbar myToolbar;
    private EditText editTextEmail;
    private Button resetBtn;
    private FirebaseAuth mAuth;
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mAuth=FirebaseAuth.getInstance();
        mAuth.setLanguageCode("ar");
        relativeLayout=findViewById(R.id.reset_RelativeLayout);

        myToolbar = findViewById(R.id.reset_bar);

        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.ResetPassword));

        editTextEmail=findViewById(R.id.reset_email);
        resetBtn=findViewById(R.id.Reset_btn);


        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=editTextEmail.getText().toString();
                if (email.equals("") || email == null) {
                    ShowSnack(getResources().getString(R.string.EmailisEmpty));
                }else {
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                ShowSnack(getResources().getString(R.string.pleaseCheckYourEmail));
                                SendUserToLoginActivity();
                            }else {
                                ShowSnack(getResources().getString(R.string.Thisemailiswrong));

                            }
                        }
                    });
                }
            }
        });


    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
         startActivity(loginIntent);
     }
    @SuppressLint("NewApi")
    private void ShowSnack(String message) {
        Snackbar mSnackBar = Snackbar.make(relativeLayout, message, Snackbar.LENGTH_LONG);
        View view = mSnackBar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.gravity = Gravity.TOP;
        view.setLayoutParams(params);
        view.setBackgroundColor(Color.WHITE);
        TextView mainTextView = (TextView) (view).findViewById(android.support.design.R.id.snackbar_text);
        mainTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
        mainTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        mSnackBar.show();
    }

}
