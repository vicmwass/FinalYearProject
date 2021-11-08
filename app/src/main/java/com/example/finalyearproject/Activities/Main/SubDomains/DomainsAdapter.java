package com.example.finalyearproject.Activities.Main.SubDomains;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalyearproject.Activities.AddUsersToPrivateDomain.AddUserToPrivateDomainAdapter;
import com.example.finalyearproject.Activities.Main.Notices.NoticeAdapter;
import com.example.finalyearproject.Activities.Main.SharedViewModel;
import com.example.finalyearproject.Activities.ViewUsers.ListAdmins.ViewAdminsAdapter;
import com.example.finalyearproject.HelperClasses.FirebaseUtils;
import com.example.finalyearproject.Modules.Domain;
import com.example.finalyearproject.Modules.Notice;
import com.example.finalyearproject.Modules.User;
import com.example.finalyearproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;

public class DomainsAdapter extends RecyclerView.Adapter<DomainsAdapter.DomainsViewHolder> implements Filterable {
    ArrayList<Domain> mDomainList=new ArrayList<Domain>();
    ArrayList<Domain> mAllDomainList=new ArrayList<Domain>();
    ArrayList<String> mIdList = new ArrayList<String>();
    ArrayList<String> mDomainNameList = new ArrayList<String>();
    SharedViewModel mViewModel;
    private CollectionReference mDomainsRef;
    private final String mUserId = FirebaseAuth.getInstance().getUid();
    //    private DocumentReference mDocDomainRef;

    public DomainsAdapter( SharedViewModel viewModel){
        this.mViewModel=viewModel;
    }

    public void populateArray() {
        mDomainList.clear();
        mAllDomainList.clear();
        mDomainsRef = FirebaseUtils.FIRESTORE.collection("Institutions").document(mViewModel.getInstCode().getValue()).collection("domains");
        if(mIdList.size()>0){
             for(String Id:mIdList){
                 mDomainsRef = mDomainsRef.document(Id).collection("domains");
            }
            mDomainsRef.getParent().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {

                    Domain lDomain = task.getResult().toObject(Domain.class).withId(task.getResult().getId());
//                        ArrayList<String> adminList = (ArrayList<String>) task.getResult().get("adminList");

                        if (!lDomain.getPrivate()){
                            if(mViewModel.getPrivacyLevel().getValue()>0){
                                if (lDomain.getAdminList().contains(mUserId)) {
                                    mViewModel.setPrivateDomainAdminLevel(lDomain.getId());
                                }
                            }else{
                                if (lDomain.getAdminList().contains(mUserId)) {
                                    mViewModel.setAdminLevel(lDomain.getId());

                                }
                            }
                        }else{
                            mViewModel.setPrivateMemberList(lDomain.getMemberList());
                            if (lDomain.getAdminList().contains(mUserId)) {
                                mViewModel.setPrivateDomainAdminLevel(lDomain.getId());
                            }
                        }

                        mViewModel.setDomainAdminList(lDomain.getAdminList());
                    }
                }
            });
        }

        mDomainsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                Domain domain = dc.getDocument().toObject(Domain.class).withId(id);
                                if(domain.getPrivate()){
                                    HashSet<String> memberSet=new HashSet<>(domain.getMemberList());
                                    if(memberSet.contains(FirebaseAuth.getInstance().getUid())){
                                        mDomainList.add(domain);
                                    }
                                }else {
                                    mDomainList.add(domain);
                                }
                                Log.d("DomainName", domain.getName());
                                DomainsAdapter.this.notifyDataSetChanged();
                                break;
                            case MODIFIED:
                                break;
                            case REMOVED:
                                break;
                        }
                    }
                    mAllDomainList.addAll(mDomainList);
                }
            }
        });

    }
    public void setDomainNameList(ArrayList<String> domainNameList){
        this.mDomainNameList=domainNameList;
    }
    public void populateData(ArrayList<String> idList){
        this.mIdList=idList;
        populateArray();
        notifyDataSetChanged();
    }

    @Override
    public DomainsViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.domain_name_card, parent, false);
        DomainsViewHolder lViewHolder = new DomainsViewHolder(view);
        return lViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull DomainsViewHolder holder, int position) {
        Domain lDomain = mDomainList.get(position);
        holder.tvDName.setText(lDomain.getName());
        holder.rlCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!lDomain.getPrivate()){
                    if(mViewModel.getPrivacyLevel().getValue()>0){
                        mViewModel.incrementPrivacyLevel();
                    }
                }else {
                    mViewModel.incrementPrivacyLevel();
                }
                if (lDomain.getChatGroup()){
                    mViewModel.addChatGroupId(lDomain.getId());
                }
                mIdList.add(lDomain.getId());
                mViewModel.setIdList(mIdList);
                mDomainNameList.add(lDomain.getName());
                mViewModel.setDomainNameList(mDomainNameList);
            }
        });


    }

    @Override
    public int getItemCount() {
            return mDomainList.size();
    }

    public class DomainsViewHolder extends RecyclerView.ViewHolder {
        TextView tvDName;
        RelativeLayout rlCard;

        public DomainsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDName=itemView.findViewById(R.id.tv_domain_name);
            rlCard=itemView.findViewById(R.id.domain_card);
        }
    }

    @Override
    public Filter getFilter() {
        return instUserFilter;
    }
    private Filter instUserFilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Domain> lFilteredList=new ArrayList<>();
            if(constraint==null||constraint.length()==0){
                lFilteredList.addAll(mAllDomainList);
            }else {
                String lFilterPattern=constraint.toString().toLowerCase().trim();
                for (Domain lDomain:mAllDomainList){
                    if(lDomain.getName().contains(lFilterPattern)){
                        lFilteredList.add(lDomain);
                    }
                }
            }
            FilterResults  lResults=new FilterResults();
            lResults.values=lFilteredList;
            return lResults;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mDomainList.clear();
            mDomainList.addAll((ArrayList)results.values);
            DomainsAdapter.this.notifyDataSetChanged();
        }
    };
}
