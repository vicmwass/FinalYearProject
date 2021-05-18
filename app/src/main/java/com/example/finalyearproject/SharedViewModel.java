package com.example.finalyearproject;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Vector;

public class SharedViewModel extends ViewModel {


    private MutableLiveData<ArrayList<String>> mIdList= new MutableLiveData<>(new ArrayList<String>());

    public void setIdList(ArrayList<String> mIdList) {
        this.mIdList.setValue(mIdList);
    }
    public MutableLiveData<ArrayList<String>> getIdList() {
        return mIdList;
    }

}
