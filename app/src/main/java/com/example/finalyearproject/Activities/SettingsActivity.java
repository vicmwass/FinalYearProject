package com.example.finalyearproject.Activities;

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
import android.widget.RelativeLayout;

import com.example.finalyearproject.Activities.AddAdmin.AddAdminActivity;
import com.example.finalyearproject.Activities.AddDomain.AddDomainActivity;
import com.example.finalyearproject.Activities.AddUsersToPrivateDomain.AddUsersToPrivateDomainActivity;
import com.example.finalyearproject.Activities.ChooseIntitution.ChooseInstitutionActivity;
import com.example.finalyearproject.Activities.Launch.RegisterForInstitutionActivity;
import com.example.finalyearproject.Activities.Main.MainActivity;
import com.example.finalyearproject.Activities.Profile.ProfileActivity;
import com.example.finalyearproject.Activities.RemoveUsersFromPrivateDomain.RemoveUserFromPrivateDomainActivity;
import com.example.finalyearproject.HelperClasses.FirebaseUtils;
import com.example.finalyearproject.Modules.Domain;
import com.example.finalyearproject.Modules.NavObjects;
import com.example.finalyearproject.Modules.User;
import com.example.finalyearproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.example.finalyearproject.Activities.Main.MainActivity.INSTITUTION_LIST;
import static com.example.finalyearproject.Activities.Main.MainActivity.NAV_OBJECT;

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private ArrayList<String> mIdList;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private String mInstCode;
    private NavObjects mNavObjects;
    private CollectionReference mDomainsRef;
    private int mPrivacyLevel;
    private boolean mIsAdmin;
    private RelativeLayout mProfileRl;
    private RelativeLayout mAdminRl;
    private RelativeLayout mDomainRl;
    private RelativeLayout mMemberRl;
    private RelativeLayout mRegInstRl;
    private RelativeLayout mChangeInstRl;
    private RelativeLayout mRmMemberRl;
    private RelativeLayout mSendNotice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.changeTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getIntentExtras();
        initializeViews();
        setupNavigatioView();
    }


    private void initializeViews() {
        mProfileRl = findViewById(R.id.profile_rl);
        mAdminRl = findViewById(R.id.add_admin_rl);
        mSendNotice = findViewById(R.id.send_notice_rl);
        mDomainRl = findViewById(R.id.add_domain_rl);
        mMemberRl = findViewById(R.id.add_members_rl);
        mRegInstRl = findViewById(R.id.reg_inst_rl);
        mChangeInstRl = findViewById(R.id.change_inst_rl);
        mRmMemberRl = findViewById(R.id.remove_members_rl);
        if(!mIsAdmin){
            mAdminRl.setVisibility(View.GONE);
            mSendNotice.setVisibility(View.GONE);
            mDomainRl.setVisibility(View.GONE);
            mMemberRl.setVisibility(View.GONE);
            mRmMemberRl.setVisibility(View.GONE);
        }
        if(mIsAdmin){
            if(mPrivacyLevel>0){
            mMemberRl.setVisibility(View.VISIBLE);
            mRmMemberRl.setVisibility(View.VISIBLE);
            }else {
                mMemberRl.setVisibility(View.GONE);
                mRmMemberRl.setVisibility(View.GONE);
            }

        }


    }

    private void setupViews() {
        mProfileRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent PIntent =new Intent(SettingsActivity.this, ProfileActivity.class);
                startActivity(PIntent);
            }
        });
        mAdminRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent AIntent =new Intent(SettingsActivity.this, AddAdminActivity.class);
                AIntent.putExtra(NAV_OBJECT,(Parcelable) mNavObjects);
                startActivity(AIntent);
            }
        });
        mSendNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent AIntent =new Intent(SettingsActivity.this, AddNoticeActivity.class);
                AIntent.putExtra(NAV_OBJECT,(Parcelable) mNavObjects);
                startActivity(AIntent);
            }
        });
        mDomainRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent DIntent=new Intent(SettingsActivity.this, AddDomainActivity.class);
                DIntent.putExtra(NAV_OBJECT,(Parcelable) mNavObjects);
                startActivity(DIntent);
            }
        });
        mMemberRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent MeIntent =new Intent(SettingsActivity.this, AddUsersToPrivateDomainActivity.class);
                MeIntent.putExtra(NAV_OBJECT,(Parcelable) mNavObjects);
                startActivity(MeIntent);
            }
        });
        mRegInstRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent RIntent =new Intent(SettingsActivity.this, RegisterForInstitutionActivity.class);
                RIntent.putExtra(NAV_OBJECT,(Parcelable) mNavObjects);
                startActivity(RIntent);
            }
        });
        mChangeInstRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseInstitution();
//                drawerLayout.closeDrawer(GravityCompat.START);
            }
        });
        mRmMemberRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ReIntent = new Intent(SettingsActivity.this, RemoveUserFromPrivateDomainActivity.class);
                ReIntent.putExtra(NAV_OBJECT,(Parcelable) mNavObjects);
                startActivity(ReIntent);
            }
        });
    }

    public void chooseInstitution() {
        FirebaseUtils.FIRESTORE.collection(FirebaseUtils.USERS)
                .document(FirebaseAuth.getInstance().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot lSnapshot=task.getResult();
                            User lUser=lSnapshot.toObject(User.class).withId(lSnapshot.getId());
                            ArrayList<String> lInst=lUser.getInstitutions();
                            Intent lIntent=new Intent(SettingsActivity.this, ChooseInstitutionActivity.class);
                            lIntent.putExtra(NAV_OBJECT,(Parcelable) mNavObjects);
                            lIntent.putExtra(INSTITUTION_LIST,lInst);
                            startActivity(lIntent);
                        }
                    }
                });
    }

    private void getIntentExtras() {
        Intent lIntent=getIntent();
        mNavObjects = (NavObjects) lIntent.getParcelableExtra(NAV_OBJECT);
        mIdList = mNavObjects.getIdList();
        mInstCode = mNavObjects.getInstDetails().getCode();
        mPrivacyLevel=mNavObjects.getPrivacyLevel();
        mIsAdmin=mNavObjects.getIsAdmin();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDomainDetails();
    }

    private void getDomainDetails() {
        mDomainsRef = FirebaseUtils.FIRESTORE.collection("Institutions").document(mInstCode).collection("domains");
        while (mPrivacyLevel>1){
            mIdList.remove(mIdList.size()-1);
            mPrivacyLevel-=1;
        }
        for(String Id:mIdList){
            mDomainsRef = mDomainsRef.document(Id).collection("domains");
        }
        mDomainsRef.getParent()
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String id =task.getResult().getId();
                    Domain lDomain=task.getResult().toObject(Domain.class).withId(id);
                    mNavObjects.setCurrentAdminList(lDomain.getAdminList());
                    mNavObjects.setMemberList(lDomain.getMemberList());
                    setupViews();
//                    mMemberList=lDomain.getMemberList();
//                    setupViewModel();
                }
            }
        });
    }
    private void setupNavigatioView() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Settings");

        mDrawerLayout =findViewById(R.id.drawer_layout);
        mNavigationView =findViewById(R.id.nav_view);


        mNavigationView.bringToFront();//when navdrawer items clicked show that color to represent click
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout,mToolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //to make navigation drawer clickable
        mNavigationView.setNavigationItemSelectedListener(this);

        mNavigationView.setCheckedItem(R.id.settings);

        if(mNavObjects.getIsAdmin()){
//            mNavigationView.getMenu().setGroupVisible(R.id.nav_for_admin,true);
        }else {
//            mNavigationView.getMenu().setGroupVisible(R.id.nav_for_admin,false);
        }
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        MainActivity.navigationSwitch(this,item,mNavObjects, mDrawerLayout);
        return true;
    }
}