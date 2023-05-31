package com.example.attendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Admin extends AppCompatActivity {
    EditText edittextemail,edittextpassword;
    ProgressBar progressBar;
    Button adlogbtn;
    FirebaseAuth firebaseAuth;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        edittextemail=findViewById(R.id.userid2);
        edittextpassword=findViewById(R.id.password2);
        progressBar=findViewById(R.id.progressBar);
        firebaseAuth=FirebaseAuth.getInstance();


        // Show Hide Password using Eye icon
        ImageView imageviewshowhide = findViewById(R.id.imageview_show_hide_pwd);
        imageviewshowhide.setImageResource(R.drawable.ic_hide_pwd);
        imageviewshowhide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edittextpassword.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //If password is visible then hide it
                    edittextpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //Change icon
                    imageviewshowhide.setImageResource(R.drawable.ic_hide_pwd);
                }else{
                    edittextpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageviewshowhide.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });
        // ArrayAdapter <ContactsContract.CommonDataKinds.Email adapter=ArrayAdapter.createFromResource(this,R.array.)
        adlogbtn=findViewById(R.id.Register);
        adlogbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();


            }
        });
    }
    public void login(){
        String email=edittextemail.getText().toString();
        String pass=edittextpassword.getText().toString();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(Admin.this,"Please enter your email",Toast.LENGTH_LONG).show();
            edittextemail.setError("Email is required");
            edittextemail.requestFocus();
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(Admin.this,"Please enter valid email",Toast.LENGTH_LONG).show();
            edittextemail.setError("Email is required");
            edittextemail.requestFocus();
        }else if(TextUtils.isEmpty(pass)){
            Toast.makeText(Admin.this,"Please enter your password",Toast.LENGTH_LONG).show();
            edittextpassword.setError("Password is required");
            edittextpassword.requestFocus();
        }else if (pass.length() < 8) {
            Toast.makeText(Admin.this, "Please enter Password.", Toast.LENGTH_LONG).show();
            edittextpassword.setError("Password should be minimum 8 Digit");
            edittextpassword.requestFocus();
        }else{
            progressBar.setVisibility(View.VISIBLE);
            loginUser(email,pass);

        }
    }


    public void loginUser(String email,String pass) {
        firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (email.equals("admin@gmail.com") && pass.equals("AdminLogin")) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Admin.this, "You can Register Student Now", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Admin.this, Register.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(Admin.this, "Admin is not registered", Toast.LENGTH_LONG).show();
                    }

                }else {
                    Toast.makeText(Admin.this, "Please Enter Admin's Email and Password", Toast.LENGTH_LONG).show();

                }
                progressBar.setVisibility(View.GONE);

            }

        });
    }
}