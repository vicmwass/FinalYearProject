package com.example.finalyearproject.Activities.Main.SubDomains;

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

import com.example.finalyearproject.Activities.Main.SharedViewModel;
import com.example.finalyearproject.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class DomainListFragment extends Fragment {
    private RecyclerView mDomainRecyclerView;
    private DomainsAdapter mDomainsAdapter;
    private SharedViewModel mViewModel;
    private ArrayList<String> mIdList=new ArrayList<String>();
    private ArrayList<String> mDomainNameList=new ArrayList<String>();
    private SwipeRefreshLayout mSwipeRefreshLayout;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable  Bundle savedInstanceState) {
        View lView = inflater.inflate(R.layout.domain_container, container, false);
        return lView;
    }

    @Override
    public void onViewCreated(@NonNull  View view, @Nullable  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(getActivity()).get(SharedViewModel.class);
        setupAdapter(view);
        setupSwipeRefresh(view);

    }

    public void setupSwipeRefresh(@NotNull View view) {
        mSwipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mDomainsAdapter.populateData(mIdList);
                mDomainsAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);

            }
        });
    }

    public void setupAdapter(@NotNull View view) {
        mDomainRecyclerView = view.findViewById(R.id.domain_recycler_view);
        mDomainsAdapter = new DomainsAdapter(mViewModel);
        mDomainRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        mDomainRecyclerView.setAdapter(mDomainsAdapter);
    }

    @Override
    public void onActivityCreated(@Nullable  Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel.getIdList().observe(getViewLifecycleOwner(), new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(@Nullable ArrayList<String> idList) {
                mIdList=idList;
                mDomainsAdapter.populateData(idList);
                mDomainsAdapter.notifyDataSetChanged();
            }
        });
        mViewModel.getDomainNameList().observe(getViewLifecycleOwner(), new Observer<ArrayList<String>>() {
            @Override
            public void onChanged(ArrayList<String> domainNameList) {
                mDomainsAdapter.setDomainNameList(domainNameList);
            }
        });
    }

}
