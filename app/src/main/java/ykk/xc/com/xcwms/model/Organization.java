package ykk.xc.com.xcwms.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 客户表t_Orgnaization
 */
public class Organization implements Parcelable {
    private int id;
    private int fitemID;
    private String fnumber;
    private String fname;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFitemID() {
        return fitemID;
    }

    public void setFitemID(int fitemID) {
        this.fitemID = fitemID;
    }

    public String getFnumber() {
        return fnumber;
    }

    public void setFnumber(String fnumber) {
        this.fnumber = fnumber;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public Organization() {
        super();
    }

    /**
     * 这里的读的顺序必须与writeToParcel(Parcel dest, int flags)方法中
     * 写的顺序一致，否则数据会有差错，比如你的读取顺序如果是：
     * nickname = source.readString();
     * username=source.readString();
     * age = source.readInt();
     * 即调换了username和nickname的读取顺序，那么你会发现你拿到的username是nickname的数据，
     * 而你拿到的nickname是username的数据
     * @param p
     */
    public Organization(Parcel p) {
        id = p.readInt();
        fitemID = p.readInt();
        fnumber = p.readString();
        fname = p.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel p, int flags) {
        p.writeInt(id);
        p.writeInt(fitemID);
        p.writeString(fnumber);
        p.writeString(fname);
    }

    public static final Creator<Organization> CREATOR = new Creator<Organization>() {
        /**
         * 供外部类反序列化本类数组使用
         */
        @Override
        public Organization[] newArray(int size) {
            return new Organization[size];
        }

        /**
         * 从Parcel中读取数据
         */
        @Override
        public Organization createFromParcel(Parcel source) {
            return new Organization(source);
        }
    };

}
