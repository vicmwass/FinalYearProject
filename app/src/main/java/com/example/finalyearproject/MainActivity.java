 package com.example.finalyearproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    public static final String IDLIST="idList";
    public static final String DNAME="domainN";
    public static final String NOTICE="notice";
    public SharedViewModel mViewModel;
    ArrayList<String> mIdList;
    ArrayList<String> mDomainNameList=new ArrayList<String>();
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private ViewPageAdapter mViewPageAdapter;
    private Toolbar mToolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private String mInstCode;
    private Boolean finalExit=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Intent lIntent=getIntent();
        mInstCode = lIntent.getStringExtra("InstitutionCode");



        setupViewModel();

        setupNavigatioView();

        setupViewPager();

//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.domains_frame, new DomainListFragment())
//                .add(R.id.notices_frame, new NoticeListFragment())
//                .commit();




    }

    private void setupViewPager() {
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

        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.nav_view);


        navigationView.bringToFront();//when navdrawer items clicked show that color to represent click
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,mToolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //to make navigation drawer clickable
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_home);
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
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater= getMenuInflater();
        inflater.inflate(R.menu.domains,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.to_new_domains:
                Intent DIntent=new Intent(this, AddDomain.class);
                DIntent.putExtra(IDLIST, mIdList);
                DIntent.putExtra("InstitutionCode", mInstCode);
                startActivity(DIntent);
                break;
            case R.id.to_new_notice:
                Intent NIntent =new Intent(this,AddNotice.class);
                NIntent.putExtra(IDLIST, mIdList);
                NIntent.putExtra(DNAME, mDomainNameList.get(mDomainNameList.size()-1));
                NIntent.putExtra("InstitutionCode", mInstCode);
                startActivity(NIntent);
                break;
            case R.id.sign_out:
                FirebaseAuth.getInstance().signOut();
                Intent OIntent=new Intent(this,LaunchActivity.class);
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
            finalExit=false;
            mIdList.remove(sz-1);
            mDomainNameList.remove(sz);
            mViewModel.setIdList(mIdList);

        }else{
            if(finalExit){
                super.onBackPressed();
            }else {
                finalExit=true;
                Toast.makeText(this,"press again to exit",Toast.LENGTH_LONG).show();
            }

        }


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:
                Intent lIntent=new Intent(this, MainActivity.class);
                lIntent.putExtra(IDLIST, mIdList);
                lIntent.putExtra("InstitutionCode", mInstCode);
                startActivity(lIntent);
                break;
            case R.id.to_new_domains:
                Intent DIntent=new Intent(this, AddDomain.class);
                DIntent.putExtra(IDLIST, mIdList);
                DIntent.putExtra("InstitutionCode", mInstCode);
                DIntent.putExtra(DNAME, mDomainNameList.get(mDomainNameList.size()-1));
                startActivity(DIntent);
                break;
            case R.id.to_new_notice:
                Intent NIntent =new Intent(this,AddNotice.class);
                NIntent.putExtra(IDLIST, mIdList);
                NIntent.putExtra(DNAME, mDomainNameList.get(mDomainNameList.size()-1));
                NIntent.putExtra("InstitutionCode", mInstCode);
                startActivity(NIntent);
                break;
            case R.id.choose_institution:
                chooseInstitution();
                break;

        }
        return true;
    }

    private void chooseInstitution() {
        FirebaseUtils.FIRESTORE.collection(FirebaseUtils.USERS)
                .document(FirebaseUtils.sFirebaseAuth.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot lSnapshot=task.getResult();
                            User lUser=lSnapshot.toObject(User.class).withId(lSnapshot.getId());
                            ArrayList<String> lInst=lUser.getInstitutions();
                            Intent lIntent=new Intent(MainActivity.this,ChooseInstitution.class);
                            lIntent.putExtra(IDLIST, mIdList);
                            lIntent.putExtra(DNAME, mDomainNameList.get(mDomainNameList.size()-1));
                            lIntent.putExtra("InstitutionCode", mInstCode);
                            lIntent.putExtra("institutionList",lInst);
                            startActivity(lIntent);
                            finish();
                        }

                    }
                });

    }
}