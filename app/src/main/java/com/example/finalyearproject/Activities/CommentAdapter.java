package com.example.finalyearproject.Activities;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.finalyearproject.HelperClasses.FirebaseUtils;
import com.example.finalyearproject.Modules.Text;
import com.example.finalyearproject.Modules.User;
import com.example.finalyearproject.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    ArrayList<Text> mCommentsList =new ArrayList<>();
    ArrayList<String> mUsernames =new ArrayList<>();
    Context mContext;
    String mInstCode;
    String mNoticeId;
    private CollectionReference mCommentRef;
    private final RecyclerView mRecyclerView;
//    public FirebaseAuth mFirebaseAuth;

    public CommentAdapter(Context context,String instCode,String noticeId,RecyclerView recyclerView){
        mContext=context;
        mInstCode=instCode;
        mNoticeId=noticeId;
        mRecyclerView=recyclerView;
        populateArray();
    }

    private void populateArray() {
        mCommentsList.clear();
        mCommentRef = FirebaseUtils.FIRESTORE.collection("Institutions").document(mInstCode).collection(FirebaseUtils.COMMENTS);
        mCommentRef = mCommentRef.document(mNoticeId).collection(FirebaseUtils.MY_COMMENTS);


        mCommentRef.orderBy("timeStamp", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                Text lComment = dc.getDocument().toObject(Text.class).withId(id);
                                mCommentsList.add(lComment);
                                CommentAdapter.this.notifyDataSetChanged();
                                int sz=getItemCount();
                                mRecyclerView.scrollToPosition(sz-1);
                                break;
                            case MODIFIED:
                            case REMOVED:
                                break;
                        }
                    }
                }

            }
        });

    }
    @NonNull
    @NotNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View lView= LayoutInflater.from(parent.getContext()).inflate(R.layout.comment_item,parent,false);
        CommentViewHolder lViewHolder=new CommentViewHolder(lView);
        return lViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CommentAdapter.CommentViewHolder holder, int position) {
        Text lComment=mCommentsList.get(position);
            FirebaseUtils.FIRESTORE.collection("users").document(lComment.getUserID())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()){
//                        mUsernames.add();
                        holder.mUsernameTv.setText((String) task.getResult().get(User.USERNAME));
                        Log.d("Comment user", (String) task.getResult().get(User.USERNAME));
                    }
                }
            });
        holder.mCommentTv.setText(lComment.getMessage());
    }

    @Override
    public int getItemCount() {
        return mCommentsList.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{

        private final TextView mUsernameTv;
        private final TextView mCommentTv;

        public CommentViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mUsernameTv = itemView.findViewById(R.id.username_comment);
            mCommentTv = itemView.findViewById(R.id.textview_comment);

        }
    }
}
