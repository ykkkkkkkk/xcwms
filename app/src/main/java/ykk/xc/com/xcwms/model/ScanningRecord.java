package ykk.xc.com.xcwms.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.List;

/**
 * Scanning_record 出入库记录
 */
public class ScanningRecord implements Serializable {
    private int id;
    private int type;
    private int sourceId;
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
    private int createUserId;			//创建人id
    private String createUserName;		//创建人
    private char sourceType; 			// 来源单据类型（1.物料，2.采购订单，3.收料通知单，4.生产任务单，5.销售订货单，6.拣货单，7.生产装箱，8.采购收料任务单，9.复核单）
    // 新加的
    private String receiveOrgFnumber;
    private String purOrgFnumber;
    private String supplierFnumber;
    private String mtlFnumber;
    private String unitFnumber;
    private String batchFnumber;
    private String stockFnumber;
    private int poFid; // 采购订单id
    private String poFbillno; // 采购订单编码
    private double poFmustqty; // 采购订单剩余数
    private String departmentFnumber;
    private String custFnumber;
    private int entryId; // 订单分录内码
    private String k3UserName; // 对应k3的用户
    private String k3UserFnumber; // 对应k3的操作员代码
    private int tempId; // 来源的主键id
    private String relationObj; // 来源的对象
    private String fsrcBillTypeId; // 来源单据类型名称
    private String fRuleId; // 下推来源单据类型名称
    private String fsTableName; // 下推来源表体
    private String fcarriageNo; // 运输单号
    private String expressNumber; // 物流公司
    private int salOrderId; // 销售订单id
    private String salOrderNo; // 销售订单号
    private int salOrderEntryId; // 销售订单分录id
    // 临时变量
    private List<String> listBarcode; // 记录每行中扫的条码barcode
    private String strBarcodes; // 用逗号拼接的条码号
    private String kdAccount; // k3 用户的密码
    private String kdAccountPassword; // k3 用户的密码

    public int getId() {
        return id;
    }
    public int getType() {
        return type;
    }
    public int getSourceId() {
        return sourceId;
    }
    public void setSourceId(int source_Id) {
        this.sourceId = sourceId;
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
    public int getPoFid() {
        return poFid;
    }
    public String getPoFbillno() {
        return poFbillno;
    }
    public double getPoFmustqty() {
        return poFmustqty;
    }
    public void setPoFid(int poFid) {
        this.poFid = poFid;
    }
    public void setPoFbillno(String poFbillno) {
        this.poFbillno = poFbillno;
    }
    public void setPoFmustqty(double poFmustqty) {
        this.poFmustqty = poFmustqty;
    }
    public int getCreateUserId() {
        return createUserId;
    }
    public void setCreateUserId(int createUserId) {
        this.createUserId = createUserId;
    }
    public String getCreateUserName() {
        return createUserName;
    }
    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }
    public String getDepartmentFnumber() {
        return departmentFnumber;
    }
    public void setDepartmentFnumber(String departmentFnumber) {
        this.departmentFnumber = departmentFnumber;
    }
    public String getCustFnumber() {
        return custFnumber;
    }
    public void setCustFnumber(String custFnumber) {
        this.custFnumber = custFnumber;
    }
    public int getEntryId() { return entryId;
    }
    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }
    public String getK3UserName() {
        return k3UserName;
    }
    public void setK3UserName(String k3UserName) {
        this.k3UserName = k3UserName;
    }
    public String getK3UserFnumber() {
        return k3UserFnumber;
    }
    public void setK3UserFnumber(String k3UserFnumber) {
        this.k3UserFnumber = k3UserFnumber;
    }
    public char getSourceType() {
        return sourceType;
    }
    public void setSourceType(char sourceType) {
        this.sourceType = sourceType;
    }
    public int getTempId() {
        return tempId;
    }
    public void setTempId(int tempId) {
        this.tempId = tempId;
    }
    public String getRelationObj() {
        return relationObj;
    }
    public void setRelationObj(String relationObj) {
        this.relationObj = relationObj;
    }
    public String getFsTableName() {
        return fsTableName;
    }
    public void setFsTableName(String fsTableName) {
        this.fsTableName = fsTableName;
    }
    public String getfRuleId() {
        return fRuleId;
    }
    public void setfRuleId(String fRuleId) {
        this.fRuleId = fRuleId;
    }
    public String getFsrcBillTypeId() {
        return fsrcBillTypeId;
    }
    public void setFsrcBillTypeId(String fsrcBillTypeId) {
        this.fsrcBillTypeId = fsrcBillTypeId;
    }
    public String getFcarriageNo() {
        return fcarriageNo;
    }
    public void setFcarriageNo(String fcarriageNo) {
        this.fcarriageNo = fcarriageNo;
    }
    public String getExpressNumber() {
        return expressNumber;
    }
    public void setExpressNumber(String expressNumber) {
        this.expressNumber = expressNumber;
    }
    public List<String> getListBarcode() {
        return listBarcode;
    }
    public String getStrBarcodes() {
        return strBarcodes;
    }
    public void setListBarcode(List<String> listBarcode) {
        this.listBarcode = listBarcode;
    }
    public void setStrBarcodes(String strBarcodes) {
        this.strBarcodes = strBarcodes;
    }
    public String getKdAccount() {
        return kdAccount;
    }
    public String getKdAccountPassword() {
        return kdAccountPassword;
    }
    public void setKdAccount(String kdAccount) {
        this.kdAccount = kdAccount;
    }
    public void setKdAccountPassword(String kdAccountPassword) {
        this.kdAccountPassword = kdAccountPassword;
    }
    public int getSalOrderId() {
        return salOrderId;
    }
    public String getSalOrderNo() {
        return salOrderNo;
    }
    public int getSalOrderEntryId() {
        return salOrderEntryId;
    }
    public void setSalOrderId(int salOrderId) {
        this.salOrderId = salOrderId;
    }
    public void setSalOrderNo(String salOrderNo) {
        this.salOrderNo = salOrderNo;
    }
    public void setSalOrderEntryId(int salOrderEntryId) {
        this.salOrderEntryId = salOrderEntryId;
    }

    public ScanningRecord() {
        super();
    }
}
