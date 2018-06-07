package ykk.xc.com.xcwms.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 销售订单表 （SEOrder）
 */
public class SeOrder implements Parcelable {
    // ID
    // seorder_id int KIS 销售订单主表ID
    // seroder_nmber nvarchar(50) 销售订单号
    // FCustID int 客户ID(KIS)
    // seroder_fdate datetime 销售订单日期
    // seroder_entryID int KIS 销售订单行号ID
    // fitemID int KIS物料表
    // seorder_fqty decimal(18, 2) 订单数量
    // seorder_stockqty decimal(18, 2) 出库数量
    private int id;
    private int seorderId;
    private String seroderNmber;
    private int fcustId;
    private Organization organization;
    private String seroderFdate;
    private int seroderEntryId;
    private int fitemId;
    private Mtl mtl;
    private double seorderFqty;
    private double seorderStockqty;
    private String barcode;
    private int isCheck; // 新加的是否选中

    public int getId() {
        return id;
    }

    public int getSeorderId() {
        return seorderId;
    }

    public String getSeroderNmber() {
        return seroderNmber.trim();
    }

    public int getFcustId() {
        return fcustId;
    }

    public String getSeroderFdate() {
        return seroderFdate;
    }

    public int getSeroderEntryID() {
        return seroderEntryId;
    }

    public int getFitemID() {
        return fitemId;
    }

    public double getSeorderFqty() {
        return seorderFqty;
    }

    public double getSeorderStockqty() {
        return seorderStockqty;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSeorderId(int seorderId) {
        this.seorderId = seorderId;
    }

    public void setSeroderNmber(String seroderNmber) {
        this.seroderNmber = seroderNmber;
    }

    public void setFcustId(int fcustId) {
        this.fcustId = fcustId;
    }

    public void setSeroderFdate(String seroderFdate) {
        this.seroderFdate = seroderFdate;
    }

    public void setSeroderEntryID(int seroderEntryID) {
        this.seroderEntryId = seroderEntryID;
    }

    public void setFitemID(int fitemID) {
        this.fitemId = fitemID;
    }

    public void setSeorderFqty(double seorderFqty) {
        this.seorderFqty = seorderFqty;
    }

    public void setSeorderStockqty(double seorderStockqty) {
        this.seorderStockqty = seorderStockqty;
    }

    public Organization getOrganization() {
        return organization;
    }

    public Mtl getMtl() {
        return mtl;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }

    public void setMtl(Mtl mtl) {
        this.mtl = mtl;
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

    public SeOrder() {
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
    public SeOrder(Parcel p) {
        id = p.readInt();
        seorderId = p.readInt();
        seroderNmber = p.readString();
        fcustId = p.readInt();
        organization = p.readParcelable(Organization.class.getClassLoader());
        seroderFdate = p.readString();
        seroderEntryId = p.readInt();
        fitemId = p.readInt();
        mtl = p.readParcelable(Mtl.class.getClassLoader());
        seorderFqty = p.readDouble();
        seorderStockqty = p.readDouble();
        barcode = p.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel p, int flags) {
        p.writeInt(id);
        p.writeInt(seorderId);
        p.writeString(seroderNmber);
        p.writeInt(fcustId);
        p.writeParcelable(organization, flags);
        p.writeString(seroderFdate);
        p.writeInt(seroderEntryId);
        p.writeInt(fitemId);
        p.writeParcelable(mtl, flags);
        p.writeDouble(seorderFqty);
        p.writeDouble(seorderStockqty);
        p.writeString(barcode);
    }

    public static final Creator<SeOrder> CREATOR = new Creator<SeOrder>() {
        /**
         * 供外部类反序列化本类数组使用
         */
        @Override
        public SeOrder[] newArray(int size) {
            return new SeOrder[size];
        }

        /**
         * 从Parcel中读取数据
         */
        @Override
        public SeOrder createFromParcel(Parcel source) {
            return new SeOrder(source);
        }
    };


}
