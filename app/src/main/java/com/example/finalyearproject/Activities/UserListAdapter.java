package com.example.finalyearproject.Activities;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalyearproject.Activities.AddAdmin.AdminAdapter;
import com.example.finalyearproject.HelperClasses.FirebaseUtils;
import com.example.finalyearproject.Modules.InstUser;
import com.example.finalyearproject.Modules.User;
import com.example.finalyearproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> implements Filterable {
    private ArrayList<User> mInstUserList =new ArrayList<>();
    ArrayList<User> mAllInstUserList=new ArrayList<>();
    private final String mInstCode;
    private ArrayList<String> mUsers;

    public UserListAdapter(String instCode){
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
                                    mAllInstUserList.add(lUser);
                                    mInstUserList.add(lUser);
                                    UserListAdapter.this.notifyDataSetChanged();
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
    @NonNull
    @NotNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View lView= LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item,parent,false);
        UserListViewHolder lUserListViewHolder=new UserListViewHolder(lView);
        return lUserListViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull UserListAdapter.UserListViewHolder holder, int position) {
        User lUser= mInstUserList.get(position);
        holder.mUsername.setText(lUser.getEmail());
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
            UserListAdapter.this.notifyDataSetChanged();
        }
    };

    public class UserListViewHolder extends RecyclerView.ViewHolder{
        private final TextView mUsername;
        public UserListViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mUsername = itemView.findViewById(R.id.tv_user_name2);
        }
    }
}
