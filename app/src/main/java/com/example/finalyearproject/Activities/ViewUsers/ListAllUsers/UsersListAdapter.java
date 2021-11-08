package com.example.finalyearproject.Activities.ViewUsers.ListAllUsers;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.example.finalyearproject.Activities.ViewUsers.ContactDialog;
import com.example.finalyearproject.HelperClasses.FirebaseUtils;
import com.example.finalyearproject.Modules.User;
import com.example.finalyearproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.UserListViewHolder> implements Filterable, ContactDialog.ContactDialogListener {
    private ArrayList<User> mInstUserList =new ArrayList<>();
    ArrayList<User> mAllInstUserList=new ArrayList<>();
    private final String mInstCode;
    private ArrayList<String> mUsers;
    private FragmentActivity mFragmentActivity;
    private ArrayList<String> mMembers;
    private Activity mActivity;

    public UsersListAdapter(FragmentActivity fragmentActivity, Activity activity, String instCode, ArrayList members){
        mFragmentActivity =fragmentActivity;
        mMembers=members;
        mActivity=activity;
        mInstCode = instCode;
        populateData();
    }
    private void populateData() {
        mInstUserList.clear();
        mAllInstUserList.clear();

        FirebaseUtils.FIRESTORE.collection(FirebaseUtils.INSTITUTIONS).document(mInstCode)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    mUsers = (ArrayList<String>) task.getResult().get("users");
                    for (String userid: mUsers) {
                        FirebaseUtils.FIRESTORE.collection("users").document(userid)
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    User lUser=task.getResult().toObject(User.class).withId(task.getResult().getId());
                                    if (mMembers.size()>0){
                                        if (mMembers.contains(lUser.getId())){
                                            mAllInstUserList.add(lUser);
                                            mInstUserList.add(lUser);
                                        }
                                    }else {
                                        mAllInstUserList.add(lUser);
                                        mInstUserList.add(lUser);
                                    }

                                    UsersListAdapter.this.notifyDataSetChanged();
                                }
                            }
                        });
                    }

                }
            }
        });

//        CollectionReference tempRef = FirebaseUtils.FIRESTORE.collection("Institutions").document(mInstCode).collection("Users");

//        tempRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
//            @Override
//            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
//                if (error != null) {
//                    Log.e("SnapshotListener", error.getMessage());
//                    return;
//                }
//                if(value.getDocumentChanges().size()>0) {
//                    for (DocumentChange dc : value.getDocumentChanges()) {
//                        switch (dc.getType()) {
//                            case ADDED:
//                                User lInstUser = dc.getDocument().toObject(User.class);
//                                mAllInstUserList.add(lInstUser);
//                                Log.d("DomainName", lInstUser.getEmail());
//                                break;
//                            case MODIFIED:
//                                break;
//                            case REMOVED:
//                                break;
//                        }
//                    }
//                }
//                mInstUserList.addAll(mAllInstUserList);
//                UserListAdapter.this.notifyDataSetChanged();
//            }
//        });
    }
    private void openDialog(String email,String phoneNo) {
        ContactDialog lContactDialog=new ContactDialog();
        Bundle args = new Bundle();
        args.putString("email", email);
        args.putString("phoneNo", phoneNo);
        lContactDialog.setArguments(args);
        lContactDialog.show(mFragmentActivity.getSupportFragmentManager(),"Contact Users");

    }
    @NonNull
    @NotNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View lView= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item,parent,false);
        UserListViewHolder lUserListViewHolder=new UserListViewHolder(lView);
        return lUserListViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull UsersListAdapter.UserListViewHolder holder, int position) {
        User lUser= mInstUserList.get(position);
        if(FirebaseAuth.getInstance().getUid().equals(lUser.getId())){
            holder.mUsername.setText("you");
        }else {
            holder.mUsername.setText(lUser.getEmail());
        }
        holder.mRCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(askPermissions()){
                    if(!FirebaseAuth.getInstance().getUid().equals(lUser.getId())){
                        openDialog(lUser.getEmail(),lUser.getPhoneNo());
                    }
                }
            }
        });
        if(lUser.getImgUri()!=null){
            showImage(lUser.getImgUri(), holder.mImageView);
        }else {
            holder.mImageView.setImageResource(R.drawable.person_outline_24);
        }
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupImage(holder.mImageView,lUser.getImgUri());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mInstUserList.size();
    }

    @Override
    public void applyText(String email, String password) {

    }

    private  boolean askPermissions(){

        if (ContextCompat.checkSelfPermission(mFragmentActivity, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED
                &&ContextCompat.checkSelfPermission(mFragmentActivity, Manifest.permission.CALL_PHONE)
                == PackageManager.PERMISSION_GRANTED) {

            return true;
        }else {
            ActivityCompat.requestPermissions(mFragmentActivity,
                    new String[] { Manifest.permission.SEND_SMS, Manifest.permission.CALL_PHONE},
                    1);
            return false;

        }

    }

    public class UserListViewHolder extends RecyclerView.ViewHolder {
        private final TextView mUsername;
        private final RelativeLayout mRCard;
        private final CircleImageView mImageView;
        public UserListViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mUsername = itemView.findViewById(R.id.tv_user_name2);
            mRCard=itemView.findViewById(R.id.inst_user_card);
            mImageView=itemView.findViewById(R.id.profile_image);
        }
    }

    @Override
    public Filter getFilter() {
        return instUserFilter;
    }
    private Filter instUserFilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<User> lFilteredList=new ArrayList<>();
            if(constraint==null||constraint.length()==0){
                lFilteredList.addAll(mAllInstUserList);
            }else {
                String lFilterPattern=constraint.toString().toLowerCase().trim();
                for (User lUser:mAllInstUserList){
                    if(lUser.getEmail().contains(lFilterPattern)){
                        lFilteredList.add(lUser);
                    }
                }
            }
            FilterResults  lResults=new FilterResults();
            lResults.values=lFilteredList;
            return lResults;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mInstUserList.clear();
            mInstUserList.addAll((ArrayList)results.values);
            UsersListAdapter.this.notifyDataSetChanged();
        }
    };

    public void showImage(String url, CircleImageView imgView){
        if(url!=null&&url.isEmpty()==false){
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Glide.with(mFragmentActivity).load(url).override(width*1/2, width*2/3).
                    centerCrop().into(imgView);
        }
    }

    public void popupImage(CircleImageView imgView,String url){
//        Picasso.setSingletonInstance(new Picasso.Builder(this).build()); // Only needed if you are using Picasso
        final ImagePopup imagePopup = new ImagePopup(mFragmentActivity);
        imagePopup.setWindowHeight(500); // Optional
        imagePopup.setWindowWidth(500); // Optional
        imagePopup.setBackgroundColor(Color.TRANSPARENT);  // Optional
//        imagePopup.setFullScreen(true); // Optional
        imagePopup.setHideCloseIcon(true);  // Optional
        imagePopup.setImageOnClickClose(true);  // Optional
//        if(url!=null)
//            imagePopup.initiatePopupWithGlide(url);
//        else
        imagePopup.initiatePopup(imgView.getDrawable()); // Load Image from Drawable

        imagePopup.viewPopup();

    }


}
