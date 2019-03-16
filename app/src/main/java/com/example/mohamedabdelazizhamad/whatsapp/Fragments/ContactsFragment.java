package com.example.mohamedabdelazizhamad.whatsapp.Fragments;


import android.content.Intent;
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
public class ContactsFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private DatabaseReference contactsRef, userRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    public ContactsFragment() {
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

        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");

        mRecyclerView = v.findViewById(R.id.Contacts_recycler_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return v;

    }
    //----------------------------------------------------------------------------------------------

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(contactsRef, Contacts.class)
                .build();
        FirebaseRecyclerAdapter<Contacts, ContactsHolder> adapter = new FirebaseRecyclerAdapter<Contacts, ContactsHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsHolder holder, int position, @NonNull final Contacts model) {
                final String uID = getRef(position).getKey();
                userRef.child(uID).addValueEventListener(new ValueEventListener() {
                    private String image = "default_image";

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.child("userState").hasChild("state")) {
                                String state = dataSnapshot.child("userState").child("state").getValue().toString();
                                if (state.equals("online")) {
                                    holder.imOnline.setVisibility(View.VISIBLE);
                                } else if (state.equals("offline")) {
                                    holder.imOnline.setVisibility(View.INVISIBLE);

                                }
                            } else {
                                holder.imOnline.setVisibility(View.INVISIBLE);
                                holder.userStatu.setText(getActivity().getApplicationContext().getResources().getString(R.string.offLine));
                            }
                            if (dataSnapshot.hasChild("image")) {
                                image = dataSnapshot.child("image").getValue().toString();
                                Picasso.get().load(image).placeholder(R.drawable.profile_image).into(holder.imageProfile);
                            }
                            final String name = dataSnapshot.child("name").getValue().toString();
                            String status = dataSnapshot.child("status").getValue().toString();

                            holder.userName.setText(name);
                            holder.userStatu.setText(status);
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getContext(), ChatActivity.class);
                                    intent.putExtra("uid", uID);
                                    intent.putExtra("name", name);
                                    intent.putExtra("image", image);
                                    Log.i("OOOO:", image);
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

            @NonNull
            @Override
            public ContactsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.user_friends_row, viewGroup, false);
                return new ContactsHolder(view);
            }
        };
        adapter.onDataChanged();
        adapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }
    //----------------------------------------------------------------------------------------------

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(contactsRef, Contacts.class)
                .build();
        FirebaseRecyclerAdapter<Contacts, ContactsHolder> adapter = new FirebaseRecyclerAdapter<Contacts, ContactsHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsHolder holder, int position, @NonNull final Contacts model) {
                final String uID = getRef(position).getKey();
                userRef.child(uID).addValueEventListener(new ValueEventListener() {
                    private String image = "default_image";

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("userState").hasChild("state")) {
                            String state = dataSnapshot.child("userState").child("state").getValue().toString();
                            if (state.equals("online")) {
                                holder.imOnline.setVisibility(View.VISIBLE);
                            } else if (state.equals("offline")) {
                                holder.imOnline.setVisibility(View.INVISIBLE);

                            }
                        } else {
                            holder.imOnline.setVisibility(View.INVISIBLE);
                            holder.userStatu.setText(getActivity().getApplicationContext().getResources().getString(R.string.offLine));
                        }
                        if (dataSnapshot.hasChild("image")) {
                            image = dataSnapshot.child("image").getValue().toString();
                            Picasso.get().load(image).placeholder(R.drawable.profile_image).into(holder.imageProfile);
                        }
                        final String name = dataSnapshot.child("name").getValue().toString();
                        String status = dataSnapshot.child("status").getValue().toString();

                        holder.userName.setText(name);
                        holder.userStatu.setText(status);
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), ChatActivity.class);
                                intent.putExtra("uid", uID);
                                intent.putExtra("name", name);
                                intent.putExtra("image", image);
                                Log.i("OOOO:", image);
                                startActivity(intent);
                            }
                        });


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public ContactsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(getContext()).inflate(R.layout.user_friends_row, viewGroup, false);
                return new ContactsHolder(view);
            }
        };
        adapter.onDataChanged();
        adapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    //----------------------------------------------------------------------------------------------

    public static class ContactsHolder extends RecyclerView.ViewHolder {
        TextView userName, userStatu;
        CircleImageView imageProfile, imOnline;

        public ContactsHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name_row);
            userStatu = itemView.findViewById(R.id.user_status_row);
            imageProfile = itemView.findViewById(R.id.profile_image_row);
            imOnline = itemView.findViewById(R.id.onlineUser_row);


        }
    }
}
