package com.imaginarywings.capstonedesign.remo.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by S.JJ on 2017-04-28.
 */

public class PhotoSpotModel implements Parcelable{

    private int id;
    private String type;
    private String aid;
    private String subject;
    private String address;
    //private List<String> imgList;
    private String imgSrc;

    private double latitude;
    private double longitude;

    public PhotoSpotModel(int id, String type, String aid, String subject, String address, String imgSrc, double latitude, double longitude)
    {
        this.id = id;
        this.type = type;
        this.aid = aid;
        this.subject = subject;
        this.address = address;
        this.imgSrc = imgSrc;
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getAid() {
        return aid;
    }

    public String getSubject() {
        return subject;
    }

    public String getAddress() {
        return address;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "PhotoSpotModel{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", aid='" + aid + '\'' +
                ", subject='" + subject + '\'' +
                ", address='" + address + '\'' +
                ", imgSrc='" + imgSrc + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }

    //Parcelable 인터페이스를 구현하려면 아래의 두 메소드를 오버라이드 해줘야한다.
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.type);
        dest.writeString(this.aid);
        dest.writeString(this.subject);
        dest.writeString(this.address);
        dest.writeString(this.imgSrc);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
    }

    protected PhotoSpotModel(Parcel in) {
        this.id = in.readInt();
        this.type = in.readString();
        this.aid = in.readString();
        this.subject = in.readString();
        this.address = in.readString();
        this.imgSrc = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
    }

    public static final Parcelable.Creator<PhotoSpotModel> CREATOR = new Parcelable.Creator<PhotoSpotModel>() {
        @Override
        public PhotoSpotModel createFromParcel(Parcel source) {
            return new PhotoSpotModel(source);
        }

        @Override
        public PhotoSpotModel[] newArray(int size) {
            return new PhotoSpotModel[size];
        }
    };
}
