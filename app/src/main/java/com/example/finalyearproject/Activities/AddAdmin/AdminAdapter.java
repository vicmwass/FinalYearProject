package com.example.finalyearproject.Activities.AddAdmin;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.example.finalyearproject.Activities.AddDomain.AddDomainViewModel;
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

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.AdminViewHolder> implements Filterable {
    ArrayList<User> mInstUserList=new ArrayList<>();
    ArrayList<User> mInstUserPreFilterList=new ArrayList<>();
    ArrayList<User> mAllInstUserList=new ArrayList<>();
    Activity mActivity;
//    ArrayList<String> mInstNameList;
    AddAdminViewModel mViewModel;

    String mInstCode;
    private HashSet<String> mCurrentAdmins;
    private HashSet<String> mMembersOfPrivateDomain;
    private ArrayList<String> mUsers;

    public AdminAdapter(Activity activity,String instCode,AddAdminViewModel viewModel) {

        this.mInstCode=instCode;
        this.mViewModel=viewModel;
        this.mActivity=activity;
        mCurrentAdmins = mViewModel.getCurrentAdminSet().getValue();
        mMembersOfPrivateDomain = mViewModel.getMembersOfPrivateDomain().getValue();
        populateData();

    }

    public void includeAllPotentialAdmins() {

        mInstUserList.clear();
        mInstUserPreFilterList.clear();
        for (User lUser:mAllInstUserList){
            if(!mCurrentAdmins.contains(lUser.getId())){
                mInstUserList.add(lUser);
            }
        }
        mInstUserPreFilterList.addAll(mInstUserList);
        AdminAdapter.this.notifyDataSetChanged();

    }

    private void populateData() {

        mAllInstUserList.clear();
        FirebaseUtils.FIRESTORE.collection(FirebaseUtils.INSTITUTIONS).document(mInstCode)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    mUsers = (ArrayList<String>) task.getResult().get("users");
                    for (String userid:mUsers) {
                        FirebaseUtils.FIRESTORE.collection("users").document(userid)
                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    User lUser=task.getResult().toObject(User.class).withId(task.getResult().getId());
                                    if(mMembersOfPrivateDomain.size()>0){
                                        if(mMembersOfPrivateDomain.contains(lUser.getId())){
                                            mAllInstUserList.add(lUser);
                                        }
                                    }else{
                                        mAllInstUserList.add(lUser);
                                    }
                                    includeAllPotentialAdmins();
                                }
                            }
                        });
                    }
                }
            }
        });

    }

    public void includeMembersOnly(AddDomainViewModel domainViewModel){
        domainViewModel.getMembersIdSet().observe((LifecycleOwner) mActivity, new Observer<HashSet<String>>() {
            @Override
            public void onChanged(HashSet<String> strings) {
                mInstUserList.clear();
                mInstUserPreFilterList.clear();
                for (User lUser:mAllInstUserList){
                    if(strings.contains(lUser.getId())){
                        mInstUserList.add(lUser);
                    }
                }
                mInstUserPreFilterList.addAll(mInstUserList);
                AdminAdapter.this.notifyDataSetChanged();
            }
        });

    }

    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View lView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_users_card,parent,false);
        AdminViewHolder lAdminViewHolder=new AdminViewHolder(lView);
        return lAdminViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdminAdapter.AdminViewHolder holder, int position) {
        User lInstUser =mInstUserList.get(position);
        holder.mTvName.setText(lInstUser.getEmail());
        mViewModel.getAdminIdSet().observe((LifecycleOwner) mActivity, new Observer<HashSet<String>>() {
            @Override
            public void onChanged(HashSet<String> strings) {
                if(strings.contains(lInstUser.getId())){
                    holder.mCbUser.setChecked(true);
                }else {
                    holder.mCbUser.setChecked(false);
                }
                if(lInstUser.getImgUri()!=null){
                    showImage(lInstUser.getImgUri(), holder.mImageView);
                }else {
                    holder.mImageView.setImageResource(R.drawable.person_outline_24);
                }
            }
        });
        holder.mRCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.mCbUser.isChecked()){
                    holder.mCbUser.setChecked(false);
                    mViewModel.removeAdminFromSet(lInstUser.getId());
                }else{
                    holder.mCbUser.setChecked(true);
                    mViewModel.addAdminToSet(lInstUser.getId());
                }
            }
        });
        holder.mCbUser.setClickable(false);
        if(lInstUser.getImgUri()!=null){
            showImage(lInstUser.getImgUri(), holder.mImageView);
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

    @Override
    public Filter getFilter() {
        return instUserFilter;
    }

    private Filter instUserFilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<User> lFilteredList=new ArrayList<>();
            if(constraint==null||constraint.length()==0){
                lFilteredList.addAll(mInstUserPreFilterList);
            }else {
                String lFilterPattern=constraint.toString().toLowerCase().trim();
                for (User lUser:mInstUserPreFilterList){
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
            AdminAdapter.this.notifyDataSetChanged();
        }
    };

    public class AdminViewHolder extends RecyclerView.ViewHolder{

        private final RelativeLayout mRCard;
        private final TextView mTvName;
        private final CheckBox mCbUser;
        private final CircleImageView mImageView;

        public AdminViewHolder(@NonNull @NotNull View itemView) {
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
