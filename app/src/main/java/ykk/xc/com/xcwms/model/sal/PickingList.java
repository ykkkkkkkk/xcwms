package ykk.xc.com.xcwms.model.sal;

import java.io.Serializable;
import java.util.List;

import ykk.xc.com.xcwms.model.Material;
import ykk.xc.com.xcwms.model.Stock;
import ykk.xc.com.xcwms.model.StockPosition;

/*
 * 拣货单
 */
public class PickingList implements Serializable {
    private int id; // 主键id
    private String pickingListNo; // 拣货单
    private double pickingListNum; // 拣货数量
    private int fId; // 单据id,
    private String fbillno; // 单据编号,
    private String deliDate; // 发货日期
    private int custId; // 客户Id,
    private String custNumber; // 客户代码,
    private String custName; // 客户,
    private int deliOrgId; // 发货组织id
    private String deliOrgNumber; // 发货代码
    private String deliOrgName; // 发货组织
    private int salOrgId; // 销售组织id
    private String salOrgNumber; // 销售代码
    private String salOrgName; // 销售组织
    private int mtlId; // 物料id
    private Material mtl; // 物料对象
    private String mtlFnumber; // 物料编码
    private String mtlFname; // 物料名称
    private String mtlUnitName; // 单位
    private int stockId; // 出货仓库id
    private String stockNumber; // 出货仓库代码
    private String stockName; // 出货仓库名称
    private int stockPositionId; // 库位id
    private String stockPositionNumber; // 库位代码
    private String stockPositionName; // 库位名称
    private double deliFqty; // 销售数量
    private double deliFremainoutqty; // 未出库数量
    private String deliveryWay; // 发货方式
    /*对应k3单据分录号字段*/
    private int entryId;
    private String batchNo; // 批次号
    private String snNo; // 序列号
    private String barcode; // 条码号
    private int createUserId;
    private String createUserName;
    private String createDate;
    /* 可用的数量(未存表)  */
    private double usableFqty;
    private char pickingType; // 拣货类型：1.拣货装箱，2.拣货装车
    private String salOrderNo; // 销售订单号
    private int salOrderNoEntryId; // 销售订单分录id
    /*物流公司id*/
    private String deliveryCompanyId;
    /*物流公司代码*/
    private String deliveryCompanyNumber;
    /*物流公司名称*/
    private String deliveryCompanyName;
    private int isCheck; // 新加的，用于前台临时用判断是否选中
    private Stock stock;
    private StockPosition stockPosition;
    private List<String> listBarcode; // 记录每行中扫的条码barcode
    private String strBarcodes; // 用逗号拼接的条码号

    public PickingList() {
        super();
    }

    public int getId() {
        return id;
    }

    public String getPickingListNo() {
        return pickingListNo;
    }

    public double getPickingListNum() {
        return pickingListNum;
    }

    public int getfId() {
        return fId;
    }

    public String getFbillno() {
        return fbillno;
    }

    public String getDeliDate() {
        return deliDate;
    }

    public int getCustId() {
        return custId;
    }

    public String getCustNumber() {
        return custNumber;
    }

    public String getCustName() {
        return custName;
    }

    public int getDeliOrgId() {
        return deliOrgId;
    }

    public String getDeliOrgNumber() {
        return deliOrgNumber;
    }

    public String getDeliOrgName() {
        return deliOrgName;
    }

    public int getMtlId() {
        return mtlId;
    }

    public Material getMtl() {
        return mtl;
    }

    public String getMtlFnumber() {
        return mtlFnumber;
    }

    public String getMtlFname() {
        return mtlFname;
    }

    public String getMtlUnitName() {
        return mtlUnitName;
    }

    public int getStockId() {
        return stockId;
    }

    public String getStockNumber() {
        return stockNumber;
    }

    public String getStockName() {
        return stockName;
    }

    public int getStockPositionId() {
        return stockPositionId;
    }

    public String getStockPositionNumber() {
        return stockPositionNumber;
    }

    public String getStockPositionName() {
        return stockPositionName;
    }

    public double getDeliFqty() {
        return deliFqty;
    }

    public double getDeliFremainoutqty() {
        return deliFremainoutqty;
    }

    public String getDeliveryWay() {
        return deliveryWay;
    }

    public int getEntryId() {
        return entryId;
    }

    public String getBatchNo() {
        return batchNo;
    }

    public String getSnNo() {
        return snNo;
    }

    public int getCreateUserId() {
        return createUserId;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPickingListNo(String pickingListNo) {
        this.pickingListNo = pickingListNo;
    }

    public void setPickingListNum(double pickingListNum) {
        this.pickingListNum = pickingListNum;
    }

    public void setfId(int fId) {
        this.fId = fId;
    }

    public void setFbillno(String fbillno) {
        this.fbillno = fbillno;
    }

    public void setDeliDate(String deliDate) {
        this.deliDate = deliDate;
    }

    public void setCustId(int custId) {
        this.custId = custId;
    }

    public void setCustNumber(String custNumber) {
        this.custNumber = custNumber;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public void setDeliOrgId(int deliOrgId) {
        this.deliOrgId = deliOrgId;
    }

    public void setDeliOrgNumber(String deliOrgNumber) {
        this.deliOrgNumber = deliOrgNumber;
    }

    public void setDeliOrgName(String deliOrgName) {
        this.deliOrgName = deliOrgName;
    }

    public void setMtlId(int mtlId) {
        this.mtlId = mtlId;
    }

    public void setMtl(Material mtl) {
        this.mtl = mtl;
    }

    public void setMtlFnumber(String mtlFnumber) {
        this.mtlFnumber = mtlFnumber;
    }

    public void setMtlFname(String mtlFname) {
        this.mtlFname = mtlFname;
    }

    public void setMtlUnitName(String mtlUnitName) {
        this.mtlUnitName = mtlUnitName;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public void setStockNumber(String stockNumber) {
        this.stockNumber = stockNumber;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public void setStockPositionId(int stockPositionId) {
        this.stockPositionId = stockPositionId;
    }

    public void setStockPositionNumber(String stockPositionNumber) {
        this.stockPositionNumber = stockPositionNumber;
    }

    public void setStockPositionName(String stockPositionName) {
        this.stockPositionName = stockPositionName;
    }

    public void setDeliFqty(double deliFqty) {
        this.deliFqty = deliFqty;
    }

    public void setDeliFremainoutqty(double deliFremainoutqty) {
        this.deliFremainoutqty = deliFremainoutqty;
    }

    public void setDeliveryWay(String deliveryWay) {
        this.deliveryWay = deliveryWay;
    }

    public void setEntryId(int entryId) {
        this.entryId = entryId;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public void setSnNo(String snNo) {
        this.snNo = snNo;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public void setCreateUserId(int createUserId) {
        this.createUserId = createUserId;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public int getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(int isCheck) {
        this.isCheck = isCheck;
    }

    public double getUsableFqty() {
        return usableFqty;
    }

    public void setUsableFqty(double usableFqty) {
        this.usableFqty = usableFqty;
    }

    public char getPickingType() {
        return pickingType;
    }

    public void setPickingType(char pickingType) {
        this.pickingType = pickingType;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public StockPosition getStockPosition() {
        return stockPosition;
    }

    public void setStockPosition(StockPosition stockPosition) {
        this.stockPosition = stockPosition;
    }

    public String getSalOrderNo() {
        return salOrderNo;
    }

    public void setSalOrderNo(String salOrderNo) {
        this.salOrderNo = salOrderNo;
    }

    public int getSalOrderNoEntryId() {
        return salOrderNoEntryId;
    }

    public void setSalOrderNoEntryId(int salOrderNoEntryId) {
        this.salOrderNoEntryId = salOrderNoEntryId;
    }

    public String getDeliveryCompanyId() {
        return deliveryCompanyId;
    }

    public void setDeliveryCompanyId(String deliveryCompanyId) {
        this.deliveryCompanyId = deliveryCompanyId;
    }

    public String getDeliveryCompanyNumber() {
        return deliveryCompanyNumber;
    }

    public void setDeliveryCompanyNumber(String deliveryCompanyNumber) {
        this.deliveryCompanyNumber = deliveryCompanyNumber;
    }

    public String getDeliveryCompanyName() {
        return deliveryCompanyName;
    }

    public void setDeliveryCompanyName(String deliveryCompanyName) {
        this.deliveryCompanyName = deliveryCompanyName;
    }

    public List<String> getListBarcode() {
        return listBarcode;
    }

    public void setListBarcode(List<String> listBarcode) {
        this.listBarcode = listBarcode;
    }

    public String getStrBarcodes() {
        return strBarcodes;
    }

    public void setStrBarcodes(String strBarcodes) {
        this.strBarcodes = strBarcodes;
    }

    public int getSalOrgId() {
        return salOrgId;
    }

    public String getSalOrgNumber() {
        return salOrgNumber;
    }

    public String getSalOrgName() {
        return salOrgName;
    }

    public void setSalOrgId(int salOrgId) {
        this.salOrgId = salOrgId;
    }

    public void setSalOrgNumber(String salOrgNumber) {
        this.salOrgNumber = salOrgNumber;
    }

    public void setSalOrgName(String salOrgName) {
        this.salOrgName = salOrgName;
    }


}
