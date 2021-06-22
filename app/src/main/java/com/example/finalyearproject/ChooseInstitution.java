package com.example.finalyearproject;

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

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

import static com.example.finalyearproject.LaunchActivity.INSTITUTION_DETAILS;
import static com.example.finalyearproject.MainActivity.DNAME;
import static com.example.finalyearproject.MainActivity.IDLIST;

import static com.example.finalyearproject.MainActivity.INSTITUTION_LIST;

public class ChooseInstitution extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_institution);

        initializeViews();
        mIntent = getIntent();
        mInstList = mIntent.getStringArrayListExtra(INSTITUTION_LIST);
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

        mIdList = mIntent.getStringArrayListExtra(IDLIST);
        mDomainName = mIntent.getStringExtra(DNAME);
        mInstDetails = (Institution) mIntent.getSerializableExtra(INSTITUTION_DETAILS);
        if(mInstDetails!=null)mInstCode = mInstDetails.getCode();
        mInstNameList=new ArrayList<String>(Collections.nCopies(mInstList.size(), ""));
    }

    private void initializeViews() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.app_name);

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
        MainActivity.navigationSwitch(this,item, mIdList, mInstDetails, mDrawerLayout, mDomainName);
        return true;
    }

}
