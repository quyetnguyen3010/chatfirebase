package com.triet.quyetchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    // Android Layout

    private CircleImageView mDisplayImage;
    private TextView mName;
    private TextView mStatus;

    private Toolbar mToolbar;

    private Button btn_setImage, btn_setStatus;


    //const final
    private static final int GALLERY_PICK = 1;

    // Storage Firebase
    StorageReference mImageStorage;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mToolbar = (Toolbar) findViewById(R.id.setting_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Setting User");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDisplayImage = (CircleImageView) findViewById(R.id.ccimage_setting);
        mName = (TextView) findViewById(R.id.txt_settingName);
        mStatus = (TextView) findViewById(R.id.txt_settingstatus);
        btn_setImage = (Button) findViewById(R.id.btn_settingchannelImage);
        btn_setStatus = (Button) findViewById(R.id.btn_settingchannelStatus);

        mImageStorage = FirebaseStorage.getInstance().getReference();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = mCurrentUser.getUid();
        mUserDatabase =FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mUserDatabase.keepSynced(true);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                final String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                final String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

              mName.setText(name);
              mStatus.setText(status);

              if(!image.equals("Default")){
                  Picasso.with(SettingsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.girl1).into(mDisplayImage, new Callback() {
                      @Override
                      public void onSuccess() {

                      }

                      @Override
                      public void onError() {
                          Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.girl1).into(mDisplayImage);
                      }
                  });
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // set status
            btn_setStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String status_value = mStatus.getText().toString();

                    Intent statusIntent = new Intent(SettingsActivity.this, StatusActivity.class);
                    statusIntent.putExtra("Status_value", status_value);
                    startActivity(statusIntent);
                }
            });

        // set image
        btn_setImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), GALLERY_PICK);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1, 1)
                    .setMinCropResultSize(500, 500)
                    .start(this);

        }
        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == RESULT_OK){

                mProgressDialog = new ProgressDialog(SettingsActivity.this);
                mProgressDialog.setTitle("Uploading Image...");
                mProgressDialog.setMessage("Please wait....");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();

                Uri resultUri = result.getUri();
                String current_user_id = mCurrentUser.getUid();


              StorageReference filepath = mImageStorage.child("profile_image").child(current_user_id + ".jpg");
              filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                  @Override
                  public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                      if(task.isSuccessful()){

                          String download_url = task.getResult().getDownloadUrl().toString();
                          mUserDatabase.child("image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task) {

                                  if(task.isSuccessful()){
                                      mProgressDialog.dismiss();
                                      Toast.makeText(SettingsActivity.this, "Success Uploading", Toast.LENGTH_SHORT).show();


                                  }

                              }
                          });


                      }else{
                          Toast.makeText(SettingsActivity.this, "Error in uploading", Toast.LENGTH_SHORT).show();
                     mProgressDialog.dismiss();
                      }
                  }
              });

            }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){

               Exception error = result.getError();

            }
        }

    }

    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++) {
            tempChar = (char) (generator.nextInt(97) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
