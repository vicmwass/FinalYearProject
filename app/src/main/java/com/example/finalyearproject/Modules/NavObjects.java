package com.example.finalyearproject.Modules;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;

public class NavObjects implements Serializable,Parcelable {
    ArrayList<String> mIdList;
    Institution mInstDetails;
    String mDomainName;
    ArrayList<String> mCurrentAdminList;
    ArrayList<String> mMemberList;
    Boolean mIsAdmin;


    public NavObjects(ArrayList<String> idList, Institution instDetails, String domainName, ArrayList<String> currentAdminList, ArrayList<String> memberList,boolean isAdmin) {
        mIdList = idList;
        mInstDetails = instDetails;
        mDomainName = domainName;
        mCurrentAdminList = currentAdminList;
        mMemberList=memberList;
        mIsAdmin=isAdmin;
    }


    protected NavObjects(Parcel in) {
        mDomainName = in.readString();
        mIdList = in.createStringArrayList();
        mCurrentAdminList = in.createStringArrayList();
        mMemberList = in.createStringArrayList();
        mInstDetails = (Institution) in.readParcelable(Institution.class.getClassLoader());
        mIsAdmin=in.readByte()!=0;
    }

    public static final Creator<NavObjects> CREATOR = new Creator<NavObjects>() {
        @Override
        public NavObjects createFromParcel(Parcel in) {
            return new NavObjects(in);
        }

        @Override
        public NavObjects[] newArray(int size) {
            return new NavObjects[size];
        }
    };

    public Boolean getIsAdmin() {
        return mIsAdmin;
    }

    public void setIsAdmin(Boolean admin) {
        mIsAdmin = admin;
    }

    public ArrayList<String> getIdList() {
        return mIdList;
    }

    public void setIdList(ArrayList<String> idList) {
        mIdList = idList;
    }

    public Institution getInstDetails() {
        return mInstDetails;
    }

    public void setInstDetails(Institution instDetails) {
        mInstDetails = instDetails;
    }

    public String getDomainName() {
        return mDomainName;
    }

    public void setDomainName(String domainName) {
        mDomainName = domainName;
    }

    public ArrayList<String> getCurrentAdminList() {
        return mCurrentAdminList;
    }

    public void setCurrentAdminList(ArrayList<String> currentAdminList) {
        mCurrentAdminList = currentAdminList;
    }

    public ArrayList<String> getMemberList() {
        return mMemberList;
    }

    public void setMemberList(ArrayList<String> memberList) {
        mMemberList = memberList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mDomainName);
        dest.writeStringList(mIdList);
        dest.writeStringList(mCurrentAdminList);
        dest.writeStringList(mMemberList);
        dest.writeParcelable(mInstDetails,flags);
        dest.writeByte((byte) (mIsAdmin?1:0));

    }
}
