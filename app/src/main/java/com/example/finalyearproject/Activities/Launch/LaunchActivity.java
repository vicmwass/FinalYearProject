package com.example.finalyearproject.Activities.Launch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.finalyearproject.Activities.Main.MainActivity;
import com.example.finalyearproject.Activities.ChooseIntitution.ChooseInstitutionActivity;
import com.example.finalyearproject.HelperClasses.FirebaseUtils;
import com.example.finalyearproject.Modules.Institution;
import com.example.finalyearproject.Modules.User;
import com.example.finalyearproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.ArrayList;

import static com.example.finalyearproject.Activities.Main.MainActivity.INSTITUTION_LIST;
import static com.example.finalyearproject.Activities.Main.MainActivity.SHARED_PREFS;

public class LaunchActivity extends AppCompatActivity implements FirebaseAuth.AuthStateListener  {

    public static final int NORMAL_SIGN_IN = 3397;
    public static final int CREATE_INST_SIGN_IN = 3398;
    public static final int REGISTER_FOR_INST_SIGN_IN = 3399;
    public static final int LOAD_INST_SIGN_IN = 3400;
    public static final String INSTITUTION_DETAILS = "InstitutionDetails";
    public static final String INSTITUTION_CODE = "institutionCode";
    private RelativeLayout mRelCreate;
    private RelativeLayout mRelReg;
    private RelativeLayout mRelLoad;
    ProgressBar progressBar;
    private Toolbar mToolbar;
    public  FirebaseAuth.AuthStateListener mAuthStateListener;
    public  FirebaseAuth mFirebaseAuth;
    private String mInstCode;
    private Menu mOptionMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseUtils.openFirebaseReference(this);
        mFirebaseAuth = FirebaseAuth.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        initializeViews();
//        if (mFirebaseAuth.getCurrentUser() != null) {
//            mRelLoad.setVisibility(View.VISIBLE);
//        }else{
//            mRelLoad.setVisibility(View.GONE);
//        }



    }

    private void initializeViews() {
        mRelCreate = findViewById(R.id.create_inst);
        mRelReg = findViewById(R.id.reg_for_inst);
        mRelLoad = findViewById(R.id.load_inst);
        mToolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progressBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.app_name);
        mRelCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFirebaseAuth.getCurrentUser() == null) {
                    FirebaseUtils.signIn(CREATE_INST_SIGN_IN);
                }else{
                    Intent lIntent=new Intent(LaunchActivity.this, RegisterInstitutionActivity.class);
                    startActivity(lIntent);
                }

            }
        });
        mRelReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFirebaseAuth.getCurrentUser() == null) {
                    FirebaseUtils.signIn(REGISTER_FOR_INST_SIGN_IN);
                }else {
                    Intent lIntent = new Intent(LaunchActivity.this, RegisterForInstitutionActivity.class);
                    startActivity(lIntent);
                }
            }
        });
        mRelLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFirebaseAuth.getCurrentUser() == null) {
                    FirebaseUtils.signIn(LOAD_INST_SIGN_IN);
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    getInstitutions();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode== NORMAL_SIGN_IN &&resultCode==RESULT_OK){
//            mRelLoad.setVisibility(View.VISIBLE);
        }
        if(requestCode== CREATE_INST_SIGN_IN &&resultCode==RESULT_OK){
            Intent lIntent=new Intent(LaunchActivity.this, RegisterInstitutionActivity.class);
            startActivity(lIntent);
        }
        if(requestCode== REGISTER_FOR_INST_SIGN_IN &&resultCode==RESULT_OK){
            Intent lIntent = new Intent(LaunchActivity.this, RegisterForInstitutionActivity.class);
            startActivity(lIntent);
        }
        if(requestCode== LOAD_INST_SIGN_IN &&resultCode==RESULT_OK){
            progressBar.setVisibility(View.VISIBLE);
            getInstitutions();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.launcher_menu,menu);
        mOptionMenu = menu;
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem lMenuItemIn=mOptionMenu.findItem(R.id.launch_sign_in);
        MenuItem lMenuItemOut=mOptionMenu.findItem(R.id.launch_sign_out);
        if (mFirebaseAuth.getCurrentUser() != null) {
            lMenuItemIn.setVisible(false);
            lMenuItemOut.setVisible(true);
        }else{
            lMenuItemOut.setVisible(false);
            lMenuItemIn.setVisible(true);
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(this);
    }

    @Override
    public void onAuthStateChanged(@NonNull @NotNull FirebaseAuth firebaseAuth) {
        if (mFirebaseAuth.getCurrentUser() != null) {
            if(checkSavedInstitution()){
                getInstDetails();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.launch_sign_in:
                    FirebaseUtils.signIn(NORMAL_SIGN_IN);
                    invalidateOptionsMenu();
                break;
            case R.id.launch_sign_out:
                FirebaseUtils.signOut();
                invalidateOptionsMenu();
                break;
        }
        return true;
    }

    private boolean checkSavedInstitution() {
        SharedPreferences lSharedPreferences=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        mInstCode = lSharedPreferences.getString(INSTITUTION_CODE,"");
        if(mInstCode.equals("")){
            return false;
        }
        return true;
    }

    private void getInstDetails() {
        FirebaseUtils.FIRESTORE.collection(FirebaseUtils.INSTITUTIONS).document(mInstCode)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Institution selectedInst=task.getResult().toObject(Institution.class);
                    Intent lIntent=new Intent(LaunchActivity.this, MainActivity.class);
                    lIntent.putExtra(INSTITUTION_DETAILS, (Parcelable) selectedInst);
                    lIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(lIntent);
                    finish();
                }
            }
        });
    }

    private void getInstitutions() {
            FirebaseUtils.FIRESTORE.collection(FirebaseUtils.USERS)
                    .document(FirebaseUtils.sFirebaseAuth.getUid()).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot lSnapshot=task.getResult();
                                try{
                                    User lUser=lSnapshot.toObject(User.class).withId(lSnapshot.getId());
                                    ArrayList<String> lInst=lUser.getInstitutions();
                                    progressBar.setVisibility(View.GONE);
                                    Intent lIntent=new Intent(LaunchActivity.this, ChooseInstitutionActivity.class);
                                    lIntent.putExtra(INSTITUTION_LIST,lInst);
                                    startActivity(lIntent);
                                }catch (NullPointerException e){
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(LaunchActivity.this,"You are not registered to any domain",Toast.LENGTH_LONG).show();
                                    return;
                                }


                            }

                        }
                    });

    }
}