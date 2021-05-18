package com.example.finalyearproject;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    public NoticeAdapter(Context context){
        mContext=context;

        }


    private void populateArray() {
        mNoticeList.clear();
        mNoticeRef = FirebaseUtils.mFireStore.collection("notices");
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
        holder.tvFile.setText(lNotice.getFileName());
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUtils.saveFileLocally(mContext,lNotice);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mNoticeList.size();
    }

    public class NoticeViewHolder extends RecyclerView.ViewHolder {
        TextView tvSender;
        TextView tvSubject;
        TextView tvFile;
        Button button;

        public NoticeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSender=itemView.findViewById(R.id.tv_sender);
            tvSubject=itemView.findViewById(R.id.tv_subject);
            tvFile=itemView.findViewById(R.id.tv_file);
            button=itemView.findViewById(R.id.notice_download_button);
        }
    }



        }
