package com.example.finalyearproject.Activities.AddUsersToPrivateDomain;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashSet;

public class UserViewModel extends ViewModel {
    private MutableLiveData<ArrayList<String>> membersIdList =
            new MutableLiveData<>(new ArrayList<String>());
    private MutableLiveData<HashSet<String>> membersIdSet =
            new MutableLiveData<>(new HashSet<>());


    public MutableLiveData<HashSet<String>> getMembersIdSet() {
        return membersIdSet;
    }
    public void addMemberToSet(String id){
        HashSet<String> tempSet= membersIdSet.getValue();
        tempSet.add(id);
        this.membersIdSet.setValue(tempSet);
    }
    public void removeMemberFromSet(String id){
        HashSet<String> tempSet= membersIdSet.getValue();
        tempSet.remove(id);
        this.membersIdSet.setValue(tempSet);

    }
    public MutableLiveData<ArrayList<String>> getMembersIdList(){
        HashSet<String> tempSet= membersIdSet.getValue();
        ArrayList<String> tempList=new ArrayList<>();
        tempList.addAll(tempSet);
        this.membersIdList.setValue(tempList);
        return membersIdList;
    }
}
