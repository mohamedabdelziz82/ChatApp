package com.example.mohamedabdelazizhamad.whatsapp.Activities;

 import android.content.Context;
 import android.content.Intent;
 import android.provider.DocumentsContract;
 import android.support.annotation.NonNull;
 import android.support.annotation.Nullable;
 import android.support.v7.app.ActionBar;
 import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
 import android.support.v7.widget.LinearLayoutManager;
 import android.support.v7.widget.RecyclerView;
 import android.support.v7.widget.Toolbar;
 import android.text.Editable;
 import android.text.TextUtils;
 import android.text.TextWatcher;
 import android.util.Log;
 import android.view.LayoutInflater;
 import android.view.View;
 import android.widget.EditText;
 import android.widget.ImageButton;
 import android.widget.ScrollView;
 import android.widget.TextView;
import android.widget.Toast;

 import com.example.mohamedabdelazizhamad.whatsapp.Adapters.ChatMessagesAdapter;
 import com.example.mohamedabdelazizhamad.whatsapp.Adapters.GroupMessagesAdapter;
 import com.example.mohamedabdelazizhamad.whatsapp.Model.ChatMessagesModel;
 import com.example.mohamedabdelazizhamad.whatsapp.Model.GroupMessagesModel;
 import com.example.mohamedabdelazizhamad.whatsapp.R;
 import com.google.android.gms.tasks.OnCompleteListener;
 import com.google.android.gms.tasks.Task;
 import com.google.firebase.auth.FirebaseAuth;
 import com.google.firebase.database.ChildEventListener;
 import com.google.firebase.database.DataSnapshot;
 import com.google.firebase.database.DatabaseError;
 import com.google.firebase.database.DatabaseReference;
 import com.google.firebase.database.FirebaseDatabase;
 import com.google.firebase.database.ValueEventListener;
 import com.squareup.picasso.Picasso;

 import java.text.SimpleDateFormat;
 import java.util.ArrayList;
 import java.util.Calendar;
 import java.util.HashMap;

 import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private String reciverUserID, retriveUserName, retriveUserImage,senderUserID,currentDate, currentTime;
    private TextView userName,lastSeen;
    private RecyclerView recyclerView;
    private ScrollView myScrollView;
    private CircleImageView imageView;
    private Toolbar mToolbar;
    private EditText userMessageInput;
    private ImageButton sendMessageButton;
    private DatabaseReference RootRef;
    private FirebaseAuth mAuth;
    private ChatMessagesAdapter adapter;
    private ArrayList<ChatMessagesModel> messagesList;
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        RootRef= FirebaseDatabase.getInstance().getReference();
        mAuth=FirebaseAuth.getInstance();
        senderUserID=mAuth.getCurrentUser().getUid();

        reciverUserID = getIntent().getExtras().get("uid").toString();
        retriveUserName = getIntent().getExtras().get("name").toString();
        retriveUserImage = getIntent().getExtras().get("image").toString();

        Log.i("TAG1",reciverUserID+retriveUserName+retriveUserImage);
        InitializationFields();
        userName.setText(retriveUserName);
        Picasso.get().load(retriveUserImage).placeholder(R.drawable.profile_image).into(imageView);
        DisplayLastSeen();
    }
private void DisplayLastSeen(){
    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");
    userRef.child(reciverUserID).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.child("userState").hasChild("state")) {
                String state=dataSnapshot.child("userState").child("state").getValue().toString();
                String date=dataSnapshot.child("userState").child("date").getValue().toString();
                String time=dataSnapshot.child("userState").child("time").getValue().toString();
                if (state.equals("online")) {
                     lastSeen.setText(getResources().getString(R.string.OnlineNow));
                } else  if (state.equals("offline")) {
                    lastSeen.setText(getResources().getString(R.string.LastSeen)+" : "+time+", "+date);

                }
            }else {
                lastSeen.setText(getResources().getString(R.string.offLine));
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });
}
    //----------------------------------------------------------------------------------------------
    private void InitializationFields() {
       mToolbar=findViewById(R.id.chat_bar_layout);
       setSupportActionBar(mToolbar);


        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater= (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar, null);
        actionBar.setCustomView(actionBarView);


        userName=findViewById(R.id.custom_bar_user_name);
        lastSeen=findViewById(R.id.custom_bar_lastseen);
        imageView=findViewById(R.id.custom_bar_profile_image);
        userMessageInput = findViewById(R.id.chat_inputSend);
        sendMessageButton = findViewById(R.id.chat_Send_btn);

        recyclerView = findViewById(R.id.chat_recyclerView);
        myScrollView=findViewById(R.id.myOneChatScrollView);
        messagesList = new ArrayList<>();
        adapter = new ChatMessagesAdapter(ChatActivity.this, messagesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageInfoToDatabase();
                userMessageInput.setText("");
            }

        });
    }

    //----------------------------------------------------------------------------------------------

    @Override
    protected void onStart() {
        super.onStart();
        CheckedEditText();
        RootRef.child("Messages").child(senderUserID).child(reciverUserID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if (dataSnapshot.exists()) {
                            ChatMessagesModel message = dataSnapshot.getValue(ChatMessagesModel.class);
                            messagesList.add(message);
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
                            ChatMessagesModel message = dataSnapshot.getValue(ChatMessagesModel.class);
                            messagesList.add(message);
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
                if (s.toString().trim().length() == 0) {
                    sendMessageButton.setImageResource(R.drawable.textsend);
                } else {
                    sendMessageButton.setImageResource(R.drawable.send);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() == 0) {
                    sendMessageButton.setImageResource(R.drawable.textsend);
                } else {
                    sendMessageButton.setImageResource(R.drawable.send);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() == 0) {
                    sendMessageButton.setImageResource(R.drawable.textsend);
                } else {
                    sendMessageButton.setImageResource(R.drawable.send);
                }

            }
        });
    }
    //----------------------------------------------------------------------------------------------

    private void sendMessageInfoToDatabase()
    {
        String messageText=userMessageInput.getText().toString();
        if (messageText.equals("") || messageText == null || TextUtils.isEmpty(messageText)) {
        } else {
            Calendar calcurrentDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd MMM, yyyy");
            currentDate = currentDateFormat.format(calcurrentDate.getTime());

            Calendar calcurrentTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(calcurrentTime.getTime());

            String messageSenderRef="Messages/"+senderUserID+"/"+reciverUserID;
            String messageReceiverRef="Messages/"+reciverUserID+"/"+senderUserID;

            DatabaseReference userMessagesKeyRef= RootRef.child("Messages").child(senderUserID)
                    .child(reciverUserID).push();
            String messagesPushID = userMessagesKeyRef.getKey();

            HashMap messageMap=new HashMap();
            messageMap.put("message",messageText);
            messageMap.put("date",currentDate);
            messageMap.put("from",senderUserID);
            messageMap.put("time",currentTime);
            messageMap.put("type","text");



            HashMap messageDetailsMap=new HashMap();
            messageDetailsMap.put(messageSenderRef+"/"+messagesPushID,messageMap);
            messageDetailsMap.put(messageReceiverRef+"/"+messagesPushID,messageMap);

            RootRef.updateChildren(messageDetailsMap).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()){
                        Toast.makeText(ChatActivity.this, "Sent.>>", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

}
