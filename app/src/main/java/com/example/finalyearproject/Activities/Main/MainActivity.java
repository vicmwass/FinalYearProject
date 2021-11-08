 package com.example.finalyearproject.Activities.Main;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.finalyearproject.Activities.AboutInstitution;

import com.example.finalyearproject.Activities.AddNoticeActivity;
import com.example.finalyearproject.Activities.ReportActivity;
import com.example.finalyearproject.Activities.ViewUsers.ListAllUsers.UsersListActivity;
import com.example.finalyearproject.Activities.Launch.LaunchActivity;
import com.example.finalyearproject.Activities.ChooseIntitution.ChooseInstitutionActivity;
import com.example.finalyearproject.Activities.Main.ChatGroup.ChatFragment;
import com.example.finalyearproject.Activities.Main.Notices.NoticeListFragment;
import com.example.finalyearproject.Activities.ViewUsers.ListAdmins.ViewAdminsActivity;
import com.example.finalyearproject.Activities.SettingsActivity;
import com.example.finalyearproject.HelperClasses.FirebaseUtils;
import com.example.finalyearproject.Modules.Institution;
import com.example.finalyearproject.Modules.NavObjects;
import com.example.finalyearproject.Modules.User;
import com.example.finalyearproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;


import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.finalyearproject.Activities.Launch.LaunchActivity.INSTITUTION_CODE;
import static com.example.finalyearproject.Activities.Launch.LaunchActivity.INSTITUTION_DETAILS;
import static com.example.finalyearproject.Activities.SplashScreen.APP_THEME;

 public class  MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    public static final String ID_LIST ="idList";
    public static final String D_NAME ="domainN";
    public static final String NOTICE="notice";

    public static final String INSTITUTION_LIST = "institutionList";
    public static final String SHARED_PREFS = "SharedPrefs";
     public static final String CURRENT_ADMIN_LIST = "currentAdminList";
     public static final String NAV_OBJECT = "navObject";
     public SharedViewModel mViewModel;
    ArrayList<String> mIdList;
    ArrayList<String> mDomainNameList=new ArrayList<String>();
    String mDomainName;
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
    private Boolean mIsAdmin;
    private Boolean mInitial =true;
     private CircleImageView mImageView;
     //     private int mChatLevel=0;


     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         changeTheme(this);
         setContentView(R.layout.activity_main);
         Intent lIntent=getIntent();
         mImageView=findViewById(R.id.logo_main);
         mInstDetails = (Institution) lIntent.getParcelableExtra(INSTITUTION_DETAILS);
         showImage(mInstDetails.getLogoUri());
         mInstCode = mInstDetails.getCode();
         setupViewModel();
        setupNavigationView();
        setupAdapter();
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.domains_frame, new DomainListFragment())
//                .add(R.id.notices_frame, new NoticeListFragment())
//                .commit();
    }

     public static void changeTheme(Activity activity) {
         SharedPreferences lSharedPreferences=activity.getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
         String apptheme = lSharedPreferences.getString(APP_THEME,"");
         if(apptheme!=null){
         switch (apptheme){
             case "blue focused":
                 activity.setTheme(R.style.Theme_FinalYearProject);
                 break;
             case "green focused":
                 activity.setTheme(R.style.Theme_FinalYearProject1);
                 break;
             case "red focused":
                 activity.setTheme(R.style.Theme_FinalYearProject2);
                 break;
             case "yellow focused":
                 activity.setTheme(R.style.Theme_FinalYearProject3);
                 break;
             case "purple focused":
                 activity.setTheme(R.style.Theme_FinalYearProject4);
                 break;
            }
        }
    }

     @Override
     protected void onResume() {
         super.onResume();
         mNavigationView.setCheckedItem(R.id.nav_home);
         mFinalExit =false;
     }

     @Override
     public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
         super.onSaveInstanceState(outState, outPersistentState);
         outState.putString("instCode",mInstCode);
         outState.putParcelable("instDetails",mInstDetails);
     }

     public void showImage(String url){
         if(url!=null&&url.isEmpty()==false){
             int width = Resources.getSystem().getDisplayMetrics().widthPixels;
             Glide.with(this).load(url).override(width*1/2, width*2/3).
                     centerCrop().into(mImageView);
         }
     }

     @Override
     protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
         super.onRestoreInstanceState(savedInstanceState);
         mInstDetails=savedInstanceState.getParcelable("instDetails");
         mInstCode=savedInstanceState.getString("instCode");
     }

     private void setupAdapter() {
        mViewPager = findViewById(R.id.pager);
        mTabLayout = findViewById(R.id.tab_layout);
        mViewPageAdapter = new ViewPageAdapter(getSupportFragmentManager(),0);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setAdapter(mViewPageAdapter);
    }

     private void changeFragment(int type ) {
         Fragment newFragment;
         if(type==0){
             newFragment = ChatFragment.newInstance();
             replaceFragment(newFragment,R.id.notice_fragment,"CHAT_FRAGMENT","NOTICE_FRAGMENT");
         }else {
             newFragment = new NoticeListFragment();
             replaceFragment(newFragment,R.id.chat_fragment,"NOTICE_FRAGMENT","CHAT_FRAGMENT");
         }
         mViewPageAdapter.switchTitles(type);
//         mViewPager.getAdapter().notifyDataSetChanged();


     }


     public void replaceFragment(Fragment fragment,int id, String addTag, String removeTag){
         getSupportFragmentManager();
         FragmentTransaction trans = getSupportFragmentManager()
                 .beginTransaction();
         trans.replace(R.id.root_frame, fragment,addTag);
         trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
         trans.addToBackStack(null);
         trans.commit();
         Fragment removeFragment = getSupportFragmentManager().findFragmentByTag(removeTag);
         if(removeFragment != null)
             getSupportFragmentManager().beginTransaction().remove(removeFragment).commitNow();
     }

    private void setupNavigationView() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(mInstDetails.getName());


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

        String userId= FirebaseAuth.getInstance().getUid();
        mViewModel.setDomainAdminList(mInstDetails.getAdminList());
        if(mInstDetails.getAdminList().contains(userId)){
            mViewModel.setAdminLevel("Main");
//            mViewModel.setDomainAdminList(new ArrayList<String>(Arrays.asList(userId)));
        }

        mViewModel.setInstCode(mInstCode);
        mViewModel.getIdList().observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(@Nullable ArrayList<String> idList) {
                mIdList=idList;
                if(mViewModel.getPrivacyLevel().getValue()<1){
                    if(idList.contains(mViewModel.getAdminLevel().getValue())||mViewModel.getAdminLevel().getValue().equals("Main")){
                        mIsAdmin =true;
                    }else {
                        mIsAdmin =false;
                }}

                if(mIdList.size()>0&&mViewModel.getChatGroupIds().getValue().contains(mIdList.get(mIdList.size()-1))){
                    if(!mViewModel.getIsChatGroup().getValue()) {
                        mViewModel.setIsChatGroup(true);
                    }
                }else {
                    if(mViewModel.getIsChatGroup().getValue()) {
                        mViewModel.setIsChatGroup(false);
                    }
                }
            }});

        mViewModel.getIsChatGroup().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if(!mInitial){
                if(aBoolean){
                    changeFragment(0);
                }else{
                    changeFragment(1);
                }
            }
                mInitial=false;
            }
        });

//        mViewModel.getChatGroupIds().observe(this, new Observer<HashSet<String>>() {
//            @Override
//            public void onChanged(HashSet<String> strings) {
//                if(strings.contains(mIdList.get(mIdList.size()-1))){
//
//                }
//            }
//        });

        mViewModel.getDomainNameList().observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> domainNameList) {
                mDomainNameList=domainNameList;
                mDomainName=mDomainNameList.get(mDomainNameList.size()-1);
                if(!mDomainName.equals("main"))
                getSupportActionBar().setSubtitle(mDomainName);
         }});

        mViewModel.getPrivacyLevel().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                if(integer>0){

                    if(mIdList.contains(mViewModel.getPrivateDomainAdminLevel().getValue())){
                        mIsAdmin =true;
                    }else{
                        mIsAdmin =false;
                    }
                }
            }});

    }



     @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu,menu);
        mOptionMenu = menu;
        return true;
    }

     @Override
     public boolean onPrepareOptionsMenu(Menu menu) {
         if(mIsAdmin){
             menu.setGroupVisible(R.id.for_admin,true);
         }else {
             menu.setGroupVisible(R.id.for_admin,false);
         }
         if(mViewModel.getIsChatGroup().getValue()){
             menu.setGroupVisible(R.id.report_generation,false);
         }else {
             menu.setGroupVisible(R.id.report_generation,true);
         }
         return super.onPrepareOptionsMenu(menu);
     }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        ArrayList<String> adminList;
        if(mViewModel.getPrivacyLevel().getValue()>0)
            adminList=new ArrayList<>(mViewModel.getPrivateCurrentAdminList().getValue());

        else adminList=new ArrayList<>(mViewModel.getCurrentAdminList().getValue());

        NavObjects lNavObjects=new NavObjects(mIdList,mInstDetails,mDomainName,
                adminList,mViewModel.getPrivateMemberList().getValue(), mIsAdmin,mViewModel.getPrivacyLevel().getValue());
        switch (item.getItemId()){
            case R.id.sign_out:
                FirebaseAuth.getInstance().signOut();
                clearPrefData();
                Intent OIntent=new Intent(this, LaunchActivity.class);
                OIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(OIntent);
                finish();
                break;
            case R.id.send_notice:
                Intent AIntent =new Intent(this, AddNoticeActivity.class);
                AIntent.putExtra(NAV_OBJECT,(Parcelable) lNavObjects);
                startActivity(AIntent);
                break;
            case R.id.settings:
                Intent SIntent = new Intent(this, SettingsActivity.class);
                SIntent.putExtra(NAV_OBJECT,(Parcelable) lNavObjects);
                startActivity(SIntent);
                break;
            case R.id.users_list:
                Intent MIntent =new Intent(this, UsersListActivity.class);
                MIntent.putExtra(NAV_OBJECT,(Parcelable) lNavObjects);
                startActivity(MIntent);
                break;
            case R.id.admin_list:
                Intent AdIntent =new Intent(this, ViewAdminsActivity.class);
                AdIntent.putExtra(NAV_OBJECT,(Parcelable) lNavObjects);
                startActivity(AdIntent);
                break;
            case R.id.notices_report:
                Intent RdIntent =new Intent(this, ReportActivity.class);
                RdIntent.putExtra(NAV_OBJECT,(Parcelable) lNavObjects);
                startActivity(RdIntent);



        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onBackPressed() {
        int sz = mIdList.size();

        if(sz>0){
            if(mViewModel.getChatGroupIds().getValue().contains(mIdList.get(mIdList.size()-1))){
                mViewModel.removeChatGroupId(mIdList.get(mIdList.size()-1));
            }

            mFinalExit =false;
            mViewModel.removePreviousAdmins();
            mIdList.remove(sz-1);
            mDomainNameList.remove(sz);
            mViewModel.setDomainNameList(mDomainNameList);
            if(mViewModel.getPrivacyLevel().getValue()>0)mViewModel.decrementPrivacyLevel();
            mViewModel.setIdList(mIdList);
//            if(mViewModel.getCurrentAdminList().getValue().size()>0)


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
        lEditor.remove(APP_THEME).commit();

    }
    private void clearPrefData(){
        SharedPreferences lSharedPreferences=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor lEditor=lSharedPreferences.edit();
        lEditor.remove(INSTITUTION_CODE).remove(APP_THEME).commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        ArrayList<String> adminList;
        if(mViewModel.getPrivacyLevel().getValue()>0)
            adminList=new ArrayList<>(mViewModel.getPrivateCurrentAdminList().getValue());
        else adminList=new ArrayList<>(mViewModel.getCurrentAdminList().getValue());
        if(!mViewModel.getAdminLevel().getValue().equals("")&&!mViewModel.getAdminLevel().getValue().equals("main")){
            mIsAdmin=true;
        }else {
            mIsAdmin=false;
        }
        NavObjects lNavObjects=new NavObjects(mIdList,mInstDetails
                ,mDomainNameList.get(mDomainNameList.size()-1),adminList
                ,mViewModel.getPrivateMemberList().getValue(),mIsAdmin
                ,mViewModel.getPrivacyLevel().getValue());
        navigationSwitch(this,item,lNavObjects, mDrawerLayout);
        return true;
    }

    public static void navigationSwitch(Activity activity, MenuItem item,NavObjects navObjects, DrawerLayout drawerLayout) {
        switch (item.getItemId()){
            case R.id.nav_home:
                Intent i = new Intent(activity, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(i);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.about_institution:
                Intent AIIntent =new Intent(activity, AboutInstitution.class);
                AIIntent.putExtra(NAV_OBJECT,(Parcelable) navObjects);
                activity.startActivity(AIIntent);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.settings:
                Intent SIntent =new Intent(activity, SettingsActivity.class);
                SIntent.putExtra(NAV_OBJECT,(Parcelable) navObjects);
                activity.startActivity(SIntent);
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
        }
    }

    public static void chooseInstitution(Activity activity,NavObjects navObjects, DrawerLayout drawerLayout) {
        FirebaseUtils.FIRESTORE.collection(FirebaseUtils.USERS)
                .document(FirebaseUtils.sFirebaseAuth.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot lSnapshot=task.getResult();
                            User lUser=lSnapshot.toObject(User.class).withId(lSnapshot.getId());
                            ArrayList<String> lInst=lUser.getInstitutions();
                            Intent lIntent=new Intent(activity, ChooseInstitutionActivity.class);
                            lIntent.putExtra(NAV_OBJECT,(Parcelable) navObjects);
                            lIntent.putExtra(INSTITUTION_LIST,lInst);
                            activity.startActivity(lIntent);
                        }
                    }
                });
    }
}