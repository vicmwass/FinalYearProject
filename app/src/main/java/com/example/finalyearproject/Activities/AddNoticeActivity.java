package com.example.finalyearproject.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.finalyearproject.Activities.Main.MainActivity;
import com.example.finalyearproject.HelperClasses.FirebaseUtils;
import com.example.finalyearproject.Modules.Institution;
import com.example.finalyearproject.Modules.NavObjects;
import com.example.finalyearproject.Modules.Notice;
import com.example.finalyearproject.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.example.finalyearproject.Activities.Main.MainActivity.NAV_OBJECT;

public class AddNoticeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private EditText mEtSubject;
    private TextView mTvUpload;
    private Button mButton;
    private static final int FILE_RESULT=1;
    private Uri mFileUri;
    private Notice mNotice;
    private ArrayList<String> mIdList;
    private Toolbar mToolbar;
    private EditText mDescription;
    private String mDomainName;

    private String mInstCode;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private Institution mInstDetails;
    private NavObjects mNavObjects;
    private RadioGroup mRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FirebaseUtils.signInAnonymously(this);
        setContentView(R.layout.activity_add_notice);

        Intent lIntent=getIntent();
        mNavObjects = (NavObjects) lIntent.getParcelableExtra(NAV_OBJECT);
        mIdList = mNavObjects.getIdList();
        mInstDetails = mNavObjects.getInstDetails();
        mInstCode = mInstDetails.getCode();
        mDomainName= mNavObjects.getDomainName();
        initializeViews();
        setupNavigationView();




    }
    @Override
    protected void onResume() {
        super.onResume();
        mNavigationView.setCheckedItem(R.id.to_new_notice);
    }

    private void initializeViews() {
        mEtSubject = findViewById(R.id.et_subject);
        mDescription = findViewById(R.id.et_description);
        mTvUpload = findViewById(R.id.tv_upload_file);
        mRadioGroup = findViewById(R.id.comments);
        mButton = findViewById(R.id.upload_button);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(mInstDetails.getName());
        getSupportActionBar().setSubtitle(mDomainName);

        mDrawerLayout =findViewById(R.id.drawer_layout);
        mNavigationView =findViewById(R.id.nav_view);

        mTvUpload.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!s.equals("")){
                    mButton.setVisibility(View.GONE);
                }

            }
        });
        mButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
                startActivityForResult(intent.createChooser(intent,"select file"),FILE_RESULT);
            }
        });
    }

    private void setupNavigationView() {
        mNavigationView.bringToFront();//when navdrawer items clicked show that color to represent click
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout,mToolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //to make navigation drawer clickable
        mNavigationView.setNavigationItemSelectedListener(this);

        mNavigationView.setCheckedItem(R.id.to_new_notice);

        if(mNavObjects.getIsAdmin()){
            mNavigationView.getMenu().setGroupVisible(R.id.nav_for_admin,true);
        }else {
            mNavigationView.getMenu().setGroupVisible(R.id.nav_for_admin,false);
        }
    }


    private Boolean saveDetails(){
        mNotice=new Notice();
        String lSubject=mEtSubject.getText().toString().trim();
        String lDescription=mDescription.getText().toString().trim();

        if(lSubject.length() == 0){
            mEtSubject.setError("Subject is Required");
            mEtSubject.requestFocus();
            return false;
        }
        if(lDescription.length()!=0){
            mNotice.setDescription(lDescription);
        }
        switch (mRadioGroup.getCheckedRadioButtonId()) {
            case R.id.comment_allow:
                mNotice.setCommentable(true);

        }
        mNotice.setDomainName(mDomainName);
        mNotice.setSender(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        mNotice.setSubject(lSubject);
        mNotice.setFileName(mTvUpload.getText().toString());


        return true;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==FILE_RESULT&&resultCode==RESULT_OK){
            mFileUri = data.getData();
            String lFileName=queryName(getContentResolver(),mFileUri);
            mTvUpload.setText(lFileName);
            mNotice.setFileName(lFileName);
            saveNoticeFile();
        }
    }
    private String queryName(ContentResolver resolver, Uri uri) {
        Cursor lReturnCursor =
                resolver.query(uri, null, null, null, null);
        assert lReturnCursor != null;
        int lNameIndex = lReturnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        lReturnCursor.moveToFirst();
        String lName = lReturnCursor.getString(lNameIndex);
        lReturnCursor.close();
        return lName;
    }

    public  void saveNoticeFile(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("File is being uploaded");
        progressDialog.show();
        FirebaseUtils.sFirebaseStorage = FirebaseStorage.getInstance();
        FirebaseUtils.sStorageReference =FirebaseUtils.sFirebaseStorage.getReference().child("notice_files");
        final StorageReference ref = FirebaseUtils.sStorageReference.child(mNotice.getFileName());
        ref.putFile(mFileUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    Log.e("uploadtask", "Upload task failed");
                    throw task.getException();
                }

                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    String downloadUrl = task.getResult().toString();
                    mNotice.setFileUrl(downloadUrl);
                    progressDialog.dismiss();
                } else {
                    Log.e("ImageUri", "Upload task unsuccessful");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {
                Log.e("ImageUri", "Upload failed "+e.getMessage());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater lInflater=getMenuInflater();
        lInflater.inflate(R.menu.new_notice,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_notice:
                String lId;
                if(mIdList.size()>0)
                    lId=mIdList.get(mIdList.size()-1);
                else
                    lId="0";
                if(saveDetails()){
                    FirebaseUtils.saveNotice(mInstCode,this,mNotice,lId);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        MainActivity.navigationSwitch(this,item,mNavObjects, mDrawerLayout);
        return true;
    }


}