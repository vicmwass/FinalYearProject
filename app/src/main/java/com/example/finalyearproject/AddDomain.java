package com.example.finalyearproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class AddDomain extends AppCompatActivity {


    private EditText mEtDomainName1;
    private ArrayList<String> mIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_domain);
        mEtDomainName1 = findViewById(R.id.et_domain_name);
        Intent myIntent=getIntent();
        mIdList = myIntent.getStringArrayListExtra(MainActivity.IDLIST);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_domain,menu);
        return true;
    }
    private Domain getDetails(){
        String name=mEtDomainName1.getText().toString().trim();
        Domain domain = new Domain();
        if(!name.equals("")){
            domain.setName(name);
        }
        return domain;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_domain:
                Domain lDomain = getDetails();
                if (lDomain != null) {
                    FirebaseUtils.saveDomain(lDomain,this,mIdList);
                    finish();
                }else{
                    Toast.makeText(this,"Could not add",Toast.LENGTH_LONG).show();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}