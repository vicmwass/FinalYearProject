package com.example.finalyearproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.example.finalyearproject.MainActivity.DNAME;
import static com.example.finalyearproject.MainActivity.IDLIST;

public class AddDomain extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private EditText mEtDomainName1;
    private ArrayList<String> mIdList;
    private Toolbar mToolbar;
    private Domain mDomain;
    private String mInstCode;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private String mDomainName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_domain);
        mEtDomainName1 = findViewById(R.id.et_domain_name);
        Intent lIntent=getIntent();
        mIdList = lIntent.getStringArrayListExtra(MainActivity.IDLIST);
        mInstCode = lIntent.getStringExtra("InstitutionCode");
        mDomainName=lIntent.getStringExtra(DNAME);
        setupNavigatioView();
        mDomain = new Domain();

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

        mNavigationView.setCheckedItem(R.id.to_new_domains);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_domain,menu);
        return true;
    }
    private boolean saveDetails(){
        String name=mEtDomainName1.getText().toString().trim();
        if(name.length() == 0){
            mEtDomainName1.setError("name is Required");
            mEtDomainName1.requestFocus();
            return false;
        }
        mDomain.setName(name);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_domain:
                if(saveDetails()){
                    FirebaseUtils.saveDomain(mInstCode,mDomain,this,mIdList);
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

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
                            Intent lIntent=new Intent(AddDomain.this,ChooseInstitution.class);
                            lIntent.putExtra(IDLIST, mIdList);
                            lIntent.putExtra(DNAME, mDomainName);
                            lIntent.putExtra("InstitutionCode", mInstCode);
                            lIntent.putExtra("institutionList",lInst);
                            startActivity(lIntent);
                            finish();
                        }

                    }
                });

    }
}