package com.example.finalyearproject;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class FirebaseUtils {
    private static FirebaseUtils mFirebaseUtil;
    public static FirebaseAuth mFirebaseAuth;
    public static final FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();
    private static Activity signInCaller;
    public static FirebaseAuth.AuthStateListener mAuthStateListener;
    public static final int RC_SIGN_IN = 3397;
    public static void openFirebaseReference(final Activity callerActivity) {
        Log.d("Auth", "FirebaseUtil initialised");
        if (mFirebaseUtil == null) {
            mFirebaseUtil = new FirebaseUtils();
            mFirebaseAuth = FirebaseAuth.getInstance();
            signInCaller = callerActivity;
            if (mFirebaseAuth.getCurrentUser() == null) {
                FirebaseUtils.signIn();
            }
            mAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (mFirebaseAuth.getCurrentUser() == null) {
                        FirebaseUtils.signIn();
                    }
                    Log.d("Auth", "Listener");
                }
            };



        }


    }
    private static void signIn() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
//                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

// Create and launch sign-in intent
        signInCaller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }
    public static void saveDomain(Domain domain, Context context,ArrayList<String> idList){
        CollectionReference tempRef=mFireStore.collection("domains");
        final DocumentReference DomainsRef;
        if(idList.size()>0){
            for(String Id:idList){
                tempRef =tempRef.document(Id).collection("domains");
            }
        }

        DomainsRef = tempRef
                .document();
        DomainsRef.set(domain)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context,"Added successful",Toast.LENGTH_LONG).show();
//                        Log.d("Firestore", "Document updated with ID: " + PatientPostRef.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,"Failed to add",Toast.LENGTH_LONG).show();
//                        Log.e("Firestore", "Error updating document", e);
                    }
                });
    }
    public static void getDomains(Domain domain, Context context){

    }
}
