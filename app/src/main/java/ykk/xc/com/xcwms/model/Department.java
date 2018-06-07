package ykk.xc.com.xcwms.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 部门表t_Department
 */
public class Department implements Parcelable {
    private int id;
    private int fitemID;
    private String fnumber;
    private String fname;
    private String barcode;

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

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Department() {
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
    public Department(Parcel p) {
        id = p.readInt();
        fitemID = p.readInt();
        fnumber = p.readString();
        fname = p.readString();
        barcode = p.readString();
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
        p.writeString(barcode);
    }

    public static final Creator<Department> CREATOR = new Creator<Department>() {
        /**
         * 供外部类反序列化本类数组使用
         */
        @Override
        public Department[] newArray(int size) {
            return new Department[size];
        }

        /**
         * 从Parcel中读取数据
         */
        @Override
        public Department createFromParcel(Parcel source) {
            return new Department(source);
        }
    };

}
