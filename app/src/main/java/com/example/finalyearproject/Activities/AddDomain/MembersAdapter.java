package com.example.finalyearproject.Activities.AddDomain;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.example.finalyearproject.HelperClasses.FirebaseUtils;
import com.example.finalyearproject.Modules.User;
import com.example.finalyearproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;

import de.hdodenhof.circleimageview.CircleImageView;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MembersViewHolder> {
    ArrayList<User> mInstUserList=new ArrayList<>();

    AddDomainViewModel mViewModel;

    String mInstCode;
    private HashSet<String> mMembersOfPrivateDomain;
    private Activity mActivity;

    public MembersAdapter(Activity activity, String instCode, AddDomainViewModel viewModel) {
        mViewModel = viewModel;
        mActivity=activity;
        mInstCode = instCode;
        mMembersOfPrivateDomain = mViewModel.getMembersOfPrivateDomain().getValue();
        populateData();
    }

    private void populateData() {
        FirebaseUtils.FIRESTORE.collection(FirebaseUtils.INSTITUTIONS).document(mInstCode)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    ArrayList<String> mUsers = (ArrayList<String>) task.getResult().get("users");
                    for (String userid: mUsers) {
                        FirebaseUtils.FIRESTORE.collection("users").document(userid)
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    User lUser=task.getResult().toObject(User.class).withId(task.getResult().getId());
                                    mInstUserList.add(lUser);
                                    MembersAdapter.this.notifyDataSetChanged();
                                }
                            }
                        });
                    }

                }
            }
        });

//        CollectionReference tempRef = FirebaseUtils.FIRESTORE.collection("Institutions").document(mInstCode).collection("Users");
//
//
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
//                                String id = dc.getDocument().getId();
//                                InstUser lInstUser = dc.getDocument().toObject(InstUser.class);
//                                mInstUserList.add(lInstUser);
//                                Log.d("DomainName", lInstUser.getEmail());
//                                MembersAdapter.this.notifyDataSetChanged();
//                                break;
//                            case MODIFIED:
//                                break;
//                            case REMOVED:
//                                break;
//                        }
//                    }
//                }
//            }
//        });
    }

    @NonNull
    @NotNull
    @Override
    public MembersViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View lView= LayoutInflater.from(parent.getContext()).inflate(R.layout.select_users_card,parent,false);
        MembersViewHolder lViewHolder=new MembersViewHolder(lView);
        return lViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MembersAdapter.MembersViewHolder holder, int position) {
        User lInstUser =mInstUserList.get(position);
        holder.mTvName.setText(lInstUser.getEmail());
        holder.mRCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.mCbUser.isChecked()){
                    holder.mCbUser.setChecked(false);
                    mViewModel.removeMemberFromSet(lInstUser.getId());
                }else{
                    holder.mCbUser.setChecked(true);
                    mViewModel.addMemberToSet(lInstUser.getId());
                }
            }
        });
        holder.mCbUser.setClickable(false);
        if(lInstUser.getImgUri()!=null){
            showImage(lInstUser.getImgUri(), holder.mImageView);
        }else {
            holder.mImageView.setImageResource(R.drawable.person_outline_24);
        }
        holder.mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupImage(holder.mImageView,lInstUser.getImgUri());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mInstUserList.size();
    }

    public class MembersViewHolder extends RecyclerView.ViewHolder{
        private final RelativeLayout mRCard;
        private final TextView mTvName;
        private final CheckBox mCbUser;
        private final CircleImageView mImageView;

        public MembersViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mRCard = itemView.findViewById(R.id.admin_card);
            mTvName = itemView.findViewById(R.id.tv_user_name);
            mCbUser = itemView.findViewById(R.id.user_checkbox);
            mImageView=itemView.findViewById(R.id.profile_image);
        }
    }
    public void showImage(String url, CircleImageView imgView){
        if(url!=null&&url.isEmpty()==false){
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Glide.with(mActivity).load(url).override(width*1/2, width*2/3).
                    centerCrop().into(imgView);
        }
    }
    public void popupImage(CircleImageView imgView,String url){
//        Picasso.setSingletonInstance(new Picasso.Builder(this).build()); // Only needed if you are using Picasso
        final ImagePopup imagePopup = new ImagePopup(mActivity);
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
