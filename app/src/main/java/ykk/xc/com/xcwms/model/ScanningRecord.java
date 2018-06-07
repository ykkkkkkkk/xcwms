package ykk.xc.com.xcwms.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Scanning_record
 */
public class ScanningRecord implements Parcelable {

    /**
     * ID : 1
     * type : 2
     * source_finterID : 1
     * fitemID : 3
     * batchno : sample string 4
     * fqty : 1.0
     * stock_id : 5
     * stock_area_id : 1
     * stock_position_id : 1
     * supplierID : 1
     * customerID : 1
     * fdate : 2018-05-08T17:38:51.9715695+08:00
     * empID : 1
     * operationID : 1
     */
    private int id;
    private int type;
    private int sourceFinterId;
    private int fitemId;
    private String batchno;
    private double fqty;
    private int stockId;
    private int stockAreaId;
    private int stockPositionId;
    private int supplierId;
    private int customerId;
    private String fdate;
    private int empId;
    private int operationId;
    private String srNo;
    private int fentryId;
    private String k3no;
    private String sequenceNo;
    private int tmpSourceId; // 新加的单据id（采购id,销售id）

    public int getTmpSourceId() {
        return tmpSourceId;
    }

    public void setTmpSourceId(int tmpSourceId) {
        this.tmpSourceId = tmpSourceId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSourceFinterId() {
        return sourceFinterId;
    }

    public void setSourceFinterId(int sourceFinterId) {
        this.sourceFinterId = sourceFinterId;
    }

    public int getFitemId() {
        return fitemId;
    }

    public void setFitemId(int fitemId) {
        this.fitemId = fitemId;
    }

    public String getBatchno() {
        return batchno;
    }

    public void setBatchno(String batchno) {
        this.batchno = batchno;
    }

    public double getFqty() {
        return fqty;
    }

    public void setFqty(double fqty) {
        this.fqty = fqty;
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public int getStockAreaId() {
        return stockAreaId;
    }

    public void setStockAreaId(int stockAreaId) {
        this.stockAreaId = stockAreaId;
    }

    public int getStockPositionId() {
        return stockPositionId;
    }

    public void setStockPositionId(int stockPositionId) {
        this.stockPositionId = stockPositionId;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getFdate() {
        return fdate;
    }

    public void setFdate(String fdate) {
        this.fdate = fdate;
    }

    public int getEmpId() {
        return empId;
    }

    public void setEmpId(int empId) {
        this.empId = empId;
    }

    public int getOperationId() {
        return operationId;
    }

    public void setOperationId(int operationId) {
        this.operationId = operationId;
    }

    public String getSrNo() {
        return srNo;
    }

    public void setSrNo(String srNo) {
        this.srNo = srNo;
    }

    public int getFentryId() {
        return fentryId;
    }

    public void setFentryId(int fentryId) {
        this.fentryId = fentryId;
    }

    public String getK3no() {
        return k3no;
    }

    public void setK3no(String k3no) {
        this.k3no = k3no;
    }

    public String getSequenceNo() {
        return sequenceNo;
    }

    public void setSequenceNo(String sequenceNo) {
        this.sequenceNo = sequenceNo;
    }

    public ScanningRecord() {
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
    public ScanningRecord(Parcel p) {
        id = p.readInt();
        type = p.readInt();
        sourceFinterId = p.readInt();
        fitemId = p.readInt();
        batchno = p.readString();
        fqty = p.readDouble();
        stockId = p.readInt();
        stockAreaId = p.readInt();
        stockPositionId = p.readInt();
        supplierId = p.readInt();
        customerId = p.readInt();
        fdate = p.readString();
        empId = p.readInt();
        operationId = p.readInt();
        srNo = p.readString();
        fentryId = p.readInt();
        k3no = p.readString();
        sequenceNo = p.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel p, int flags) {
        p.writeInt(id);
        p.writeInt(type);
        p.writeInt(sourceFinterId);
        p.writeInt(fitemId);
        p.writeString(batchno);
        p.writeDouble(fqty);
        p.writeInt(stockId);
        p.writeInt(stockAreaId);
        p.writeInt(stockPositionId);
        p.writeInt(supplierId);
        p.writeInt(customerId);
        p.writeString(fdate);
        p.writeInt(empId);
        p.writeInt(operationId);
        p.writeString(srNo);
        p.writeInt(fentryId);
        p.writeString(k3no);
        p.writeString(sequenceNo);
    }

    public static final Creator<ScanningRecord> CREATOR = new Creator<ScanningRecord>() {
        /**
         * 供外部类反序列化本类数组使用
         */
        @Override
        public ScanningRecord[] newArray(int size) {
            return new ScanningRecord[size];
        }

        /**
         * 从Parcel中读取数据
         */
        @Override
        public ScanningRecord createFromParcel(Parcel source) {
            return new ScanningRecord(source);
        }
    };
}
