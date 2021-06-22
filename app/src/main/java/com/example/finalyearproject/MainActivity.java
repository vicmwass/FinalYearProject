 package com.example.finalyearproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;

import static com.example.finalyearproject.LaunchActivity.INSTITUTION_CODE;
import static com.example.finalyearproject.LaunchActivity.INSTITUTION_DETAILS;

 public class  MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    public static final String IDLIST="idList";
    public static final String DNAME="domainN";
    public static final String NOTICE="notice";

    public static final String INSTITUTION_LIST = "institutionList";
    public static final String SHARED_PREFS = "SharedPrefs";
     public static final String CURRENT_ADMIN_LIST = "currentAdminList";
     public SharedViewModel mViewModel;
    ArrayList<String> mIdList;
    ArrayList<String> mDomainNameList=new ArrayList<String>();
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private ViewPageAdapter mViewPageAdapter;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private String mInstCode;
    private Boolean mFinalExit =false;
    private Institution mInstDetails;
    private Menu mOptionMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Intent lIntent=getIntent();
        mInstDetails = (Institution) lIntent.getSerializableExtra(INSTITUTION_DETAILS);
        mInstCode = mInstDetails.getCode();
        setupViewModel();
        setupNavigatioView();
        setupAdapter();
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.domains_frame, new DomainListFragment())
//                .add(R.id.notices_frame, new NoticeListFragment())
//                .commit();
    }

     @Override
     protected void onResume() {
         super.onResume();
         mNavigationView.setCheckedItem(R.id.nav_home);
         mFinalExit =false;
     }

     private void setupAdapter() {
        mViewPager = findViewById(R.id.pager);
        mTabLayout = findViewById(R.id.tab_layout);
        mViewPageAdapter = new ViewPageAdapter(getSupportFragmentManager(),0);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setAdapter(mViewPageAdapter);
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

        //to lIntentmake navigation drawer clickable
        mNavigationView.setNavigationItemSelectedListener(this);


    }

    private void setupViewModel() {
        mViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        mViewModel.setInstCode(mInstCode);
        mViewModel.getIdList().observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(@Nullable ArrayList<String> idList) {
                mIdList=idList;
            }
        });
        mViewModel.getDomainNameList().observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> domainNameList) {
                mDomainNameList=domainNameList;
            }
        });
        String userId=FirebaseUtils.sFirebaseAuth.getUid();
        if(userId.equals(mInstDetails.getCreator())){
            mViewModel.setDomainAdminList(new ArrayList<String>(Arrays.asList(mInstDetails.getCreator())));
        }
        if(userId.equals(mInstDetails.getCreator())|| mInstDetails.getAdminList().contains(FirebaseUtils.sFirebaseAuth.getUid())){
            mViewModel.setAdminLevel("Main");
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu,menu);
        mOptionMenu = menu;
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mViewModel.getAdminLevel().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if(s.equals("Main")||mIdList.contains(s)){
                    menu.setGroupVisible(R.id.for_admin, true);
                }else{
                    menu.setGroupVisible(R.id.for_admin, false);
                }
            }
        });


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.to_new_domains:
                Intent DIntent=new Intent(this, AddDomain.class);
                DIntent.putExtra(IDLIST, mIdList);
                DIntent.putExtra(INSTITUTION_DETAILS, mInstDetails);
                startActivity(DIntent);
                break;
            case R.id.to_new_notice:
                Intent NIntent =new Intent(this,AddNotice.class);
                NIntent.putExtra(IDLIST, mIdList);
                NIntent.putExtra(DNAME, mDomainNameList.get(mDomainNameList.size()-1));
                NIntent.putExtra(INSTITUTION_DETAILS, mInstDetails);
                startActivity(NIntent);
                break;
            case R.id.add_admin:
                Intent AIntent =new Intent(this,AddAdmin.class);
                AIntent.putExtra(IDLIST, mIdList);
                AIntent.putExtra(DNAME, mDomainNameList.get(mDomainNameList.size()-1));
                AIntent.putExtra(CURRENT_ADMIN_LIST,mViewModel.getCurrentAdminList().getValue());
                AIntent.putExtra(INSTITUTION_CODE, mInstCode);
                startActivity(AIntent);
                break;
            case R.id.sign_out:
                FirebaseAuth.getInstance().signOut();
                clearPrefData();
                Intent OIntent=new Intent(this,LaunchActivity.class);
                OIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(OIntent);
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onBackPressed() {
        int sz = mIdList.size();

        if(sz>0){
            mFinalExit =false;
            mViewModel.removePreviousAdmins();
            mIdList.remove(sz-1);
            mDomainNameList.remove(sz);
            mViewModel.setIdList(mIdList);

        }else{
            if(mFinalExit){
                savePrefData();
                super.onBackPressed();
                finishAffinity();
            }else {
                mFinalExit =true;
                Toast.makeText(this,"press again to exit",Toast.LENGTH_LONG).show();
            }

        }


    }

    private void savePrefData() {
        SharedPreferences lSharedPreferences=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor lEditor=lSharedPreferences.edit();
        lEditor.putString(INSTITUTION_CODE,mInstCode);
        lEditor.apply();
    }
    private void clearPrefData(){
        SharedPreferences lSharedPreferences=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor lEditor=lSharedPreferences.edit();
        lEditor.remove(INSTITUTION_CODE).commit();


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        navigationSwitch(this,item, mIdList, mInstDetails, mDrawerLayout,  mDomainNameList.get(mDomainNameList.size()-1));
        return true;
    }

    public static void navigationSwitch(Activity activity, @NotNull MenuItem item, ArrayList<String> idList, Institution instDetails, DrawerLayout drawerLayout, String domainName) {
        switch (item.getItemId()){
            case R.id.nav_home:
                Intent i = new Intent(activity, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(i);
//                Intent lIntent=new Intent(activity, MainActivity.class);
//                lIntent.putExtra(IDLIST, idList);
//                lIntent.putExtra(INSTITUTION_DETAILS, instDetails);
//                activity.startActivity(lIntent);
//                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.to_new_domains:
                Intent DIntent=new Intent(activity, AddDomain.class);
                DIntent.putExtra(IDLIST, idList);
                DIntent.putExtra(INSTITUTION_DETAILS, instDetails);
                DIntent.putExtra(DNAME, domainName);
                activity.startActivity(DIntent);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.to_new_notice:
                Intent NIntent =new Intent(activity,AddNotice.class);
                NIntent.putExtra(IDLIST, idList);
                NIntent.putExtra(DNAME, domainName);
                NIntent.putExtra(INSTITUTION_DETAILS, instDetails);
                activity.startActivity(NIntent);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.choose_institution:
                chooseInstitution(activity, idList, instDetails, drawerLayout, domainName);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

        }
    }

    public static void chooseInstitution(Activity activity,ArrayList<String> idList, Institution instDetails, DrawerLayout drawerLayout, String domainName) {
        FirebaseUtils.FIRESTORE.collection(FirebaseUtils.USERS)
                .document(FirebaseUtils.sFirebaseAuth.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot lSnapshot=task.getResult();
                            User lUser=lSnapshot.toObject(User.class).withId(lSnapshot.getId());
                            ArrayList<String> lInst=lUser.getInstitutions();
                            Intent lIntent=new Intent(activity,ChooseInstitution.class);
                            lIntent.putExtra(IDLIST, idList);
                            lIntent.putExtra(DNAME, domainName);
                            lIntent.putExtra(INSTITUTION_DETAILS, instDetails);
                            lIntent.putExtra(INSTITUTION_LIST,lInst);
                            activity.startActivity(lIntent);

                        }

                    }
                });

    }
}