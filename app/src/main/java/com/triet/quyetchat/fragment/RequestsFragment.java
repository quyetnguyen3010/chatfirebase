package com.triet.quyetchat.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.triet.quyetchat.R;
import com.triet.quyetchat.adapter.UserAdapter;
import com.triet.quyetchat.allusers.Users;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private UserAdapter mUserAdapter;
    private List<Users> mUsers;
    private EditText search_view;

    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
          View view = inflater.inflate(R.layout.fragment_requests, container, false);

        mRecyclerView = view.findViewById(R.id.rv_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsers = new ArrayList<>();
        readUser();

        search_view = view.findViewById(R.id.edt_search);
        search_view.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUsers(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        return view;

    }

    private void searchUsers(String s) {

        final FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = FirebaseDatabase.getInstance().getReference("Users").orderByChild("name")
                .startAt(s)
                .endAt(s + "\uf8ff");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mUsers.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Users user = snapshot.getValue(Users.class);


                    assert user != null;
                    assert fuser != null;

                    if(!user.getId().equals(fuser.getUid())){
                        mUsers.add(user);
                    }
                }

                mUserAdapter = new UserAdapter(getContext(), mUsers);
                mRecyclerView.setAdapter(mUserAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    private void readUser() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUsers.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Users user = snapshot.getValue(Users.class);
                    assert user != null;
                    assert firebaseUser != null;
                        mUsers.add(user);

                }
                mUserAdapter = new UserAdapter(getContext(), mUsers);
                mRecyclerView.setAdapter(mUserAdapter);
                mUserAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
