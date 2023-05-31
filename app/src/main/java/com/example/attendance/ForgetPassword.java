package com.example.attendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.regex.Pattern;

public class ForgetPassword extends AppCompatActivity {

    private EditText EmailAddress;
    private Button Button;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;
    private static  final String TAG="ForgetPassword";


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();

        }
        EmailAddress=(EditText) findViewById(R.id.editTextTextEmailAddress);
        Button=(Button) findViewById(R.id.resetPasswordButton);
        progressBar=findViewById(R.id.progressBar);

        Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=EmailAddress.getText().toString();
                if(TextUtils.isEmpty(email))
                {
                    Toast.makeText(ForgetPassword.this, "Please Enter your registerd email", Toast.LENGTH_SHORT).show();
                    EmailAddress.setError("Email is Required");
                    EmailAddress.requestFocus();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(ForgetPassword.this, "Please Enter your valid email", Toast.LENGTH_SHORT).show();
                    EmailAddress.setError("Valid email is required");
                    EmailAddress.requestFocus();
                }else{
                    progressBar.setVisibility(View.VISIBLE);
                    resetPassword(email);
                }
            }
        });
    }
    private void resetPassword(String email)
    {authProfile=FirebaseAuth.getInstance();
        authProfile.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(ForgetPassword.this, "Please Check Your Inbox For Password reset link", Toast.LENGTH_SHORT).show();

                    Intent intent=new Intent(ForgetPassword.this,MainActivity.class);

                    //Clear stack to prevent user coming back to ForgetPasswordActivity
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException e) {
                        EmailAddress.setError("User doesn't exist or is no longer valid. Please register again");
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(ForgetPassword.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    showAlertDialog();
                }
                progressBar.setVisibility(View.GONE);
            }

            private void showAlertDialog() {

                //setup the Alert Builder
                AlertDialog.Builder builder=new AlertDialog.Builder(ForgetPassword.this);
                builder.setTitle("Password Reset Link");
                builder.setMessage("Password Reset Link has been sent to your Email. Press continue to Reset your Password ");

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
        });

    }
}