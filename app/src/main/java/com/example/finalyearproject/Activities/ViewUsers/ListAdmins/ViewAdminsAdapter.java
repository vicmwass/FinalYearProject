package com.example.finalyearproject.Activities.ViewUsers.ListAdmins;

import android.app.Activity;
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

public class ViewAdminsAdapter extends RecyclerView.Adapter<ViewAdminsAdapter.ViewAdminsViewHolder> implements Filterable  {
    private ArrayList<User> mDomainAdminList =new ArrayList<>();
    ArrayList<User> mAllDomainAdminList =new ArrayList<>();
    private final String mInstCode;
    private final ArrayList<String> mCurrentAdmins;
    private final FragmentActivity mFragmentActivity;
    private Activity mActivity;

    public ViewAdminsAdapter(FragmentActivity fragmentActivity,Activity activity,String instCode, ArrayList<String> currentAdmins){
        mInstCode = instCode;
        mActivity=activity;
        mFragmentActivity=fragmentActivity;
        mCurrentAdmins = currentAdmins;
        populateData();
    }

    private void populateData() {
        mAllDomainAdminList.clear();
        mDomainAdminList.clear();
        for (String userid:mCurrentAdmins) {
            FirebaseUtils.FIRESTORE.collection("users").document(userid)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        User lUser=task.getResult().toObject(User.class).withId(task.getResult().getId());
                            mAllDomainAdminList.add(lUser);
                            mDomainAdminList.add(lUser);
                            ViewAdminsAdapter.this.notifyDataSetChanged();
                    }
                }
            });
        }

    }
    @NonNull
    @NotNull
    @Override
    public ViewAdminsViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View lView= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item,parent,false);
        ViewAdminsAdapter.ViewAdminsViewHolder lViewAdminsViewHolder=new ViewAdminsViewHolder(lView);
        return lViewAdminsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewAdminsAdapter.ViewAdminsViewHolder holder, int position) {
        User lUser= mDomainAdminList.get(position);
        if(FirebaseAuth.getInstance().getUid().equals(lUser.getId())){
            holder.mUsername.setText("you");
        }else {
            holder.mUsername.setText(lUser.getEmail());
        }

        holder.mRCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!FirebaseAuth.getInstance().getUid().equals(lUser.getId())){
                    openDialog(lUser.getEmail(),lUser.getPhoneNo());
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
        return mDomainAdminList.size();
    }

    public class ViewAdminsViewHolder extends RecyclerView.ViewHolder{
        private final TextView mUsername;
        private final RelativeLayout mRCard;
        private final CircleImageView mImageView;
        public ViewAdminsViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mUsername = itemView.findViewById(R.id.tv_user_name2);
            mRCard=itemView.findViewById(R.id.inst_user_card);
            mImageView=itemView.findViewById(R.id.profile_image);
        }
    }

    private void openDialog(String email,String phoneNo) {
        ContactDialog lContactDialog=new ContactDialog();
        Bundle args = new Bundle();
        args.putString("email", email);
        args.putString("phoneNo", phoneNo);
        lContactDialog.setArguments(args);
        lContactDialog.show(mFragmentActivity.getSupportFragmentManager(),"Contact Users");

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
                lFilteredList.addAll(mAllDomainAdminList);
            }else {
                String lFilterPattern=constraint.toString().toLowerCase().trim();
                for (User lUser: mAllDomainAdminList){
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
            mDomainAdminList.clear();
            mDomainAdminList.addAll((ArrayList)results.values);
            ViewAdminsAdapter.this.notifyDataSetChanged();
        }
    };
    public void showImage(String url,CircleImageView imgView){
        if(url!=null&&url.isEmpty()==false){
            int width = Resources.getSystem().getDisplayMetrics().widthPixels;
            Glide.with(mFragmentActivity).load(url).override(width*1/2, width*2/3).
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
