 package com.example.finalyearproject;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Collectors;

 public class SharedViewModel extends ViewModel {

    private MutableLiveData<String> adminLevel=
            new MutableLiveData<>("");
    private MutableLiveData<ArrayList<String>> idList =
            new MutableLiveData<>(new ArrayList<String>());
    private MutableLiveData<ArrayList<String>> domainNameList =
                new MutableLiveData<>(new ArrayList<String>(Arrays.asList("main")));
    private MutableLiveData<String> instCode =
            new MutableLiveData<>("");
    private MutableLiveData<ArrayList<String>> currentAdminList =
            new MutableLiveData<>(new ArrayList<String>());
    private MutableLiveData<ArrayList<String>> domainAdminList =
            new MutableLiveData<>(new ArrayList<String>());

    public MutableLiveData<String> getAdminLevel(){
        return adminLevel;
    }

    public void setAdminLevel(String level){
        adminLevel.setValue(level);
    }
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

    public MutableLiveData<ArrayList<String>> getCurrentAdminList(){
        return currentAdminList;
    }
    public void setDomainAdminList(ArrayList<String> admins){
        this.domainAdminList.setValue(admins);
        addCurrentAdmins();
    }

    private void addCurrentAdmins(){
        ArrayList<String> tempList=this.currentAdminList.getValue();
        tempList.addAll(domainAdminList.getValue());
        this.currentAdminList.setValue(tempList);
    }

    public void removePreviousAdmins(){
        ArrayList<String> tempList=new ArrayList<>();
        HashSet<String> tempSet=currentAdminList.getValue().stream().collect(Collectors.toCollection(HashSet::new));
        for(String admin:domainAdminList.getValue()){
            tempSet.remove(admin);
        }
        tempList.addAll(tempSet);
        this.currentAdminList.setValue(tempList);

    }


    public void setIdList(ArrayList<String> mIdList) {
        this.idList.setValue(mIdList);
    }
    public MutableLiveData<ArrayList<String>> getIdList() {
        return idList;
    }

}
