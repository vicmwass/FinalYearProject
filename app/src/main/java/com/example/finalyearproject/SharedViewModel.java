package com.example.finalyearproject;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class SharedViewModel extends ViewModel {


    private MutableLiveData<ArrayList<String>> idList = new MutableLiveData<>(new ArrayList<String>());
    private MutableLiveData<String> domainName = new MutableLiveData<>("main");

    public MutableLiveData<String> getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName.setValue(domainName);
    }


    public void setIdList(ArrayList<String> mIdList) {
        this.idList.setValue(mIdList);
    }
    public MutableLiveData<ArrayList<String>> getIdList() {
        return idList;
    }

}
