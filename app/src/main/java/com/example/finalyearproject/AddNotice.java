package com.example.finalyearproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        FirebaseUtils.signInAnonymously(this);
        setContentView(R.layout.activity_add_notice);
        mEtSender = findViewById(R.id.et_sender);
        mEtSubject = findViewById(R.id.et_subject);
        mTvUpload = findViewById(R.id.tv_upload_file);
        mButton = findViewById(R.id.upload_button);
        Intent lIntent=getIntent();
        mIdList = lIntent.getStringArrayListExtra(MainActivity.IDLIST);
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
    private Notice saveDetails(){
        Notice lNotice=new Notice();
        String lSender=mEtSender.getText().toString().trim();
        String lSubject=mEtSubject.getText().toString().trim();
        mNotice.setSender(lSender);
        mNotice.setSubject(lSubject);


        return mNotice;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==FILE_RESULT&&resultCode==RESULT_OK){
            mFileUri = data.getData();
            mTvUpload.setText(mFileUri.getLastPathSegment());
            mNotice=FirebaseUtils.saveNoticeFile(mFileUri,mNotice);
        }
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

                FirebaseUtils.saveNotice(this,saveDetails(),lId);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}