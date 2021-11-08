package com.example.finalyearproject.Activities.ChooseIntitution;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

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

import java.util.ArrayList;
import java.util.Collections;

import static com.example.finalyearproject.Activities.Main.MainActivity.INSTITUTION_LIST;
import static com.example.finalyearproject.Activities.Main.MainActivity.NAV_OBJECT;
import static com.example.finalyearproject.Activities.Main.MainActivity.SHARED_PREFS;

public class ChooseInstitutionActivity extends AppCompatActivity{

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
        MainActivity.changeTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_institution);

        mIntent = getIntent();
        mInstList = mIntent.getStringArrayListExtra(INSTITUTION_LIST);

        initializeViews();
        extrasForNavPurpose();
        getInstNames();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
//            getSupportActionBar().setSubtitle(mDomainName);
        }

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mDrawerLayout =findViewById(R.id.drawer_layout);
        mNavigationView =findViewById(R.id.nav_view);
        mInstNameList=new ArrayList<String>(Collections.nCopies(mInstList.size(), ""));
    }

//    private void hideNavigationView() {
//        mToolbar.setVisibility(View.GONE);
//        mDrawerLayout.removeView(mNavigationView);
////        mNavigationView.setVisibility(View.INVISIBLE);
//    }



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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }




}
