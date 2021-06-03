package com.example.finalyearproject;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class SharedViewModel extends ViewModel {


    private MutableLiveData<ArrayList<String>> idList = new MutableLiveData<>(new ArrayList<String>());
    private MutableLiveData<ArrayList<String>> domainNameList =
                new MutableLiveData<>(new ArrayList<String>(Arrays.asList("main")));
    private MutableLiveData<String> instCode = new MutableLiveData<>("");

    public MutableLiveData<String> getInstCode() {
        return instCode;
    }

    public void setInstCode(String instCode) {
        this.instCode.setValue(instCode);
    }

    public MutableLiveData<ArrayList<String>> getDomainNameList() {
        return domainNameList;
    }

    public void setDomainNameList(ArrayList<String>  domainNameList) {
        this.domainNameList.setValue(domainNameList);
    }


    public void setIdList(ArrayList<String> mIdList) {
        this.idList.setValue(mIdList);
    }
    public MutableLiveData<ArrayList<String>> getIdList() {
        return idList;
    }

}
