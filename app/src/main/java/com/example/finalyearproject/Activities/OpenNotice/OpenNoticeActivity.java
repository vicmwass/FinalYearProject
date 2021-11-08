package com.example.finalyearproject.Activities.OpenNotice;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.finalyearproject.Activities.Launch.LaunchActivity;
import com.example.finalyearproject.Activities.Main.MainActivity;
import com.example.finalyearproject.HelperClasses.FirebaseUtils;
import com.example.finalyearproject.Modules.Comment;
import com.example.finalyearproject.Modules.Notice;
import com.example.finalyearproject.Modules.User;
import com.example.finalyearproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OpenNoticeActivity extends AppCompatActivity {


    private TextView mTvSender;
    private TextView mTvSubject;
    private TextView mTvDescription;
    private TextView mTvFile;
    private TextView mTvDate;
    private LinearLayout mLlOpenFile;
    private Toolbar mToolbar;
    private TextView mTvFileAttach;
    private RelativeLayout mCommentSection;
    private Notice mNotice;
    private EditText mInputComment;
    private String mInstCode;
    private ImageView mSendComment;
    private CollectionReference mNoticeRef;
    private ArrayList<String> mIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.changeTheme(this);
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
        mIdList = lIntent.getStringArrayListExtra("Domain ids");
        if(!mNotice.isCommentable()){
            mCommentSection.setVisibility(View.GONE);
        }
        FirebaseUtils.FIRESTORE.collection("users").document(mNotice.getSenderId())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    mTvSender.setText((String) task.getResult().get(User.USERNAME));
                }
            }
        });

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
        if(mNotice.getTimeStamp()!=null){
            Date currentDate = new Date(mNotice.getTimeStamp()*1000);
            SimpleDateFormat dateFormat= new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");
            String dateOnly = dateFormat.format(currentDate);
            mTvDate.setText(dateOnly);
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
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mTvSender = findViewById(R.id.tv_display_sender);
        mTvSubject = findViewById(R.id.tv_display_subject);
        mTvDescription = findViewById(R.id.tv_display_description);
        mTvFile = findViewById(R.id.tv_display_file);
//        mTvFileAttach = findViewById(R.id.tv_file_attach);
        mTvDate=findViewById(R.id.tv_display_date);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.open_notice_menu,menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(FirebaseAuth.getInstance().getUid()!=mNotice.getSenderId()){
            menu.setGroupVisible(R.id.for_notice_creator,false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_notice:
                mNoticeRef = FirebaseUtils.FIRESTORE.collection("Institutions").document(mInstCode).collection("notices");
                if(mIdList.size()>0){
                    mNoticeRef = mNoticeRef.document(mIdList.get(mIdList.size()-1)).collection("my_notices");
                }else{
                    mNoticeRef = mNoticeRef.document("0").collection("my_notices");
                }
                mNoticeRef.document(mNotice.getId())
                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(OpenNoticeActivity.this, "Added successful", Toast.LENGTH_LONG).show();
                            OpenNoticeActivity.this.onBackPressed();
                            OpenNoticeActivity.this.finish();
                        }
                    }
                });
                break;
            case android.R.id.home:
                finish();
                break;

        }

        return true;
    }
}