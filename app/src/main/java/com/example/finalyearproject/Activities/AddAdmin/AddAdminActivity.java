package com.example.finalyearproject.Activities.AddAdmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;


import com.example.finalyearproject.Activities.Main.MainActivity;
import com.example.finalyearproject.HelperClasses.FirebaseUtils;
import com.example.finalyearproject.Modules.NavObjects;
import com.example.finalyearproject.R;
import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;

import static com.example.finalyearproject.Activities.Main.MainActivity.NAV_OBJECT;


public class AddAdminActivity extends AppCompatActivity {

    private EditText mEtAdminName1;
    private ArrayList<String> mIdList;
    private Toolbar mToolbar;
    private String mInstCode;
    private String mDomainName;
    private String mAdminName;
    private AddAdminViewModel mViewModel;
    private ArrayList<String> mAdminList;
    private ArrayList<String> mMemberList;
    private NavObjects mNavObjects;
    private AdminAdapter mAdminAdapter;
    private SearchView mSearchView;
    private CollectionReference mDomainsRef;
    private int mPrivacyLevel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.changeTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_admin);
        getIntentExtras();
        initializeViews();



    }

    private void setupViewModel() {
        mViewModel = new ViewModelProvider(this).get(AddAdminViewModel.class);
        mViewModel.setCurrentAdminSet(mAdminList);
        if(mMemberList.size()>0)mViewModel.setMembersOfPrivateDomain(mMemberList);
        setupAdapter();
    }

    private void getIntentExtras() {
        Intent lIntent=getIntent();
        mNavObjects = (NavObjects) lIntent.getParcelableExtra(NAV_OBJECT);
        mIdList = mNavObjects.getIdList();
        mInstCode = mNavObjects.getInstDetails().getCode();
        mDomainName= mNavObjects.getDomainName();
        mAdminList=mNavObjects.getCurrentAdminList();
        mPrivacyLevel=mNavObjects.getPrivacyLevel();
        mMemberList=mNavObjects.getMemberList();
        setupViewModel();
//        getMemberList();

    }

    private void setupAdapter() {
        mAdminAdapter = new AdminAdapter(this,mInstCode,mViewModel);
        RecyclerView adminRecycleView = findViewById(R.id.admin_recycler_view);
        adminRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adminRecycleView.setAdapter(mAdminAdapter);
    }

    private boolean saveDetails(){
        mAdminName = mEtAdminName1.getText().toString().trim();
        if(mAdminName.length() == 0){
            mEtAdminName1.setError("name is Required");
            mEtAdminName1.requestFocus();
            return false;
        }

        return true;
    }


    private void initializeViews() {
//        mEtAdminName1 = findViewById(R.id.et_admin_name);
        mToolbar = findViewById(R.id.toolbar);
        mSearchView = findViewById(R.id.action_search);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(mDomainName);
        getSupportActionBar().setSubtitle("Add Admin");

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        mSearchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdminAdapter.getFilter().filter(newText);
                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_admin_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_admin:
                    ArrayList<String> adminList=mViewModel.getAdminIdList().getValue();
                    FirebaseUtils.addDomainAdmin(this,mInstCode,mIdList,adminList);
                    break;
            case android.R.id.home:
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}