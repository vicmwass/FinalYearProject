package com.example.finalyearproject.Activities.Launch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.finalyearproject.Activities.Main.MainActivity;
import com.example.finalyearproject.HelperClasses.FirebaseUtils;
import com.example.finalyearproject.Modules.InstUser;
import com.example.finalyearproject.Modules.User;
import com.example.finalyearproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;

public class RegisterForInstitutionActivity extends AppCompatActivity {

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
                    checkInstitutionExists();
                }
            }
        });

    }

    private Boolean saveDetails() {
        mInstCode = mEtInstCode.getText().toString().trim();
        if(mInstCode.length()<0){
            mEtInstCode.setError("Code is Required");
            mEtInstCode.requestFocus();
            return false;
        }

        return true;
    }

    private void checkInstitutionExists() {
        FirebaseUtils.FIRESTORE.collection(FirebaseUtils.INSTITUTIONS).document(mInstCode)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                    if(task.getResult().exists()){
                        mInstUser.setUserId(mFirebaseAuth.getUid());
                        mInstUser.setEmail(mFirebaseAuth.getCurrentUser().getEmail());
                        mUser.setId(mFirebaseAuth.getUid());
                        mUser.addInstitution(mInstCode);
                        FirebaseUtils.saveInstUserDetails(mInstCode,mInstUser);
                        FirebaseUtils.saveUserDetails(mUser);
                        Intent lIntent=new Intent(RegisterForInstitutionActivity.this, MainActivity.class);
                        lIntent.putExtra("InstitutionCode", mInstCode);
                        startActivity(lIntent);
                    }else {
                        Toast.makeText(RegisterForInstitutionActivity.this,"Institution code does not exit",Toast.LENGTH_LONG).show();
                        return ;
                    }

                }

        });
    }


}