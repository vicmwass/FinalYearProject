package com.example.finalyearproject.Activities.AddUsersToPrivateDomain;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.finalyearproject.Activities.Main.MainActivity;
import com.example.finalyearproject.HelperClasses.FirebaseUtils;
import com.example.finalyearproject.Modules.NavObjects;
import com.example.finalyearproject.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FieldValue;

import java.util.ArrayList;

import static com.example.finalyearproject.Activities.Main.MainActivity.NAV_OBJECT;

public class AddUsersToPrivateDomainActivity extends AppCompatActivity {
    private ArrayList<String> mUsers;
    private CollectionReference mDomainsRef;
    private ArrayList<String> mIdList;
    private String mInstCode;
    private String mDomainId;
    private AddUserToPrivateDomainAdapter mAdapter;
    private UserViewModel mViewModel;
    private int mPrivacyLevel;
    private Toolbar mToolbar;
    private NavObjects mNavObjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.changeTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_users_to_private_domain);
        Intent lIntent=getIntent();
        getIntentExtras();
        mDomainId = mIdList.get(mIdList.size()-1);
        mViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Add Members");
        setupAdapter();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView mSearchView = findViewById(R.id.action_search);
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
    private void getIntentExtras() {
        Intent lIntent=getIntent();
        mNavObjects = (NavObjects) lIntent.getParcelableExtra(NAV_OBJECT);
        mIdList = mNavObjects.getIdList();
        mInstCode = mNavObjects.getInstDetails().getCode();
        mUsers=mNavObjects.getMemberList();
        mPrivacyLevel= mNavObjects.getPrivacyLevel();
    }


    private void setupAdapter() {
        mAdapter = new AddUserToPrivateDomainAdapter(this,mInstCode,mUsers,mViewModel);
        RecyclerView usersRecycleView = findViewById(R.id.add_member_recycler_view);
        usersRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        usersRecycleView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_members,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_members:
                if(mViewModel.getMembersIdList().getValue().size()<1){
                    Toast.makeText(this,"Select members to add",Toast.LENGTH_LONG).show();
                    break;
                }
                addMembers();

        }
        return super.onOptionsItemSelected(item);
    }



    public void addMembers(){
         mDomainsRef = FirebaseUtils.FIRESTORE.collection("Institutions").document(mInstCode).collection("domains");
        while (mPrivacyLevel>1){
            mIdList.remove(mIdList.size()-1);
            mPrivacyLevel-=1;
        }
        for(String Id:mIdList){
            mDomainsRef = mDomainsRef.document(Id).collection("domains");
        }
            for(String memberId:mViewModel.getMembersIdList().getValue()){
                mDomainsRef.getParent()
                        .update("memberList", FieldValue.arrayUnion(memberId))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(AddUsersToPrivateDomainActivity.this,"Added successful",Toast.LENGTH_LONG).show();
                                AddUsersToPrivateDomainActivity.this.onBackPressed();
//                        Log.d("Firestore", "Document updated with ID: " + PatientPostRef.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddUsersToPrivateDomainActivity.this,"Failed to add",Toast.LENGTH_LONG).show();
//                        Log.e("Firestore", "Error updating document", e);
                            }
                        });
            }





    }
}