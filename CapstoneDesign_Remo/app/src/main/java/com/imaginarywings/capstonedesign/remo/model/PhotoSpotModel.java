package com.imaginarywings.capstonedesign.remo.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by S.JJ on 2017-04-28.
 * 포토스팟 데이터 클래스
 * Parcelable 클래스 : 클래스나 오브젝트를 다른 컴포넌트로 전달해 줘야 하는 경우에 사용하는 클래스
 */

public class PhotoSpotModel implements Parcelable{

    //포토스팟 모델의 속성
    private int id;
    private String type;
    private String aid;
    private String subject;
    private String address;
    //private List<String> imgList;
    private String imgSrc;

    private double latitude;
    private double longitude;

    //생성자
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


    // 각 필드에 대한 getter, setter
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

    //-----------------------------------------------------------------------------------------------------
    // Parcelable 인터페이스를 구현하려면 아래의 두 메소드를 오버라이드 해줘야한다.(describeContests, wrtieToparcel)
    //-----------------------------------------------------------------------------------------------------

    @Override   //Parcel 하려는 오브젝트의 종류를 정의한다.
    public int describeContents() {
        return 0;
    }

    @Override   //실제 오브젝트 serialization/flattening을 하는 메소드. 오브젝트의 각 엘리먼트를 각각 parcel 해줘야 한다.
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

    //위의 메소드를 오버라이드 한 뒤 parcel에서 데이터를 de-serialize 하는 단계를 추가한다.
    //Parcelable.Creator 타입의 CREATOR 라는 변수를 정의해야 한다.
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

    //PhotoSpotModel 모든 parcel된 데이터를 복구하는 생성자를 정의해야한다.
    //주의점은 writeToParcel()메소드에 기록한 순서와 동일하게 복구해야한다.
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

}
