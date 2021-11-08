package com.example.finalyearproject.Activities.Main.Notices;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalyearproject.Activities.Launch.LaunchActivity;
import com.example.finalyearproject.Activities.Main.MainActivity;
import com.example.finalyearproject.Activities.Main.SharedViewModel;
import com.example.finalyearproject.Activities.OpenNotice.OpenNoticeActivity;
import com.example.finalyearproject.Activities.ViewUsers.ListAdmins.ViewAdminsAdapter;
import com.example.finalyearproject.HelperClasses.FirebaseUtils;
import com.example.finalyearproject.Modules.Notice;
import com.example.finalyearproject.Modules.User;
import com.example.finalyearproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.NoticeViewHolder> implements Filterable {
    ArrayList<Notice> mNoticeList=new ArrayList<Notice>();
    ArrayList<Notice> mAllNoticeList=new ArrayList<Notice>();
    private ArrayList<String> mSenderList =new ArrayList<>();
    private ArrayList<String> mAllSenderList =new ArrayList<>();
    ArrayList <String> mIdList = new ArrayList<String>();
    Context mContext;
    private CollectionReference mNoticeRef;
    SharedViewModel mViewModel;
    public final FirebaseAuth mFirebaseAuth=FirebaseAuth.getInstance();

    public NoticeAdapter(Context context, SharedViewModel viewModel){
        mContext=context;
        this.mViewModel=viewModel;

        }


    private void populateArray() {
        mNoticeList.clear();
        mAllNoticeList.clear();
        mNoticeRef = FirebaseUtils.FIRESTORE.collection("Institutions").document(mViewModel.getInstCode().getValue()).collection("notices");
        if(mIdList.size()>0){
                mNoticeRef = mNoticeRef.document(mIdList.get(mIdList.size()-1)).collection("my_notices");
        }else{
            mNoticeRef = mNoticeRef.document("0").collection("my_notices");
        }

        mNoticeRef.orderBy("timeStamp", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                mAllNoticeList.add(lNotice);
                                FirebaseUtils.FIRESTORE.collection("users").document(lNotice.getSenderId())
                                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            mAllSenderList.add((String) task.getResult().get(User.USERNAME));
                                            if(mAllSenderList.size()==mAllNoticeList.size()){
                                                NoticeAdapter.this.notifyDataSetChanged();
                                            }
                                        }
                                    }
                                });
                                Log.d("NoticeSender", lNotice.getSenderId());
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
        FirebaseUtils.FIRESTORE.collection("users").document(lNotice.getSenderId())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    holder.tvSender.setText((String) task.getResult().get(User.USERNAME));
                }
            }
        });

        holder.tvSubject.setText(lNotice.getSubject());
        holder.rlCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent lIntent=new Intent(mContext, OpenNoticeActivity.class);
                lIntent.putExtra(MainActivity.NOTICE,lNotice);
                lIntent.putExtra(LaunchActivity.INSTITUTION_CODE,mViewModel.getInstCode().getValue());
                lIntent.putExtra("Domain ids",mViewModel.getIdList().getValue());
                mContext.startActivity(lIntent);
            }
        });
        if(lNotice.getDescription()!=null){
            holder.tvDescription.setVisibility(View.VISIBLE);
            holder.tvDescription.setText(lNotice.getDescription());
        }else{
            holder.tvDescription.setVisibility(View.INVISIBLE);
        }

        if(lNotice.getTimeStamp()!=null){
        Date currentDate = new Date(lNotice.getTimeStamp()*1000);
        SimpleDateFormat dateFormat= new SimpleDateFormat("dd MMM ");
        String dateOnly = dateFormat.format(currentDate);
        holder.tvSentDate.setText(dateOnly);
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
//        TextView tvFrom;
//        TextView tvFromDomain;
        TextView tvSentDate;

        public NoticeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSender=itemView.findViewById(R.id.tv_sender);
            tvSubject=itemView.findViewById(R.id.tv_subject);
            tvDescription=itemView.findViewById(R.id.tv_description);
            rlCard=itemView.findViewById(R.id.notice_card);
//            tvFrom = itemView.findViewById(R.id.tv_from);
//            tvFromDomain = itemView.findViewById(R.id.tv_from_domain_name);
            tvSentDate=itemView.findViewById(R.id.tv_sent_date);
//            button=itemView.findViewById(R.id.notice_download_button);
        }
    }

    @Override
    public Filter getFilter() {
        return instUserFilter;
    }
    private Filter instUserFilter=new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Notice> lFilteredList=new ArrayList<>();
            if(constraint==null||constraint.length()==0){
                lFilteredList.addAll(mAllNoticeList);
            }else {
                String lFilterPattern=constraint.toString().toLowerCase().trim();
                for (Notice lNotice:mAllNoticeList){
                    if(!lFilteredList.contains(lNotice)){
                    if(lNotice.getSubject().contains(lFilterPattern)){
                        lFilteredList.add(lNotice);
                    }else if(lNotice.getDescription().contains(lFilterPattern)){
                        lFilteredList.add(lNotice);
                    }
                    }
                }
                for(int i=0;i<mAllSenderList.size();i++){
//                    mSenderList.clear();
                    if(!lFilteredList.contains(mAllNoticeList.get(i))&&
                            mAllSenderList.get(i).contains(lFilterPattern)){
                        lFilteredList.add(mAllNoticeList.get(i));
                    }

                }
            }
            FilterResults  lResults=new FilterResults();
            lResults.values=lFilteredList;
            return lResults;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mNoticeList.clear();
            mNoticeList.addAll((ArrayList)results.values);
            NoticeAdapter.this.notifyDataSetChanged();
        }
    };

}
