package ykk.xc.com.xcwms.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 库区表stock_area
 */
public class StockArea implements Parcelable {
    //    id	int	Unchecked
//    stock_id	int	Unchecked
//    fnumber	nvarchar(50)	Unchecked
//    fname	nvarchar(50)	Unchecked
//    is_storage_location	bit	Unchecked
    private int id;
    private int stock_id;
    private String fnumber;
    private String fname;
    private boolean is_storage_location;

    private String barcode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStock_id() {
        return stock_id;
    }

    public void setStock_id(int stock_id) {
        this.stock_id = stock_id;
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

    public boolean isIs_storage_location() {
        return is_storage_location;
    }

    public void setIs_storage_location(boolean is_storage_location) {
        this.is_storage_location = is_storage_location;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public StockArea() {
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
    public StockArea(Parcel p) {
        id = p.readInt();
        stock_id = p.readInt();
        fnumber = p.readString();
        fname = p.readString();
        int storage_location = p.readInt();
        is_storage_location = storage_location > 0 ? true : false;
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
        p.writeString(fnumber);
        p.writeString(fname);
        p.writeInt(is_storage_location ? 1 : 0);
        p.writeString(barcode);
    }

    public static final Creator<StockArea> CREATOR = new Creator<StockArea>() {
        /**
         * 供外部类反序列化本类数组使用
         */
        @Override
        public StockArea[] newArray(int size) {
            return new StockArea[size];
        }

        /**
         * 从Parcel中读取数据
         */
        @Override
        public StockArea createFromParcel(Parcel source) {
            return new StockArea(source);
        }
    };
}
