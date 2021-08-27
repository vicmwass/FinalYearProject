package com.example.finalyearproject.Activities.Main.ChatGroup;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.finalyearproject.Activities.Main.SharedViewModel;
import com.example.finalyearproject.HelperClasses.FirebaseUtils;
import com.example.finalyearproject.Modules.ChatMessage;
import com.example.finalyearproject.R;
import com.google.firebase.firestore.FieldValue;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class ChatFragment extends Fragment {

    private SharedViewModel mViewModel;
    private EditText mInputChat;
    private ImageView mSendChat;
    private ArrayList<String> mIdList=new ArrayList<String>();
    private ChatAdapter mChatAdapter;
    private RecyclerView mChatRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.chat_fragment, container, false);


    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        // TODO: Use the ViewModel
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViewModel(view);

        mInputChat = view.findViewById(R.id.inputsms);
        mSendChat = view.findViewById(R.id.imageViewsendtext);
        mSendChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendText();
            }
        });


    }

    public void setupViewModel(@NotNull View view) {
        ViewModelProvider viewModelProvider = new ViewModelProvider(getActivity().getViewModelStore(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()));
        mViewModel =viewModelProvider.get(SharedViewModel.class);
        setupAdapter(view);

        mViewModel.getIdList().observe(getViewLifecycleOwner(), new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(@Nullable ArrayList<String> idList) {
                mIdList=idList;
                mChatAdapter.populateData(idList);

            }
        });
    }

    private void sendText() {
        String text =mInputChat.getText().toString().trim();
        if (text.length() == 0) {
            mInputChat.setError("text required");
            mInputChat.requestFocus();
            return;
        }
        mInputChat.setText("");
        ChatMessage chat=new ChatMessage();
        chat.setMessage(text);
        chat.setUsername(FirebaseUtils.sFirebaseAuth.getCurrentUser().getDisplayName());
//        Long tsLong = System.currentTimeMillis()/1000;
        chat.addTimeStampToken(FieldValue.serverTimestamp());
        FirebaseUtils.addChat(mViewModel.getInstCode().getValue(),getActivity(),chat,mViewModel.getIdList().getValue().get(mViewModel.getIdList().getValue().size()-1));

    }
    public void setupAdapter(@NotNull View view) {
        mChatRecyclerView = view.findViewById(R.id.chat_recycler_view);
        mChatAdapter = new ChatAdapter(getActivity(),mViewModel,mChatRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mChatRecyclerView.setLayoutManager(mLinearLayoutManager);
        mChatRecyclerView.setAdapter(mChatAdapter);

    }
}