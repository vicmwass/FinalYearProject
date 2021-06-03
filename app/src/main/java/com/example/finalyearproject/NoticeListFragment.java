package com.example.finalyearproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

public class NoticeListFragment extends Fragment {
    private RecyclerView mNoticeRecyclerView;
    private NoticeAdapter mNoticeAdapter;
    private SharedViewModel mViewModel;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<String> mIdList=new ArrayList<String>();



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View lView = inflater.inflate(R.layout.notices_container, container, false);
        mViewModel = new ViewModelProvider(getActivity()).get(SharedViewModel.class);
        mNoticeRecyclerView = lView.findViewById(R.id.notice_recycler_view);
        mNoticeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mNoticeAdapter = new NoticeAdapter(getContext(),mViewModel);
        mNoticeRecyclerView.setAdapter(mNoticeAdapter);
        mSwipeRefreshLayout = lView.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mNoticeAdapter.populateData(mIdList);
                mNoticeAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);

            }
        });
        return lView;
    }
    @Override
    public void onActivityCreated(@Nullable  Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ViewModelProvider viewModelProvider = new ViewModelProvider(getActivity().getViewModelStore(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()));
        mViewModel =viewModelProvider.get(SharedViewModel.class);
        mViewModel.getIdList().observe(getActivity(), new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(@Nullable ArrayList<String> idList) {
                mIdList=idList;
                mNoticeAdapter.populateData(idList);
                mNoticeAdapter.notifyDataSetChanged();
            }
        });

    }



}
