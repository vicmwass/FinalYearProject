package com.example.finalyearproject.Activities.ViewUsers.ListAllUsers;

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
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;

import com.example.finalyearproject.Activities.Main.MainActivity;
import com.example.finalyearproject.Activities.ReportActivity;
import com.example.finalyearproject.Activities.ViewUsers.ContactDialog;
import com.example.finalyearproject.Activities.ViewUsers.ListAdmins.ViewAdminsActivity;
import com.example.finalyearproject.Modules.NavObjects;
import com.example.finalyearproject.Modules.User;
import com.example.finalyearproject.R;

import java.util.ArrayList;

import static com.example.finalyearproject.Activities.Main.MainActivity.NAV_OBJECT;

public class UsersListActivity extends AppCompatActivity implements ContactDialog.ContactDialogListener {

    private Toolbar mToolbar;
    private NavObjects mNavObjects;
    private String mInstCode;
    private String mDomainName;
    private ArrayList<String> mMembers;
    private UsersListAdapter mUserListAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.changeTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inst_users);
        getIntentExtras();
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(mDomainName);
        getSupportActionBar().setSubtitle("Members");
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        RecyclerView lRecyclerView=findViewById(R.id.user_list_rv);
        mUserListAdapter = new UsersListAdapter(this,this, mInstCode,mMembers);
        SearchView mSearchView = findViewById(R.id.action_search2);
        lRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        lRecyclerView.setAdapter(mUserListAdapter);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

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
                mUserListAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    private void getIntentExtras() {
        Intent lIntent=getIntent();
        mNavObjects = (NavObjects) lIntent.getParcelableExtra(NAV_OBJECT);
        mInstCode = mNavObjects.getInstDetails().getCode();
        mDomainName=mNavObjects.getDomainName();
        mMembers=mNavObjects.getMemberList();

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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_list_menu,menu);
//        mOptionMenu = menu;/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            case R.id.report:
                if(askMemoryPermissions()){
                    Intent Intent =new Intent(this, ReportActivity.class);
                    ArrayList<User> temp=mUserListAdapter.mAllInstUserList;
                    Intent.putExtra("UserList",temp);
                    Intent.putExtra("DomainName",mDomainName);
                    Intent.putExtra("UserType","members");
                    startActivity(Intent);

                }
        }

        return super.onOptionsItemSelected(item);
    }




    @Override
    public void applyText(String email, String password) {

    }
}