package com.example.mohamedabdelazizhamad.whatsapp.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohamedabdelazizhamad.whatsapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.internal.FirebaseInstanceIdInternal;

public class LoginActivity extends AppCompatActivity {
    private EditText UserEmail, UserPassword;
    private Button LoginButton, LoginPhoneButton;
    private TextView NeedNewAccountlink, ForgetPasswordlink;
    private RelativeLayout relativeLayout;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;
    private ProgressDialog progressDialog;
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        usersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        InitializationFields();

        LoginPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phone = new Intent(LoginActivity.this, PhoneLoginActivity.class);
                startActivity(phone);
            }
        });

        NeedNewAccountlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToRegsiterActivity();
            }
        });
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllowCurrentUser();
            }
        });
        ForgetPasswordlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToResetPassawordActivity();

            }
        });
    }
    //----------------------------------------------------------------------------------------------

    private void InitializationFields() {
        UserEmail = findViewById(R.id.login_email);
        UserPassword = findViewById(R.id.login_password);
        LoginButton = findViewById(R.id.btn_login);
        LoginPhoneButton = findViewById(R.id.btn_login_Phone);
        NeedNewAccountlink = findViewById(R.id.need_newAccount_link);
        ForgetPasswordlink = findViewById(R.id.forget_password_link);
        relativeLayout = findViewById(R.id.login_layout);

    }

    //----------------------------------------------------------------------------------------------
    private void AllowCurrentUser() {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        if (email.equals("") || email == null) {
            ShowSnack(getResources().getString(R.string.EmailisEmpty));
        } else if (password.equals("") || password == null) {
            ShowSnack(getResources().getString(R.string.passwordisEmpty));
        } else {
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setTitle(getResources().getString(R.string.login));
            progressDialog.setMessage(getResources().getString(R.string.pleaseWait));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                String currentUsetID=mAuth.getCurrentUser().getUid();
                                String deviceToken= FirebaseInstanceId.getInstance().getToken();
                                usersRef.child(currentUsetID).child("device_Token")
                                        .setValue(deviceToken)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                              if (task.isSuccessful()){
                                                  SendUserToMainActivity();
                                                  ShowSnack(getResources().getString(R.string.loggedinSuccessful));
                                                  progressDialog.dismiss();
                                              }
                                            }
                                        });

                            } else {
                                ShowSnack(getResources().getString(R.string.EmailOrPasswordError));
                                progressDialog.dismiss();

                            }
                        }
                    });
        }
    }

    //----------------------------------------------------------------------------------------------

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
    //----------------------------------------------------------------------------------------------
    private void SendUserToResetPassawordActivity() {
        Intent Intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
        startActivity(Intent);
    }
    //----------------------------------------------------------------------------------------------

    private void SendUserToRegsiterActivity() {
        Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }
    //----------------------------------------------------------------------------------------------

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
