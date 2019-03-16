package com.example.mohamedabdelazizhamad.whatsapp.Fragments;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mohamedabdelazizhamad.whatsapp.Model.Contacts;
import com.example.mohamedabdelazizhamad.whatsapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private DatabaseReference requestsRef, usersRef, contactsRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private String type;
    String Uid;
    //----------------------------------------------------------------------------------------------

    public RequestsFragment() {
        // Required empty public constructor
    }

    //----------------------------------------------------------------------------------------------

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_requests, container, false);

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        requestsRef = FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        contactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts");

        mRecyclerView = v.findViewById(R.id.Requests_recycler_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return v;
    }
    //----------------------------------------------------------------------------------------------

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setmRecyclerView();
    }
    //----------------------------------------------------------------------------------------------

    @Override
    public void onStart() {
        super.onStart();
        setmRecyclerView();
    }

    //----------------------------------------------------------------------------------------------
    private void setmRecyclerView() {
        FirebaseRecyclerOptions<Contacts> options = new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(requestsRef.child(currentUserID), Contacts.class)
                .build();
        //----------------------------------------------------------------------------------------------

        FirebaseRecyclerAdapter<Contacts, RequestHolder> adapter = new FirebaseRecyclerAdapter<Contacts, RequestHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final RequestHolder holder, int position, @NonNull Contacts model) {
                final String uid = getRef(position).getKey();
                Uid = uid;
                DatabaseReference typeRef = getRef(position).child("Request Type").getRef();

                typeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            type = dataSnapshot.getValue().toString();
                            if (type.equals("received")) {
                                Log.i("TAG1", type);
                                Log.i("TAG2", uid);
                                usersRef.child(uid).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild("image")) {
                                            final String image = dataSnapshot.child("image").getValue().toString();
                                            Picasso.get().load(image).placeholder(R.drawable.profile_image).into(holder.imageProfile);
                                        }
                                        final String name = dataSnapshot.child("name").getValue().toString();
                                        final String status = dataSnapshot.child("status").getValue().toString();
                                        holder.userName.setText(name);
                                        holder.userStatu.setText(status);
                                        holder.itemView.findViewById(R.id.requestConfirm).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                contactsRef.child(currentUserID).child(uid)
                                                        .child("Contact").setValue("saved")
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    contactsRef.child(uid).child(currentUserID)
                                                                            .child("Contact").setValue("saved")
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        requestsRef.child(currentUserID).child(uid)
                                                                                                .removeValue()
                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                        if (task.isSuccessful()) {
                                                                                                            requestsRef.child(uid).child(currentUserID)
                                                                                                                    .removeValue()
                                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                        @Override
                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                                            if (task.isSuccessful()) {

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
                                        });
                                        //----------------------------------------------------------------------------------------------

                                        holder.itemView.findViewById(R.id.requestCancel).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                requestsRef.child(currentUserID).child(uid)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    requestsRef.child(Uid).child(currentUserID)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        Toast.makeText(getContext(), "deleted", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            } else if (type.equals("sent")) {
                                Log.i("TAG1", type);
                                Log.i("TAG2", uid);
                                holder.confirmButton.setVisibility(View.INVISIBLE);
                                holder.cancelButton.setText(getResources().getString(R.string.cancelRequest));
                                usersRef.child(uid).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild("image")) {
                                            final String image = dataSnapshot.child("image").getValue().toString();
                                            Picasso.get().load(image).placeholder(R.drawable.profile_image).into(holder.imageProfile);
                                        }
                                        final String name = dataSnapshot.child("name").getValue().toString();
                                        holder.userName.setText(name);
                                        holder.userStatu.setText(getResources().getString(R.string.Youhavesentarequestto) + name);
                                        holder.itemView.findViewById(R.id.requestCancel).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                requestsRef.child(currentUserID).child(uid)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    requestsRef.child(uid).child(currentUserID)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @NonNull
            @Override
            public RequestHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                ClickListener listener = new ClickListener() {
                    @Override
                    public void onPositionClicked(int position) {

                    }
                };
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_requests_row, viewGroup, false);
                return new RequestHolder(view, listener);
            }
        };

        mRecyclerView.setAdapter(adapter);
        adapter.startListening();
    }


    //----------------------------------------------------------------------------------------------

    public class RequestHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView userName, userStatu;
        CircleImageView imageProfile;
        Button confirmButton, cancelButton;
        private WeakReference<ClickListener> listenerRef;

        //----------------------------------

        public RequestHolder(@NonNull View itemView, ClickListener listener) {
            super(itemView);
            listenerRef = new WeakReference<>(listener);
            userName = itemView.findViewById(R.id.request_user_name);
            userStatu = itemView.findViewById(R.id.request_user_status);
            imageProfile = itemView.findViewById(R.id.request_profile_image);
            confirmButton = itemView.findViewById(R.id.requestConfirm);
            cancelButton = itemView.findViewById(R.id.requestCancel);
            confirmButton.setOnClickListener(this);
            cancelButton.setOnClickListener(this);
        }
        //------------------------------------

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.requestConfirm) {
                Log.i("RR", Uid + type);
                contactsRef.child(currentUserID).child(Uid)
                        .child("Contact").setValue("saved")
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    contactsRef.child(Uid).child(currentUserID)
                                            .child("Contact").setValue("saved")
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        requestsRef.child(currentUserID).child(Uid)
                                                                .removeValue()
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            requestsRef.child(Uid).child(currentUserID)
                                                                                    .removeValue()
                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if (task.isSuccessful()) {

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
            } else if (v.getId() == R.id.requestCancel) {
                Log.i("RR", Uid + type);
                requestsRef.child(currentUserID).child(Uid)
                        .removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    requestsRef.child(Uid).child(currentUserID)
                                            .removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                    }
                                                }
                                            });
                                }
                            }
                        });
            }

            listenerRef.get().onPositionClicked(getAdapterPosition());

        }
    }

    public interface ClickListener {

        void onPositionClicked(int position);

    }
}
