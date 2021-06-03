package com.example.finalyearproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.example.finalyearproject.MainActivity.DNAME;
import static com.example.finalyearproject.MainActivity.IDLIST;

public class ChooseInstitution extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private InstitutionAdapter mInstitutionAdapter;
    private RecyclerView mInstRecycleView;
    private ArrayList<String> mInstList;
    ArrayList<String> mInstNameList=new ArrayList<String>();
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private ArrayList<String> mIdList;
    private String mDomainName;
    private String mInstCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_institution);
        Intent lIntent=getIntent();
        mInstList = lIntent.getStringArrayListExtra("institutionList");
        mIdList = lIntent.getStringArrayListExtra(IDLIST);
        mDomainName = lIntent.getStringExtra(DNAME);
        mInstCode = lIntent.getStringExtra("InstitutionCode");
        if(mInstCode!=null){
            setupNavigatioView();
        }
//        setupAdapter();

        getInstNames();
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

        //to make navigation drawer clickable
        mNavigationView.setNavigationItemSelectedListener(this);

        mNavigationView.setCheckedItem(R.id.choose_institution);
    }

    private void setupAdapter() {
        mInstitutionAdapter = new InstitutionAdapter(this, mInstList,mInstNameList);
        mInstRecycleView = findViewById(R.id.inst_recycler_view);
        mInstRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mInstRecycleView.setAdapter(mInstitutionAdapter);
    }

    private void getInstNames() {
             FirebaseUtils.FIRESTORE.collection(FirebaseUtils.INSTITUTIONS).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.e("SnapshotListener", error.getMessage());
                        return;
                    }
                    if(value.getDocumentChanges().size()>0) {

                        for (DocumentChange dc : value.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    String id = dc.getDocument().getId();
                                    Institution lInst= dc.getDocument().toObject(Institution.class);
                                    if(mInstList.contains(lInst.getCode())){
                                        mInstNameList.add(lInst.getName());
                                    }
                                    break;
                                case MODIFIED:
                                    break;
                                case REMOVED:
                                    break;
                            }
                        }
                        setupAdapter();
                    }
                }
            });

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:
                Intent Intent=new Intent(this, MainActivity.class);
                Intent.putExtra(IDLIST, mIdList);
                Intent.putExtra("InstitutionCode", mInstCode);
                startActivity(Intent);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.to_new_domains:
                Intent DIntent=new Intent(this, AddDomain.class);
                DIntent.putExtra(IDLIST, mIdList);
                DIntent.putExtra("InstitutionCode", mInstCode);
                startActivity(DIntent);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.to_new_notice:
                Intent NIntent =new Intent(this,AddNotice.class);
                NIntent.putExtra(IDLIST, mIdList);
                NIntent.putExtra(DNAME, mDomainName);
                NIntent.putExtra("InstitutionCode", mInstCode);
                startActivity(NIntent);
                mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.choose_institution:
                chooseInstitution();
                mDrawerLayout.closeDrawer(GravityCompat.START);
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
                            Intent lIntent=new Intent(ChooseInstitution.this,ChooseInstitution.class);
                            lIntent.putExtra(IDLIST, mIdList);
                            lIntent.putExtra(DNAME, mDomainName);
                            lIntent.putExtra("InstitutionCode", mInstCode);
                            lIntent.putExtra("institutionList",lInst);
                            startActivity(lIntent);
                        }

                    }
                });

    }


}
