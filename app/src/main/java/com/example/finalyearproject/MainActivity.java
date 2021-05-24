package com.example.finalyearproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TableLayout;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    public static final String IDLIST="idList";
    public static final String DNAME="domainN";
    public static final String NOTICE="notice";
    public SharedViewModel mViewModel;
    ArrayList<String> mIdList;
    String mDomainName;
    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private ViewPageAdapter mViewPageAdapter;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseUtils.openFirebaseReference(this);
        setContentView(R.layout.activity_main);

        mViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        mViewModel.getIdList().observe(this, new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(@Nullable ArrayList<String> idList) {
                mIdList=idList;
            }
        });
        mViewModel.getDomainName().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                mDomainName=s;

            }
        });
        mToolbar = findViewById(R.id.toolbar);
         setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        mViewPager = findViewById(R.id.pager);
        mTabLayout = findViewById(R.id.tab_layout);
        mViewPageAdapter = new ViewPageAdapter(getSupportFragmentManager(),0);
        mTabLayout.setupWithViewPager(mViewPager);
        mViewPager.setAdapter(mViewPageAdapter);

//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.domains_frame, new DomainListFragment())
//                .add(R.id.notices_frame, new NoticeListFragment())
//                .commit();




    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUtils.attachListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtils.detachListener();
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
                startActivity(DIntent);
                break;
            case R.id.to_new_notice:
                Intent NIntent =new Intent(this,AddNotice.class);
                NIntent.putExtra(IDLIST, mIdList);
                NIntent.putExtra(DNAME,mDomainName);
                startActivity(NIntent);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        int sz = mIdList.size();
        if(sz>0){
            mIdList.remove(sz-1);
            mViewModel.setIdList(mIdList);
        }else{
            super.onBackPressed();
        }

    }
}