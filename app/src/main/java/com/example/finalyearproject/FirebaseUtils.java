package com.example.finalyearproject;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

public class FirebaseUtils {
    public static final String INSTITUTIONS = "Institutions";
    public static final String USERS = "users";
    public static final String INSTUSERS = "Users";
    public static final String INSTITUTIONS1FIELD = "institutions";
    private static FirebaseUtils sFirebaseUtils;
    public static FirebaseAuth sFirebaseAuth;
    public static final FirebaseFirestore FIRESTORE = FirebaseFirestore.getInstance();
    private static Activity sSignInCaller;
    public static FirebaseAuth.AuthStateListener sAuthStateListener;
    public static final int RC_SIGN_IN = 3397;
    public static StorageReference sStorageReference;
    public static FirebaseStorage sFirebaseStorage;



    public static void openFirebaseReference(final Activity callerActivity) {
        Log.d("Auth", "FirebaseUtil initialised");
        if (sFirebaseUtils == null) {
            sFirebaseUtils = new FirebaseUtils();
            sFirebaseAuth = FirebaseAuth.getInstance();
            sSignInCaller = callerActivity;
            if (sFirebaseAuth.getCurrentUser() == null) {
                FirebaseUtils.signIn();
            }
//            sAuthStateListener = new FirebaseAuth.AuthStateListener() {
//                @Override
//                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                    if (sFirebaseAuth.getCurrentUser() == null) {
//                        signIn();
//                    }
//                    Log.d("Auth", "Listener");
//                }
//            };
        }
    }



    public static void attachListener() {
        sFirebaseAuth.addAuthStateListener(sAuthStateListener);
    }

    public static void detachListener() {
        sFirebaseAuth.removeAuthStateListener(sAuthStateListener);
    }

    public static void signIn() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build()
//                ,
//                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

// Create and launch sign-in intent
        sSignInCaller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    private static void addUserInst(User user){
        for(String inst:user.getInstitutions()){
            FIRESTORE.collection(USERS)
                    .document(user.getId())
                    .update(INSTITUTIONS1FIELD, FieldValue.arrayUnion(inst));
        }

    }

    public static void saveUserDetails(User user){
        FIRESTORE.collection(USERS)
                .document(user.getId())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    Log.d("UserDetails", "No such document");
                    addUserInst(user);

                } else {
                    Log.d("UserDetails", "No such document");
                    addUser(user);
                }

            }
        });

    }

    private static void addUser(User user) {
        FIRESTORE.collection(USERS)
                .document(user.getId())
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "DocumentSnapshot added");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });
    }

    public static void saveInstUserDetails(String Code,InstUser user){
        FIRESTORE.collection(INSTITUTIONS).document(Code).collection(INSTUSERS)
                .document(user.getUserId())
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "DocumentSnapshot added");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error adding document", e);
            }
        });




//        babyRef = mFirestore.collection("users")
//                .document(mFirebaseAuth.getUid())
//                .collection("children")
//                .document(baby.getId());
//        babyRef.update(
//                "firstName", baby.getFirstName(),
//                "lastName", baby.getLastName(),
//                "dob", baby.getDob(),
//                "sex", baby.getSex()
//        );


    }


    public static void saveInstitution(Context context,Institution institution){
        final DocumentReference institutionRef= FIRESTORE.collection(INSTITUTIONS).document(institution.getCode());
        institutionRef.set(institution)
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


    public static void saveDomain(String instCode,Domain domain, Context context,ArrayList<String> idList){
        CollectionReference tempRef= FIRESTORE.collection(INSTITUTIONS).document(instCode).collection("domains");
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
        sFirebaseAuth = FirebaseAuth.getInstance();
        sFirebaseAuth.signInAnonymously().addOnSuccessListener((Activity) context, new  OnSuccessListener<AuthResult>() {
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

    public static void saveNotice(String instCode,Activity activity,Notice notice,String Id){
        final DocumentReference noticeRef;
            noticeRef= FIRESTORE.collection(INSTITUTIONS).document(instCode).collection("notices")
                    .document(Id)
                    .collection("my_notices")
                    .document();
        noticeRef.set(notice)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(activity,"Added successful",Toast.LENGTH_LONG).show();
                        activity.finish();
                        Log.d("Firestore", "Document updated with sender: " + notice.getSender());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull  Exception e) {
                        Toast.makeText(activity,"Failed to add",Toast.LENGTH_LONG).show();
                        Log.e("Firestore", "Failed to add notice with sender: " + notice.getSender());
                    }
                });


    }


    public static Notice saveNoticeFile(Uri fileUri, Notice notice){
        sFirebaseStorage = FirebaseStorage.getInstance();
        sStorageReference = sFirebaseStorage.getReference().child("notice_files");
        final StorageReference ref = sStorageReference.child(notice.getFileName());
        ref.putFile(fileUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    Log.e("uploadtask", "Upload task failed");
                    throw task.getException();

                }

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
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {
                Log.e("ImageUri", "Upload failed "+e.getMessage());

            }
        });
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return notice;
    }


    public static void deleteFile(Notice notice){
        sFirebaseStorage = FirebaseStorage.getInstance();
        if(notice.getFileUrl()!=null){
            StorageReference reference = sFirebaseStorage.getReferenceFromUrl(notice.getFileUrl());
            reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // File deleted successfully
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Uh-oh, an error occurred!
                }
            });
        }



    }


    public static void saveFileLocally(Context context, Notice notice, ProgressDialog progressDialog){
        sFirebaseStorage = FirebaseStorage.getInstance();
        StorageReference reference = sFirebaseStorage.getReferenceFromUrl(notice.getFileUrl());
        askPermissions(context);
            File storagePath = new File(context.getExternalFilesDir(""),"Notice_Files");
        if(!storagePath.exists()) {
            storagePath.mkdir();
        }
        final String[] type = new String[2];


        reference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
            @Override
            public void onSuccess(StorageMetadata storageMetadata) {
                type[0] =storageMetadata.getContentType().split("/")[0];
                type[1]=storageMetadata.getContentType().split("/")[1];
                String mime = type[0]+"/"+type[1];

                      final File localFile = new File(storagePath, notice.getFileName());
                      if(localFile.exists()){
                          progressDialog.setTitle("searching for file");
                          progressDialog.show();
                          Uri uri= FileProvider.getUriForFile(context,context.getApplicationContext().getPackageName()+".provider",localFile);
                          Intent lIntent=new Intent();
                          lIntent.setAction(Intent.ACTION_VIEW);
                          lIntent.setDataAndType(uri,mime);
                          lIntent.setFlags(FLAG_GRANT_READ_URI_PERMISSION | FLAG_GRANT_WRITE_URI_PERMISSION);
                          progressDialog.dismiss();
                          context.startActivity(Intent.createChooser(lIntent,"Open File"));
                      }else {
                          progressDialog.setTitle("Downloading file");
                          progressDialog.show();
                          reference.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                              @Override
                              public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                  progressDialog.dismiss();
                                  progressDialog.setTitle("searching for file");
                                  progressDialog.show();
                                  Uri uri= FileProvider.getUriForFile(context,context.getApplicationContext().getPackageName()+".provider",localFile);
                                  Log.d("saveFileLocally", "file downloaded");
                                  Intent lIntent = new Intent();
                                  lIntent.setAction(Intent.ACTION_VIEW);
                                  lIntent.setDataAndType(uri, mime);
                                  lIntent.setFlags(FLAG_GRANT_READ_URI_PERMISSION | FLAG_GRANT_WRITE_URI_PERMISSION);
                                  progressDialog.dismiss();
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
