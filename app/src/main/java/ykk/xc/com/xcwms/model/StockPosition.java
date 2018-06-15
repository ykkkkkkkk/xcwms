package ykk.xc.com.xcwms.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 库位表stock_position
 */
public class StockPosition implements Serializable {
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

}
