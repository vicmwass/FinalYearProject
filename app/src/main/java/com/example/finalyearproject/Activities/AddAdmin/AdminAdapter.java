package com.example.finalyearproject.Activities.AddAdmin;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalyearproject.Activities.AddDomain.AddDomainViewModel;
import com.example.finalyearproject.HelperClasses.FirebaseUtils;
import com.example.finalyearproject.Modules.InstUser;
import com.example.finalyearproject.Modules.Institution;
import com.example.finalyearproject.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.AdminViewHolder> implements Filterable {
    ArrayList<InstUser> mInstUserList=new ArrayList<InstUser>();
    ArrayList<InstUser> mInstUserPreFilterList=new ArrayList<InstUser>();
    ArrayList<InstUser> mAllInstUserList=new ArrayList<InstUser>();
    Activity mActivity;
//    ArrayList<String> mInstNameList;
    AddAdminViewModel mViewModel;

    String mInstCode;
    private HashSet<String> mCurrentAdmins;
    private HashSet<String> mMembersOfPrivateDomain;

    public AdminAdapter(Activity activity,String instCode,AddAdminViewModel viewModel) {
        this.mInstCode=instCode;
        this.mViewModel=viewModel;
        this.mActivity=activity;
        mCurrentAdmins = mViewModel.getCurrentAdminSet().getValue();
        mMembersOfPrivateDomain = mViewModel.getMembersOfPrivateDomain().getValue();
        populateData();

    }

    public void includeAllPotentialAdmins() {
        mViewModel.clearAdminSet();
        mInstUserList.clear();
        mInstUserPreFilterList.clear();
        for (InstUser lUser:mAllInstUserList){
            if(!mCurrentAdmins.contains(lUser.getUserId())){
                mInstUserList.add(lUser);
            }
        }
        mInstUserPreFilterList.addAll(mInstUserList);
        AdminAdapter.this.notifyDataSetChanged();
    }

    private void populateData() {
        mAllInstUserList.clear();
            CollectionReference tempRef = FirebaseUtils.FIRESTORE.collection("Institutions").document(mInstCode).collection("Users");

        tempRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("SnapshotListener", error.getMessage());
                    return;
                }
                if(value.getDocumentChanges().size()>0) {


                    for (DocumentChange dc : value.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                String id = dc.getDocument().getId();
                                InstUser lInstUser = dc.getDocument().toObject(InstUser.class);
                                if(mMembersOfPrivateDomain.size()>0){
                                    if(mMembersOfPrivateDomain.contains(lInstUser.getUserId())){
                                        mAllInstUserList.add(lInstUser);
                                    }
                                }else{
                                    mAllInstUserList.add(lInstUser);
                                }


                                Log.d("DomainName", lInstUser.getEmail());
                                break;
                            case MODIFIED:
                                break;
                            case REMOVED:
                                break;
                        }
                    }
                    includeAllPotentialAdmins();
                }
            }
        });
    }
    public void includeMembersOnly(AddDomainViewModel domainViewModel){
        mViewModel.clearAdminSet();
        domainViewModel.getMembersIdSet().observe((LifecycleOwner) mActivity, new Observer<HashSet<String>>() {
            @Override
            public void onChanged(HashSet<String> strings) {
                mInstUserList.clear();
                mInstUserPreFilterList.clear();
                for (InstUser lUser:mAllInstUserList){
                    if(strings.contains(lUser.getUserId())){
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
        View lView = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_users_card,parent,false);
        AdminViewHolder lAdminViewHolder=new AdminViewHolder(lView);
        return lAdminViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdminAdapter.AdminViewHolder holder, int position) {
        InstUser lInstUser =mInstUserList.get(position);
        holder.mTvName.setText(lInstUser.getEmail());
        mViewModel.getAdminIdSet().observe((LifecycleOwner) mActivity, new Observer<HashSet<String>>() {
            @Override
            public void onChanged(HashSet<String> strings) {
                if(strings.contains(lInstUser.getUserId())){
                    holder.mCbUser.setChecked(true);
                }else {
                    holder.mCbUser.setChecked(false);
                }
            }
        });
        holder.mRCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.mCbUser.isChecked()){
                    holder.mCbUser.setChecked(false);
                    mViewModel.removeAdminFromSet(lInstUser.getUserId());
                }else{
                    holder.mCbUser.setChecked(true);
                    mViewModel.addAdminToSet(lInstUser.getUserId());

                }
            }
        });
        holder.mCbUser.setClickable(false);
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
            ArrayList<InstUser> lFilteredList=new ArrayList<InstUser>();
            if(constraint==null||constraint.length()==0){
                lFilteredList.addAll(mInstUserPreFilterList);
            }else {
                String lFilterPattern=constraint.toString().toLowerCase().trim();
                for (InstUser lUser:mInstUserPreFilterList){
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

        public AdminViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mRCard = itemView.findViewById(R.id.admin_card);
            mTvName = itemView.findViewById(R.id.tv_user_name);
            mCbUser = itemView.findViewById(R.id.user_checkbox);
        }
    }
}
