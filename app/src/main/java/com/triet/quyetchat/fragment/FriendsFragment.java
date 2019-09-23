package com.triet.quyetchat.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.triet.quyetchat.Friends;
import com.triet.quyetchat.R;
import com.triet.quyetchat.adapter.FriendsAdapter;
import com.triet.quyetchat.allusers.Users;

import java.util.ArrayList;
import java.util.List;


public class FriendsFragment extends Fragment {
    RecyclerView mRecyclerView;
    private FriendsAdapter mFriendsAdapter;
    private List<Friends> mFriends;
    String mCurrent_user_id;


    public FriendsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);

        mRecyclerView = view.findViewById(R.id.friends_fragment);
        mFriends = new ArrayList<>();
        mCurrent_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mFriendsAdapter = new FriendsAdapter(getContext(), mFriends, true);
        mRecyclerView.setAdapter(mFriendsAdapter);

        readFriends();
        return view;

    }

    private void readFriends() {

        final DatabaseReference mFriendsreference = FirebaseDatabase.getInstance().getReference("Users");
        mFriendsreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                String mCurrent_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                mFriends.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    final Users mUserID = snapshot.getValue(Users.class);

                    if (!mUserID.getId().equals(mCurrent_user_id)) {


                        FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id).child(mUserID.getId()).child("date").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot1) {
                                if (dataSnapshot1.exists()) {
                                    String date = dataSnapshot1.getValue().toString();
                                    Friends mFriend = new Friends();
                                    mFriend.setDate(date);
                                    mFriend.setId(mUserID.getId());
                                    mFriend.setImage(mUserID.getImage());
                                    mFriend.setName(mUserID.getName());

                                    mFriends.add(mFriend);
                                    mFriendsAdapter.notifyDataSetChanged();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}