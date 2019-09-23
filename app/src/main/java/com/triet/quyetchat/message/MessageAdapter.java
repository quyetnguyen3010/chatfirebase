package com.triet.quyetchat.message;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.triet.quyetchat.Friends;
import com.triet.quyetchat.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<message> mMessagesList;
    private DatabaseReference mUserDatabase;

    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;


    public MessageAdapter(List<message> messageList) {
        this.mMessagesList = messageList;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == MSG_TYPE_RIGHT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_layout, parent, false);
            return new MessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_single_right, parent, false);
            return new MessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {

        message c = mMessagesList.get(position);
        String from_user = c.getFrom();
        String message_type = c.getType();

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();

                holder.displayName.setText(name);

                Picasso.with(holder.mCircleImageView.getContext()).load(image)
                        .placeholder(R.drawable.girl1).into(holder.mCircleImageView);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        if(message_type.equals("text")) {

            holder.messageText.setText(c.getMessage());
            holder.messageImage.setVisibility(View.INVISIBLE);


        } else {

            holder.messageText.setVisibility(View.INVISIBLE);
            Picasso.with(holder.mCircleImageView.getContext()).load(c.getMessage())
                    .placeholder(R.drawable.girl1).into(holder.messageImage);

        }
    }

    @Override
    public int getItemCount() {
        return mMessagesList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        private TextView messageText;
        private CircleImageView mCircleImageView;
        public TextView displayName;
        public ImageView messageImage;

        public MessageViewHolder(@NonNull View view) {
            super(view);

            messageText = (TextView) view.findViewById(R.id.message_message);
            mCircleImageView = (CircleImageView) view.findViewById(R.id.chatimage);
            displayName = (TextView) view.findViewById(R.id.message_displayname);
            messageImage = (ImageView)view.findViewById(R.id.message_image_layout);

        }
    }
    @Override
    public int getItemViewType(int position) {

        FirebaseUser fuser = FirebaseAuth.getInstance().getCurrentUser();
        if (mMessagesList.get(position).getFrom().equals(fuser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }



}
