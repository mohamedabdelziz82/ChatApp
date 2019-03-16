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

public class RegisterActivity extends AppCompatActivity {
    private EditText UserEmail, UserPassword;
    private Button CreateAccountButton;
    private TextView AlreadyHaveAccountlink;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAtuh;
    private DatabaseReference reference;
    private RelativeLayout relativeLayout;
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAtuh = FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();
        InitializationFields();
        AlreadyHaveAccountlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToLoginActivity();
            }
        });
        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });
    }

    //----------------------------------------------------------------------------------------------

    private void InitializationFields() {
        UserEmail = findViewById(R.id.register_email);
        UserPassword = findViewById(R.id.register_password);
        CreateAccountButton = findViewById(R.id.btn_createAccount);
        AlreadyHaveAccountlink = findViewById(R.id.already_have_an_account_link);
        relativeLayout = findViewById(R.id.register_layout);
    }
    //----------------------------------------------------------------------------------------------

    private void CreateNewAccount() {

        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        if (email.equals("") || email == null) {
            ShowSnack(getResources().getString(R.string.EmailisEmpty));
        } else if (password.equals("") || password == null) {
            ShowSnack(getResources().getString(R.string.passwordisEmpty));
        } else if (password.length() < 6) {
            ShowSnack(getResources().getString(R.string.ThePasswordShouldBe));
        } else {
            progressDialog = new ProgressDialog(RegisterActivity.this);
            progressDialog.setTitle(getResources().getString(R.string.CreatingNewAccount));
            progressDialog.setMessage(getResources().getString(R.string.PleaseWaitCreatingNewAccount));
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            mAtuh.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                String currentUser = mAtuh.getCurrentUser().getUid();
                                String deviceToken= FirebaseInstanceId.getInstance().getToken();

                                reference.child("Users").child(currentUser).setValue("");
                                reference.child("Users").child(currentUser).child("device_Token")
                                        .setValue(deviceToken).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            SendUserToMainActivity();
                                            ShowSnack(getResources().getString(R.string.AccountCreatedSuccessfully));
                                            progressDialog.dismiss();
                                        }
                                    }
                                });

                            } else {
                                if (task.getException().getMessage().equals("The email address is already in use by another account.")) {
                                    ShowSnack(getResources().getString(R.string.TheEmailAddressIsAlreadyInUse));
                                    progressDialog.dismiss();

                                } else {

                                    String message = task.getException().toString();
                                    ShowSnack("Error : " + message);
                                    progressDialog.dismiss();

                                }
                            }
                        }
                    });

        }
    }

    //----------------------------------------------------------------------------------------------

    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
    //----------------------------------------------------------------------------------------------

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
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
