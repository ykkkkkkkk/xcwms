package ykk.xc.com.xcwms.model;

import java.io.Serializable;

/**
 * 库区表stock_area
 */
public class StockArea implements Serializable {
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

}
