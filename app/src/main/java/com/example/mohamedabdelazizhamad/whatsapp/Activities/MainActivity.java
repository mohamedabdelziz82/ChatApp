package com.example.mohamedabdelazizhamad.whatsapp.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohamedabdelazizhamad.whatsapp.Fragments.ChatsFragment;
import com.example.mohamedabdelazizhamad.whatsapp.Fragments.ContactsFragment;
import com.example.mohamedabdelazizhamad.whatsapp.Fragments.GroupsFragment;
import com.example.mohamedabdelazizhamad.whatsapp.Fragments.RequestsFragment;
import com.example.mohamedabdelazizhamad.whatsapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabsAdapters tabsAdapter;
    private FirebaseAuth mAuth;
    private DatabaseReference Refroot;
    private Toolbar myToolbar;
    private RelativeLayout relativeLayout;
    //----------------------------------------------------------------------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();
        Refroot = FirebaseDatabase.getInstance().getReference();


        myToolbar = findViewById(R.id.main_bar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
        myViewPager = findViewById(R.id.mainViewPager);
        myTabLayout = findViewById(R.id.mainTabs);
        relativeLayout = findViewById(R.id.MainLayout);
        tabsAdapter = new TabsAdapters(getSupportFragmentManager());
        myViewPager.setAdapter(tabsAdapter);
        myTabLayout.setupWithViewPager(myViewPager);
        myTabLayout.getTabAt(3).setIcon(R.drawable.chat_requests);
    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            SendUserToLoginActivity();
        } else {
            onlineStatus("online");
            VerifyUser();
        }
    }

    //----------------------------------------------------------------------------------------------


    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            onlineStatus("offline");
        }
    }

    @Override
    protected void onDestroy() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        super.onDestroy();
        if (currentUser != null) {
            onlineStatus("offline");
        }
    }

//----------------------------------------------------------------------------------------------

    private void onlineStatus(String state) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Calendar calcurrentDate = Calendar.getInstance();
        SimpleDateFormat currentDateFormat = new SimpleDateFormat("dd MMM, yyyy");
        String currentDate = currentDateFormat.format(calcurrentDate.getTime());

        Calendar calcurrentTime = Calendar.getInstance();
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
        String currentTime = currentTimeFormat.format(calcurrentTime.getTime());

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("date", currentDate);
        hashMap.put("time", currentTime);
        hashMap.put("state", state);
        DatabaseReference ref = userRef.child(currentUser.getUid()).child("userState");
        ref.updateChildren(hashMap);
    }
    //----------------------------------------------------------------------------------------------

    private void VerifyUser() {
        String currentUser = mAuth.getCurrentUser().getUid();
        Refroot.child("Users").child(currentUser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if ((dataSnapshot.child("name").exists())) {
                } else {
                    SendUserToSettingsActivity();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch (item.getItemId()) {
            case R.id.main_logout:
                onlineStatus("offline");
                mAuth.signOut();
                SendUserToLoginActivity();
                break;
            case R.id.main_setting_option:
                SendUserToSettingsActivity();
                break;
            case R.id.main_createGroup:
                RequestNewGroup();
                break;
            case R.id.main_find_friends:
                SendUserToFindFriendsActivity();
                break;
        }
        return true;
    }
    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------

    private void RequestNewGroup() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        builder.setTitle(getResources().getString(R.string.EnterGroupName));
        final EditText txtgroupname = new EditText(MainActivity.this);
        txtgroupname.setHint(getResources().getString(R.string.EnterGroupName));
        builder.setView(txtgroupname);
        builder.setPositiveButton(getResources().getString(R.string.Create), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = txtgroupname.getText().toString();
                if (groupName.equals("") || groupName == null) {
                } else {
                    CreateGroupInFireDB(groupName);

                }
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.Cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
    //----------------------------------------------------------------------------------------------


    private void CreateGroupInFireDB(final String groupName) {
        Refroot.child("Group").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            ShowSnack(groupName + " " + getResources().getString(R.string.groupiscreated));
                        }
                    }
                });
    }
    //----------------------------------------------------------------------------------------------

    private void SendUserToSettingsActivity() {
        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);

    }
    //----------------------------------------------------------------------------------------------

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
    //----------------------------------------------------------------------------------------------

    private void SendUserToFindFriendsActivity() {
        Intent FindFriendsIntent = new Intent(MainActivity.this, FindFriendsActivity.class);
        startActivity(FindFriendsIntent);
    }

    //----------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------


    //    Tabs Adapter
    public class TabsAdapters extends FragmentPagerAdapter {
        public TabsAdapters(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    ChatsFragment chatsFragment = new ChatsFragment();
                    return chatsFragment;
                case 1:
                    GroupsFragment groupsFragment = new GroupsFragment();

                    return groupsFragment;
                case 2:
                    ContactsFragment contactsFragment = new ContactsFragment();

                    return contactsFragment;
                case 3:
                    RequestsFragment requestsFragment = new RequestsFragment();
                    return requestsFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.Chats);
                case 1:
                    return getResources().getString(R.string.Groups);
                case 2:
                    return getResources().getString(R.string.Contacts);
                case 3:
//                    getResources().getString(R.string.ChatRequests);
                    return null;
                default:
                    return null;
            }
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

