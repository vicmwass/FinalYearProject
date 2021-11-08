package com.example.finalyearproject.Activities.ViewUsers.ListAdmins;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.finalyearproject.Activities.Main.MainActivity;
import com.example.finalyearproject.Activities.ReportActivity;
import com.example.finalyearproject.Activities.ViewUsers.ContactDialog;
import com.example.finalyearproject.Modules.NavObjects;
import com.example.finalyearproject.Modules.User;
import com.example.finalyearproject.R;
import com.google.firebase.firestore.CollectionReference;

import java.util.ArrayList;

import static com.example.finalyearproject.Activities.Main.MainActivity.NAV_OBJECT;

public class ViewAdminsActivity extends AppCompatActivity implements ContactDialog.ContactDialogListener {
    private ArrayList<String> mUsers;
    private CollectionReference mNoticeRef;
    private ArrayList<String> mIdList;
    private String mInstCode;

    private ViewAdminsAdapter mAdapter;
    private ArrayList<String> mCurrentAdmins;
    private Toolbar mToolbar;
    private NavObjects mNavObjects;
    private int mPrivacyLevel;
    private String mDomainName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.changeTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_admins);
        getIntentExtras();
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(mDomainName);
        getSupportActionBar().setSubtitle("Admins");
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        setupAdapter();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView mSearchView = findViewById(R.id.action_search2);
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
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_list_menu,menu);
//        mOptionMenu = menu;/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // handle arrow click here

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.report:
                if(askMemoryPermissions()){
                    Intent Intent =new Intent(this, ReportActivity.class);
                    ArrayList<User> temp=mAdapter.mAllDomainAdminList;
                    Intent.putExtra("UserList",temp);
                    Intent.putExtra("DomainName",mDomainName);
                    Intent.putExtra("UserType","admins");
                    startActivity(Intent);

                }
        }

        return super.onOptionsItemSelected(item);
    }
    private  boolean askMemoryPermissions(){

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED
                &&ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            return true;
        }else {
            ActivityCompat.requestPermissions(this,
                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
            return false;

        }

    }

    private void getIntentExtras() {
        Intent lIntent=getIntent();
        mNavObjects = (NavObjects) lIntent.getParcelableExtra(NAV_OBJECT);
        mIdList = mNavObjects.getIdList();
        mInstCode = mNavObjects.getInstDetails().getCode();
        mUsers= mNavObjects.getMemberList();
        mPrivacyLevel = mNavObjects.getPrivacyLevel();
        mCurrentAdmins=mNavObjects.getCurrentAdminList();
        mDomainName=mNavObjects.getDomainName();
    }

    private void setupAdapter() {
        mAdapter = new ViewAdminsAdapter(this,this,mInstCode,mCurrentAdmins);
        RecyclerView usersRecycleView = findViewById(R.id.admins_list_rv);
        usersRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        usersRecycleView.setAdapter(mAdapter);
    }


    @Override
    public void applyText(String email, String password) {

    }

}