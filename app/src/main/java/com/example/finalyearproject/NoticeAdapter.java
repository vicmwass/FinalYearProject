package com.example.finalyearproject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder>{
    ArrayList<Notice> mNoticeList=new ArrayList<Notice>();
    ArrayList <String> mIdList = new ArrayList<String>();
    Context mContext;
    private CollectionReference mNoticeRef;
    SharedViewModel mViewModel;

    public NoticeAdapter(Context context, SharedViewModel viewModel){
        mContext=context;
        this.mViewModel=viewModel;

        }


    private void populateArray() {
        mNoticeList.clear();
        mNoticeRef = FirebaseUtils.FIRESTORE.collection("Institutions").document(mViewModel.getInstCode().getValue()).collection("notices");
        if(mIdList.size()>0){
                mNoticeRef = mNoticeRef.document(mIdList.get(mIdList.size()-1)).collection("my_notices");
        }else{
            mNoticeRef = mNoticeRef.document("0").collection("my_notices");
        }

        mNoticeRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                Notice lNotice = dc.getDocument().toObject(Notice.class).withId(id);
                                mNoticeList.add(lNotice);
                                Log.d("NoticeSender", lNotice.getSender());
                                NoticeAdapter.this.notifyDataSetChanged();
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
    public void populateData(ArrayList<String> idList){
        this.mIdList=idList;
        populateArray();
        notifyDataSetChanged();
    }


    @Override
    public NoticeViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View lView= LayoutInflater.from(parent.getContext()).inflate(R.layout.notice_card,parent,false);
        NoticeViewHolder lNoticeViewHolder=new NoticeViewHolder(lView);
        return lNoticeViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeAdapter.NoticeViewHolder holder, int position) {
        Notice lNotice = mNoticeList.get(position);
        holder.tvSender.setText(lNotice.getSender());
        holder.tvSubject.setText(lNotice.getSubject());
        holder.rlCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent lIntent=new Intent(mContext,NoticeActivity.class);
                lIntent.putExtra(MainActivity.NOTICE,lNotice);
                mContext.startActivity(lIntent);
            }
        });
        if(lNotice.getDescription()!=null){
            holder.tvDescription.setVisibility(View.VISIBLE);
            holder.tvDescription.setText(lNotice.getDescription());
        }else{
            holder.tvDescription.setVisibility(View.INVISIBLE);
        }

        if(lNotice.getDomainName()==null){
            holder.tvFrom.setVisibility(View.INVISIBLE);
            holder.tvFromDomain.setVisibility(View.INVISIBLE);
        }else{
            holder.tvFrom.setVisibility(View.VISIBLE);
            holder.tvFromDomain.setVisibility(View.VISIBLE);
            holder.tvFromDomain.setText(lNotice.getDomainName());
        }


    }

    @Override
    public int getItemCount() {
        return mNoticeList.size();
    }

    public class NoticeViewHolder extends RecyclerView.ViewHolder {
        TextView tvSender;
        TextView tvSubject;
        TextView tvDescription;
        Button button;
        RelativeLayout rlCard;
        TextView tvFrom;
        TextView tvFromDomain;

        public NoticeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSender=itemView.findViewById(R.id.tv_sender);
            tvSubject=itemView.findViewById(R.id.tv_subject);
            tvDescription=itemView.findViewById(R.id.tv_description);
            rlCard=itemView.findViewById(R.id.notice_card);
            tvFrom = itemView.findViewById(R.id.tv_from);
            tvFromDomain = itemView.findViewById(R.id.tv_from_domain_name);
//            button=itemView.findViewById(R.id.notice_download_button);
        }
    }



        }
