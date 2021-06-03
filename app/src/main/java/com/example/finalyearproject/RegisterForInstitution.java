package com.example.finalyearproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

public class RegisterForInstitution extends AppCompatActivity {

    private EditText mEtInstCode;
    private User mUser;
    private FirebaseAuth mFirebaseAuth;
    private InstUser mInstUser;
    private String mInstCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_for_institution);
        mEtInstCode = findViewById(R.id.et_institution_code);
        Button lButton=findViewById(R.id.btn_save_reg_for_inst);
        mFirebaseAuth = FirebaseAuth.getInstance();

        mUser = new User();
        mInstUser = new InstUser();

        lButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(saveDetails()){
                    Intent lIntent=new Intent(RegisterForInstitution.this,MainActivity.class);
                    lIntent.putExtra("InstitutionCode", mInstCode);
                    startActivity(lIntent);
                }
            }
        });

    }

    private Boolean saveDetails() {
        mInstCode = mEtInstCode.getText().toString().trim();
        if(mInstCode.length()<0){
            mEtInstCode.setError("Name is Required");
            mEtInstCode.requestFocus();
            return false;
        }
        mInstUser.setUserId(mFirebaseAuth.getUid());
        mInstUser.setEmail(mFirebaseAuth.getCurrentUser().getEmail());
        mUser.setId(mFirebaseAuth.getUid());
        mUser.addInstitution(mInstCode);
        FirebaseUtils.saveInstUserDetails(mInstCode,mInstUser);
        FirebaseUtils.saveUserDetails(mUser);
        return true;
    }


}