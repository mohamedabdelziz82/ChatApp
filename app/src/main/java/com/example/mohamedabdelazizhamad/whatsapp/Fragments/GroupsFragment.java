package com.example.mohamedabdelazizhamad.whatsapp.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.mohamedabdelazizhamad.whatsapp.Activities.GroupChatActivity;
import com.example.mohamedabdelazizhamad.whatsapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupsFragment extends Fragment {

    private View groupView;
    private ArrayList<String> arrayList = new ArrayList();
    private ArrayAdapter<String> arrayAdapter;
    private ListView listView;
    private DatabaseReference GroupRef;
    public GroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        groupView = inflater.inflate(R.layout.fragment_groups, container, false);
        GroupRef=FirebaseDatabase.getInstance().getReference().child("Group");

        InitializationFields();
        GetAndDisplayGroups();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String currentGroupName=parent.getItemAtPosition(position).toString();
                Intent chatGroupIntent=new Intent(getContext(),GroupChatActivity.class);
                chatGroupIntent.putExtra("GroupName",currentGroupName);
                startActivity(chatGroupIntent);
            }
        });
        return groupView;
    }


    private void InitializationFields() {
        listView = groupView.findViewById(R.id.listView);
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(arrayAdapter);
    }
    private void GetAndDisplayGroups()
    {
        GroupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set <String> set=new HashSet<>();
                Iterator iterator= dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()){
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }
                arrayList.clear();
                arrayList.addAll(set);
                arrayAdapter.notifyDataSetChanged();
                Log.i("out :",set.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}
