package com.example.mohamedabdelazizhamad.whatsapp.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {
    private static EditText phoneNumber, CodeVerification;
    private static Button ContinueButton, LoginButton;
    private static TextView textCount, textVerification, textPhone, text, textEditNum;
    private RelativeLayout relativeLayout;
    private FirebaseAuth mAuth;
    private static PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String mVerificationId;
    private static PhoneAuthProvider.ForceResendingToken mResendToken;
    private ProgressDialog progressDialog;
    private counter ct;
    private int count = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);

        InitializationFields();
        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("ar");

        ContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile = phoneNumber.getText().toString();


                if (mobile.isEmpty() || mobile.length() < 10) {
                    phoneNumber.setError(getResources().getString(R.string.EnteravalidPhone));
                } else {
                    progressDialog.setTitle(getResources().getString(R.string.VerificationCode));
                    progressDialog.setMessage(getResources().getString(R.string.WaitforthecodeIsent));
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            "+20" + mobile,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            PhoneLoginActivity.this,               // Activity (for callback binding)
                            mCallbacks);

//                    textEditNum.setVisibility(View.VISIBLE);

                }
            }
        });
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                String code = credential.getSmsCode();

                //sometime the code is not detected automatically
                //in this case the code will be null
                //so user has to manually enter the code
                if (code != null) {
                    CodeVerification.setText(code);
                    //verifying the code
                    credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                    signInWithPhoneAuthCredential(credential);

                }
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                ShowSnack(getResources().getString(R.string.EnteravalidPhone));
                progressDialog.dismiss();
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                mVerificationId = verificationId;
                mResendToken = token;
                ShowSnack(getResources().getString(R.string.codehasbeensent));
                phoneNumber.setVisibility(View.INVISIBLE);
                textPhone.setVisibility(View.INVISIBLE);
                ContinueButton.setVisibility(View.INVISIBLE);
                text.setVisibility(View.INVISIBLE);
                progressDialog.dismiss();

                CodeVerification.setVisibility(View.VISIBLE);
                textVerification.setVisibility(View.VISIBLE);
                LoginButton.setVisibility(View.VISIBLE);
                textCount.setVisibility(View.VISIBLE);

                startCount();
                if (count == 0) {
                    textEditNum.setVisibility(View.VISIBLE);
                    textCount.setText(getResources().getString(R.string.Iamhavingtrouble));
                    ct.cancel();
                }
            }
        };
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = CodeVerification.getText().toString().trim();
                if (code.isEmpty() || code.length() < 6) {
                    CodeVerification.setError(getResources().getString(R.string.Invalidcodeentered));
                } else {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });
//        ResendButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String mobile = phoneNumber.getText().toString().trim();
//
//                PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                        "+20" + mobile,        // Phone number to verify
//                        60,                 // Timeout duration
//                        TimeUnit.SECONDS,   // Unit of timeout
//                        PhoneLoginActivity.this,               // Activity (for callback binding)
//                        mCallbacks,         // OnVerificationStateChangedCallbacks
//                        mResendToken);
//            }
//        });
    }

    private void InitializationFields() {
        phoneNumber = findViewById(R.id.editTextphoneNumber);
        CodeVerification = findViewById(R.id.editTextCode);
        ContinueButton = findViewById(R.id.Continue_btn);
        LoginButton = findViewById(R.id.buttonLoginPhone);
        text = findViewById(R.id.text);
        textCount = findViewById(R.id.textcount);
        textPhone = findViewById(R.id.textphone);
        textVerification = findViewById(R.id.textVerification);
        ContinueButton = findViewById(R.id.Continue_btn);
        relativeLayout = findViewById(R.id.PhoneLoginActivity);
        textEditNum = findViewById(R.id.textEditPhoneNumber);
        progressDialog = new ProgressDialog(this);


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            ShowSnack(getResources().getString(R.string.loggedinSuccessful));
                            SendUserToMainActivity();
                        } else {
                            ShowSnack(getResources().getString(R.string.Invalidcodeentered));
                        }
                    }
                });
    }


    public void startCount() {
        ct = new counter(10000, 1000);
        ct.start();
    }

    public class counter extends CountDownTimer {


        public counter(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            textCount.setVisibility(View.VISIBLE);
            count--;
            if (count < 10) {
                textCount.setText(getResources().getString(R.string.ResendCodein) + "0" + count);
            } else {
                textCount.setText(getResources().getString(R.string.ResendCodein) + count);
            }
            if (count == 0) {
                textEditNum.setVisibility(View.VISIBLE);
                textCount.setText(getResources().getString(R.string.Iamhavingtrouble));
                textCount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog();
                        bottomSheetDialog.show(getSupportFragmentManager(), bottomSheetDialog.getTag());
                    }
                });
                textEditNum.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        phoneNumber.setVisibility(View.VISIBLE);
                        textPhone.setVisibility(View.VISIBLE);
                        ContinueButton.setVisibility(View.VISIBLE);
                        text.setVisibility(View.VISIBLE);

                        CodeVerification.setVisibility(View.INVISIBLE);
                        textVerification.setVisibility(View.INVISIBLE);
                        textEditNum.setVisibility(View.INVISIBLE);
                        LoginButton.setVisibility(View.INVISIBLE);
                        textCount.setVisibility(View.INVISIBLE);

                    }
                });
                ct.cancel();

            }

        }

        @Override
        public void onFinish() {
            startCount();
        }
    }


    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(PhoneLoginActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }


    private static void ResendVerificationCode() {
        String mobile = phoneNumber.getText().toString().trim();
        PhoneLoginActivity phoneLoginActivity = new PhoneLoginActivity();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+20" + mobile,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                phoneLoginActivity,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                mResendToken);

    }

//    -------------------------------------SheetBottomDailog----------------------------------------------

    @SuppressLint("ValidFragment")
    public static class BottomSheetDialog extends BottomSheetDialogFragment {

        @SuppressLint("RestrictedApi")
        @Override
        public void setupDialog(final Dialog dialog, int style) {
            super.setupDialog(dialog, style);
            View v = View.inflate(getContext(), R.layout.custom_sheet_dialog, null);
            dialog.setContentView(v);

            TextView textNum = v.findViewById(R.id.sheetDialogNum);
            Button resend = v.findViewById(R.id.sheet_resend_btn);
            Button cancel = v.findViewById(R.id.sheet_cancel);

            textNum.setText(getResources().getString(R.string.ResendTo) + " " + phoneNumber.getText().toString());
            resend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mobile = phoneNumber.getText().toString().trim();

                    ResendVerificationCode();
                    dialog.dismiss();

                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
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
