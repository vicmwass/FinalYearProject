package com.example.finalyearproject.Activities.Main.Notices;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.finalyearproject.Activities.Main.SharedViewModel;
import com.example.finalyearproject.R;

import org.jetbrains.annotations.NotNull;

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
        setupAdapter(lView);
        setupSwipeRefresh(lView);

        return lView;
    }

    public void setupAdapter(View view) {
        mNoticeRecyclerView = view.findViewById(R.id.notice_recycler_view);
        mNoticeRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mNoticeAdapter = new NoticeAdapter(getContext(),mViewModel);
        mNoticeRecyclerView.setAdapter(mNoticeAdapter);
    }

    public void setupSwipeRefresh(View view) {
        mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mNoticeAdapter.populateData(mIdList);
                mNoticeAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);

            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable  Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView mSearchView = getView().findViewById(R.id.action_search);
        mSearchView.setSearchableInfo(searchManager
                .getSearchableInfo(getActivity().getComponentName()));
        mSearchView.setMaxWidth(Integer.MAX_VALUE);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mNoticeAdapter.getFilter().filter(newText);
                return false;
            }
        });


    }


    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ViewModelProvider viewModelProvider = new ViewModelProvider(getActivity().getViewModelStore(),
                ViewModelProvider.AndroidViewModelFactory.getInstance(getActivity().getApplication()));
        mViewModel =viewModelProvider.get(SharedViewModel.class);
        mViewModel.getIdList().observe(getViewLifecycleOwner(), new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(@Nullable ArrayList<String> idList) {
                mIdList=idList;
                mNoticeAdapter.populateData(idList);
                mNoticeAdapter.notifyDataSetChanged();
            }
        });
    }
}
