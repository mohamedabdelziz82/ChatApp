package com.example.mohamedabdelazizhamad.whatsapp.Fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mohamedabdelazizhamad.whatsapp.Activities.ChatActivity;
import com.example.mohamedabdelazizhamad.whatsapp.Model.Contacts;
import com.example.mohamedabdelazizhamad.whatsapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private DatabaseReference chatsRef, userRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    public ChatsFragment() {
        // Required empty public constructor
    }
    //----------------------------------------------------------------------------------------------


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_contacts, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        chatsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mRecyclerView = v.findViewById(R.id.Contacts_recycler_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return v;
    }
    public boolean isOnlineConnection() {
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    //----------------------------------------------------------------------------------------------

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        final boolean ischat = true;
        super.onActivityCreated(savedInstanceState);
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chatsRef, Contacts.class)
                .build();
        FirebaseRecyclerAdapter<Contacts, ChatsFragment.ChatsHolder> adapter = new FirebaseRecyclerAdapter<Contacts, ChatsFragment.ChatsHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatsFragment.ChatsHolder holder, int position, @NonNull final Contacts model) {
                final String uID = getRef(position).getKey();
                userRef.child(uID).addValueEventListener(new ValueEventListener() {
                    private String image = "default_image";

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.hasChild("image")) {
                                image = dataSnapshot.child("image").getValue().toString();
                                Picasso.get().load(image).placeholder(R.drawable.profile_image).into(holder.imageProfile);
                            }
                            final String name = dataSnapshot.child("name").getValue().toString();
                            String status = dataSnapshot.child("status").getValue().toString();

                            holder.userName.setText(name);

                            if (dataSnapshot.child("userState").hasChild("state")) {
                                String state = dataSnapshot.child("userState").child("state").getValue().toString();
                                String date = dataSnapshot.child("userState").child("date").getValue().toString();
                                String time = dataSnapshot.child("userState").child("time").getValue().toString();
                                if (state.equals("online")) {
                                    holder.imOnline.setVisibility(View.VISIBLE);
                                    Activity activity = getActivity();
                                    if(activity != null && isAdded()) {
                                        holder.userStatu.setText(getResources().getString(R.string.OnlineNow));
                                    }
                                } else if (state.equals("offline")) {
                                    holder.imOnline.setVisibility(View.INVISIBLE);
                                    Activity activity = getActivity();
                                    if(activity != null && isAdded()) {
                                        holder.userStatu.setText(getResources().getString(R.string.LastSeen) + " : " + time + ", " + date);
                                    }
                                }
                            } else {
                                holder.imOnline.setVisibility(View.INVISIBLE);
                            }
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getContext(), ChatActivity.class);
                                    intent.putExtra("uid", uID);
                                    intent.putExtra("name", name);
                                    intent.putExtra("image", image);
                                    startActivity(intent);
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            //--------------------------------------------------

            @NonNull
            @Override
            public ChatsFragment.ChatsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.user_friends_row, viewGroup, false);
                return new ChatsFragment.ChatsHolder(view);
            }
        };
        adapter.onDataChanged();
        adapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(chatsRef, Contacts.class)
                .build();
        FirebaseRecyclerAdapter<Contacts, ChatsFragment.ChatsHolder> adapter = new FirebaseRecyclerAdapter<Contacts, ChatsFragment.ChatsHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ChatsFragment.ChatsHolder holder, int position, @NonNull final Contacts model) {
                final String uID = getRef(position).getKey();
                userRef.child(uID).addValueEventListener(new ValueEventListener() {
                    private String image = "default_image";

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.hasChild("image")) {
                                image = dataSnapshot.child("image").getValue().toString();
                                Picasso.get().load(image).placeholder(R.drawable.profile_image).into(holder.imageProfile);
                            }
                            final String name = dataSnapshot.child("name").getValue().toString();
                            String status = dataSnapshot.child("status").getValue().toString();

                            holder.userName.setText(name);

                            if (dataSnapshot.child("userState").hasChild("state")) {
                                String state = dataSnapshot.child("userState").child("state").getValue().toString();
                                String date = dataSnapshot.child("userState").child("date").getValue().toString();
                                String time = dataSnapshot.child("userState").child("time").getValue().toString();
                                if (state.equals("online")) {
                                    holder.imOnline.setVisibility(View.VISIBLE);
                                    Activity activity = getActivity();
                                    if(activity != null && isAdded()) {
                                        holder.userStatu.setText(getResources().getString(R.string.OnlineNow));
                                    }
                                } else if (state.equals("offline")) {
                                    holder.imOnline.setVisibility(View.INVISIBLE);
                                    Activity activity = getActivity();
                                    if(activity != null && isAdded()) {
                                        holder.userStatu.setText(getResources().getString(R.string.LastSeen) + " : " + time + ", " + date);
                                    }
                                }
                            } else {
                                holder.imOnline.setVisibility(View.INVISIBLE);
                             }
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getContext(), ChatActivity.class);
                                    intent.putExtra("uid", uID);
                                    intent.putExtra("name", name);
                                    intent.putExtra("image", image);
                                    startActivity(intent);
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            //--------------------------------------------------

            @NonNull
            @Override
            public ChatsFragment.ChatsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.user_friends_row, viewGroup, false);
                return new ChatsFragment.ChatsHolder(view);
            }
        };
        adapter.onDataChanged();
        adapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class ChatsHolder extends RecyclerView.ViewHolder {
        TextView userName, userStatu;
        CircleImageView imageProfile, imOnline;

        public ChatsHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name_row);
            userStatu = itemView.findViewById(R.id.user_status_row);
            imageProfile = itemView.findViewById(R.id.profile_image_row);
            imOnline = itemView.findViewById(R.id.onlineUser_row);

        }
    }
}
