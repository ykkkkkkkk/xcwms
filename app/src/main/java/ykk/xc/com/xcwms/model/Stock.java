package ykk.xc.com.xcwms.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 仓库表 t_stock
 */
public class Stock implements Parcelable {
    /*id*/
    private Integer id;
    /*仓库id*/
    private Integer fStockid ;
    /*仓库编码*/
    private String fNumber;
    /*仓库名称*/
    private String fName;
    /*仓库负责人编码*/
    private String staffFnumber;
    /*联系电话*/
    private String fTel;
    /*仓位*/
    private String fLocationid;
    /*仓位属性*/
    private String stockProperty;
    /*仓库地址*/
    private String fAddress;
    /*库存状态类型*/
    private String fStockStatusType;
    /*库存默认状态编码*/
    private String stockStatusFnumber;
    /*默认收料状态*/
    private String rechargeStatusFnumber;
    /*供应商编码*/
    private String supFnumber;
    /*客户编码*/
    private String cusFnumber;
    /*创建组织编码*/
    private String corgFnumber;
    /*使用组织编码*/
    private String uorgFnumber;
    /*K3数据状态*/
    private String dataStatus;
    /*wms非物理删除标识*/
    private String isDelete;
    /*k3是否禁用*/
    private String enabled;
    /*  启用库区管理 */
    private boolean reservoirArea;
    /* 启用库位管理 */
    private boolean storageLocation;
    /* 条码 */
    private String barcode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getfStockid() {
        return fStockid;
    }

    public void setfStockid(Integer fStockid) {
        this.fStockid = fStockid;
    }

    public String getfNumber() {
        return fNumber;
    }

    public void setfNumber(String fNumber) {
        this.fNumber = fNumber;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getStaffFnumber() {
        return staffFnumber;
    }

    public void setStaffFnumber(String staffFnumber) {
        this.staffFnumber = staffFnumber;
    }

    public String getfTel() {
        return fTel;
    }

    public void setfTel(String fTel) {
        this.fTel = fTel;
    }

    public String getfLocationid() {
        return fLocationid;
    }

    public void setfLocationid(String fLocationid) {
        this.fLocationid = fLocationid;
    }

    public String getStockProperty() {
        return stockProperty;
    }

    public void setStockProperty(String stockProperty) {
        this.stockProperty = stockProperty;
    }

    public String getfAddress() {
        return fAddress;
    }

    public void setfAddress(String fAddress) {
        this.fAddress = fAddress;
    }

    public String getfStockStatusType() {
        return fStockStatusType;
    }

    public void setfStockStatusType(String fStockStatusType) {
        this.fStockStatusType = fStockStatusType;
    }

    public String getStockStatusFnumber() {
        return stockStatusFnumber;
    }

    public void setStockStatusFnumber(String stockStatusFnumber) {
        this.stockStatusFnumber = stockStatusFnumber;
    }

    public String getRechargeStatusFnumber() {
        return rechargeStatusFnumber;
    }

    public void setRechargeStatusFnumber(String rechargeStatusFnumber) {
        this.rechargeStatusFnumber = rechargeStatusFnumber;
    }

    public String getSupFnumber() {
        return supFnumber;
    }

    public void setSupFnumber(String supFnumber) {
        this.supFnumber = supFnumber;
    }

    public String getCusFnumber() {
        return cusFnumber;
    }

    public void setCusFnumber(String cusFnumber) {
        this.cusFnumber = cusFnumber;
    }

    public String getCorgFnumber() {
        return corgFnumber;
    }

    public void setCorgFnumber(String corgFnumber) {
        this.corgFnumber = corgFnumber;
    }

    public String getUorgFnumber() {
        return uorgFnumber;
    }

    public void setUorgFnumber(String uorgFnumber) {
        this.uorgFnumber = uorgFnumber;
    }

    public String getDataStatus() {
        return dataStatus;
    }

    public void setDataStatus(String dataStatus) {
        this.dataStatus = dataStatus;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
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
        fStockid = p.readInt();
        fNumber = p.readString();
        fName = p.readString();
        staffFnumber = p.readString();
        fTel = p.readString();
        fLocationid = p.readString();
        stockProperty = p.readString();
        fAddress = p.readString();
        fStockStatusType = p.readString();
        stockStatusFnumber = p.readString();
        rechargeStatusFnumber = p.readString();
        supFnumber = p.readString();
        cusFnumber = p.readString();
        corgFnumber = p.readString();
        uorgFnumber = p.readString();
        dataStatus = p.readString();
        isDelete = p.readString();
        enabled = p.readString();
        int reservoir_area = p.readInt();
        reservoirArea = reservoir_area > 0 ? true : false;
        int storage_location = p.readInt();
        storageLocation = storage_location > 0 ? true : false;
        barcode = p.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel p, int flags) {
        p.writeInt(id);
        p.writeInt(fStockid);
        p.writeString(fNumber);
        p.writeString(fName);
        p.writeString(staffFnumber);
        p.writeString(fTel);
        p.writeString(fLocationid);
        p.writeString(stockProperty);
        p.writeString(fAddress);
        p.writeString(fStockStatusType);
        p.writeString(stockStatusFnumber);
        p.writeString(rechargeStatusFnumber);
        p.writeString(supFnumber);
        p.writeString(cusFnumber);
        p.writeString(corgFnumber);
        p.writeString(uorgFnumber);
        p.writeString(dataStatus);
        p.writeString(isDelete);
        p.writeString(enabled);
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
