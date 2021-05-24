package com.example.finalyearproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class AddNotice extends AppCompatActivity {

    private EditText mEtSender;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FirebaseUtils.signInAnonymously(this);
        setContentView(R.layout.activity_add_notice);
        mEtSender = findViewById(R.id.et_sender);
        mEtSubject = findViewById(R.id.et_subject);
        mDescription = findViewById(R.id.et_description);
        mTvUpload = findViewById(R.id.tv_upload_file);
        mButton = findViewById(R.id.upload_button);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.app_name);
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
        Intent lIntent=getIntent();
        mIdList = lIntent.getStringArrayListExtra(MainActivity.IDLIST);
        mDomainName=lIntent.getStringExtra(MainActivity.DNAME);
        mNotice = new Notice();
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
    private Boolean saveDetails(){
        Notice lNotice=new Notice();
        String lSender=mEtSender.getText().toString().trim();
        String lSubject=mEtSubject.getText().toString().trim();
        String lDescription=mDescription.getText().toString().trim();
        if(lSender.length() == 0){
            mEtSender.setError("Sender is Required");
            mEtSender.requestFocus();
            return false;
        }
        if(lSubject.length() == 0){
            mEtSubject.setError("Subject is Required");
            mEtSubject.requestFocus();
            return false;
        }
        if(lDescription.length()!=0){
            mNotice.setDescription(lDescription);
        }
        mNotice.setDomainName(mDomainName);
        mNotice.setSender(lSender);
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
            mNotice=FirebaseUtils.saveNoticeFile(mFileUri,mNotice);
        }
    }
    private String queryName(ContentResolver resolver, Uri uri) {
        Cursor returnCursor =
                resolver.query(uri, null, null, null, null);
        assert returnCursor != null;
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        returnCursor.moveToFirst();
        String name = returnCursor.getString(nameIndex);
        returnCursor.close();
        return name;
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
                    FirebaseUtils.saveNotice(this,mNotice,lId);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        FirebaseUtils.deleteFile(mNotice);
        super.onBackPressed();
    }
}