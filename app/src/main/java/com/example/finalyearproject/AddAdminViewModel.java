package com.example.finalyearproject;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

public class AddAdminViewModel extends ViewModel {
    private MutableLiveData<ArrayList<String>> adminNameList =
            new MutableLiveData<>(new ArrayList<String>());
    private MutableLiveData<HashSet<String>> adminNameSet =
            new MutableLiveData<>(new HashSet<>());
    private MutableLiveData<HashSet<String>> currentAdminNameSet =
            new MutableLiveData<>(new HashSet<String>());
    public MutableLiveData<HashSet<String>> getCurrentAdminNameSet(){
        return currentAdminNameSet;
    }
    public void setCurrentAdminNameSet(ArrayList<String> currentAdmins) {
        HashSet<String> tempSet=currentAdmins.stream().collect(Collectors.toCollection(HashSet::new));
        this.currentAdminNameSet.setValue(tempSet);
    }
    public void addAdminToSet(String id){
        HashSet<String> tempSet=adminNameSet.getValue();
        tempSet.add(id);
        this.adminNameSet.setValue(tempSet);
    }
    public void removeAdminFromSet(String id){
        HashSet<String> tempSet=adminNameSet.getValue();
        tempSet.remove(id);
        this.adminNameSet.setValue(tempSet);

    }
    public MutableLiveData<ArrayList<String>> getAdminNameList(){
        HashSet<String> tempSet=adminNameSet.getValue();
        ArrayList<String> tempList=this.adminNameList.getValue();
        tempList.addAll(tempSet);
        this.adminNameList.setValue(tempList);
        return adminNameList;
    }

}
