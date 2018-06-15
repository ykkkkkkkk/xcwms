package ykk.xc.com.xcwms.model.pur;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import ykk.xc.com.xcwms.model.Mtl;
import ykk.xc.com.xcwms.model.Supplier;

/**
 * 采购表 PO_list
 */
public class PoList implements Serializable {
//    [id] [int] IDENTITY(1,1) NOT NULL,
//	[poId] [int] NOT NULL,
//	[poNumber] [nvarchar](50) NULL,
//            [supplierId] [int] NOT NULL,
//	[poFdate] [date] NOT NULL,
//	[poEntyId] [int] NULL,
//            [fitemId] [int] NULL,
//            [poFqty] [numeric](18, 2) NULL,
//            [poStockqty] [numeric](18, 2) NULL,
    private int id;
    private int poId;
    private String poNumber;
    private int supplierId; // 供应商id
    private Supplier supplier; // 供应商对象
    private String poFdate;
    private int poEntyId;
    private int fitemId; // 物料id
    private Mtl mtl;
    private double poFqty;
    private double poStockqty;
    private boolean is_close;
    private String barcode;
    private int isCheck; // 新加的是否选中

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPoId() {
        return poId;
    }

    public void setPoId(int poId) {
        this.poId = poId;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public String getPoFdate() {
        return poFdate;
    }

    public void setPoFdate(String poFdate) {
        this.poFdate = poFdate;
    }

    public int getPoEntyId() {
        return poEntyId;
    }

    public void setPoEntyId(int poEntyId) {
        this.poEntyId = poEntyId;
    }

    public int getFitemId() {
        return fitemId;
    }

    public void setFitemId(int fitemId) {
        this.fitemId = fitemId;
    }

    public Mtl getMtl() {
        return mtl;
    }

    public void setMtl(Mtl mtl) {
        this.mtl = mtl;
    }

    public double getPoFqty() {
        return poFqty;
    }

    public void setPoFqty(double poFqty) {
        this.poFqty = poFqty;
    }

    public double getPoStockqty() {
        return poStockqty;
    }

    public void setPoStockqty(double poStockqty) {
        this.poStockqty = poStockqty;
    }

    public boolean isIs_close() {
        return is_close;
    }

    public void setIs_close(boolean is_close) {
        this.is_close = is_close;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public int getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(int isCheck) {
        this.isCheck = isCheck;
    }

    public PoList() {
        super();
    }



}
