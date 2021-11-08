package com.example.finalyearproject.Activities.Profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.finalyearproject.Activities.Main.MainActivity;
import com.example.finalyearproject.HelperClasses.FirebaseUtils;
import com.example.finalyearproject.Modules.User;
import com.example.finalyearproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

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
    private CircleImageView mProfileImage;
    private final StorageReference reference = FirebaseStorage.getInstance().getReference();
    private Uri mImageUri;
    private ProgressBar mProgressBar;

    public static final FirebaseFirestore mFirestore=FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.changeTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        initializeViews();
        FirebaseUtils.FIRESTORE.collection("users").document(mFirebaseAuth.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    User lUser=task.getResult().toObject(User.class).withId(task.getResult().getId());
                    mUserName = lUser.getUsername();
                    mUserPhoneNo = lUser.getPhoneNo();
                    mUserEmail = lUser.getEmail();
                    mEtName.setText(mUserName);
                    mEtEmail.setText(mUserEmail);
                    mEtPhoneNo.setText(mUserPhoneNo);
                    if(lUser.getImgUri()!=null){
                        showImage(lUser.getImgUri());
                    }
                }
            }
        });

        mProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 2);
            }
        });
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
                    if(mUserPhoneNo==null||!mUserPhoneNo.equals(mEtPhoneNo.getText().toString().trim())){
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

    private void initializeViews() {
        mEtName = findViewById(R.id.user_edit_name);
        mEtEmail = findViewById(R.id.user_edit_email);
        mEtPhoneNo = findViewById(R.id.user_edit_phone);
        mImEditName = findViewById(R.id.edit_name);
        mImEditEmail = findViewById(R.id.edit_email);
        mImEditPhoneNo = findViewById(R.id.edit_phone);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mProfileImage = findViewById(R.id.profile_image);
        mProgressBar = findViewById(R.id.progressBarupload);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            mImageUri = data.getData();
            uploadToFirebase(mImageUri);
//            profileimage.setImageURI(imageUri);
        }

    }

    public void showImage(String url){
        if(url!=null&&url.isEmpty()==false){
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Glide.with(this).load(url).override(width*1/2, width*2/3).
                    centerCrop().into(mProfileImage);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
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
                                        Toast.makeText(ProfileActivity.this,"username updated failed",Toast.LENGTH_LONG).show();
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

    public void updateProfileImage() {
        UserProfileChangeRequest.Builder profileUpdates = new UserProfileChangeRequest.Builder();
        profileUpdates.setPhotoUri(mImageUri);
        mFirebaseAuth.getCurrentUser().updateProfile(profileUpdates.build())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            DocumentReference  userRef = mFirestore.collection("users")
                                    .document(mFirebaseAuth.getUid());
                            userRef.update(
                                    "imgUri", mImageUri.toString()
                            ).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(ProfileActivity.this,"profile pic updated successful",Toast.LENGTH_LONG).show();
                                        showImage(mImageUri.toString());
                                    }else {
                                        Toast.makeText(ProfileActivity.this, "profile pic updated failed", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        }
                    }
                });

    }

    private void uploadToFirebase(Uri uri) {

        final StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                       updateProfileImage();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                mProgressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(ProfileActivity.this, "Uploading Failed", Toast.LENGTH_LONG).show();
            }
        });


    }

    private String getFileExtension(Uri mUri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }
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