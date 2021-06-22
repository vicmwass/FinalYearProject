package com.example.finalyearproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.example.finalyearproject.LaunchActivity.INSTITUTION_DETAILS;
import static com.example.finalyearproject.MainActivity.DNAME;


public class AddDomain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private EditText mEtDomainName1;
    private ArrayList<String> mIdList;
    private Toolbar mToolbar;
    private Domain mDomain;
    private String mInstCode;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private String mDomainName;
    private Institution mInstDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_domain);
        initializeViews();

        Intent lIntent=getIntent();
        mIdList = lIntent.getStringArrayListExtra(MainActivity.IDLIST);
        mInstDetails = (Institution) lIntent.getSerializableExtra(INSTITUTION_DETAILS);
        mInstCode = mInstDetails.getCode();
        mDomainName=lIntent.getStringExtra(DNAME);
        setupNavigatioView();
        mDomain = new Domain();

    }

    private void initializeViews() {
        mEtDomainName1 = findViewById(R.id.et_domain_name);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        mDrawerLayout =findViewById(R.id.drawer_layout);
        mNavigationView =findViewById(R.id.nav_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mNavigationView.setCheckedItem(R.id.to_new_domains);
    }

    private void setupNavigatioView() {



        mNavigationView.bringToFront();//when navdrawer items clicked show that color to represent click
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout,mToolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //to make navigation drawer clickable
        mNavigationView.setNavigationItemSelectedListener(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_domain,menu);
        return true;
    }
    private boolean saveDetails(){
        String name=mEtDomainName1.getText().toString().trim();
        if(name.length() == 0){
            mEtDomainName1.setError("name is Required");
            mEtDomainName1.requestFocus();
            return false;
        }
        mDomain.setName(name);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_domain:
                if(saveDetails()){
                    FirebaseUtils.saveDomain(mInstCode,mDomain,this,mIdList);
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        MainActivity.navigationSwitch(this,item, mIdList, mInstDetails, mDrawerLayout, mDomainName);
        return true;
    }


}