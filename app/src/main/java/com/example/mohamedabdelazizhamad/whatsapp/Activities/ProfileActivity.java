package com.example.mohamedabdelazizhamad.whatsapp.Activities;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohamedabdelazizhamad.whatsapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private String receiverUserID, senderUserID, currentState;
    private DatabaseReference userRef, chatRequestRef, contactsRef, notificationsRef;
    private Button sendChatRequestButton, declineChatRequestButton;
    private TextView userName, userStatus;
    private CircleImageView profile_image;
    private FirebaseAuth mAuth;
    private Drawable img;

    //----------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        receiverUserID = getIntent().getExtras().get("id").toString();
        mAuth = FirebaseAuth.getInstance();
        senderUserID = mAuth.getCurrentUser().getUid();

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        chatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");
        notificationsRef = FirebaseDatabase.getInstance().getReference().child("Notifications");

        Toast.makeText(this, "id  :" + receiverUserID, Toast.LENGTH_SHORT).show();
        InitializationFields();

        currentState = "new";
        RetrieveUserInfo();


    }
    //----------------------------------------------------------------------------------------------

    private void InitializationFields() {
        sendChatRequestButton = findViewById(R.id.profile_send_message);
        declineChatRequestButton = findViewById(R.id.profile_decline_send_message);
        profile_image = findViewById(R.id.profile_photo);
        userName = findViewById(R.id.profile_user_name);
        userStatus = findViewById(R.id.profile_user_Status);
        img = getResources().getDrawable(R.drawable.remove_contact);


    }
    //----------------------------------------------------------------------------------------------

    private void RetrieveUserInfo() {
        userRef.child(receiverUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChild("image")) {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();
                    String image = dataSnapshot.child("image").getValue().toString();

                    userName.setText(name);
                    userStatus.setText(status);
                    Picasso.get().load(image).placeholder(R.drawable.profile_image).into(profile_image);
                    ManageChatRequests();
                } else {
                    String name = dataSnapshot.child("name").getValue().toString();
                    String status = dataSnapshot.child("status").getValue().toString();
                    userName.setText(name);
                    userStatus.setText(status);
                    ManageChatRequests();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //----------------------------------------------------------------------------------------------

    private void ManageChatRequests() {
        chatRequestRef.child(senderUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(receiverUserID)) {
                            String Request = dataSnapshot.child(receiverUserID).child("Request Type").getValue().toString();
                            if (Request.equals("sent")) {
                                currentState = "Request sent";
                                Log.i("Request", Request);
                                sendChatRequestButton.setText(getResources().getString(R.string.cancelChatRequest));
                            } else if (Request.equals("received")) {
                                currentState = "Request received";
                                sendChatRequestButton.setText(getResources().getString(R.string.AcceptChatRequest));
                                declineChatRequestButton.setVisibility(View.VISIBLE);
                                declineChatRequestButton.setEnabled(true);
                                declineChatRequestButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        CancelChatRequest();
                                    }
                                });
                            }
                        } else {
                            contactsRef.child(senderUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(receiverUserID)) {
                                        sendChatRequestButton.setEnabled(true);
                                        currentState = "friends";
                                        sendChatRequestButton.setText(getResources().getString(R.string.Deletethecontact));
                                        sendChatRequestButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        if (!senderUserID.equals(receiverUserID)) {
            sendChatRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendChatRequestButton.setEnabled(false);
                    if (currentState.equals("new")) {
                        SendChatRequest();
                    }
                    if (currentState.equals("Request sent")) {
                        CancelChatRequest();
                    }
                    if (currentState.equals("Request received")) {
                        AcceptChatRequest();
                    }
                    if (currentState.equals("friends")) {
                        DeleteOneContact();
                    }

                }
            });
        } else {
            sendChatRequestButton.setVisibility(View.INVISIBLE);
        }

    }

    //----------------------------------------------------------------------------------------------
    private void DeleteOneContact() {
        contactsRef.child(senderUserID).child(receiverUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            contactsRef.child(receiverUserID).child(senderUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            sendChatRequestButton.setEnabled(true);
                                            currentState = "new";
                                            sendChatRequestButton.setText(getResources().getString(R.string.sendChatRequest));
                                            declineChatRequestButton.setVisibility(View.INVISIBLE);
                                            declineChatRequestButton.setEnabled(false);
                                        }
                                    });
                        }
                    }
                });
    }

    //----------------------------------------------------------------------------------------------
    private void AcceptChatRequest() {
        contactsRef.child(senderUserID).child(receiverUserID)
                .child("Contact").setValue("saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            contactsRef.child(receiverUserID).child(senderUserID)
                                    .child("Contacts").setValue("saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                chatRequestRef.child(senderUserID).child(receiverUserID)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    chatRequestRef.child(receiverUserID).child(senderUserID)
                                                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                                sendChatRequestButton.setEnabled(true);
                                                                                currentState = "friends";
                                                                                sendChatRequestButton.setText(getResources().getString(R.string.Deletethecontact));
                                                                                sendChatRequestButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                                                                                declineChatRequestButton.setVisibility(View.INVISIBLE);
                                                                                declineChatRequestButton.setEnabled(false);
                                                                            }
                                                                        }
                                                                    });
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    //----------------------------------------------------------------------------------------------
    private void CancelChatRequest() {
        chatRequestRef.child(senderUserID).child(receiverUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            chatRequestRef.child(receiverUserID).child(senderUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            sendChatRequestButton.setEnabled(true);
                                            currentState = "new";
                                            sendChatRequestButton.setText(getResources().getString(R.string.sendChatRequest));
                                            declineChatRequestButton.setVisibility(View.INVISIBLE);
                                            declineChatRequestButton.setEnabled(false);
                                        }
                                    });
                        }
                    }
                });
    }
    //----------------------------------------------------------------------------------------------

    private void SendChatRequest() {
        chatRequestRef.child(senderUserID).child(receiverUserID).child("Request Type")
                .setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            chatRequestRef.child(receiverUserID).child(senderUserID).child("Request Type")
                                    .setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            HashMap<String, String> chatNotificationsMap = new HashMap<>();
                                            chatNotificationsMap.put("from", senderUserID);
                                            chatNotificationsMap.put("type", "request");

                                            notificationsRef.child(receiverUserID).push()
                                                    .setValue(chatNotificationsMap)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                sendChatRequestButton.setEnabled(true);
                                                                currentState = "Request sent";
                                                                sendChatRequestButton.setText(getResources().getString(R.string.cancelChatRequest));

                                                            }
                                                        }
                                                    });
                                        }
                                    });
                        }
                    }
                });
    }
}
