 package com.example.finalyearproject.Activities.Main;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

 public class SharedViewModel extends ViewModel {
     private SavedStateHandle state;

     private MutableLiveData<String> adminLevel=
            new MutableLiveData<>("");
     private MutableLiveData<String> privateDomainAdminLevel =
             new MutableLiveData<>("");
    private MutableLiveData<ArrayList<String>> idList =
            new MutableLiveData<>(new ArrayList<String>());
    private MutableLiveData<Integer> privacyLevel=new MutableLiveData<>(0);
    private MutableLiveData<ArrayList<String>> domainNameList =
                new MutableLiveData<>(new ArrayList<String>(Arrays.asList("main")));
    private MutableLiveData<String> instCode =
            new MutableLiveData<>("");
    private MutableLiveData<HashSet<String>> currentAdminList =
            new MutableLiveData<>(new HashSet<>());
    private MutableLiveData<HashSet<String>> privateCurrentAdminList =
                 new MutableLiveData<>(new HashSet<>());
    private MutableLiveData<Boolean> isChatGroup=new MutableLiveData<>(false);
    private MutableLiveData<HashSet<String>> chatGroupIds=new MutableLiveData<>(new HashSet<>());
     private MutableLiveData<ArrayList<String>> privateMemberList =
             new MutableLiveData<>(new ArrayList<String>());
     private MutableLiveData<ArrayList<String>> domainAdminList =
             new MutableLiveData<>(new ArrayList<String>());
//
//     public SharedViewModel(SavedStateHandle savedStateHandle) {
////         state=savedStateHandle;
////         if(state!=null) {
////             adminLevel = savedStateHandle.getLiveData("adminLevel");
////             privateDomainAdminLevel = savedStateHandle.getLiveData("privateDomainAdminLevel");
////             idList = savedStateHandle.getLiveData("idList");
////             privacyLevel = savedStateHandle.getLiveData("privacyLevel");
////             domainNameList = savedStateHandle.getLiveData("domainNameList");
////             instCode = savedStateHandle.getLiveData("instCode");
////             currentAdminList = savedStateHandle.getLiveData("currentAdminList");
////             privateCurrentAdminList = savedStateHandle.getLiveData("privateCurrentAdminList");
////             isChatGroup = savedStateHandle.getLiveData("isChatGroup");
////             chatGroupIds = savedStateHandle.getLiveData("chatGroupIds");
////             privateMemberList = savedStateHandle.getLiveData("privateMemberList");
////             domainAdminList = savedStateHandle.getLiveData("domainAdminList");
////         }
////         idList=savedStateHandle.getLiveData("privateDomainAdminLevel");
//     }

     public MutableLiveData<HashSet<String>> getChatGroupIds() {
         return chatGroupIds;
     }

     public void addChatGroupId(String chatGroupId) {
         HashSet<String> temp=getChatGroupIds().getValue();
         temp.add(chatGroupId);
         this.chatGroupIds.setValue(temp);
     }

     public  void removeChatGroupId(String chatGroupId){
         HashSet<String> temp=getChatGroupIds().getValue();
         temp.remove(chatGroupId);
         this.chatGroupIds.setValue(temp);
     }

     public MutableLiveData<Boolean> getIsChatGroup() {
         return isChatGroup;
     }

     public void setIsChatGroup(Boolean isChatGroup) {
         this.isChatGroup.setValue(isChatGroup);
     }



    public void incrementPrivacyLevel(){
        int level=this.privacyLevel.getValue();
        level+=1;
        this.privacyLevel.setValue(level);
    }
     public void decrementPrivacyLevel(){
         int level=this.privacyLevel.getValue();
         level-=1;
         if(level==0){
             privateMemberList.setValue(new ArrayList<String>());
             privateCurrentAdminList.setValue(new HashSet<>());
             privateDomainAdminLevel.setValue("");
         }
         this.privacyLevel.setValue(level);
     }

     public MutableLiveData<Integer> getPrivacyLevel(){
        return  privacyLevel;
     }
//     public void resetPrivacyLevel(){
//        privacyLevel.setValue(0);
//     }


    public void setPrivateCurrentAdminList(ArrayList adminList){
        privateCurrentAdminList.setValue(new HashSet<>(adminList));
    }
    public MutableLiveData<HashSet<String>> getPrivateCurrentAdminList(){
        return privateCurrentAdminList;
    }


    public void setPrivateMemberList(ArrayList adminList){
        privateMemberList.setValue(adminList);
     }
    public MutableLiveData<ArrayList<String>> getPrivateMemberList(){
        return privateMemberList;
    }


    public MutableLiveData<String> getAdminLevel(){
        return adminLevel;
    }
    public void setAdminLevel(String level){
        adminLevel.setValue(level);


    }


    public MutableLiveData<String> getPrivateDomainAdminLevel(){
        return privateDomainAdminLevel;
     }
    public void setPrivateDomainAdminLevel(String level){
         privateDomainAdminLevel.setValue(level);
    }


    private void addPrivateCurrentAdminList(){
         HashSet<String> tempList=this.privateCurrentAdminList.getValue();
         tempList.addAll(domainAdminList.getValue());
         this.privateCurrentAdminList.setValue(tempList);
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
    public void setDomainNameList(ArrayList<String> nameList) {
        domainNameList.setValue(nameList);
    }
    public void setDomainAdminList(ArrayList<String> admins){
        this.domainAdminList.setValue(admins);
        if(privacyLevel.getValue()>0)addPrivateCurrentAdminList();
        else addCurrentAdmins();
    }


    public MutableLiveData<HashSet<String>> getCurrentAdminList(){
         return currentAdminList;
     }
    private void addCurrentAdmins(){
        HashSet<String> tempSet=this.currentAdminList.getValue();
//        HashSet<String> tempSet= new HashSet<>(currentAdminList.getValue());
        if(domainAdminList.getValue().size()>0&&!tempSet.contains(domainAdminList.getValue().get(0)))  {
            tempSet.addAll(domainAdminList.getValue());
        }
        this.currentAdminList.setValue(tempSet);
    }
     public void removePreviousAdmins(){
         ArrayList<String> tempList=new ArrayList<>();
         HashSet<String> tempSet;
         if(privacyLevel.getValue()>0){
             tempSet= privateCurrentAdminList.getValue();
             for(String admin:domainAdminList.getValue()){
                 tempSet.remove(admin);
             }
             this.privateCurrentAdminList.setValue(tempSet);
         }else {
             tempSet = currentAdminList.getValue();
             for(String admin:domainAdminList.getValue()){
                 tempSet.remove(admin);
             }
//             tempList.addAll(tempSet);
             this.currentAdminList.setValue(tempSet);
         }


     }





    public void setIdList(ArrayList<String> mIdList) {
        this.idList.setValue(mIdList);
    }
    public MutableLiveData<ArrayList<String>> getIdList() {
        return idList;
    }

}
