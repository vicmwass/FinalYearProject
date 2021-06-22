package com.example.finalyearproject;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.AdminViewHolder> {
    ArrayList<InstUser> mInstUserList=new ArrayList<InstUser>();
//    Activity mActivity;
//    ArrayList<String> mInstNameList;
    AddAdminViewModel mViewModel;

    String mInstCode;

    public AdminAdapter(String instCode,AddAdminViewModel viewModel) {
        this.mInstCode=instCode;
        this.mViewModel=viewModel;
        populateData();
    }

    private void populateData() {
            CollectionReference tempRef = FirebaseUtils.FIRESTORE.collection("Institutions").document(mInstCode).collection("Users");


        tempRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.e("SnapshotListener", error.getMessage());
                    return;
                }
                if(value.getDocumentChanges().size()>0) {
                    HashSet<String> currentAdmins=mViewModel.getCurrentAdminNameSet().getValue();

                    for (DocumentChange dc : value.getDocumentChanges()) {
                        switch (dc.getType()) {
                            case ADDED:
                                String id = dc.getDocument().getId();
                                InstUser lInstUser = dc.getDocument().toObject(InstUser.class);
                                if(!currentAdmins.contains(lInstUser.getUserId())){
                                    mInstUserList.add(lInstUser);
                                }
                                Log.d("DomainName", lInstUser.getEmail());
                                AdminAdapter.this.notifyDataSetChanged();
                                break;
                            case MODIFIED:
                                break;
                            case REMOVED:
                                break;
                        }
                    }
                }
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
