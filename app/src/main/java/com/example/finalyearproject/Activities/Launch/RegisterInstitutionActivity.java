package com.example.finalyearproject.Activities.Launch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.finalyearproject.Activities.Main.MainActivity;
import com.example.finalyearproject.HelperClasses.FirebaseUtils;
import com.example.finalyearproject.Modules.InstUser;
import com.example.finalyearproject.Modules.Institution;
import com.example.finalyearproject.Modules.User;
import com.example.finalyearproject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.finalyearproject.Activities.Launch.LaunchActivity.INSTITUTION_DETAILS;
import static com.example.finalyearproject.Activities.Main.MainActivity.SHARED_PREFS;
import static com.example.finalyearproject.Activities.SplashScreen.APP_THEME;
import static com.example.finalyearproject.HelperClasses.FirebaseUtils.FIRESTORE;
import static com.example.finalyearproject.HelperClasses.FirebaseUtils.INSTITUTIONS;

public class RegisterInstitutionActivity extends AppCompatActivity {
    private  String[] mThemes;
    private EditText mEtInstName;
    private Institution mInst;
    private FirebaseAuth mFirebaseAuth;
    private User mUser;
    private InstUser mInstUser;
    private String mSelectedTheme;
    private CircleImageView mImageView;
    private Uri mImageUri;
    private final StorageReference reference = FirebaseStorage.getInstance().getReference();
    private EditText mEtDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_institution);
        mThemes=getResources().getStringArray(R.array.themes);
        mSelectedTheme=mThemes[1];
        mEtInstName = findViewById(R.id.et_inst_name);
        mEtDescription = findViewById(R.id.et_description);
        mImageView = findViewById(R.id.logo_image);
        Button btSave=findViewById(R.id.btn_save_inst);
        Spinner spin = findViewById(R.id.theme_spinner);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (mThemes[position]){
                    case "blue focused":
                        setTheme(R.style.Theme_FinalYearProject);
                        mSelectedTheme=mThemes[1];
                        break;
                    case "green focused":
                        setTheme(R.style.Theme_FinalYearProject1);
                        mSelectedTheme=mThemes[2];
                        break;
                    case "red focused":
                        setTheme(R.style.Theme_FinalYearProject2);
                        mSelectedTheme=mThemes[3];
                        break;
                    case "yellow focused":
                        setTheme(R.style.Theme_FinalYearProject3);
                        mSelectedTheme=mThemes[4];
                        break;
                        case "purple focused":
                        setTheme(R.style.Theme_FinalYearProject4);
                        mSelectedTheme=mThemes[5];
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                recreate();
            }
        });

        mFirebaseAuth = FirebaseAuth.getInstance();
        mInst = new Institution();
        mUser = new User();
        mInstUser = new InstUser();
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 2);
            }
        });
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDetails();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            mImageUri = data.getData();
            showImage(mImageUri.toString());
//            uploadToFirebase(mImageUri);
//            profileimage.setImageURI(imageUri);
        }

    }
    private String getFileExtension(Uri mUri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }
    private void uploadToFirebase(Uri uri) {

        final StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        mInst.setLogoUri(uri.toString());
                        saveInstitution();
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterInstitutionActivity.this,"Failed to register institution",Toast.LENGTH_LONG).show();
            }
        });


    }
    public void showImage(String url){
        if(url!=null&&url.isEmpty()==false){
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Glide.with(this).load(url).override(width*1/2, width*2/3).
                    centerCrop().into(mImageView);
        }
    }
    private void saveDetails(){
        String lInstName=mEtInstName.getText().toString().trim();
        String lInstDescription=mEtDescription.getText().toString().trim();
        if(lInstName.length()<0){
            mEtInstName.setError("Name is Required");
            mEtInstName.requestFocus();
            return ;
        }
        if(lInstDescription.length()<0){
            mEtDescription.setError("Description is Required");
            mEtDescription.requestFocus();
            return ;
        }
        mInst.setDescription(lInstDescription);
        mInst.setName(lInstName);
        mInst.setCreator(mFirebaseAuth.getUid());
        mInst.setAdminList(new ArrayList<String>(Arrays.asList(mFirebaseAuth.getUid())));
        mInst.setUsers(new ArrayList<String>(Arrays.asList(mFirebaseAuth.getUid())));
        mInst.setTheme(mSelectedTheme);
        generateCode(lInstName);
        if(mImageUri!=null)
        uploadToFirebase(mImageUri);
        else saveInstitution();
    }

    private void generateCode(String name) {
        String words[] = name.split(" ");
        int rand =(int)Math.round((Math.random()*((9000-1000)+1))+9000);
        mInst.setCode(words[0]+rand);
    }


    private  void saveInstitution(){
        final DocumentReference institutionRef= FIRESTORE.collection(INSTITUTIONS).document(mInst.getCode());
        institutionRef.set(mInst)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(RegisterInstitutionActivity.this,"Added successful",Toast.LENGTH_LONG).show();
//                        Log.d("Firestore", "Document updated with ID: " + PatientPostRef.getId());
                        saveUserToInst();
                        savePrefData(mInst.getTheme());
                        Intent lIntent=new Intent(RegisterInstitutionActivity.this, MainActivity.class);
                        lIntent.putExtra(INSTITUTION_DETAILS, (Parcelable) mInst);
                        startActivity(lIntent);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterInstitutionActivity.this,"Failed to register institution",Toast.LENGTH_LONG).show();
//                        Log.e("Firestore", "Error updating document", e);
                    }
                });

    }

    public void saveUserToInst() {
        mUser.setId(mFirebaseAuth.getUid());
        mUser.addInstitution(mInst.getCode());
        mUser.setUsername(mFirebaseAuth.getCurrentUser().getDisplayName());
        mUser.setEmail(mFirebaseAuth.getCurrentUser().getEmail());
        FirebaseUtils.saveUserDetails(mUser);
    }
    private void savePrefData(String theme) {
        SharedPreferences lSharedPreferences=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor lEditor=lSharedPreferences.edit();
        lEditor.putString(APP_THEME,theme);
        lEditor.apply();
    }
}