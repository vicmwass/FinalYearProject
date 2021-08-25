package com.example.finalyearproject.Activities.AddAdmin;

import android.widget.Filterable;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

public class AddAdminViewModel extends ViewModel {
    private MutableLiveData<ArrayList<String>> adminIdList =
            new MutableLiveData<>(new ArrayList<String>());
    private MutableLiveData<HashSet<String>> adminIdSet =
            new MutableLiveData<>(new HashSet<>());
    private MutableLiveData<HashSet<String>> currentAdminSet =
            new MutableLiveData<>(new HashSet<String>());
    private MutableLiveData<HashSet<String>> membersOfPrivateDomain =
            new MutableLiveData<>(new HashSet<>());



    public MutableLiveData<HashSet<String>> getMembersOfPrivateDomain(){
        return membersOfPrivateDomain;
    }
    public void setMembersOfPrivateDomain(ArrayList<String> membersOfPrivateDomain) {
        HashSet<String> tempSet=membersOfPrivateDomain.stream().collect(Collectors.toCollection(HashSet::new));
        this.membersOfPrivateDomain.setValue(tempSet);
    }


    public MutableLiveData<HashSet<String>> getCurrentAdminSet(){
        return currentAdminSet;
    }
    public void setCurrentAdminSet(ArrayList<String> currentAdmins) {
        HashSet<String> tempSet=currentAdmins.stream().collect(Collectors.toCollection(HashSet::new));
        this.currentAdminSet.setValue(tempSet);
    }


    public MutableLiveData<HashSet<String>> getAdminIdSet(){
        return this.adminIdSet;
    }
    public void addAdminToSet(String id){
        HashSet<String> tempSet= adminIdSet.getValue();
        tempSet.add(id);
        this.adminIdSet.setValue(tempSet);
    }
    public void removeAdminFromSet(String id){
        HashSet<String> tempSet= adminIdSet.getValue();
        tempSet.remove(id);
        this.adminIdSet.setValue(tempSet);
    }
    public void clearAdminSet(){
        HashSet<String> tempSet= adminIdSet.getValue();
        tempSet.clear();
        this.adminIdSet.setValue(tempSet);
    }


    public MutableLiveData<ArrayList<String>> getAdminIdList(){
        HashSet<String> tempSet= adminIdSet.getValue();
        ArrayList<String> tempList=this.adminIdList.getValue();
        tempList.addAll(tempSet);
        this.adminIdList.setValue(tempList);
        return adminIdList;
    }

}
