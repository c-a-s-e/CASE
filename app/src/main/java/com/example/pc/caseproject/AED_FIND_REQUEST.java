package com.example.pc.caseproject;

import android.os.Parcel;
import android.os.Parcelable;

//주변 AED 찾는 액티비티로 넘어갈 때 이 정보를 넘겨드립니다.

public class AED_FIND_REQUEST implements Parcelable {
    Double myLatitude, myLongitude, aedLatitude, aedLongitude;
    String aedAddress;

    public AED_FIND_REQUEST() {

    }

    public AED_FIND_REQUEST(Double myLatitude, Double myLongitude, Double aedLatitude, Double aedLongitude, String aedAddress) {
        this.myLatitude = myLatitude;
        this.myLongitude = myLongitude;
        this.aedLatitude = aedLatitude;
        this.aedLongitude = aedLongitude;
        this.aedAddress = aedAddress;
    }

    public AED_FIND_REQUEST(Parcel in) {
        myLatitude = in.readDouble();
        myLongitude = in.readDouble();
        aedLatitude = in.readDouble();
        aedLongitude = in.readDouble();
        aedAddress = in.readString();
    }

    public static final Creator<AED_FIND_REQUEST> CREATOR = new Creator<AED_FIND_REQUEST>() {
        @Override
        public AED_FIND_REQUEST createFromParcel(Parcel parcel) {
            return new AED_FIND_REQUEST(parcel);
        }

        @Override
        public AED_FIND_REQUEST[] newArray(int i) {
            return new AED_FIND_REQUEST[0];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(myLatitude);
        parcel.writeDouble(myLongitude);
        parcel.writeDouble(aedLatitude);
        parcel.writeDouble(aedLongitude);
        parcel.writeString(aedAddress);
    }

    public Double getMyLatitude() {
        return myLatitude;
    }

    public void setMyLatitude(Double myLatitude) {
        this.myLatitude = myLatitude;
    }

    public Double getMyLongitude() {
        return myLongitude;
    }

    public void setMyLongitude(Double myLongitude) {
        this.myLongitude = myLongitude;
    }

    public Double getAedLatitude() {
        return aedLatitude;
    }

    public void setAedLatitude(Double aedLatitude) {
        this.aedLatitude = aedLatitude;
    }

    public Double getAEDLongitude() {
        return aedLongitude;
    }

    public void setAEDLongitude(Double abedLongitude) {
        this.aedLongitude = abedLongitude;
    }

    public String getAedAddress() {
        return aedAddress;
    }

    public void setAedAddress(String aedAddress) {
        this.aedAddress = aedAddress;
    }
}
