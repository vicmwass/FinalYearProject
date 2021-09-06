package com.example.finalyearproject.Activities.Launch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.finalyearproject.Activities.Main.MainActivity;
import com.example.finalyearproject.HelperClasses.FirebaseUtils;
import com.example.finalyearproject.Modules.InstUser;
import com.example.finalyearproject.Modules.Institution;
import com.example.finalyearproject.Modules.User;
import com.example.finalyearproject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.finalyearproject.Activities.Launch.LaunchActivity.INSTITUTION_DETAILS;
import static com.example.finalyearproject.HelperClasses.FirebaseUtils.FIRESTORE;
import static com.example.finalyearproject.HelperClasses.FirebaseUtils.INSTITUTIONS;

public class RegisterInstitutionActivity extends AppCompatActivity {

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
                    Intent lIntent=new Intent(RegisterInstitutionActivity.this, MainActivity.class);
                    lIntent.putExtra(INSTITUTION_DETAILS, (Parcelable) mInst);
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
        mInst.setAdminList(new ArrayList<String>(Arrays.asList(mFirebaseAuth.getUid())));
        mInst.setUsers(new ArrayList<String>(Arrays.asList(mFirebaseAuth.getUid())));
        generateCode(lInstName);
        FirebaseUtils.saveInstitution(this,mInst);
//        mInstUser.setUserId(mFirebaseAuth.getUid());
//        mInstUser.setEmail(mFirebaseAuth.getCurrentUser().getEmail());
        mUser.setId(mFirebaseAuth.getUid());
        mUser.addInstitution(mInst.getCode());
        mUser.setUsername(mFirebaseAuth.getCurrentUser().getDisplayName());
        mUser.setEmail(mFirebaseAuth.getCurrentUser().getEmail());



//        FirebaseUtils.addUserToInst(mInst.getCode(),mFirebaseAuth.getUid());
//        FirebaseUtils.saveInstUserDetails(mInst.getCode(),mInstUser);
        FirebaseUtils.saveUserDetails(mUser);
        return true;
    }

    private void generateCode(String name) {
        String words[] = name.split(" ");
        int rand =(int)Math.round((Math.random()*((9000-1000)+1))+9000);
        mInst.setCode(words[0]+rand);
    }

    private void saveInstitution(){
        final DocumentReference institutionRef= FIRESTORE.collection(INSTITUTIONS).document(mInst.getCode());
        institutionRef.set(mInst)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(RegisterInstitutionActivity.this,"Added successful",Toast.LENGTH_LONG).show();
//                        Log.d("Firestore", "Document updated with ID: " + PatientPostRef.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterInstitutionActivity.this,"Failed to add",Toast.LENGTH_LONG).show();
//                        Log.e("Firestore", "Error updating document", e);
                    }
                });
    }
}