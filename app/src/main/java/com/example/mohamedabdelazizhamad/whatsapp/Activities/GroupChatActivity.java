package com.example.mohamedabdelazizhamad.whatsapp.Activities;


import java.text.SimpleDateFormat;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;

import com.example.mohamedabdelazizhamad.whatsapp.Adapters.GroupMessagesAdapter;
import com.example.mohamedabdelazizhamad.whatsapp.Model.GroupMessagesModel;
import com.example.mohamedabdelazizhamad.whatsapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class GroupChatActivity extends AppCompatActivity {
    private Toolbar myToolbar;
    private RecyclerView recyclerView;
    private EditText userMessageInput;
    private ImageButton sendMessageButton;
    private ScrollView myScrollView;
    private String groupName, currentUserID, currentUserName, currentDate, currentTime;
    private FirebaseAuth mAuth;
    private DatabaseReference UserRef, GroupNameRef, GroupMessageKeyRef;
    private String message;
    private GroupMessagesAdapter adapter;
    private ArrayList<GroupMessagesModel> messagesList;
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        groupName = getIntent().getExtras().get("GroupName").toString();

        mAuth = FirebaseAuth.getInstance();

        currentUserID = mAuth.getCurrentUser().getUid();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        GroupNameRef = FirebaseDatabase.getInstance().getReference().child("Group").child(groupName);

        InitializationFields();
        GetUserInfo();
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageInfoToDatabase();
                userMessageInput.setText("");

            }

        });

    }

    //----------------------------------------------------------------------------------------------

    private void InitializationFields() {
        myToolbar = findViewById(R.id.group_chat_bar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(groupName);

        userMessageInput = findViewById(R.id.group_chat_inputSend);
        sendMessageButton = findViewById(R.id.group_chat_Send_btn);
        myScrollView = findViewById(R.id.myGroupChatScrollView);

        recyclerView = findViewById(R.id.chat_group_recyclerView);
        messagesList = new ArrayList<>();
        adapter = new GroupMessagesAdapter(GroupChatActivity.this, messagesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(GroupChatActivity.this));
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onStart() {
        super.onStart();
        CheckedEditText();

        GroupNameRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    GroupMessagesModel groupMessagesModel = dataSnapshot.getValue(GroupMessagesModel.class);
                    messagesList.add(groupMessagesModel);
                    recyclerView.scrollToPosition(messagesList.size() - 1);
                    adapter.notifyItemInserted(messagesList.size() - 1);
                    adapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
                    myScrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            myScrollView.fullScroll(View.FOCUS_DOWN);
                            userMessageInput.requestFocus();
                        }
                    });
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    GroupMessagesModel groupMessagesModel = dataSnapshot.getValue(GroupMessagesModel.class);
                    messagesList.add(groupMessagesModel);
                    recyclerView.scrollToPosition(messagesList.size() - 1);
                    adapter.notifyItemInserted(messagesList.size() - 1);
                    adapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(recyclerView.getAdapter().getItemCount());
                    adapter.notifyDataSetChanged();
                    myScrollView.post(new Runnable() {
                        @Override
                        public void run() {
                            myScrollView.fullScroll(View.FOCUS_DOWN);
                            userMessageInput.requestFocus();
                        }
                    });
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
    //----------------------------------------------------------------------------------------------

    private void CheckedEditText() {
        userMessageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().trim()==""||s.toString().trim().length() == 0||TextUtils.isEmpty(s)) {
                    sendMessageButton.setImageResource(R.drawable.textsend);
                } else {
                    sendMessageButton.setImageResource(R.drawable.send);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim()==""||s.toString().trim().length() == 0||TextUtils.isEmpty(s)) {
                    sendMessageButton.setImageResource(R.drawable.textsend);
                } else {
                    sendMessageButton.setImageResource(R.drawable.send);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim()==""||s.toString().trim().length() == 0||TextUtils.isEmpty(s)) {
                    sendMessageButton.setImageResource(R.drawable.textsend);
                } else {
                    sendMessageButton.setImageResource(R.drawable.send);
                }

            }
        });
    }

    //----------------------------------------------------------------------------------------------

    private void GetUserInfo() {
        UserRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentUserName = dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //----------------------------------------------------------------------------------------------

    private void sendMessageInfoToDatabase() {
        message = userMessageInput.getText().toString();
        String messagekey = GroupNameRef.push().getKey();
        if (message.equals("") || message == null || TextUtils.isEmpty(message)) {
        } else {
            sendMessageButton.setImageResource(R.drawable.send);
            Calendar calcurrentDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd MMM, yyyy");
            currentDate = currentDateFormat.format(calcurrentDate.getTime());

            Calendar calcurrentTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(calcurrentTime.getTime());

            HashMap<String, Object> groupMessageKey = new HashMap<>();
            GroupNameRef.updateChildren(groupMessageKey);

            GroupMessageKeyRef = GroupNameRef.child(messagekey);

            HashMap<String, Object> MessageKeyRef = new HashMap<>();
            MessageKeyRef.put("name", currentUserName);
            MessageKeyRef.put("message", message);
            MessageKeyRef.put("date", currentDate);
            MessageKeyRef.put("time", currentTime);
            MessageKeyRef.put("from", currentUserID);

            MessageKeyRef.entrySet();
            GroupMessageKeyRef.updateChildren(MessageKeyRef);


//           GroupMessageKeyRef.setValue(messageModel);
        }
    }
    //----------------------------------------------------------------------------------------------


}
