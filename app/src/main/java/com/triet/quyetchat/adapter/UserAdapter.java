package com.triet.quyetchat.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;
import com.triet.quyetchat.R;
import com.triet.quyetchat.allusers.ProfileActivity;
import com.triet.quyetchat.allusers.Users;
import com.triet.quyetchat.allusers.UsersActivity;
import com.triet.quyetchat.main.MainActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<Users> mUsers;
    private FirebaseAuth mAuth;

    public UserAdapter(Context context, List<Users> users) {
        this.mContext = context;
        this.mUsers = users;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.users_single_layout, parent, false);
        return new UserAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        final Users user = mUsers.get(position);
        holder.singname.setText(user.getName());
        holder.single_status.setText(user.getStatus());

        final String user_id = user.getId();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(mContext, ProfileActivity.class);
                profileIntent.putExtra("id", user_id);
                mContext.startActivity(profileIntent);


            }
        });


        if (user.getImage().equals("Default")) {
            holder.image_sing.setImageResource(R.drawable.girl1);
        } else {
            Picasso.with(mContext).load(user.getImage()).into(holder.image_sing);

        }
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView singname, single_status;
        CircleImageView image_sing;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            singname = itemView.findViewById(R.id.sing_displayName);
            single_status = itemView.findViewById(R.id.single_status);
            image_sing = itemView.findViewById(R.id.image_sing);
        }
    }

}


