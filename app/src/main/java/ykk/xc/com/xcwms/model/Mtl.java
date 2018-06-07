package ykk.xc.com.xcwms.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 物料表
 */
public class Mtl implements Parcelable {
    //    [id] [int] IDENTITY(1,1) NOT NULL,
//	[k3FitemId] [int] NOT NULL,
//	[FShortNumber] [nvarchar](50) NULL,
//            [fnumber] [nvarchar](100) NULL,
//            [fname] [nvarchar](100) NULL,
//            [fmodel] [nvarchar](100) NULL,
//            [funitID] [int] NULL,
//            [is_batch] [bit] NULL,
//            [is_sn] [bit] NOT NULL,
//	[barcode] [nvarchar](20) NULL
    private int id;
    private int k3FitemId;
    private String FShortNumber;
    private String fnumber;
    private String fname;
    private String fmodel;
    private int funitID; // 单位id
    private boolean is_batch;
    private boolean is_sn;
    private String barcode;
    private MeasureUnit mUnit;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getK3FitemId() {
        return k3FitemId;
    }

    public void setK3FitemId(int k3FitemId) {
        this.k3FitemId = k3FitemId;
    }

    public String getFShortNumber() {
        return FShortNumber;
    }

    public void setFShortNumber(String FShortNumber) {
        this.FShortNumber = FShortNumber;
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

    public String getFmodel() {
        return fmodel;
    }

    public void setFmodel(String FModel) {
        this.fmodel = FModel;
    }

    public int getFunitID() {
        return funitID;
    }

    public void setFunitID(int funitID) {
        this.funitID = funitID;
    }

    public boolean getIs_batch() {
        return is_batch;
    }

    public void setIs_batch(boolean is_batch) {
        this.is_batch = is_batch;
    }

    public boolean getIs_sn() {
        return is_sn;
    }

    public void setIs_sn(boolean is_sn) {
        this.is_sn = is_sn;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    /**
     * @return the mUnit
     */
    public MeasureUnit getmUnit() {
        return mUnit;
    }

    /**
     * @param mUnit
     *            the mUnit to set
     */
    public void setmUnit(MeasureUnit mUnit) {
        this.mUnit = mUnit;
    }

    public Mtl() {
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
     *
     * @param source
     */
    public Mtl(Parcel source) {
        id = source.readInt();
        k3FitemId = source.readInt();
        FShortNumber = source.readString();
        fnumber = source.readString();
        fname = source.readString();
        fmodel = source.readString();
        funitID = source.readInt();
        int batch = source.readInt();
        is_batch = batch == 1 ? true : false;
        int sn = source.readInt();
        is_sn = sn == 1 ? true : false;
        barcode = source.readString();
        mUnit = source.readParcelable(MeasureUnit.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(k3FitemId);
        dest.writeString(FShortNumber);
        dest.writeString(fnumber);
        dest.writeString(fname);
        dest.writeString(fmodel);
        dest.writeInt(funitID);
        dest.writeInt(is_batch ? 1 : 0);
        dest.writeInt(is_sn ? 1 : 0);
        dest.writeString(barcode);
        dest.writeParcelable(mUnit, flags);
    }

    public static final Creator<Mtl> CREATOR = new Creator<Mtl>() {
        /**
         * 供外部类反序列化本类数组使用
         */
        @Override
        public Mtl[] newArray(int size) {
            return new Mtl[size];
        }

        /**
         * 从Parcel中读取数据
         */
        @Override
        public Mtl createFromParcel(Parcel source) {
            return new Mtl(source);
        }
    };

}
