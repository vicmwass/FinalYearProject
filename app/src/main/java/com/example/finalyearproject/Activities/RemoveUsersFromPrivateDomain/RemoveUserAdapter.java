package com.example.finalyearproject.Activities.RemoveUsersFromPrivateDomain;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalyearproject.HelperClasses.FirebaseUtils;
import com.example.finalyearproject.Modules.User;
import com.example.finalyearproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class RemoveUserAdapter extends RecyclerView.Adapter<RemoveUserAdapter.RemoveUserViewHolder> implements Filterable {
    private ArrayList<User> mInstUserList =new ArrayList<>();
    ArrayList<User> mAllInstUserList=new ArrayList<>();
    private final String mInstCode;
    private ArrayList<String> mUsers;
    private ArrayList<String> mCurrentMembers;
    private ArrayList<String> mAdmins;
    private RemoveUserViewModel mViewModel;

    public RemoveUserAdapter(String instCode,ArrayList<String> currentMembers,ArrayList<String> admins,RemoveUserViewModel removeUserViewModel){
        mInstCode = instCode;
        mAdmins=admins;
        mCurrentMembers=currentMembers;
        mViewModel =removeUserViewModel;
        populateData();
    }
    private void populateData() {
        mInstUserList.clear();
        mAllInstUserList.clear();
        for (String userid: mCurrentMembers) {
            FirebaseUtils.FIRESTORE.collection("users").document(userid)
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        User lUser=task.getResult().toObject(User.class).withId(task.getResult().getId());
                        if(!mAdmins.contains(lUser.getId())){
                            mAllInstUserList.add(lUser);
                            mInstUserList.add(lUser);
                            RemoveUserAdapter.this.notifyDataSetChanged();
                        }
                    }
                }
            });
        }
    }
    @NonNull
    @NotNull
    @Override
    public RemoveUserViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View lView= LayoutInflater.from(parent.getContext()).inflate(R.layout.select_users_card,parent,false);
        RemoveUserAdapter.RemoveUserViewHolder lRemoveUserViewHolder=new RemoveUserViewHolder(lView);
        return lRemoveUserViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RemoveUserAdapter.RemoveUserViewHolder holder, int position) {
        User lUser= mInstUserList.get(position);
        holder.mTvName.setText(lUser.getEmail());
        holder.mRCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(holder.mCbUser.isChecked()){
                    holder.mCbUser.setChecked(false);
                    mViewModel.removeMemberFromSet(lUser.getId());
                }else{
                    holder.mCbUser.setChecked(true);
                    mViewModel.addMemberToSet(lUser.getId());
                }
            }
        });
        holder.mCbUser.setClickable(false);
    }

    @Override
    public int getItemCount() {
        return mInstUserList.size();
    }

    public class RemoveUserViewHolder extends RecyclerView.ViewHolder{
        private final RelativeLayout mRCard;
        private final TextView mTvName;
        private final CheckBox mCbUser;

        public RemoveUserViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mRCard = itemView.findViewById(R.id.admin_card);
            mTvName = itemView.findViewById(R.id.tv_user_name);
            mCbUser = itemView.findViewById(R.id.user_checkbox);
        }
    }
        @Override
        public Filter getFilter() {
            return instUserFilter;
        }
        private Filter instUserFilter=new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                ArrayList<User> lFilteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    lFilteredList.addAll(mAllInstUserList);
                } else {
                    String lFilterPattern = constraint.toString().toLowerCase().trim();
                    for (User lUser : mAllInstUserList) {
                        if (lUser.getEmail().contains(lFilterPattern)) {
                            lFilteredList.add(lUser);
                        }
                    }
                }
                FilterResults lResults = new FilterResults();
                lResults.values = lFilteredList;
                return lResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mInstUserList.clear();
                mInstUserList.addAll((ArrayList) results.values);
                RemoveUserAdapter.this.notifyDataSetChanged();
            }
        };

}
