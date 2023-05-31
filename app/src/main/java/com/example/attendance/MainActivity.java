package com.example.attendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private EditText editTextLoginEmail,editTextLoginPwd;
    private TextView ForgetPass,Register;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    FirebaseUser firebaseUser;
    private static  final String TAG="MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        editTextLoginEmail=findViewById(R.id.EmailId);
        editTextLoginPwd=findViewById(R.id.password);
        progressBar=findViewById(R.id.progressBar);
        authProfile=FirebaseAuth.getInstance();
        //Show Hide Password using Eye Icon
        ImageView imgShowHidePwd=findViewById(R.id.imageview_show_hide_pwd);
        imgShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
        imgShowHidePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextLoginPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    //If password is visible then Hide it
                    editTextLoginPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    //change Icon
                    imgShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
                }else{
                    editTextLoginPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imgShowHidePwd.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });
        //open login Activity
        Button Login=findViewById(R.id.Login);
        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String textEmail=editTextLoginEmail.getText().toString();
                String textPwd=editTextLoginPwd.getText().toString();

                if(TextUtils.isEmpty((textEmail))){
                    Toast.makeText(MainActivity.this,"Please enter  your email",Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("Email is required");
                    editTextLoginEmail.requestFocus();
                }
                else if(!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(MainActivity.this,"Please re-enter your email",Toast.LENGTH_SHORT).show();
                    editTextLoginEmail.setError("valid email is required");
                    editTextLoginEmail.requestFocus();
                }else if(TextUtils.isEmpty(textPwd)){
                    Toast.makeText(MainActivity.this,"Please enter your Password",Toast.LENGTH_SHORT).show();
                    editTextLoginPwd.setError("Password is required");
                    editTextLoginPwd.requestFocus();
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(textEmail,textPwd);
                }
            }
        });
        TextView Register=findViewById(R.id.Register);
        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,Admin.class);
                startActivity(intent);
            }
        });
        ForgetPass=findViewById(R.id.ForgetPass);
        ForgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this,"You can reset your password now!",Toast.LENGTH_LONG).show();
                startActivity(new Intent(MainActivity.this,ForgetPassword.class));
            }
        });
    }
    private void loginUser(String email,String pwd) {
        authProfile.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(MainActivity.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (email.equals("admin@gmail.com")  && (pwd.equals("AdminLogin"))) {
                    editTextLoginEmail.setError("User cannot login as Admin  ");
                    editTextLoginEmail.requestFocus();
                }
                else{
                    if (task.isSuccessful()) {
                        //Get instance of the current User
                        FirebaseUser firebaseUser = authProfile.getCurrentUser();

                        //check if email is verified before user can access their profile
                        if (firebaseUser.isEmailVerified()) {
                            Toast.makeText(MainActivity.this, "You are logged in now", Toast.LENGTH_SHORT).show();
                            //open user Profile
                            startActivity(new Intent(MainActivity.this, Attendance.class));
                            finish(); //Close Login Activity..
                        } else {
                            firebaseUser.sendEmailVerification();
                            authProfile.signOut();//sign out user
                            showAlertDialog();
                        }
                    }
                    try{
                        throw task.getException();
                    }catch(FirebaseAuthInvalidUserException e){
                        editTextLoginEmail.setError("User does not exists or is no longer valid.Please register again ");
                        editTextLoginEmail.requestFocus();
                    }catch(FirebaseAuthInvalidCredentialsException e){
                        editTextLoginEmail.setError("Invalid credentials.Kindly,check and re-enter.");
                        editTextLoginEmail.requestFocus();
                    }catch(Exception e){
                        Log.e(TAG,e.getMessage());
                        Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
    private void showAlertDialog() {
        //setup the Alert Builder
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Email Not Verified");
        builder.setMessage("Please verify your email now.You cannot login without email Verification.");

        // Open Email Apps if User clicks/taps Continue button
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int which) {
                Intent intent=new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //To email app in new window and not within our app
                startActivity(intent);
            }
        });
        //Create AlertDialog
        AlertDialog alertDialog= builder.create();

        //show the AlertDialog
        alertDialog.show();
    }
    //Check if User already logged in. In such case,straightaway take the  DashBoard page
    @Override
    protected void onStart() {
        super.onStart();
        if(authProfile.getCurrentUser()!= null){
            Toast.makeText(MainActivity.this,"Already Logged In !!",Toast.LENGTH_SHORT).show();

            //Start the Attendance Activity
             startActivity(new Intent(MainActivity.this,Attendance.class));
             finish(); //Close Login Activity..
        }
        else{
            Toast.makeText(MainActivity.this,"You can login now !!",Toast.LENGTH_SHORT).show();
        }
    }
}