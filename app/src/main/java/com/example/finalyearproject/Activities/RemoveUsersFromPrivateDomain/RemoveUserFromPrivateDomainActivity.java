package com.example.finalyearproject.Activities.RemoveUsersFromPrivateDomain;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.example.finalyearproject.Activities.Main.MainActivity.NAV_OBJECT;

public class RemoveUserFromPrivateDomainActivity extends AppCompatActivity {
    private ArrayList<String> mMembers;
    private CollectionReference mDomainsRef;
    private ArrayList<String> mIdList;
    private String mInstCode;
    private String mDomainId;
    private RemoveUserAdapter mAdapter;
    private RemoveUserViewModel mViewModel;
    private ArrayList<String> mAdmins;
    private Toolbar mToolbar;
    private int mPrivacyLevel;
    private NavObjects mNavObjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainActivity.changeTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_user_from_private_domain);
        getIntentExtras();
        mDomainId = mIdList.get(mIdList.size()-1);
        mViewModel = new ViewModelProvider(this).get(RemoveUserViewModel.class);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Remove Members");
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
        mPrivacyLevel= mNavObjects.getPrivacyLevel();
        mMembers=mNavObjects.getMemberList();
        mAdmins=mNavObjects.getCurrentAdminList();
    }

    private void setupAdapter() {
        mAdapter = new RemoveUserAdapter(mInstCode, mMembers,mAdmins,mViewModel);
        RecyclerView usersRecycleView = findViewById(R.id.remove_member_recycler_view);
        usersRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        usersRecycleView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.remove_members,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.remove_members:
                if(mViewModel.getMembersIdList().getValue().size()<1){
                    Toast.makeText(RemoveUserFromPrivateDomainActivity.this,"Select members to remove",Toast.LENGTH_LONG).show();
                    break;
                }
                removeMembers();

        }
        return super.onOptionsItemSelected(item);
    }

    private void getCurrentMembers() {
        mDomainsRef = FirebaseUtils.FIRESTORE.collection("Institutions").document(mInstCode).collection("domains");
        while (mPrivacyLevel>1){
            mIdList.remove(mIdList.size()-1);
            mPrivacyLevel-=1;
        }
        for(String Id:mIdList){
            mDomainsRef = mDomainsRef.document(Id).collection("domains");
        }
        mDomainsRef.getParent()
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    mMembers = (ArrayList<String>) task.getResult().get("memberList");
                    mAdmins = (ArrayList<String>) task.getResult().get("adminList");

                }
            }
        });
    }

    public void removeMembers(){
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
                        .update("memberList", FieldValue.arrayRemove(memberId))
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(RemoveUserFromPrivateDomainActivity.this,"Removed successful",Toast.LENGTH_LONG).show();
                                RemoveUserFromPrivateDomainActivity.this.onBackPressed();
//                        Log.d("Firestore", "Document updated with ID: " + PatientPostRef.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RemoveUserFromPrivateDomainActivity.this,"Failed to add",Toast.LENGTH_LONG).show();
//                        Log.e("Firestore", "Error updating document", e);
                            }
                        });
            }

    }
}