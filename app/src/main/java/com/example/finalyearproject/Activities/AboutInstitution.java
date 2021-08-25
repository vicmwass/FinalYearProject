package com.example.finalyearproject.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.finalyearproject.Activities.Main.MainActivity;
import com.example.finalyearproject.Modules.NavObjects;
import com.example.finalyearproject.R;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

import static com.example.finalyearproject.Activities.Main.MainActivity.NAV_OBJECT;

public class AboutInstitution extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    private NavObjects mNavObjects;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_institution);

        Intent lIntent=getIntent();
        mNavObjects = (NavObjects) lIntent.getParcelableExtra(NAV_OBJECT);
        setupNavigatioView();
        TextView tvInstName=findViewById(R.id.disp_inst_name);
        TextView tvInstCode=findViewById(R.id.disp_inst_code);
        tvInstCode.setText(mNavObjects.getInstDetails().getCode());
        tvInstName.setText(mNavObjects.getInstDetails().getName());
    }
    private void setupNavigatioView() {
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(mNavObjects.getInstDetails().getName());

        mDrawerLayout =findViewById(R.id.drawer_layout);
        mNavigationView =findViewById(R.id.nav_view);


        mNavigationView.bringToFront();//when navdrawer items clicked show that color to represent click
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout,mToolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //to make navigation drawer clickable
        mNavigationView.setNavigationItemSelectedListener(this);

        mNavigationView.setCheckedItem(R.id.choose_institution);

        if(mNavObjects.getIsAdmin()){
            mNavigationView.getMenu().setGroupVisible(R.id.nav_for_admin,true);
        }else {
            mNavigationView.getMenu().setGroupVisible(R.id.nav_for_admin,false);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {

        MainActivity.navigationSwitch(this,item, mNavObjects, mDrawerLayout);
        return true;
    }
}