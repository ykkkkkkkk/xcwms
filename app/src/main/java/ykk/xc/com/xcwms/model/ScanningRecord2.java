package ykk.xc.com.xcwms.model;

import java.io.Serializable;

public class ScanningRecord2 implements Serializable {

    /**
     * ID : 1
     * type : 2
     * sourceFinterId : 1
     * fitemId : 3
     * batchno : sample string 4
     * fqty : 1.0
     * stockId : 5
     * stockAreaId : 1
     * stockPositionId : 1
     * supplierId : 1
     * customerId : 1
     * fdate : 2018-05-08T17:38:51.9715695+08:00
     * empId : 1
     * operationId : 1
     */

    private int ID;
    private int type;
    private int sourceFinterId;
    private int fitemId; // 物料id
    private Mtl mtl;
    private String batchno;
    private double fqty; // 应收数量
    private double stockqty; // 实收数量，要插入到表的数量
    private int stockId;
    private Stock stock; // 新加
    private int stockAreaId;
    private String stockAName; // 新加
    private int stockPositionId;
    private String stockPName; // 新加
    private int supplierId;
    private String supplierName; // 新加
    private int customerId;
    private String customerName; // 新加
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

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
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

    public Mtl getMtl() {
        return mtl;
    }

    public void setMtl(Mtl mtl) {
        this.mtl = mtl;
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

    public double getStockqty() {
        return stockqty;
    }

    public void setStockqty(double stockqty) {
        this.stockqty = stockqty;
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public int getStockAreaId() {
        return stockAreaId;
    }

    public void setStockAreaId(int stockAreaId) {
        this.stockAreaId = stockAreaId;
    }

    public String getStockAName() {
        return stockAName;
    }

    public void setStockAName(String stockAName) {
        this.stockAName = stockAName;
    }

    public int getStockPositionId() {
        return stockPositionId;
    }

    public void setStockPositionId(int stockPositionId) {
        this.stockPositionId = stockPositionId;
    }

    public String getStockPName() {
        return stockPName;
    }

    public void setStockPName(String stockPName) {
        this.stockPName = stockPName;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
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

    public ScanningRecord2() {
        super();
    }

}
