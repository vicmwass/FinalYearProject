package com.example.finalyearproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class LaunchActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener  {

    private Button mBtnCreate;
    private Button mBtnReg;
    public  FirebaseAuth.AuthStateListener mAuthStateListener;
    public  FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseUtils.openFirebaseReference(this);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAuth.addAuthStateListener(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        mBtnCreate = findViewById(R.id.btn_create_inst);
        mBtnReg = findViewById(R.id.btn_reg_for_inst);
        mBtnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent lIntent=new Intent(LaunchActivity.this,RegisterInstitution.class);
                startActivity(lIntent);
            }
        });
        mBtnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent lIntent=new Intent(LaunchActivity.this,RegisterForInstitution.class);
                startActivity(lIntent);
            }
        });


    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(this);
    }

    @Override
    public void onAuthStateChanged(@NonNull @NotNull FirebaseAuth firebaseAuth) {
        if (mFirebaseAuth.getCurrentUser() == null) {
            FirebaseUtils.signIn();
        }else {
            checkInstitution();
        }

    }
    private void checkInstitution() {
            FirebaseUtils.FIRESTORE.collection(FirebaseUtils.USERS)
                    .document(FirebaseUtils.sFirebaseAuth.getUid()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot lSnapshot=task.getResult();
                                User lUser=lSnapshot.toObject(User.class).withId(lSnapshot.getId());
                                ArrayList<String> lInst=lUser.getInstitutions();
                                if(lInst==null){
                                    return;
                                }
                                Intent lIntent=new Intent(LaunchActivity.this,ChooseInstitution.class);
                                lIntent.putExtra("institutionList",lInst);
                                startActivity(lIntent);
                                finish();
                            }

                        }
                    });

    }
}