package com.example.finalyearproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.ArrayList;

import static com.example.finalyearproject.LaunchActivity.INSTITUTION_CODE;
import static com.example.finalyearproject.LaunchActivity.INSTITUTION_DETAILS;
import static com.example.finalyearproject.MainActivity.DNAME;


public class AddAdmin extends AppCompatActivity {

    private EditText mEtAdminName1;
    private ArrayList<String> mIdList;
    private Toolbar mToolbar;
    private String mInstCode;
    private String mDomainName;
    private String mAdminName;
    private AddAdminViewModel mViewModel;
    private ArrayList<String> mAdminList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_admin);
        initializeViews();
        getIntentExtras();
        mViewModel = new ViewModelProvider(this).get(AddAdminViewModel.class);
        mViewModel.setCurrentAdminNameSet(mAdminList);
//        mViewModel.setCurrentAdminNameList(mAdminList);

        setupAdapter();



    }

    private void getIntentExtras() {
        Intent lIntent=getIntent();
        mIdList = lIntent.getStringArrayListExtra(MainActivity.IDLIST);
        mAdminList = lIntent.getStringArrayListExtra(MainActivity.CURRENT_ADMIN_LIST);
        mInstCode = lIntent.getStringExtra(INSTITUTION_CODE);
        mDomainName=lIntent.getStringExtra(DNAME);
    }

    private void setupAdapter() {
        AdminAdapter lAdminAdapter = new AdminAdapter(mInstCode,mViewModel);
        RecyclerView adminRecycleView = findViewById(R.id.admin_recycler_view);
        adminRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adminRecycleView.setAdapter(lAdminAdapter);
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
        mEtAdminName1 = findViewById(R.id.et_admin_name);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.app_name);

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
                    ArrayList<String> adminList=mViewModel.getAdminNameList().getValue();
                    FirebaseUtils.addDomainAdmin(this,mInstCode,mIdList,adminList);


        }
        return super.onOptionsItemSelected(item);
    }
}