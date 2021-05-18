package com.example.finalyearproject;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.io.Files;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.Executor;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

public class FirebaseUtils {
    private static FirebaseUtils mFirebaseUtil;
    public static FirebaseAuth mFirebaseAuth;
    public static final FirebaseFirestore mFireStore = FirebaseFirestore.getInstance();
    private static Activity signInCaller;
    public static FirebaseAuth.AuthStateListener mAuthStateListener;
    public static final int RC_SIGN_IN = 3397;
    public static StorageReference mStorageRef;
    public static FirebaseStorage mFirebaseStorage;
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

    public static void attachListener() {
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    public static void detachListener() {
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    private static void signIn() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build()
//                ,
//                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

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
        final DocumentReference domainsRef;
        if(idList.size()>0){
            for(String Id:idList){
                tempRef =tempRef.document(Id).collection("domains");
            }
        }

        domainsRef = tempRef
                .document();
        domainsRef.set(domain)
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

    public static void signInAnonymously(Context context) {
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseAuth.signInAnonymously().addOnSuccessListener((Activity) context, new  OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // do your stuff
            }
        }).addOnFailureListener((Activity) context, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("SignIn", "signInAnonymously:FAILURE", exception);
            }
        });
    }

    public static void saveNotice(Context context,Notice notice,String Id){
        final DocumentReference noticeRef;
            noticeRef=mFireStore.collection("notices")
                    .document(Id)
                    .collection("my_notices")
                    .document();
        noticeRef.set(notice)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context,"Added successful",Toast.LENGTH_LONG).show();
                        Log.d("Firestore", "Document updated with sender: " + notice.getSender());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull  Exception e) {
                        Toast.makeText(context,"Failed to add",Toast.LENGTH_LONG).show();
                        Log.e("Firestore", "Failed to add notice with sender: " + notice.getSender());
                    }
                });


    }
    public static Notice saveNoticeFile(Uri fileUri, Notice notice){
        mFirebaseStorage= FirebaseStorage.getInstance();
        mStorageRef=mFirebaseStorage.getReference().child("notice_files");
        final StorageReference ref = mStorageRef.child(fileUri.getLastPathSegment());
        ref.putFile(fileUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    Log.e("task", "Upload task failed");
                    throw task.getException();

                }
                ref.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        notice.setFileName(storageMetadata.getName());
                    }
                });
                return ref.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    String downloadUrl = task.getResult().toString();
                    notice.setFileUrl(downloadUrl);
                } else {
                    Log.e("ImageUri", "Upload task unsuccessful");
                }
            }
        });
        return notice;

    }



    public static void saveFileLocally(Context context,Notice notice){
        mFirebaseStorage= FirebaseStorage.getInstance();
        StorageReference reference = mFirebaseStorage.getReferenceFromUrl(notice.getFileUrl());
        askPermissions(context);
            File storagePath = new File(Environment.getExternalStorageDirectory(),"Notice_Files");
        if(!storagePath.exists()) {
            storagePath.mkdir();
        }
        final String[] name = new String[2];
        final String[] type = new String[1];


        reference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                name[0]= "docx";
                type[0] =storageMetadata.getContentType().split("/")[0];
//                name[1] =notice.getFileName().split(".")[1];

                // Metadata now contains the metadata for 'images/forest.jpg'


//                    File localFile = File.createTempFile(name[0], name[1]);
                      final File localFile = new File(storagePath, notice.getFileName());
                      if(localFile.exists()){
                          Uri uri= Uri.parse(localFile.getAbsolutePath());
                          String mime = type[0]+"/"+"*";
                          Intent lIntent=new Intent();
                          lIntent.setAction(Intent.ACTION_VIEW);
                          lIntent.setDataAndType(uri,mime);
                          lIntent.setFlags(FLAG_GRANT_READ_URI_PERMISSION | FLAG_GRANT_WRITE_URI_PERMISSION);
                          context.startActivity(Intent.createChooser(lIntent,"Open File"));

                      }else {



                          reference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                              @Override
                              public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                  // Local temp file has been created
//                    Toast.makeText(context,"file downloaded",Toast.LENGTH_LONG).show();
                                  Log.d("saveFileLocally", "file downloaded");
                                  Uri uri = Uri.parse(localFile.getAbsolutePath());
                                  String mime = type[0]+"/"+"*";
                                  Intent lIntent = new Intent();
                                  lIntent.setAction(Intent.ACTION_VIEW);
                                  lIntent.setDataAndType(uri, mime);
                                  lIntent.setFlags(FLAG_GRANT_READ_URI_PERMISSION | FLAG_GRANT_WRITE_URI_PERMISSION);
                                  context.startActivity(Intent.createChooser(lIntent, "Open File"));
                              }
                          }).addOnFailureListener(new OnFailureListener() {
                              @Override
                              public void onFailure(@NonNull Exception exception) {
                                  // Handle any errors
//                    Toast.makeText(context,"file could not be downloaded",Toast.LENGTH_LONG).show();
                                  Log.e("saveFileLocally", "file could not be downloaded" + exception.getMessage());
                              }
                          });
                      }


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
            }
        });

//        String extension = reference.getName().split(".")[1];

    }

    private static boolean askPermissions(Context context){

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==PackageManager.PERMISSION_GRANTED) {
            return true;
        }else {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                    1);
            return false;


        }

        }


}
