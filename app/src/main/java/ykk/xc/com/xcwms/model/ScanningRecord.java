package ykk.xc.com.xcwms.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Scanning_record
 */
public class ScanningRecord implements Serializable {
    private int id;
    private int type;
    private int sourceK3Id;
    private String sourceFnumber;
    private int mtlK3Id;
    private int stockK3Id;
    private int stockAreaId;
    private int stockPositionId;
    private int supplierK3Id;
    private int customerK3Id;
    private int departmentK3Id;
    private int operationId;
    private int pdaRowno;
    private double fqty;
    private String batchNo;
    private String sequenceNo;
    private String barcode;
    private String pdaNo;
    private String k3number;
    private String fdate;
    private String status;
    // 新加的
    private String receiveOrgFnumber;
    private String purOrgFnumber;
    private String supplierFnumber;
    private String mtlFnumber;
    private String unitFnumber;
    private String batchFnumber;
    private String stockFnumber;

    public ScanningRecord() {
        super();
    }
    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public int getSourceK3Id() {
        return sourceK3Id;
    }
    public String getSourceFnumber() {
        return sourceFnumber;
    }
    public int getMtlK3Id() {
        return mtlK3Id;
    }
    public int getStockK3Id() {
        return stockK3Id;
    }
    public int getStockAreaId() {
        return stockAreaId;
    }
    public int getStockPositionId() {
        return stockPositionId;
    }
    public int getSupplierK3Id() {
        return supplierK3Id;
    }
    public int getCustomerK3Id() {
        return customerK3Id;
    }
    public int getDepartmentK3Id() {
        return departmentK3Id;
    }
    public int getOperationId() {
        return operationId;
    }
    public int getPdaRowno() {
        return pdaRowno;
    }
    public double getFqty() {
        return fqty;
    }
    public String getBatchNo() {
        return batchNo;
    }
    public String getSequenceNo() {
        return sequenceNo;
    }
    public String getBarcode() {
        return barcode;
    }
    public String getPdaNo() {
        return pdaNo;
    }
    public String getK3number() {
        return k3number;
    }
    public String getFdate() {
        return fdate;
    }
    public String getStatus() {
        return status;
    }
    public String getReceiveOrgFnumber() {
        return receiveOrgFnumber;
    }
    public String getPurOrgFnumber() {
        return purOrgFnumber;
    }
    public String getSupplierFnumber() {
        return supplierFnumber;
    }
    public String getMtlFnumber() {
        return mtlFnumber;
    }
    public String getUnitFnumber() {
        return unitFnumber;
    }
    public String getBatchFnumber() {
        return batchFnumber;
    }
    public String getStockFnumber() {
        return stockFnumber;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setType(int type) {
        this.type = type;
    }
    public void setSourceK3Id(int sourceK3Id) {
        this.sourceK3Id = sourceK3Id;
    }
    public void setSourceFnumber(String sourceFnumber) {
        this.sourceFnumber = sourceFnumber;
    }
    public void setMtlK3Id(int mtlK3Id) {
        this.mtlK3Id = mtlK3Id;
    }
    public void setStockK3Id(int stockK3Id) {
        this.stockK3Id = stockK3Id;
    }
    public void setStockAreaId(int stockAreaId) {
        this.stockAreaId = stockAreaId;
    }
    public void setStockPositionId(int stockPositionId) {
        this.stockPositionId = stockPositionId;
    }
    public void setSupplierK3Id(int supplierK3Id) {
        this.supplierK3Id = supplierK3Id;
    }
    public void setCustomerK3Id(int customerK3Id) {
        this.customerK3Id = customerK3Id;
    }
    public void setDepartmentK3Id(int departmentK3Id) {
        this.departmentK3Id = departmentK3Id;
    }
    public void setOperationId(int operationId) {
        this.operationId = operationId;
    }
    public void setPdaRowno(int pdaRowno) {
        this.pdaRowno = pdaRowno;
    }
    public void setFqty(double fqty) {
        this.fqty = fqty;
    }
    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }
    public void setSequenceNo(String sequenceNo) {
        this.sequenceNo = sequenceNo;
    }
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
    public void setPdaNo(String pdaNo) {
        this.pdaNo = pdaNo;
    }
    public void setK3number(String k3number) {
        this.k3number = k3number;
    }
    public void setFdate(String fdate) {
        this.fdate = fdate;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setReceiveOrgFnumber(String receiveOrgFnumber) {
        this.receiveOrgFnumber = receiveOrgFnumber;
    }
    public void setPurOrgFnumber(String purOrgFnumber) {
        this.purOrgFnumber = purOrgFnumber;
    }
    public void setSupplierFnumber(String supplierFnumber) {
        this.supplierFnumber = supplierFnumber;
    }
    public void setMtlFnumber(String mtlFnumber) {
        this.mtlFnumber = mtlFnumber;
    }
    public void setUnitFnumber(String unitFnumber) {
        this.unitFnumber = unitFnumber;
    }
    public void setBatchFnumber(String batchFnumber) {
        this.batchFnumber = batchFnumber;
    }
    public void setStockFnumber(String stockFnumber) {
        this.stockFnumber = stockFnumber;
    }

    @Override
    public String toString() {
        return "ScanningRecord [id=" + id + ", type=" + type + ", sourceK3Id=" + sourceK3Id + ", sourceFnumber="
                + sourceFnumber + ", mtlK3Id=" + mtlK3Id + ", stockK3Id=" + stockK3Id + ", stockAreaId=" + stockAreaId
                + ", stockPositionId=" + stockPositionId + ", supplierK3Id=" + supplierK3Id + ", customerK3Id="
                + customerK3Id + ", departmentK3Id=" + departmentK3Id + ", operationId=" + operationId + ", pdaRowno="
                + pdaRowno + ", fqty=" + fqty + ", batchNo=" + batchNo + ", sequenceNo=" + sequenceNo + ", barcode="
                + barcode + ", pdaNo=" + pdaNo + ", k3number=" + k3number + ", fdate=" + fdate + ", status=" + status
                + ", receiveOrgFnumber=" + receiveOrgFnumber + ", purOrgFnumber=" + purOrgFnumber + ", supplierFnumber="
                + supplierFnumber + ", mtlFnumber=" + mtlFnumber + ", unitFnumber=" + unitFnumber + ", batchFnumber="
                + batchFnumber + ", stockFnumber=" + stockFnumber + "]";
    }

}
