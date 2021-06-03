package com.example.finalyearproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterInstitution extends AppCompatActivity {

    private EditText mEtInstName;
    private Institution mInst;
    private FirebaseAuth mFirebaseAuth;
    private User mUser;
    private InstUser mInstUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_institution);
        mEtInstName = findViewById(R.id.et_inst_name);
        Button btSave=findViewById(R.id.btn_save_inst);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mInst = new Institution();
        mUser = new User();
        mInstUser = new InstUser();
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(saveDetails()){
                    Intent lIntent=new Intent(RegisterInstitution.this,MainActivity.class);
                    lIntent.putExtra("InstitutionCode", mInst.getCode());
                    startActivity(lIntent);
                }

            }
        });

    }
    private Boolean saveDetails(){
        String lInstName=mEtInstName.getText().toString().trim();
        if(lInstName.length()<0){
            mEtInstName.setError("Name is Required");
            mEtInstName.requestFocus();
            return false;
        }
        mInst.setName(lInstName);
        mInst.setCreator(mFirebaseAuth.getUid());
        generateCode(lInstName);
        FirebaseUtils.saveInstitution(this,mInst);
        mInstUser.setUserId(mFirebaseAuth.getUid());
        mInstUser.setEmail(mFirebaseAuth.getCurrentUser().getEmail());
        mUser.setId(mFirebaseAuth.getUid());
        mUser.addInstitution(mInst.getCode());
        FirebaseUtils.saveInstUserDetails(mInst.getCode(),mInstUser);
        FirebaseUtils.saveUserDetails(mUser);
        return true;
    }

    private void generateCode(String name) {
        String words[] = name.split(" ");
        int rand =(int)Math.round((Math.random()*((9000-1000)+1))+9000);
        mInst.setCode(words[0]+rand);
    }
}