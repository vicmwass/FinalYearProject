package com.example.finalyearproject.Activities.Launch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.finalyearproject.Activities.Main.MainActivity;
import com.example.finalyearproject.HelperClasses.FirebaseUtils;
import com.example.finalyearproject.Modules.InstUser;
import com.example.finalyearproject.Modules.Institution;
import com.example.finalyearproject.Modules.NavObjects;
import com.example.finalyearproject.Modules.User;
import com.example.finalyearproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

import static com.example.finalyearproject.Activities.Launch.LaunchActivity.INSTITUTION_CODE;
import static com.example.finalyearproject.Activities.Launch.LaunchActivity.INSTITUTION_DETAILS;
import static com.example.finalyearproject.Activities.Main.MainActivity.NAV_OBJECT;

public class RegisterForInstitutionActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private EditText mEtInstCode;
    private User mUser;
    private FirebaseAuth mFirebaseAuth;
    private InstUser mInstUser;
    private String mInstCode;
    private NavObjects mNavObjects;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_for_institution);

        mFirebaseAuth = FirebaseAuth.getInstance();
        Intent lIntent=getIntent();
        mNavObjects = (NavObjects) lIntent.getParcelableExtra(NAV_OBJECT);
        initializeViews();
        if(mNavObjects!=null){
            setupNavigatioView();
        }else hideNavigationView();

        mUser = new User();
        mInstUser = new InstUser();



    }

    private void initializeViews() {
        mEtInstCode = findViewById(R.id.et_institution_code);
        Button lButton=findViewById(R.id.btn_save_reg_for_inst);
        lButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(saveDetails()){
                    checkInstitutionExists();
                }
            }
        });
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if(mNavObjects!=null){
            getSupportActionBar().setTitle(mNavObjects.getInstDetails().getName());
            getSupportActionBar().setSubtitle(mNavObjects.getDomainName());
        }

        mDrawerLayout =findViewById(R.id.drawer_layout);
        mNavigationView =findViewById(R.id.nav_view);
    }

    private void hideNavigationView() {
        mToolbar.setVisibility(View.GONE);
        mDrawerLayout.removeView(mNavigationView);
//        mNavigationView.setVisibility(View.INVISIBLE);
    }

    private void setupNavigatioView() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        mDrawerLayout =findViewById(R.id.drawer_layout);
        mNavigationView =findViewById(R.id.nav_view);


        mNavigationView.bringToFront();//when navdrawer items clicked show that color to represent click
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout,mToolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //to make navigation drawer clickable
        mNavigationView.setNavigationItemSelectedListener(this);

        mNavigationView.setCheckedItem(R.id.choose_institution);

        if(mNavObjects.getIsAdmin()){
            mNavigationView.getMenu().setGroupVisible(R.id.nav_for_admin,true);
        }else {
            mNavigationView.getMenu().setGroupVisible(R.id.nav_for_admin,false);
        }
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
                        Institution selectedInst=task.getResult().toObject(Institution.class);
//                        mInstUser.setUserId(mFirebaseAuth.getUid());
//                        mInstUser.setEmail(mFirebaseAuth.getCurrentUser().getEmail());
                        mUser.setId(mFirebaseAuth.getUid());
                        mUser.addInstitution(mInstCode);
                        mUser.setUsername(mFirebaseAuth.getCurrentUser().getDisplayName());
                        mUser.setEmail(mFirebaseAuth.getCurrentUser().getEmail());
//                        FirebaseUtils.saveInstUserDetails(mInstCode,mInstUser);
                        FirebaseUtils.saveUserDetails(mUser);
                        FirebaseUtils.addUserToInst(mInstCode,mUser.getId());
                        Intent lIntent=new Intent(RegisterForInstitutionActivity.this, MainActivity.class);
                        lIntent.putExtra(INSTITUTION_DETAILS, (Parcelable) selectedInst);
                        startActivity(lIntent);
                    }else {
                        Toast.makeText(RegisterForInstitutionActivity.this,"Institution code does not exit",Toast.LENGTH_LONG).show();
                        return ;
                    }

                }

        });
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        MainActivity.navigationSwitch(this,item,mNavObjects, mDrawerLayout);
        return true;
    }
}