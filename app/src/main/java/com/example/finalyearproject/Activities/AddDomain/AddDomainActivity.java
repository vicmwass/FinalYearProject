package com.example.finalyearproject.Activities.AddDomain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.finalyearproject.Activities.AddAdmin.AdminAdapter;
import com.example.finalyearproject.Activities.Main.MainActivity;
import com.example.finalyearproject.HelperClasses.FirebaseUtils;
import com.example.finalyearproject.Modules.Domain;
import com.example.finalyearproject.Modules.Institution;
import com.example.finalyearproject.R;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.example.finalyearproject.Activities.Launch.LaunchActivity.INSTITUTION_DETAILS;
import static com.example.finalyearproject.Activities.Main.MainActivity.DNAME;


public class AddDomainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private EditText mEtDomainName1;
    private ArrayList<String> mIdList;
    private Toolbar mToolbar;
    private Domain mDomain;
    private String mInstCode;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private String mDomainName;
    private Institution mInstDetails;
    private ArrayList<String> mAdminList;
    private ArrayList<String> mMemberList;
    private AddDomainViewModel mViewModel;
    private boolean isPrivate=false;
    private AdminAdapter mAdminAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_domain);

        getIntentExtras();
        mViewModel = new ViewModelProvider(this).get(AddDomainViewModel.class);
        mViewModel.setCurrentAdminNameSet(mAdminList);
        if(mMemberList!=null){
            mViewModel.setMembersOfPrivateDomain(mMemberList);
        }
        initializeViews();
        setupNavigatioView();
        mDomain = new Domain();

    }

    private void getIntentExtras() {
        Intent lIntent=getIntent();
        mIdList = lIntent.getStringArrayListExtra(MainActivity.IDLIST);
        mInstDetails = (Institution) lIntent.getSerializableExtra(INSTITUTION_DETAILS);
        mInstCode = mInstDetails.getCode();
        mDomainName=lIntent.getStringExtra(DNAME);
        mAdminList = lIntent.getStringArrayListExtra(MainActivity.CURRENT_ADMIN_LIST);
        mMemberList = lIntent.getStringArrayListExtra("members");

    }

    private void initializeViews() {
        setupAdapters();
        mEtDomainName1 = findViewById(R.id.et_domain_name);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        TextView selectAdmin=findViewById(R.id.select_text_admins);
//        selectAdmin.setVisibility(mMemberList.size()<=mAdminList.size()?View.sGONE:View.VISIBLE);
        mDrawerLayout =findViewById(R.id.drawer_layout);
        mNavigationView =findViewById(R.id.nav_view);
        LinearLayout lLinearLayout= findViewById(R.id.choosing_members);
        CheckBox cbIsPrivate=findViewById(R.id.set_isPrivate);
        cbIsPrivate.setVisibility(mMemberList!=null?View.GONE:View.VISIBLE);
        cbIsPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cbIsPrivate.isChecked()){
                    isPrivate=true;
                    mAdminAdapter.includeMembersOnly(mViewModel);
                }
                else {
                    mAdminAdapter.includeAllPotentialAdmins();
                    isPrivate=false;
                }
                lLinearLayout.setVisibility(isPrivate?View.VISIBLE:View.GONE);
            }
        });



    }

    private void setupAdapters() {
        mAdminAdapter = new AdminAdapter(this,mInstCode,mViewModel);
        RecyclerView adminRecyclerview=findViewById(R.id.admin_recycler_view2);
        adminRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adminRecyclerview.setAdapter(mAdminAdapter);
        MembersAdapter lMembersAdapter = new MembersAdapter(mInstCode,mViewModel);
        RecyclerView membersRecyclerview=findViewById(R.id.members_recycler_view);
        membersRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        membersRecyclerview.setAdapter(lMembersAdapter);
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
        mDomain.setAdminList(mViewModel.getAdminNameList().getValue());
        if(isPrivate){
            mDomain.setPrivate(true);
            mDomain.setMemberList(mViewModel.getMembersNameList().getValue());
        }
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