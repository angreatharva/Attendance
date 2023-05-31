package com.example.attendance;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class UploadProfileActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private ImageView imageViewUploadPic;
    private FirebaseAuth authProfile;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private static final int PICK_IMAGE_REQUEST=1;
    private Uri uriImage;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_profile);


        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Button buttonUploadPicChoose=findViewById(R.id.upload_pic_choose_button);
        Button buttonUploadPic=findViewById(R.id.upload_pic_buttton);
        progressBar=findViewById(R.id.progressBar);
        imageViewUploadPic=findViewById(R.id.iv_profile_dp);
        authProfile=FirebaseAuth.getInstance();
        firebaseUser=authProfile.getCurrentUser();
        storageReference= FirebaseStorage.getInstance().getReference("DisplayPics");
        Uri uri=firebaseUser.getPhotoUrl();

        //Set User's current DP in imageview(If uploaded already, picasso imageViewer)
        //Regular URI's
        Picasso.with(UploadProfileActivity.this).load(uri).into(imageViewUploadPic);
        buttonUploadPicChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();

            }
        });
        //upload pic
        buttonUploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(view.VISIBLE);
                UploadPic();
            }
        });
    }
    private void openFileChooser(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            uriImage=data.getData();
            imageViewUploadPic.setImageURI(uriImage);

        }

    }

    private void UploadPic(){
        if(uriImage!=null){
            //set the image with uid of the currently logged User
            StorageReference filereference=storageReference.child(authProfile.getCurrentUser().getUid() + "."
                    + getFileExtension(uriImage));
            //upload image to storage
            filereference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filereference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri=uri;
                            firebaseUser=authProfile.getCurrentUser();
                            //finally set the display image of the user after upload
                            UserProfileChangeRequest profileUpdates=new UserProfileChangeRequest.Builder().setPhotoUri(downloadUri).build();
                            firebaseUser.updateProfile(profileUpdates);
                        }
                    });
                        progressBar.setVisibility(View.GONE);
                    Toast.makeText(UploadProfileActivity.this, "Upload Successfull", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(UploadProfileActivity.this,Attendance.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
    //obtain File Extension
    private String getFileExtension(Uri uri){
        ContentResolver cR=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }



}