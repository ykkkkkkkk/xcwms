package ykk.xc.com.xcwms.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 仓库表 t_stock
 */
public class Stock implements Parcelable {
//    [id] [int] IDENTITY(1,1) NOT NULL,
//	[fitemID] [int] NOT NULL, 'K3 仓库ID
//    [FNumber] [nvarchar](50) NOT NULL, 仓库代码
//	[fname] [nvarchar](50) NOT NULL, 仓库名称
//	[is_reservoir_area] [bit] NULL, 是否启用库区管理
//	[is_storage_location] [bit] NULL, 是否启用库位管理
//	[barcode] [varchar](20) NULL, 条码号
    private int id;
    private int fitemID;
    private String fnumber;
    private String fname;
    private boolean reservoirArea; // 启用库区管理
    private boolean storageLocation; // 启用库位管理
    private String barcode;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFitemId() {
        return fitemID;
    }

    public void setFitemId(int fitemId) {
        this.fitemID = fitemId;
    }

    public String getFnumber() {
        return fnumber;
    }

    public void setFnumber(String FNumber) {
        this.fnumber = FNumber;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public boolean isReservoirArea() {
        return reservoirArea;
    }

    public void setReservoirArea(boolean reservoirArea) {
        this.reservoirArea = reservoirArea;
    }

    public boolean isStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(boolean storageLocation) {
        this.storageLocation = storageLocation;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public Stock() {
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
     * @param p
     */
    public Stock(Parcel p) {
        id = p.readInt();
        fitemID = p.readInt();
        fnumber = p.readString();
        fname = p.readString();
        int reservoirArea = p.readInt();
        this.reservoirArea = reservoirArea > 0 ? true : false;
        int storageLocation = p.readInt();
        this.storageLocation = storageLocation > 0 ? true : false;
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
        p.writeInt(reservoirArea ? 1 : 0);
        p.writeInt(storageLocation ? 1 : 0);
        p.writeString(barcode);
    }

    public static final Creator<Stock> CREATOR = new Creator<Stock>() {
        /**
         * 供外部类反序列化本类数组使用
         */
        @Override
        public Stock[] newArray(int size) {
            return new Stock[size];
        }

        /**
         * 从Parcel中读取数据
         */
        @Override
        public Stock createFromParcel(Parcel source) {
            return new Stock(source);
        }
    };


}
