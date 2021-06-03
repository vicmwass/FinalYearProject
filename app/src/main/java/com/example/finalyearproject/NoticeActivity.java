package com.example.finalyearproject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NoticeActivity extends AppCompatActivity {


    private TextView mTvSender;
    private TextView mTvSubject;
    private TextView mTvDescription;
    private TextView mTvFile;
    private LinearLayout mLlOpenFile;
    private Toolbar mToolbar;
    private TextView mTvFileAttach;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        mTvSender = findViewById(R.id.tv_display_sender);
        mTvSubject = findViewById(R.id.tv_display_subject);
        mTvDescription = findViewById(R.id.tv_display_description);
        mTvFile = findViewById(R.id.tv_display_file);
        mTvFileAttach = findViewById(R.id.tv_file_attach);
        mLlOpenFile = findViewById(R.id.open_file);

        Intent lIntent=getIntent();
        Notice lNotice= (Notice) lIntent.getSerializableExtra(MainActivity.NOTICE);
        mTvSender.setText(lNotice.getSender());
        mTvSubject.setText(lNotice.getSubject());
        if(lNotice.getDescription()!=null){
            mTvDescription.setText(lNotice.getDescription());
        }else {
            mTvDescription.setVisibility(View.INVISIBLE);
        }
        if(lNotice.getFileName()!=null){
            mTvFile.setText(lNotice.getFileName());
            mLlOpenFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ProgressDialog progressDialog = new ProgressDialog(NoticeActivity.this);
                    FirebaseUtils.saveFileLocally(NoticeActivity.this,lNotice,progressDialog);
                }
            });
        }

    }
}