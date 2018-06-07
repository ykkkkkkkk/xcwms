package ykk.xc.com.xcwms.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 采购表 PO_list
 */
public class PoList implements Parcelable {
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
    public PoList(Parcel p) {
        id = p.readInt();
        poId = p.readInt();
        poNumber = p.readString();
        supplierId = p.readInt();
        supplier = p.readParcelable(Supplier.class.getClassLoader());
        poFdate = p.readString();
        poEntyId = p.readInt();
        fitemId = p.readInt();
        mtl = p.readParcelable(Mtl.class.getClassLoader());
        poFqty = p.readDouble();
        poStockqty = p.readDouble();
        int is_close = p.readInt();
        this.is_close = is_close > 0 ? true : false;
        barcode = p.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel p, int flags) {
        p.writeInt(id);
        p.writeInt(poId);
        p.writeString(poNumber);
        p.writeInt(supplierId);
        p.writeParcelable(supplier, flags);
        p.writeString(poFdate);
        p.writeInt(poEntyId);
        p.writeInt(fitemId);
        p.writeParcelable(mtl, flags);
        p.writeDouble(poFqty);
        p.writeDouble(poStockqty);
        p.writeInt(is_close ? 1 : 0);
        p.writeString(barcode);
    }

    public static final Creator<PoList> CREATOR = new Creator<PoList>() {
        /**
         * 供外部类反序列化本类数组使用
         */
        @Override
        public PoList[] newArray(int size) {
            return new PoList[size];
        }

        /**
         * 从Parcel中读取数据
         */
        @Override
        public PoList createFromParcel(Parcel source) {
            return new PoList(source);
        }
    };


}
