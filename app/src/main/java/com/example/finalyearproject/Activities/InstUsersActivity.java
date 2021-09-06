package com.example.finalyearproject.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.finalyearproject.R;

import static com.example.finalyearproject.Activities.Launch.LaunchActivity.INSTITUTION_CODE;

public class InstUsersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inst_users);
        RecyclerView lRecyclerView=findViewById(R.id.user_list_rv);
        Intent lIntent=getIntent();
        String mInstCode=lIntent.getStringExtra(INSTITUTION_CODE);
        UserListAdapter lUserListAdapter=new UserListAdapter(mInstCode);
        SearchView mSearchView = findViewById(R.id.action_search2);
        lRecyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        lRecyclerView.setAdapter(lUserListAdapter);

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
                lUserListAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }


}