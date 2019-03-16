package com.example.mohamedabdelazizhamad.whatsapp.Activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohamedabdelazizhamad.whatsapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.HashMap;


import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private EditText userName, userStatus;
    private Button UpdateButton;
    private CircleImageView imageView;
    private FirebaseAuth mAuth;
    private DatabaseReference Refroot;
    private String currentUserID;
    private RelativeLayout relativeLayout;
    private final static int GalleryPick = 1;
    private StorageReference profileImageRef;
    private ProgressDialog progressDialog;
    private String loggg;
    private Uri image;
    private String im;
    private Toolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        profileImageRef = FirebaseStorage.getInstance().getReference().child("Profile images");


        mAuth = FirebaseAuth.getInstance();
        Refroot = FirebaseDatabase.getInstance().getReference();
        currentUserID = mAuth.getCurrentUser().getUid();


        InitializationFields();
        userName.setVisibility(View.INVISIBLE);

        UpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateUserProfile();
            }
        });

             imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = userName.getText().toString();
                    String status = userStatus.getText().toString();
                    if (name.equals("") || name == null) {
                        ShowSnack(getResources().getString(R.string.usernameisEmpty));
                    } else if (status.equals("") || status == null) {
                        ShowSnack(getResources().getString(R.string.statusisEmpty));
                    }else {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        startActivityForResult(intent, GalleryPick);
                    }
                }
            });
         RetrieveUserInfo();
    }


    private void InitializationFields() {
        UpdateButton = findViewById(R.id.btn_Update_Setting);
        imageView = findViewById(R.id.profile_image);
        relativeLayout = findViewById(R.id.SettingsLayout);
        userName = findViewById(R.id.set_user_name);
        userStatus = findViewById(R.id.set_user_Status);
        progressDialog = new ProgressDialog(SettingsActivity.this);
        myToolbar = findViewById(R.id.setting_Toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.setting));


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null && data.getData() != null) {
            image = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), image);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(bitmap);
            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                progressDialog.setTitle(getResources().getString(R.string.setProfileImage));
                progressDialog.setMessage(getResources().getString(R.string.pleaseWait));
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                Uri resultUri = result.getUri();
                final StorageReference filePath = profileImageRef.child(currentUserID + "." + getFileExtension(image));
                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                    }
                });

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    loggg = uri.toString();
                                    Log.i("loggg", loggg);
                                    Refroot.child("Users").child(currentUserID).child("image")
                                            .setValue(loggg)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(SettingsActivity.this, "yes", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(SettingsActivity.this, "no", Toast.LENGTH_SHORT).show();

                                                    }
                                                }
                                            });
                                    // Got the download URL for 'users/me/profile.png'
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });
                            ShowSnack(getResources().getString(R.string.ImageProfileUpdatedSuccessfully));
                        } else {
                            ShowSnack(getResources().getString(R.string.ImageProfileNotUpdate));
                            progressDialog.dismiss();

                        }
                    }
                });
            }


        }
    }

    private void UpdateUserProfile() {
        String name = userName.getText().toString();
        String status = userStatus.getText().toString();
        if (name.equals("") || name == null) {
            ShowSnack(getResources().getString(R.string.usernameisEmpty));
        }   else {
            if (status.equals("") || status == null) {
                status=getResources().getString(R.string.heyiamavailablenow);
            }
            HashMap<String, Object> profileMap = new HashMap<>();
            profileMap.put("uid", currentUserID);
            profileMap.put("name", name);
            profileMap.put("status", status);
             Refroot.child("Users").child(currentUserID).updateChildren(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                SendUserToMainActivity();
                                ShowSnack(getResources().getString(R.string.ProfileUpdatedSuccessfully));
                            } else {
                                ShowSnack(getResources().getString(R.string.ProfileNotUpdate));
                            }
                        }
                    });
        }

    }


    private void RetrieveUserInfo() {
        Refroot.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChild("name") && dataSnapshot.hasChild("image")) {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();
                    String image = dataSnapshot.child("image").getValue().toString();

                    Log.i("mohamed", image);

                    userName.setText(name);
                    userStatus.setText(status);
                    Picasso.get().load(image).placeholder(R.drawable.profile_image).into(imageView);
                } else if (dataSnapshot.exists() && dataSnapshot.hasChild("name")&&dataSnapshot.hasChild("status")) {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();
                    userName.setText(name);
                    userStatus.setText(status);

                } else {
                    userName.setVisibility(View.VISIBLE);
                    ShowSnack(getResources().getString(R.string.Pleasesetupdateuserinfo));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void SendUserToMainActivity() {
        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    public String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
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
