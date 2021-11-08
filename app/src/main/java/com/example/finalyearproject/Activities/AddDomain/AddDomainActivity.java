package com.example.finalyearproject.Activities.AddDomain;

import androidx.annotation.NonNull;
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
import com.google.firebase.firestore.CollectionReference;

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
    private CollectionReference mDomainsRef;
    private int mPrivacyLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.changeTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_domain);

        getIntentExtras();


        initializeViews();
        mDomain = new Domain();

    }

    private void setupViewModel() {
        mViewModel = new ViewModelProvider(this).get(AddDomainViewModel.class);
        mViewModel.setCurrentAdminSet(mAdminList);

        if(mMemberList.size()>0){
            mViewModel.setMembersOfPrivateDomain(mMemberList);
        }
        setupAdapters();
    }

    private void getIntentExtras() {
        Intent lIntent=getIntent();
        mNavObjects = (NavObjects) lIntent.getParcelableExtra(NAV_OBJECT);
        mIdList = mNavObjects.getIdList();
        mInstCode =  mNavObjects.getInstDetails().getCode();
        mDomainName= mNavObjects.getDomainName();
        mAdminList=mNavObjects.getCurrentAdminList();
        mMemberList=mNavObjects.getMemberList();
        setupViewModel();
//        mPrivacyLevel=mNavObjects.getPrivacyLevel();
//        getMemberList();
    }


    private void initializeViews() {

        mEtDomainName1 = findViewById(R.id.et_domain_name);
        mRadioGroup = findViewById(R.id.categories);
        mToolbar = findViewById(R.id.toolbar);
        mNavigationView =findViewById(R.id.nav_view);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(mDomainName);
        getSupportActionBar().setSubtitle("Add Domain");

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

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
        MembersAdapter lMembersAdapter = new MembersAdapter(this,mInstCode,mViewModel);
        RecyclerView membersRecyclerview=findViewById(R.id.members_recycler_view);
        membersRecyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        membersRecyclerview.setAdapter(lMembersAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mNavigationView.setCheckedItem(R.id.to_new_domains);
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
            case android.R.id.home:
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        MainActivity.navigationSwitch(this,item,mNavObjects, mDrawerLayout);
        return true;
    }


}