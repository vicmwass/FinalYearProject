package com.example.finalyearproject.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.finalyearproject.HelperClasses.FirebaseUtils;
import com.example.finalyearproject.Modules.User;
import com.example.finalyearproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Handler;

public class ProfileActivity extends AppCompatActivity implements EmailDialog.EmailDialogListener {
    public static final String PROFILE = "Profile";
    public FirebaseAuth mFirebaseAuth;
    private String mUserName;
    private String mUserPhoneNo;
    private String mUserEmail;
    private EditText mEtName;
    private EditText mEtEmail;
    private EditText mEtPhoneNo;
    private ImageView mImEditName;
    private ImageView mImEditEmail;
    private ImageView mImEditPhoneNo;
    private String mCurrentEmail;
    private String mCurrentPassword;
    private boolean mEmailEditable;
    private boolean mUsernameEditable;
    private boolean mPhoneNoEditable;

    public static final FirebaseFirestore mFirestore=FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mEtName = findViewById(R.id.user_edit_name);
        mEtEmail = findViewById(R.id.user_edit_email);
        mEtPhoneNo = findViewById(R.id.user_edit_phone);
        mImEditName = findViewById(R.id.edit_name);
        mImEditEmail = findViewById(R.id.edit_email);
        mImEditPhoneNo = findViewById(R.id.edit_phone);
        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUtils.FIRESTORE.collection("users").document(mFirebaseAuth.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    User lUser=task.getResult().toObject(User.class).withId(task.getResult().getId());
                    mUserName = mFirebaseAuth.getCurrentUser().getDisplayName();
                    mUserPhoneNo = "";
                    mUserEmail = mFirebaseAuth.getCurrentUser().getEmail();
                    mEtName.setText(mUserName);
                    mEtEmail.setText(mUserEmail);
                    mEtPhoneNo.setText(mUserPhoneNo);
                }
            }
        });





//        Button saveProfile=findViewById(R.id.btn_save_profile);

//        mEtName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(!hasFocus){
//
//
//                }else{
//                    mEtName.setFocusableInTouchMode(true);
//                }
//            }
//        });

//        mEtEmail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(!hasFocus){
//
//                }else{
//                    mEtEmail.setFocusableInTouchMode(true);
//                }
//            }
//        });

//        mEtPhoneNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(!hasFocus){
//
//
//                }else{
//                    mEtPhoneNo.setFocusableInTouchMode(true);
//                }
//            }
//        });

        mImEditEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mEmailEditable) {
                    mEtEmail.setEnabled(true);
                    mEtEmail.setFocusableInTouchMode(true);
                    mEtEmail.requestFocus();
                    mImEditEmail.setImageResource(R.drawable.save_24);
                    mEmailEditable=true;
                }else {
                    if(!mUserEmail.equals(mEtEmail.getText().toString().trim())){
                        updateEmail();
                    }else {
                        mEtEmail.setText(mUserEmail);
                    }

                }
            }
        });

        mImEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mUsernameEditable) {
                    mEtName.setEnabled(true);
                    mEtName.setFocusableInTouchMode(true);
                    mEtName.requestFocus();
                    mImEditName.setImageResource(R.drawable.save_24);
                    mUsernameEditable=true;
                }else {
                    if(!mUserName.equals(mEtName.getText().toString().trim())){
                        updateUsername();
                    }
                    else {
                        mEtName.setText(mUserName);
                    }


                }
            }
        });

        mImEditPhoneNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mPhoneNoEditable) {
                    mEtPhoneNo.setEnabled(true);
                    mEtPhoneNo.setFocusableInTouchMode(true);
                    mEtPhoneNo.requestFocus();
                    mImEditPhoneNo.setImageResource(R.drawable.save_24);
                    mPhoneNoEditable=true;
                }else {
                    if(!mUserPhoneNo.equals(mEtPhoneNo.getText().toString().trim())){
                        updatePhoneNo();
                    }else {
                        mEtPhoneNo.setText(mUserPhoneNo);
                    }

                }

            }
        });

//        saveProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                saveDetails();
//            }
//        });
    }

    public void updatePhoneNo() {
        DocumentReference  userRef = mFirestore.collection("users")
                .document(mFirebaseAuth.getUid());
        userRef.update(
                "phoneNo",mEtPhoneNo.getText().toString().trim()
        ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(PROFILE, "phone no updated.");
                    Toast.makeText(ProfileActivity.this,"phone updated successful",Toast.LENGTH_LONG).show();
                    mUserPhoneNo=mEtPhoneNo.getText().toString().trim();
                }else{
                    mEtPhoneNo.setText(mUserPhoneNo);
                    Toast.makeText(ProfileActivity.this,"phone updated failed",Toast.LENGTH_LONG).show();
                }
                mEtPhoneNo.setEnabled(false);
                mPhoneNoEditable=false;
                mImEditPhoneNo.setImageResource(R.drawable.edit_24);
            }
        });

    }

    public void updateEmail(){
        // Get auth credentials from the user for re-authentication
        openDialog();


    }

    private void openDialog() {
        EmailDialog lEmailDialog=new EmailDialog();
        lEmailDialog.show(getSupportFragmentManager(),"Current Credentials");
    }

    public void updateUsername() {
        UserProfileChangeRequest.Builder profileUpdates = new UserProfileChangeRequest.Builder();
        profileUpdates.setDisplayName(mEtName.getText().toString().trim());
        mFirebaseAuth.getCurrentUser().updateProfile(profileUpdates.build())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            DocumentReference  userRef = mFirestore.collection("users")
                                    .document(mFirebaseAuth.getUid());
                            userRef.update(
                                    "username", mEtName.getText().toString().trim()
                            ).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Log.d(PROFILE, "User name updated.");
                                        Toast.makeText(ProfileActivity.this,"username updated successful",Toast.LENGTH_LONG).show();
                                        mUserName=mEtName.getText().toString().trim();
                                    }else {
                                        mEtName.setText(mUserName);
                                        Toast.makeText(ProfileActivity.this,"phone updated failed",Toast.LENGTH_LONG).show();
                                    }
                                    mEtName.setEnabled(false);
                                    mUsernameEditable=false;
                                    mImEditName.setImageResource(R.drawable.edit_24);
                                }
                            });

                        }
                    }
    });

    }

//    private void saveDetails() {
//        boolean changed=false;
//        UserProfileChangeRequest.Builder profileUpdates = new UserProfileChangeRequest.Builder();
//
//        if(!mUserName.equals(mEtName.getText().toString().trim())){
//            changed=true;
//            profileUpdates.setDisplayName(mEtName.getText().toString().trim());
//        }
//        if(!mUserPhoneNo.equals(mEtPhoneNo.getText().toString().trim())){
//            changed=true;
//        }
//        if(changed){
//            mFirebaseAuth.getCurrentUser().updateProfile(profileUpdates.build())
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull @NotNull Task<Void> task) {
//                            if (task.isSuccessful()) {
//                                Log.d("TAG", "User profile updated.");
//                            }
//                        }
//        });
//    }
//}

    @Override
    public void applyText(String email, String password) {
//        mCurrentEmail= email;
//        mCurrentPassword= password;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider
                .getCredential(email, password); // Current Login Credentials \\
        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.updateEmail(mEtEmail.getText().toString().trim())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            DocumentReference  userRef = mFirestore.collection("users")
                                                    .document(mFirebaseAuth.getUid());
                                            userRef.update(
                                                    "email", mEtEmail.getText().toString().trim()
                                            ).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                    if(task.isSuccessful()){
                                                        Toast.makeText(ProfileActivity.this,"email updated successful",Toast.LENGTH_LONG).show();
                                                        Log.d(PROFILE, "email updated.");
                                                        mUserEmail=mEtEmail.getText().toString().trim();

                                                    }else {
                                                        mEtEmail.setText(mUserEmail);
                                                        Toast.makeText(ProfileActivity.this,"email updated failed",Toast.LENGTH_LONG).show();

                                                    }
                                                    mEtEmail.setEnabled(false);
                                                    mEmailEditable=false;
                                                    mImEditEmail.setImageResource(R.drawable.edit_24);
                                                }
                                            });
                                        }
                                    }
                                });
                    }
                });

    }
}