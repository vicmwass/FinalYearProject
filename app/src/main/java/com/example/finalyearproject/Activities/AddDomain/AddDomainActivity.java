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
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.finalyearproject.Activities.AddAdmin.AdminAdapter;
import com.example.finalyearproject.Activities.Main.MainActivity;
import com.example.finalyearproject.HelperClasses.FirebaseUtils;
import com.example.finalyearproject.Modules.Domain;
import com.example.finalyearproject.Modules.NavObjects;
import com.example.finalyearproject.R;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.example.finalyearproject.Activities.Main.MainActivity.NAV_OBJECT;


public class AddDomainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private EditText mEtDomainName1;
    private ArrayList<String> mIdList;
    private Toolbar mToolbar;
    private Domain mDomain;
    private String mInstCode;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private String mDomainName;

    private ArrayList<String> mAdminList;
    private ArrayList<String> mMemberList;
    private AddDomainViewModel mViewModel;
    private boolean isPrivate=false;
    private AdminAdapter mAdminAdapter;
    private NavObjects mNavObjects;
    private RadioGroup mGroupCategory;
    private RadioGroup mRadioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_domain);

        getIntentExtras();
        mViewModel = new ViewModelProvider(this).get(AddDomainViewModel.class);
        mViewModel.setCurrentAdminSet(mAdminList);

        if(mMemberList.size()>0){
            mViewModel.setMembersOfPrivateDomain(mMemberList);
        }

        initializeViews();
        setupNavigatioView();
        mDomain = new Domain();

    }

    private void getIntentExtras() {
        Intent lIntent=getIntent();
        mNavObjects = (NavObjects) lIntent.getParcelableExtra(NAV_OBJECT);
        mIdList = mNavObjects.getIdList();
        mInstCode =  mNavObjects.getInstDetails().getCode();
        mDomainName= mNavObjects.getDomainName();
        mAdminList = mNavObjects.getCurrentAdminList();
        mMemberList = mNavObjects.getMemberList();
    }

    private void initializeViews() {
        setupAdapters();
        mEtDomainName1 = findViewById(R.id.et_domain_name);
        mRadioGroup = findViewById(R.id.categories);
        mToolbar = findViewById(R.id.toolbar);
        mNavigationView =findViewById(R.id.nav_view);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(mNavObjects.getInstDetails().getName());
        getSupportActionBar().setSubtitle(mDomainName);

//        TextView selectAdmin=findViewById(R.id.select_text_admins);
//        selectAdmin.setVisibility(mMemberList.size()<=mAdminList.size()?View.sGONE:View.VISIBLE);
        mDrawerLayout =findViewById(R.id.drawer_layout);
        mNavigationView =findViewById(R.id.nav_view);
        LinearLayout lLinearLayout= findViewById(R.id.choosing_members);
        CheckBox cbIsPrivate=findViewById(R.id.set_isPrivate);
        cbIsPrivate.setVisibility(mMemberList.size()>0?View.GONE:View.VISIBLE);
        cbIsPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cbIsPrivate.isChecked()){
                    isPrivate=true;
                    mViewModel.clearAdminSet();
                    mAdminAdapter.includeMembersOnly(mViewModel);
                }else{
                    isPrivate=false;
                    mViewModel.clearAdminSet();
                    mAdminAdapter.includeAllPotentialAdmins();
                }
                lLinearLayout.setVisibility(isPrivate?View.VISIBLE:View.GONE);
            }
        });

        mGroupCategory = findViewById(R.id.categories);

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

        if(mNavObjects.getIsAdmin()){
            mNavigationView.getMenu().setGroupVisible(R.id.nav_for_admin,true);
        }else {
            mNavigationView.getMenu().setGroupVisible(R.id.nav_for_admin,false);
        }
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

        if(isPrivate){
            if(mViewModel.getMembersIdList().getValue().size()<1){
                Toast.makeText(this,"Must Have At Least One Member",Toast.LENGTH_LONG).show();
                return false;
            }
            if(mViewModel.getAdminIdList().getValue().size()<1){
                Toast.makeText(this,"Must Have At Least One Admin",Toast.LENGTH_LONG).show();
                return false;
            }

            mDomain.setPrivate(true);
            mDomain.setMemberList(mViewModel.getMembersIdList().getValue());
        }
        switch (mRadioGroup.getCheckedRadioButtonId()) {
            case R.id.chatGroup_rb:
                mDomain.setChatGroup(true);
        }
        mDomain.setName(name);
        mDomain.setAdminList(mViewModel.getAdminIdList().getValue());
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_domain:
                if(saveDetails()){
                    FirebaseUtils.saveDomain(mInstCode,mDomain,this,mIdList);
                    onBackPressed();
//                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        MainActivity.navigationSwitch(this,item,mNavObjects, mDrawerLayout);
        return true;
    }


}