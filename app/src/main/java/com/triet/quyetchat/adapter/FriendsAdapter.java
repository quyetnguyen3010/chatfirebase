package com.triet.quyetchat.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.triet.quyetchat.Friends;

import com.triet.quyetchat.R;
import com.triet.quyetchat.ChatsActivity;
import com.triet.quyetchat.allusers.ProfileActivity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    private Context mContext;
    private List<Friends> mFriendsList;
    private boolean ischat;


    public FriendsAdapter(Context context, List<Friends> friendsList, boolean ischat) {
        this.mContext = context;
        this.mFriendsList = friendsList;
        this.ischat = ischat;
    }

    @NonNull
    @Override
    public FriendsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.users_single_layout, parent, false);
        return new FriendsAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull final FriendsAdapter.ViewHolder holder, final int position) {

        final Friends friends = mFriendsList.get(position);
        final String user_id = friends.getId();

        holder.singname.setText(friends.getName());
        holder.single_status.setText(friends.getDate());

        if (friends.getImage().equals("Default")) {
            holder.image_sing.setImageResource(R.mipmap.ic_launcher);
        } else {
            Picasso.with(mContext).load(friends.getImage()).into(holder.image_sing);

        }

        if (ischat) {
            if (friends.isOnline()) {
                holder.img_on.setVisibility(View.VISIBLE);
                holder.img_off.setVisibility(View.GONE);
            } else {
                holder.img_on.setVisibility(View.GONE);
                holder.img_off.setVisibility(View.VISIBLE);
            }
        } else {
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.GONE);
        }

        //onclick hodel

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence option[] = new CharSequence[]{"Open Profile", "Send message"};

                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

                builder.setTitle("Select Options");
                builder.setItems(option, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        if (i == 0) {
                            Intent profileIntent = new Intent(mContext, ProfileActivity.class);
                            profileIntent.putExtra("id", user_id);
                            mContext.startActivity(profileIntent);
                        }
                        if (i == 1) {

                            Intent chatIntent = new Intent(mContext, ChatsActivity.class);
                            chatIntent.putExtra("id", user_id);
                            chatIntent.putExtra("user_name",friends.getName());
                            mContext.startActivity(chatIntent);

                        }

                    }
                });

                builder.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mFriendsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView singname;
        TextView single_status;
        CircleImageView image_sing;
        ImageView img_on;
        ImageView img_off;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            singname = itemView.findViewById(R.id.sing_displayName);
            single_status = itemView.findViewById(R.id.single_status);
            image_sing = itemView.findViewById(R.id.image_sing);
            img_on = itemView.findViewById(R.id.image_online);
            img_off = itemView.findViewById(R.id.image_offline);


        }
    }


}
