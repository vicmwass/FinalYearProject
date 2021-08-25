package com.example.finalyearproject.Activities.ChooseIntitution;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.finalyearproject.Activities.Main.MainActivity;
import com.example.finalyearproject.HelperClasses.FirebaseUtils;
import com.example.finalyearproject.Modules.Institution;
import com.example.finalyearproject.Modules.NavObjects;
import com.example.finalyearproject.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

import static com.example.finalyearproject.Activities.Launch.LaunchActivity.INSTITUTION_DETAILS;
import static com.example.finalyearproject.Activities.Main.MainActivity.DNAME;
import static com.example.finalyearproject.Activities.Main.MainActivity.IDLIST;

import static com.example.finalyearproject.Activities.Main.MainActivity.INSTITUTION_LIST;
import static com.example.finalyearproject.Activities.Main.MainActivity.NAV_OBJECT;

public class ChooseInstitutionActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private InstitutionAdapter mInstitutionAdapter;
    private RecyclerView mInstRecycleView;
    private ArrayList<String> mInstList;
    ArrayList<String> mInstNameList=new ArrayList<String>();
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private ArrayList<String> mIdList;
    private String mDomainName;
    private String mInstCode;
    private Intent mIntent;
    private Institution mInstDetails;
    private NavObjects mNavObjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_institution);

        mIntent = getIntent();
        mInstList = mIntent.getStringArrayListExtra(INSTITUTION_LIST);

        initializeViews();
        extrasForNavPurpose();

        if(mInstCode!=null){
            setupNavigatioView();
        }else hideNavigationView();
//        setupAdapter();

        getInstNames();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mInstCode!=null) mNavigationView.setCheckedItem(R.id.choose_institution);
    }

    private void extrasForNavPurpose() {
        mNavObjects = (NavObjects) mIntent.getParcelableExtra(NAV_OBJECT);
        if(mNavObjects!=null){
            mInstCode = mNavObjects.getInstDetails().getCode();
        }
    }

    private void initializeViews() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        if(mNavObjects!=null){
            getSupportActionBar().setTitle(mInstDetails.getName());
            getSupportActionBar().setSubtitle(mDomainName);
        }

        mDrawerLayout =findViewById(R.id.drawer_layout);
        mNavigationView =findViewById(R.id.nav_view);
        mInstNameList=new ArrayList<String>(Collections.nCopies(mInstList.size(), ""));
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

    private void setupAdapter() {
        mInstitutionAdapter = new InstitutionAdapter(this, mInstList,mInstNameList);
        mInstRecycleView = findViewById(R.id.inst_recycler_view);
        mInstRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mInstRecycleView.setAdapter(mInstitutionAdapter);
    }

    private void getInstNames() {
             FirebaseUtils.FIRESTORE.collection(FirebaseUtils.INSTITUTIONS).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.e("SnapshotListener", error.getMessage());
                        return;
                    }
                    if(value.getDocumentChanges().size()>0) {

                        for (DocumentChange dc : value.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    String id = dc.getDocument().getId();
                                    Institution lInst= dc.getDocument().toObject(Institution.class);
                                    if(mInstList.contains(lInst.getCode())){
                                        int index=mInstList.indexOf(lInst.getCode());
                                        mInstNameList.set(index,lInst.getName());
                                    }
                                    break;
                                case MODIFIED:
                                    break;
                                case REMOVED:
                                    break;
                            }
                        }
                        setupAdapter();
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
