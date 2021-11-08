package com.example.finalyearproject.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.finalyearproject.Activities.Main.MainActivity;
import com.example.finalyearproject.Activities.Profile.ProfileActivity;
import com.example.finalyearproject.Modules.Institution;
import com.example.finalyearproject.Modules.NavObjects;
import com.example.finalyearproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.finalyearproject.Activities.Main.MainActivity.NAV_OBJECT;
import static com.example.finalyearproject.HelperClasses.FirebaseUtils.INSTITUTIONS;

public class AboutInstitution extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    private NavObjects mNavObjects;
    public FirebaseAuth mFirebaseAuth;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private CircleImageView mImageView;
    private Institution mInstitution;
    private Uri mImageUri;
    private final StorageReference reference = FirebaseStorage.getInstance().getReference();
    public static final FirebaseFirestore mFirestore=FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.changeTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_institution);
        mFirebaseAuth = FirebaseAuth.getInstance();
        Intent lIntent=getIntent();
        mNavObjects = (NavObjects) lIntent.getParcelableExtra(NAV_OBJECT);
        mInstitution = mNavObjects.getInstDetails();
        setupNavigatioView();
        TextView tvInstName=findViewById(R.id.disp_inst_name);
        TextView tvInstCode=findViewById(R.id.disp_inst_code);
        TextView tvInstDesription=findViewById(R.id.disp_inst_description);
        mImageView = findViewById(R.id.logo_image);
        if(mInstitution.getLogoUri()!=null){
            showImage(mInstitution.getLogoUri());
        }
        tvInstCode.setText(mNavObjects.getInstDetails().getCode());
        tvInstName.setText(mNavObjects.getInstDetails().getName());
        tvInstDesription.setText(mNavObjects.getInstDetails().getDescription());
        if(FirebaseAuth.getInstance().getUid().equals(mInstitution.getCreator())){
            mImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent galleryIntent = new Intent();
                    galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(galleryIntent, 2);
                }
            });
        }
    }

    public void showImage(String url){
        if(url!=null&&url.isEmpty()==false){
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Glide.with(this).load(url).override(width*1/2, width*2/3).
                    centerCrop().into(mImageView);
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
    private void uploadToFirebase(Uri uri) {

        final StorageReference fileRef = reference.child(System.currentTimeMillis() + "." + getFileExtension(uri));
        fileRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        updateLogoImage();
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
                Toast.makeText(AboutInstitution.this, "Uploading Failed", Toast.LENGTH_LONG).show();
            }
        });


    }

    private void setupNavigatioView() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(mNavObjects.getInstDetails().getName());

        mDrawerLayout =findViewById(R.id.drawer_layout);
        mNavigationView =findViewById(R.id.nav_view);


        mNavigationView.bringToFront();//when navdrawer items clicked show that color to represent click
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout,mToolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //to make navigation drawer clickable
        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setCheckedItem(R.id.about_institution);

        if(mNavObjects.getIsAdmin()){
//            mNavigationView.getMenu().setGroupVisible(R.id.nav_for_admin,true);
        }else {
//            mNavigationView.getMenu().setGroupVisible(R.id.nav_for_admin,false);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {

        MainActivity.navigationSwitch(this,item, mNavObjects, mDrawerLayout);
        return true;
    }
    private void updateLogoImage() {
        DocumentReference userRef = mFirestore.collection(INSTITUTIONS)
                .document(mNavObjects.getInstDetails().getCode());
        userRef.update(
                "logoUri", mImageUri.toString()
        ).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(AboutInstitution.this,"logo updated successful",Toast.LENGTH_LONG).show();
                    showImage(mImageUri.toString());
                }else {
                    Toast.makeText(AboutInstitution.this, "logo updated failed", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    private String getFileExtension(Uri mUri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(mUri));
    }
}

