package com.example.finalyearproject.Activities.AddDomain;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.finalyearproject.Activities.AddAdmin.AddAdminViewModel;

import java.util.ArrayList;
import java.util.HashSet;

public class AddDomainViewModel extends AddAdminViewModel {
    private MutableLiveData<ArrayList<String>> membersNameList =
            new MutableLiveData<>(new ArrayList<String>());
    public MutableLiveData<HashSet<String>> getMembersNameSet() {
        return membersNameSet;
    }

    private MutableLiveData<HashSet<String>> membersNameSet =
            new MutableLiveData<>(new HashSet<>());
    public void addMemberToSet(String id){
        HashSet<String> tempSet=membersNameSet.getValue();
        tempSet.add(id);
        this.membersNameSet.setValue(tempSet);
    }
    public void removeMemberFromSet(String id){
        HashSet<String> tempSet=membersNameSet.getValue();
        tempSet.remove(id);
        removeAdminFromSet(id);
        this.membersNameSet.setValue(tempSet);

    }
    public MutableLiveData<ArrayList<String>> getMembersNameList(){
        HashSet<String> tempSet=membersNameSet.getValue();
        ArrayList<String> tempList=this.membersNameList.getValue();
        tempList.addAll(tempSet);
        this.membersNameList.setValue(tempList);
        return membersNameList;
    }

}
