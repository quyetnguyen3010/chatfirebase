package com.triet.quyetchat.allusers;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.triet.quyetchat.R;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mProfileImage;
    TextView mProfileName, mProfileStatus, mProfileFriendsCount;
    Button btn_sendFriend, btn_declin;

    private DatabaseReference mUserDatabase;
    private DatabaseReference mFriendReqDatabase;

    private DatabaseReference mFriendDatabase;
    private DatabaseReference mNotificationDatabase;
    private FirebaseUser mCurrent_user;

    private DatabaseReference mRootRef;


    FirebaseUser fuser;
    Toolbar mToolbar;
    private List<Users> mUsers;
    ProgressDialog mProgressDialog;
    private String mCurrent_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mToolbar = (Toolbar) findViewById(R.id.proUsers_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Profile User");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        final String user_id = getIntent().getStringExtra("id");
        mUserDatabase = FirebaseDatabase.getInstance().getReference("Users").child(user_id);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();


        mProfileImage = findViewById(R.id.profile_image);
        mProfileName = findViewById(R.id.profile_deisplayName);
        mProfileStatus = findViewById(R.id.profile_userstatus);
        mProfileFriendsCount = findViewById(R.id.profile_totlefren);
        btn_sendFriend = findViewById(R.id.btn_sendfriend);
        btn_declin = findViewById(R.id.btn_Declinefriend);

        mCurrent_state = "not_friends";

        btn_declin.setVisibility(View.INVISIBLE);
        btn_declin.setEnabled(false);

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading Users Data");
        mProgressDialog.setMessage("Please wait");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        mRootRef = FirebaseDatabase.getInstance().getReference();

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                mProfileName.setText(display_name);
                mProfileStatus.setText(status);
                Picasso.with(ProfileActivity.this).load(image).placeholder(R.drawable.girl1).into(mProfileImage);

                Users user = dataSnapshot.getValue(Users.class);
                mProfileName.setText(user.getName());
                mProfileStatus.setText(user.getStatus());



                //===========FRIENDS LIST / REQUEST FEATURE======
                mFriendReqDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(user_id)) {
                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();
                            if (req_type.equals("received")) {

                                mCurrent_state = "req_received";
                                btn_sendFriend.setText("Accept Friend Request");

                                btn_declin.setVisibility(View.VISIBLE);
                                btn_declin.setEnabled(true);

                            } else if (req_type.equals("sent")) {
                                mCurrent_state = "req_sent";
                                btn_sendFriend.setText("Cancel Friend Request");
//
                                btn_declin.setVisibility(View.INVISIBLE);
                                btn_declin.setEnabled(false);

                            }
                            mProgressDialog.dismiss();


                        } else {
                            mFriendDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(user_id)) {
                                        mCurrent_state = "friends";
                                        btn_sendFriend.setText("Unfriend this Person");

                                        btn_declin.setVisibility(View.INVISIBLE);
                                        btn_declin.setEnabled(false);

                                    }
                                    mProgressDialog.dismiss();

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    mProgressDialog.dismiss();
                                }
                            });

                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btn_sendFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_sendFriend.setEnabled(false);

                //===================NOT FRIENDS STATE============================

                if (mCurrent_state.equals("not_friends")) {

                    DatabaseReference newNotificationref = mRootRef.child("notifications").child(user_id).push();
                    String newNotificationId = newNotificationref.getKey();

                    HashMap<String, String> notificationData = new HashMap<>();
                    notificationData.put("from", mCurrent_user.getUid());
                    notificationData.put("type", "request");


                    Map requestMap = new HashMap();
                    requestMap.put("Friend_req/" + mCurrent_user.getUid() + "/" + user_id + "/request_type", "sent");
                    requestMap.put("Friend_req/" + user_id + "/" + mCurrent_user.getUid() + "/request_type", "received");
                    requestMap.put("notifications/" + user_id + "/" + newNotificationId, notificationData);

                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Toast.makeText(ProfileActivity.this, "There was some error in sending request", Toast.LENGTH_SHORT);

                            }
                            btn_sendFriend.setEnabled(true);
                            mCurrent_state = "req_sent";
                            btn_sendFriend.setText("Cancel Friend Request");

                        }
                    });

                }

                //===============CANNEL REQUEST STATE========================

                if (mCurrent_state.equals("req_sent")) {
                    mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendReqDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    btn_sendFriend.setEnabled(true);
                                    mCurrent_state = "not_friends";
                                    btn_sendFriend.setText("Send Friend Request");

                                    btn_declin.setVisibility(View.INVISIBLE);
                                    btn_declin.setEnabled(false);


                                }
                            });
                        }
                    });

                }
                //============REQ RECEIVED STATE==========

                if (mCurrent_state.equals("req_received")) {
                    final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    Map friendsmap = new HashMap<>();
                    friendsmap.put("Friends/" + mCurrent_user.getUid() + "/" + user_id + "/date", currentDate);
                    friendsmap.put("Friends/" + user_id + "/" + mCurrent_user.getUid() + "/date", currentDate);

                    friendsmap.put("Friend_req/" + mCurrent_user.getUid() + "/" + user_id, null);
                    friendsmap.put("Friend_req/" + user_id + "/" + mCurrent_user.getUid(), null);

                    mRootRef.updateChildren(friendsmap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError == null) {
                                btn_sendFriend.setEnabled(true);

                                mCurrent_state = "friends";
                                btn_sendFriend.setText("Unfriend this Person");

                                btn_declin.setVisibility(View.INVISIBLE);
                                btn_declin.setEnabled(false);
                            } else {
                                String error = databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                            }


                        }
                    });

                }
                //===========UNFRIENDS=====
                if (mCurrent_state.equals("friends")) {

                    Map unfriendMap = new HashMap();
                    unfriendMap.put("Friends/" + mCurrent_user.getUid() + "/" + user_id, null);
                    unfriendMap.put("Friends/" + user_id + "/" + mCurrent_user.getUid(), null);
                    mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError == null) {
                                mCurrent_state = "not_friends";
                                btn_sendFriend.setText("Send Friend Request");

                                btn_declin.setVisibility(View.INVISIBLE);
                                btn_declin.setEnabled(false);
                            } else {
                                String error = databaseError.getMessage();
                                Toast.makeText(ProfileActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                            btn_sendFriend.setEnabled(true);

                        }
                    });

                }

            }
        });
    }

}
