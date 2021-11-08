package com.example.finalyearproject.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;

import com.example.finalyearproject.Activities.Launch.LaunchActivity;
import com.example.finalyearproject.Activities.Main.MainActivity;
import com.example.finalyearproject.HelperClasses.FirebaseUtils;
import com.example.finalyearproject.Modules.Institution;
import com.example.finalyearproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import static com.example.finalyearproject.Activities.Launch.LaunchActivity.INSTITUTION_CODE;
import static com.example.finalyearproject.Activities.Launch.LaunchActivity.INSTITUTION_DETAILS;
import static com.example.finalyearproject.Activities.Main.MainActivity.SHARED_PREFS;

public class SplashScreen extends AppCompatActivity implements FirebaseAuth.AuthStateListener  {
    public static final String APP_THEME = "theme";
    public  FirebaseAuth.AuthStateListener mAuthStateListener;
    public  FirebaseAuth mFirebaseAuth;
    private String mInstCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mFirebaseAuth = FirebaseAuth.getInstance();
    }
    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(this);
    }
    @Override
    public void onAuthStateChanged(@NonNull @NotNull FirebaseAuth firebaseAuth) {

        if(mFirebaseAuth.getCurrentUser() != null&&checkSavedInstitution()){
            getInstDetails();
            return;
        }
        Intent lIntent=new Intent(SplashScreen.this, LaunchActivity.class);
        startActivity(lIntent);
        finish();
    }
    private void getInstDetails() {
        FirebaseUtils.FIRESTORE.collection(FirebaseUtils.INSTITUTIONS).document(mInstCode)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Institution selectedInst=task.getResult().toObject(Institution.class);
                    savePrefData(selectedInst.getTheme());
                    Intent lIntent=new Intent(SplashScreen.this, MainActivity.class);
                    lIntent.putExtra(INSTITUTION_DETAILS, (Parcelable) selectedInst);
                    lIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(lIntent);
                    finish();
                }
            }
        });
    }
    private boolean checkSavedInstitution() {
        SharedPreferences lSharedPreferences=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        mInstCode = lSharedPreferences.getString(INSTITUTION_CODE,"");
        if(mInstCode.equals("")){
            return false;
        }
        return true;
    }
    private void savePrefData(String theme) {
        SharedPreferences lSharedPreferences=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor lEditor=lSharedPreferences.edit();
        lEditor.putString(APP_THEME,theme);
        lEditor.apply();
    }
}