package com.example.attendance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;


/*import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;*/


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    private EditText editTextRegisterFullName, editTextRegistrationEmail, editTextRegistrationDOB,
            editTextRegistrationPass, editTextRegistrationSid, editTextRegistrationClassDiv;
    private RadioGroup radioGroupRegisterGender;
    private RadioButton radioButtonRegisterGenderSelected;
    private ProgressBar progressBar;
    private ImageView imageView;
    private DatePickerDialog picker;
    private static final String TAG="Register";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //RadioButton for Gender
        radioGroupRegisterGender=findViewById(R.id.radiogender);
        radioGroupRegisterGender.clearCheck();

        progressBar = findViewById(R.id.progressBar);
        editTextRegisterFullName = findViewById(R.id.fname);
        editTextRegistrationDOB = findViewById(R.id.dob);
        editTextRegistrationEmail = findViewById(R.id.email);
        editTextRegistrationPass = findViewById(R.id.password);
        editTextRegistrationSid = findViewById(R.id.studentid);
        editTextRegistrationClassDiv = findViewById(R.id.class_div);

        //Setting up DatePicker on editText
        editTextRegistrationDOB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);
                int year = calendar.get(Calendar.YEAR);

                //Date Picker Dialog
                picker = new DatePickerDialog(Register.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        editTextRegistrationDOB.setText(dayOfMonth + "/" + (month + 1) + "/" + year);

                    }
                }, year, month, day);
                picker.show();
            }
        });

        Button SignUp = findViewById(R.id.button_register);
        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedGenderId=radioGroupRegisterGender.getCheckedRadioButtonId();
                radioButtonRegisterGenderSelected=findViewById(selectedGenderId);

                String textFullName = editTextRegisterFullName.getText().toString();
                String textEmail = editTextRegistrationEmail.getText().toString();
                String textDOB = editTextRegistrationDOB.getText().toString();
                String textPass = editTextRegistrationPass.getText().toString();
                String textSid = editTextRegistrationSid.getText().toString();
                String textClassDiv = editTextRegistrationClassDiv.getText().toString();
                String textGender; //can't obtain the value before verifying if any button was selected or not

                if (TextUtils.isEmpty(textFullName)) {
                    Toast.makeText(Register.this, "Please Enter your full name", Toast.LENGTH_LONG).show();
                    editTextRegisterFullName.setError("Full Name is required");
                    editTextRegisterFullName.requestFocus();
                } else if (TextUtils.isEmpty(textEmail)) {
                    Toast.makeText(Register.this, "Please enter your Email Id", Toast.LENGTH_LONG).show();
                    editTextRegistrationEmail.setError("Email Id is required");
                    editTextRegistrationEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(Register.this, "Please re-enter your email", Toast.LENGTH_LONG).show();
                    editTextRegistrationEmail.setError("Valid email is required");
                    editTextRegistrationEmail.requestFocus();
                } else if (TextUtils.isEmpty(textDOB)) {
                    Toast.makeText(Register.this, "Please Enter your Date-of-Birth", Toast.LENGTH_LONG).show();
                    editTextRegistrationDOB.setError("Date Of Birth is required");
                    editTextRegistrationDOB.requestFocus();
                }else if (TextUtils.isEmpty(textSid)) {
                    Toast.makeText(Register.this, "Please Enter student_ID", Toast.LENGTH_LONG).show();
                    editTextRegistrationSid.setError("Student ID is required");
                    editTextRegistrationSid.requestFocus();
                } else if (TextUtils.isEmpty(textClassDiv)) {
                    Toast.makeText(Register.this, "Please Enter Division", Toast.LENGTH_LONG).show();
                    editTextRegistrationClassDiv.setError("Division is required");
                    editTextRegistrationClassDiv.requestFocus();
                } else if (TextUtils.isEmpty(textPass)) {
                    Toast.makeText(Register.this, "Please Enter password", Toast.LENGTH_LONG).show();
                    editTextRegistrationPass.setError("Password is required");
                    editTextRegistrationPass.requestFocus();
                } else if (textPass.length() < 6) {
                    Toast.makeText(Register.this, "Password should be at least 6 digits", Toast.LENGTH_LONG).show();
                    editTextRegistrationPass.setError("Password too weak");
                    editTextRegistrationPass.requestFocus();
                } else {
                    textGender=radioButtonRegisterGenderSelected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(textFullName, textEmail, textDOB, textGender, textPass, textClassDiv, textSid);
                }
            }
        });
    }

    private void registerUser(String textFullName, String textEmail, String textDOB, String textGender, String textPass, String textClassDiv, String textSid) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        //create User Profile
        auth.createUserWithEmailAndPassword(textEmail, textPass).addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    FirebaseUser firebaseUser = auth.getCurrentUser();

                    //update display name of user
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                    firebaseUser.updateProfile(profileChangeRequest);

                    //Enter User Data into the Firebase Realtime Database.
                    ReadwriteUserDetails writeUserDetails = new ReadwriteUserDetails(textFullName, textEmail, textDOB, textSid, textClassDiv,textGender,textPass);
                    DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Users");
                    referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //send verification Email
                                firebaseUser.sendEmailVerification();
                                Toast.makeText(Register.this, "User registered successfully.Please verify your email", Toast.LENGTH_LONG).show();

                                //open user Profile after successful registration
                                Intent intent = new Intent(Register.this, Register.class);
                                //to prevent user from returning  back to Register Activity on  pressing back button after registration
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                                        Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish(); //to close register activity
                            } else {
                                Toast.makeText(Register.this, "User registered Fail .Please try again", Toast.LENGTH_LONG).show();
                            }
                            //hide progressBar without user creation is successful or failed
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        editTextRegistrationPass.setError("Your password is too weak.kindly use mix of alphabets,numbers,and special characters");
                        editTextRegistrationPass.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        editTextRegistrationEmail.setError("Your email is invalid or already in use .kindly re-enter,");
                        editTextRegistrationEmail.requestFocus();
                    } catch (FirebaseAuthUserCollisionException e) {
                        editTextRegistrationEmail.setError("User is already with this email.use another email");
                        editTextRegistrationEmail.requestFocus();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    //hide progressBar without user creation is successful or failed
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}