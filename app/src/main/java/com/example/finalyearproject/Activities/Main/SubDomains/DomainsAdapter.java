package com.example.finalyearproject.Activities.Main.SubDomains;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalyearproject.Activities.Main.SharedViewModel;
import com.example.finalyearproject.HelperClasses.FirebaseUtils;
import com.example.finalyearproject.Modules.Domain;
import com.example.finalyearproject.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashSet;

public class DomainsAdapter extends RecyclerView.Adapter<DomainsAdapter.DomainsViewHolder> {
    ArrayList<Domain> mDomainList=new ArrayList<Domain>();
    ArrayList<String> mIdList = new ArrayList<String>();
    ArrayList<String> mDomainNameList = new ArrayList<String>();
    SharedViewModel mViewModel;
    private CollectionReference mDomainsRef;
//    private DocumentReference mDocDomainRef;

    public DomainsAdapter( SharedViewModel viewModel){
        this.mViewModel=viewModel;
    }

    public void populateArray() {
        mDomainList.clear();
        mDomainsRef = FirebaseUtils.FIRESTORE.collection("Institutions").document(mViewModel.getInstCode().getValue()).collection("domains");
        if(mIdList.size()>0){
             for(String Id:mIdList){
                mDomainsRef = mDomainsRef.document(Id).collection("domains");
            }
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
                                    if(memberSet.contains(FirebaseUtils.sFirebaseAuth.getUid())){
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
        String userId = FirebaseUtils.sFirebaseAuth.getUid();
        holder.tvDName.setText(lDomain.getName());
        holder.rlCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIdList.add(lDomain.getId());
                mViewModel.setIdList(mIdList);
                mDomainNameList.add(lDomain.getName());
                mViewModel.setDomainNameList(mDomainNameList);
                if (!lDomain.getPrivate()){
                    mViewModel.setDomainAdminList(lDomain.getAdminList());
                    if(mViewModel.getPrivacyLevel().getValue()>0){
                        mViewModel.incrementPrivacyLevel();
                        if (lDomain.getAdminList().contains(userId)) {
                            mViewModel.setPrivateDomainAdminLevel(lDomain.getId());
                        }
                    }else{
                        if (lDomain.getAdminList().contains(userId)) {
                            mViewModel.setAdminLevel(lDomain.getId());
                        }
                    }
            }else{
                    mViewModel.incrementPrivacyLevel();
                    mViewModel.setDomainAdminList(lDomain.getAdminList());
                    mViewModel.setPrivateMemberList(lDomain.getMemberList());
                    if (lDomain.getAdminList().contains(userId)) {
                        mViewModel.setPrivateDomainAdminLevel(lDomain.getId());
                    }
                }

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
}
