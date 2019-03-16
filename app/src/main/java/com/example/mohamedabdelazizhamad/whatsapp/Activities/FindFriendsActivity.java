package com.example.mohamedabdelazizhamad.whatsapp.Activities;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mohamedabdelazizhamad.whatsapp.Model.Contacts;
import com.example.mohamedabdelazizhamad.whatsapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FindFriendsActivity extends AppCompatActivity {
    private Toolbar myToolbar;
    private RecyclerView recyclerView;
    private DatabaseReference userRef;
    private EditText search;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friends);

        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        recyclerView = findViewById(R.id.findFriends_recycler_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myToolbar = findViewById(R.id.findFriends_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.find_friends));
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

            LayoutInflater layoutInflater= (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View actionBarView = layoutInflater.inflate(R.layout.custom_search_bar, null);
            actionBar.setCustomView(actionBarView);


            search=findViewById(R.id.findFriends_search);
            search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                SearchUsers(s.toString());
                 }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
     }

    private void SearchUsers(String s) {
       Query query=userRef.orderByChild("name").startAt(s).endAt(s+"\uf8ff");
        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(query, Contacts.class)
                        .build();

        FirebaseRecyclerAdapter<Contacts, FindFriendsHolder> adapter= new FirebaseRecyclerAdapter<Contacts, FindFriendsHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendsHolder holder, final int position, @NonNull Contacts model) {
                holder.userName.setText(model.getName());
                holder.userStatu.setText(model.getStatus());
                Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.imageProfile);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = getRef(position).getKey();
                        Intent intent = new Intent(FindFriendsActivity.this, ProfileActivity.class);
                        intent.putExtra("id", id);
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public FindFriendsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_friends_row, viewGroup, false);
                return new FindFriendsHolder(view);
            }
        };
        recyclerView.clearOnChildAttachStateChangeListeners();
        recyclerView.clearOnScrollListeners();
        adapter.notifyDataSetChanged();
        adapter.onDataChanged();
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(userRef, Contacts.class)
                        .build();

        FirebaseRecyclerAdapter<Contacts, FindFriendsHolder> adapter = new FirebaseRecyclerAdapter<Contacts, FindFriendsHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FindFriendsHolder holder, final int position, @NonNull Contacts model) {
               if (search.getText().toString().equals("")){
                holder.userName.setText(model.getName());
                holder.userStatu.setText(model.getStatus());
                Picasso.get().load(model.getImage()).placeholder(R.drawable.profile_image).into(holder.imageProfile);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = getRef(position).getKey();
                        Intent intent = new Intent(FindFriendsActivity.this, ProfileActivity.class);
                        intent.putExtra("id", id);
                        startActivity(intent);
                    }
                });
               }
            }

            @NonNull
            @Override
            public FindFriendsHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.user_friends_row, viewGroup, false);
                return new FindFriendsHolder(view);
            }
        };
        adapter.notifyDataSetChanged();
        adapter.onDataChanged();
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FindFriendsHolder extends RecyclerView.ViewHolder {
        TextView userName, userStatu;
        CircleImageView imageProfile;

        public FindFriendsHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.user_name_row);
            userStatu = itemView.findViewById(R.id.user_status_row);
            imageProfile = itemView.findViewById(R.id.profile_image_row);
        }
    }
}
