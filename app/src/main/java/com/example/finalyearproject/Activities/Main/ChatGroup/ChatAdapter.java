package com.example.finalyearproject.Activities.Main.ChatGroup;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
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
import com.example.finalyearproject.Modules.ChatMessage;
import com.example.finalyearproject.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    ArrayList<ChatMessage> mChatList =new ArrayList<ChatMessage>();
    ArrayList <String> mIdList = new ArrayList<String>();
    Context mContext;
    private CollectionReference mChatRef;
    SharedViewModel mViewModel;
    private final RecyclerView mRecyclerView;
    public FirebaseAuth mFirebaseAuth;

    public ChatAdapter(Context context,SharedViewModel viewModel,RecyclerView recyclerView){
        mContext=context;
        mViewModel=viewModel;
        mRecyclerView = recyclerView;
        mFirebaseAuth = FirebaseAuth.getInstance();
    }
    private void populateArray() {
        mChatList.clear();
        mChatRef = FirebaseUtils.FIRESTORE.collection("Institutions").document(mViewModel.getInstCode().getValue()).collection("chats");
        mChatRef = mChatRef.document(mIdList.get(mIdList.size()-1)).collection(FirebaseUtils.MY_CHATS);


        mChatRef.orderBy("timeStamp", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                ChatMessage lChatMessage = dc.getDocument().toObject(ChatMessage.class).withId(id);
                                mChatList.add(lChatMessage);
                                Log.d("Chat Display", lChatMessage.getUsername());
                                ChatAdapter.this.notifyDataSetChanged();
                                int sz=getItemCount();
                                mRecyclerView.scrollToPosition(sz-1);
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
//    public void

    @NonNull
    @NotNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View lView= LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item,parent,false);
        ChatViewHolder lChatViewHolder=new ChatViewHolder(lView);
        return lChatViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ChatAdapter.ChatViewHolder holder, int position) {
        ChatMessage chat=mChatList.get(position);
        holder.mText.setText(chat.getMessage());
        holder.mChatContainer.setGravity(Gravity.LEFT);
        if(mFirebaseAuth.getCurrentUser().getDisplayName().equals(chat.getUsername())){
            holder.mUsername.setText("you");
            holder.mChatContainer.setGravity(Gravity.RIGHT);
        }else {
            holder.mUsername.setText(chat.getUsername());
        }

    }
    private Date getDate(long timeStamp){
        Date ts=new Date(timeStamp*1000);
        return ts;
    }

    @Override
    public int getItemCount() {
        return mChatList.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {

        private final TextView mText;
        private final TextView mUsername;
        private final RelativeLayout mChatContainer;

        public ChatViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mText = itemView.findViewById(R.id.message);
            mUsername = itemView.findViewById(R.id.username_chat);
            mChatContainer = itemView.findViewById(R.id.chat_container);
        }
    }
}
