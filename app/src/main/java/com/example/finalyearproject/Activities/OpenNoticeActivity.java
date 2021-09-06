package com.example.finalyearproject.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.finalyearproject.Activities.Launch.LaunchActivity;
import com.example.finalyearproject.Activities.Main.MainActivity;
import com.example.finalyearproject.HelperClasses.FirebaseUtils;
import com.example.finalyearproject.Modules.Comment;
import com.example.finalyearproject.Modules.Notice;
import com.example.finalyearproject.R;
import com.google.firebase.firestore.FieldValue;

public class OpenNoticeActivity extends AppCompatActivity {


    private TextView mTvSender;
    private TextView mTvSubject;
    private TextView mTvDescription;
    private TextView mTvFile;
    private LinearLayout mLlOpenFile;
    private Toolbar mToolbar;
    private TextView mTvFileAttach;
    private RelativeLayout mCommentSection;
    private Notice mNotice;
    private EditText mInputComment;
    private String mInstCode;
    private ImageView mSendComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        initializeViews();
        loadData();
        setupAdapter();

    }

    private void loadData() {
        Intent lIntent=getIntent();
        mNotice = (Notice) lIntent.getSerializableExtra(MainActivity.NOTICE);
        mInstCode = lIntent.getStringExtra(LaunchActivity.INSTITUTION_CODE);
        if(!mNotice.isCommentable()){
            mCommentSection.setVisibility(View.GONE);
        }
        mTvSender.setText(mNotice.getSender());
        mTvSubject.setText(mNotice.getSubject());
        if(mNotice.getDescription()!=null){
            mTvDescription.setText(mNotice.getDescription());
        }else {
            mTvDescription.setVisibility(View.INVISIBLE);
        }
        if(mNotice.getFileName()!=null){
            mTvFile.setText(mNotice.getFileName());
            mLlOpenFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ProgressDialog progressDialog = new ProgressDialog(OpenNoticeActivity.this);
                    FirebaseUtils.saveFileLocally(OpenNoticeActivity.this, mNotice,progressDialog);
                }
            });
        }
    }

    public void setupAdapter() {
        RecyclerView lRecyclerView =findViewById(R.id.comments_rv);
        CommentAdapter lCommentAdapter = new CommentAdapter(this,mInstCode,mNotice.getId(),lRecyclerView);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        lRecyclerView.setLayoutManager(mLinearLayoutManager);
        lRecyclerView.setAdapter(lCommentAdapter);

    }

    private void initializeViews() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        mTvSender = findViewById(R.id.tv_display_sender);
        mTvSubject = findViewById(R.id.tv_display_subject);
        mTvDescription = findViewById(R.id.tv_display_description);
        mTvFile = findViewById(R.id.tv_display_file);
        mTvFileAttach = findViewById(R.id.tv_file_attach);
        mLlOpenFile = findViewById(R.id.open_file);
        mCommentSection = findViewById(R.id.comment_section);
        mInputComment = findViewById(R.id.inputcomment);
        mSendComment = findViewById(R.id.imageView_send_comment);
        mSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendText();
            }
        });

    }
    private void sendText() {
        String text =mInputComment.getText().toString().trim();
        if (text.length() == 0) {
            mInputComment.setError("text required");
            mInputComment.requestFocus();
            return;
        }
        mInputComment.setText("");
        Comment lComment=new Comment();
        lComment.setMessage(text);
        lComment.setUserID(FirebaseUtils.sFirebaseAuth.getUid());
//        Long tsLong = System.currentTimeMillis()/1000;
//        Timestamp tm=FieldValue.serverTimestamp();
        FieldValue ts=FieldValue.serverTimestamp();
        lComment.addTimeStampToken(ts);
        FirebaseUtils.addComment(mInstCode,this,lComment,mNotice.getId());

    }
}