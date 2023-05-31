package com.example.attendance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Printer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class Attendance extends AppCompatActivity {
    private TextView textViewFullName,textViewRollNo,textViewClassDiv;
    private TextView textViewFaculty1,textViewSubject1,textViewTotal1,textViewAbsent1,textViewPresent1,
                    textViewFaculty2,textViewSubject2,textViewTotal2,textViewAbsent2,textViewPresent2,
                    textViewFaculty3,textViewSubject3,textViewTotal3,textViewAbsent3,textViewPresent3,
                     textViewFaculty4,textViewSubject4,textViewTotal4,textViewAbsent4,textViewPresent4,
                    textViewFaculty5,textViewSubject5,textViewTotal5,textViewAbsent5,textViewPresent5,
                    textViewFaculty6,textViewSubject6,textViewTotal6,textViewAbsent6,textViewPresent6;
    private TextView progress1,progress2,progress3,progress4,progress5,progress6;
    private ImageView imageView;
    private String fullname,classdiv,stdid;
    private String fac1,sub1,abs1,pre1,tot1,
            fac2,sub2,abs2,pre2,tot2,
            fac3,sub3,abs3,pre3,tot3,
            fac4,sub4,abs4,pre4,tot4,
            fac5,sub5,abs5,pre5,tot5,
            fac6,sub6,abs6,pre6,tot6;
    private float percentage1;
    private float percentage2;
    private float percentage3;
    private float percentage4;
    private float percentage5;
    private float percentage6;
    private float avg1,avg2,Average;
    public  String rollno;
    public Float Total1,Total2,Total3,Total4,Total5,Total6;
    private FirebaseAuth authProfile;
    private Button Logout,Blacklist;
    AlertDialog.Builder builder;
    private SwipeRefreshLayout swipeContainer;
    private ProgressBar progressBar;
    private ImageView imageViewUploadPic;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private static final int PICK_IMAGE_REQUEST=1;
    private Uri uriImage;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        swipeToRefresh();


        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button buttonUploadPicChoose=findViewById(R.id.upload_pic_choose_button);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button buttonUploadPic=findViewById(R.id.upload_pic_buttton);
        progressBar=findViewById(R.id.progressBar);
        imageViewUploadPic=findViewById(R.id.iv_profile_dp);
        authProfile=FirebaseAuth.getInstance();
        firebaseUser=authProfile.getCurrentUser();
        storageReference= FirebaseStorage.getInstance().getReference("DisplayPics");
        Uri uri=firebaseUser.getPhotoUrl();

        //set onclicklistener on Image View to open upload profile activity
        imageView=findViewById(R.id.image);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Attendance.this, UploadProfileActivity.class);
                startActivity(intent);
            }
        });

        Blacklist=findViewById(R.id.Blist);
        builder=new AlertDialog.Builder(this);
        Blacklist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.setTitle("Black List");
                builder.setCancelable(true);
                Log.d("Att", String.valueOf(Average));
                if(Average<=45){
                    builder.setMessage("Sorry..! As your Attendance is "+Average+"%"+" You are in Black List.\nPlease cope-up with your Attendance");
                }
                else{
                    builder.setMessage("Hurray..! As your Attendance is "+Average+"%"+" You are not in Black List");
                }
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .show();
            }
        });


        Logout=findViewById(R.id.Logout);
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(Attendance.this, "Logged Out", Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(Attendance.this,MainActivity.class);
                //Clear stack to prevent user coming back to DashBoard on back button after Logging out
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();//close Attendance page

            }
        });

        textViewFullName=findViewById(R.id.TextName);
        textViewRollNo=findViewById(R.id.TextRollNo);
        textViewClassDiv=findViewById(R.id.TextClassDiv);

        textViewSubject1=findViewById(R.id.sub_1);
        textViewFaculty1=findViewById(R.id.faculty1);
        textViewTotal1=findViewById(R.id.Total_1);
        textViewAbsent1=findViewById(R.id.Absent_1);
        textViewPresent1=findViewById(R.id.Present_1);
        progress1=findViewById(R.id.Overall1);

        textViewSubject2=findViewById(R.id.sub_2);
        textViewFaculty2=findViewById(R.id.faculty2);
        textViewTotal2=findViewById(R.id.Total_2);
        textViewAbsent2=findViewById(R.id.Absent_2);
        textViewPresent2=findViewById(R.id.Present_2);
        progress2=findViewById(R.id.Overall2);

        textViewSubject3=findViewById(R.id.sub_3);
        textViewFaculty3=findViewById(R.id.faculty3);
        textViewTotal3=findViewById(R.id.Total_3);
        textViewAbsent3=findViewById(R.id.Absent_3);
        textViewPresent3=findViewById(R.id.Present_3);
        progress3=findViewById(R.id.Overall3);

        textViewSubject4=findViewById(R.id.sub_4);
        textViewFaculty4=findViewById(R.id.faculty4);
        textViewTotal4=findViewById(R.id.Total_4);
        textViewAbsent4=findViewById(R.id.Absent_4);
        textViewPresent4=findViewById(R.id.Present_4);
        progress4=findViewById(R.id.Overall4);

        textViewSubject5=findViewById(R.id.sub_5);
        textViewFaculty5=findViewById(R.id.faculty5);
        textViewTotal5=findViewById(R.id.Total_5);
        textViewAbsent5=findViewById(R.id.Absent_5);
        textViewPresent5=findViewById(R.id.Present_5);
        progress5=findViewById(R.id.Overall5);

        textViewSubject6=findViewById(R.id.sub_6);
        textViewFaculty6=findViewById(R.id.faculty6);
        textViewTotal6=findViewById(R.id.Total_6);
        textViewAbsent6=findViewById(R.id.Absent_6);
        textViewPresent6=findViewById(R.id.Present_6);
        progress6=findViewById(R.id.Overall6);

        progressBar=findViewById(R.id.progressBar);
        imageView=findViewById(R.id.image);
        Logout=findViewById(R.id.Logout);

        authProfile=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser= authProfile.getCurrentUser();

        if(firebaseUser==null){
            Toast.makeText(Attendance.this, "Something went wrong.! User's details are not Available at the moment", Toast.LENGTH_SHORT).show();

        }
        else{
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }

    }

    private void swipeToRefresh() {
        //look up for swipe container
        swipeContainer=findViewById(R.id.swipeContainer);
        //setup refresh listener
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //code to refresh
                startActivity(getIntent());
                finish();
                overridePendingTransition(0,0);
                swipeContainer.setRefreshing(false);


            }
        });

        //configure
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,android.R.color.holo_green_light,android.R.color.holo_orange_light,android.R.color.holo_red_light);
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
                    Toast.makeText(Attendance.this, "Upload Successfull", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(Attendance.this,Attendance.class);
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

    private void showUserProfile(FirebaseUser firebaseUser) {

        //exctracting user refeference from db for registered user
        String userId = firebaseUser.getUid();
        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
        referenceProfile.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadwriteUserDetails readwriteUserDetails = snapshot.getValue(ReadwriteUserDetails.class);
                if (readwriteUserDetails != null) {
                    fullname = readwriteUserDetails.FullName;
                    rollno = readwriteUserDetails.Sid;
                    classdiv = readwriteUserDetails.ClassDiv;

                    showUserProfile1(rollno);

                    textViewFullName.setText(fullname);
                    textViewClassDiv.setText(classdiv);
                    textViewRollNo.setText(rollno);

                    //set user dp
                    Uri uri = firebaseUser.getPhotoUrl();
                    //imageView
                    Picasso.with(Attendance.this).load(uri).into(imageView);
                } else {
                    Toast.makeText(Attendance.this, "Something went wrong.! ", Toast.LENGTH_SHORT).show();

                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Attendance.this, "Something went wrong.! ", Toast.LENGTH_SHORT).show();

            }
        });
    }
    private void showUserProfile1(String rollno1) {
        DatabaseReference referenceData = FirebaseDatabase.getInstance().getReference("1sZYcxIAK_UWY2HzM72YFUkIGjD3DU2WCJqF31g7kt3Q");
        referenceData.child("Sheet1").addChildEventListener(new ChildEventListener() {


            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Detailsdata readData = snapshot.getValue(Detailsdata.class);
                if (readData != null) {
                    stdid = String.valueOf(readData.Student_ID);
                    fac1 = readData.Faculty_Name1;
                    pre1 = String.valueOf(readData.Present1);
                    abs1 = String.valueOf(readData.Absent1);
                    sub1 = readData.Subject1;

                    fac2 = readData.Faculty_Name2;
                    pre2 = String.valueOf(readData.Present2);
                    abs2 = String.valueOf(readData.Absent2);
                    sub2 = readData.Subject2;

                    fac3 = readData.Faculty_Name3;
                    pre3 = String.valueOf(readData.Present3);
                    abs3 = String.valueOf(readData.Absent3);
                    sub3 = readData.Subject3;

                    fac4 = readData.Faculty_Name4;
                    pre4 = String.valueOf(readData.Present4);
                    abs4 = String.valueOf(readData.Absent4);
                    sub4 = readData.Subject4;

                    fac5 = readData.Faculty_Name5;
                    pre5 = String.valueOf(readData.Present5);
                    abs5 = String.valueOf(readData.Absent5);
                    sub5 = readData.Subject5;

                    fac6 = readData.Faculty_Name6;
                    pre6 = String.valueOf(readData.Present6);
                    abs6 = String.valueOf(readData.Absent6);
                    sub6 = readData.Subject6;

                    avg1=percentage1;
                    avg2=percentage2;
                    Average=((percentage1+percentage2+percentage3+percentage4+percentage5+percentage6)/6);

                    Long a1, p1;
                    a1 = (readData.Absent1);
                    p1 = (readData.Present1);
                    tot1 = String.valueOf((a1 + p1));
                    Total1= Float.valueOf(tot1);
                    percentage1= (p1/Total1)*100;




                    Float a2, p2;
                    a2 = Float.valueOf(readData.Absent2);
                    p2 = Float.valueOf(readData.Present2);
                    tot2 = String.valueOf((a2 + p2));
                    Total2= Float.valueOf(tot2);
                    percentage2= (p2/Total2)*100;

                    Float a3, p3;
                    a3 = Float.valueOf(readData.Absent3);
                    p3 = Float.valueOf(readData.Present3);
                    tot3 = String.valueOf(a3 + p3);
                    Total3= Float.valueOf(tot3);
                    percentage3= (p3/Total3)*100;

                    Float a4, p4;
                    a4 = Float.valueOf(readData.Absent4);
                    p4 = Float.valueOf(readData.Present4);
                    tot4 = String.valueOf(a4 + p4);
                    Total4= Float.valueOf(tot4);
                    percentage4= (p4/Total4)*100;

                    Float a5, p5;
                    a5 = Float.valueOf(readData.Absent5);
                    p5 = Float.valueOf(readData.Present5);
                    tot5 = String.valueOf(a5 + p5);
                    Total5= Float.valueOf(tot5);
                    percentage5= (p5/Total5)*100;

                    Float a6, p6;
                    a6 = Float.valueOf(readData.Absent6);
                    p6 = Float.valueOf(readData.Present6);
                    tot6 = String.valueOf(a6 + p6);
                    Total6= Float.valueOf(tot6);
                    percentage6= (p6/Total6)*100;
                    



                    Integer s1, s2;
                    s1 = Integer.valueOf(stdid);
                    s2 = Integer.valueOf(rollno1);

                    if (s1 == s2) {
                        textViewSubject1.setText(sub1);
                        textViewFaculty1.setText(fac1);
                        textViewAbsent1.setText(abs1);
                        textViewTotal1.setText(tot1);
                        textViewPresent1.setText(pre1);
                        progress1.setText(percentage1+"%");

                        textViewSubject2.setText(sub2);
                        textViewFaculty2.setText(fac2);
                        textViewAbsent2.setText(abs2);
                        textViewTotal2.setText(tot2);
                        textViewPresent2.setText(pre2);
                        progress2.setText(percentage2+"%");

                        textViewSubject3.setText(sub3);
                        textViewFaculty3.setText(fac3);
                        textViewAbsent3.setText(abs3);
                        textViewTotal3.setText(tot3);
                        textViewPresent3.setText(pre3);
                        progress3.setText(percentage3+"%");

                        textViewSubject4.setText(sub4);
                        textViewFaculty4.setText(fac4);
                        textViewAbsent4.setText(abs4);
                        textViewTotal4.setText(tot4);
                        textViewPresent4.setText(pre4);
                        progress4.setText(percentage4+"%");

                        textViewSubject5.setText(sub5);
                        textViewFaculty5.setText(fac5);
                        textViewAbsent5.setText(abs5);
                        textViewTotal5.setText(tot5);
                        textViewPresent5.setText(pre5);
                        progress5.setText(percentage5+"%");

                        textViewSubject6.setText(sub6);
                        textViewFaculty6.setText(fac6);
                        textViewAbsent6.setText(abs6);
                        textViewTotal6.setText(tot6);
                        textViewPresent6.setText(pre6);
                        progress6.setText(percentage6+"%");


                    } else {
                    }


                } else {
                    Toast.makeText(Attendance.this, "Something went wrong.! ", Toast.LENGTH_SHORT).show();

                }
                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("11122", String.valueOf(error));
                Toast.makeText(Attendance.this, "Something went wrong.! readData", Toast.LENGTH_SHORT).show();


            }

        });
    }

}