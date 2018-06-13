package ykk.xc.com.xcwms.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 库位表stock_position
 */
public class StockPosition implements Parcelable {
    // [ID] [int] IDENTITY(1,1) NOT NULL,
    // [area_id] [int] NOT NULL,
    // [fnumber] [nvarchar](50) NOT NULL,
    // [fname] [nvarchar](50) NOT NULL,
    private int id;
    private int stock_id;
    private int area_id;
    private String fnumber;
    private String fname;
    private String barcode;

    public int getId() {
        return id;
    }

    public int getStock_id() {
        return stock_id;
    }

    public int getArea_id() {
        return area_id;
    }

    public String getFnumber() {
        return fnumber;
    }

    public String getFname() {
        return fname;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStock_id(int stock_id) {
        this.stock_id = stock_id;
    }

    public void setArea_id(int area_id) {
        this.area_id = area_id;
    }

    public void setFnumber(String fnumber) {
        this.fnumber = fnumber;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public StockPosition() {
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
    public StockPosition(Parcel p) {
        id = p.readInt();
        stock_id = p.readInt();
        area_id = p.readInt();
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
        p.writeInt(stock_id);
        p.writeInt(area_id);
        p.writeString(fnumber);
        p.writeString(fname);
        p.writeString(barcode);
    }

    public static final Creator<StockPosition> CREATOR = new Creator<StockPosition>() {
        /**
         * 供外部类反序列化本类数组使用
         */
        @Override
        public StockPosition[] newArray(int size) {
            return new StockPosition[size];
        }

        /**
         * 从Parcel中读取数据
         */
        @Override
        public StockPosition createFromParcel(Parcel source) {
            return new StockPosition(source);
        }
    };


}
