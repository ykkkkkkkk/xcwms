package ykk.xc.com.xcwms.model.sal;

import java.io.Serializable;

import ykk.xc.com.xcwms.comm.Comm;
import ykk.xc.com.xcwms.model.Material;
import ykk.xc.com.xcwms.model.Organization;
import ykk.xc.com.xcwms.model.Staff;
import ykk.xc.com.xcwms.model.Stock;

/**
 * 发货通知单
 * @author Administrator
 *
 */
public class DeliOrder implements Serializable{
	private int fId; // 单据id,
	private String fbillno; // 单据编号,
	private String deliDate; // 发货日期
	private int custId; // 客户Id,
	private String custNumber; // 客户代码,
	private String custName; // 客户,
	private int deliOrgId; // 发货组织id
	private String deliOrgNumber; // 发货组织代码
	private String deliOrgName; // 发货组织
	private Organization deliOrg;
	private int mtlId; // 物料id
	private Material mtl; // 物料对象
	private String mtlFnumber; // 物料编码
	private String mtlFname; // 物料名称
	private String mtlUnitName; // 单位
	private int stockId; // 出货仓库id
	private String stockName; // 出货仓库名称
	private String stockNumber;//出库仓库代码
	private Stock stock; // 出货仓库
	private double deliFqty; // 销售数量
	private double deliFremainoutqty; // 未出库数量
	/*单价*/
	private double fprice;
	/*金额*/
	private double famount;
	private String deliveryWay; // 发货方式
	/*对应k3单据分录号字段*/
	private int entryId;
	private int isCheck; // 新加的是否选中
	private String salOrderNo; // 销售订单号
	private Integer salOrderEntryId;//销售订单分录entryId
	private String billCloseStatus;//发货通知单单据头关闭状态 ( A:未关闭 B:已关闭 )
	private String entryCloseStatus;//发货通知单单据体关闭状态 ( A:未关闭 B:已关闭 )
	// 临时使用的字段
	private double number;
	/*销售订单销售组织ID*/
	private int salOrgId;
	/*销售订单销售组织编码*/
	private String salOrgNumber;
	/*销售订单销售组织名称*/
	private String salOrgName;
	/*物流公司id*/
	private String deliveryCompanyId;
	/*物流公司代码*/
	private String deliveryCompanyNumber;
	/*物流公司名称*/
	private String deliveryCompanyName;
	/*叶片*/
	private String leaf;
	/*叶片1*/
	private String leaf1;
	/*发货通知里的宽，用于打印装箱清单取值*/
	private String width;
	/*发货通知里的高，用于打印装箱清单取值*/
	private String high;
	/*出库类型*/
	private String exitType;

	/*出库类型代码*/
	private String exitTypeNumber;
	/*客服*/
	private String customerService;
	/*收货人电话*/
	private String freceivetel;
	/*收货地址*/
	private String freceive;
	/*收货人*/
	private String fconsignee;
	/*运输方式代码*/
	private String deliverWayNumber;
	/*运输方式名称*/
	private String deliverWayName;
	/*销售员代码*/
	private String salerNumber;
	/*销售员名称*/
	private String salerName;
	/*摘要*/
	private String summary;
	/*承运商代码*/
	private String carrierNumber;
	/*承运商名称*/
	private String carrierName;
	/*销售部门代码*/
	private String saleDeptNumber;
	/*销售部门名称*/
	private String saleDeptName;
	/*订单套数*/
	private int coveQty;
	/* 发货单子表备注 */
	private String entryRemark;
	/* 发货单子表面积 */
	private double entryArea;
	/*仓管员id*/
	private int stockStaffId;
	/*仓管员代码*/
	private String stockStaffNumber;
	/*仓管员名称*/
	private String stockStaffName;
	private Staff stockStaff; // 仓管员对象
	/* 调拨单价 */
	private double flhDbdj;
	/* 调拨价 */
	private double flhDbj;
	/* 出库类型   */
	private String outStockType;
	private int inStockId; // 调入仓库id
	private String inStockName; // 调入仓库名称
	private String inStockNumber;//调入仓库代码
	private Stock inStock; // 调入仓库

	public DeliOrder() {
		super();
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

	public Organization getDeliOrg() {
		return deliOrg;
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

	public String getStockName() {
		return stockName;
	}

	public Stock getStock() {
		return stock;
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

	public int getIsCheck() {
		return isCheck;
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

	public void setDeliOrg(Organization deliOrg) {
		this.deliOrg = deliOrg;
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

	public void setStockName(String stockName) {
		this.stockName = stockName;
	}

	public String getStockNumber() {
		return stockNumber;
	}

	public void setStockNumber(String stockNumber) {
		this.stockNumber = stockNumber;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
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

	public void setIsCheck(int isCheck) {
		this.isCheck = isCheck;
	}

	public String getSalOrderNo() {
		return salOrderNo;
	}

	public void setSalOrderNo(String salOrderNo) {
		this.salOrderNo = salOrderNo;
	}

	public Integer getSalOrderEntryId() {
		return salOrderEntryId;
	}

	public void setSalOrderEntryId(Integer salOrderEntryId) {
		this.salOrderEntryId = salOrderEntryId;
	}

	public double getNumber() {
		return number;
	}

	public void setNumber(double number) {
		this.number = number;
	}

	public String getBillCloseStatus() {
		return billCloseStatus;
	}

	public void setBillCloseStatus(String billCloseStatus) {
		this.billCloseStatus = billCloseStatus;
	}

	public String getEntryCloseStatus() {
		return entryCloseStatus;
	}

	public void setEntryCloseStatus(String entryCloseStatus) {
		this.entryCloseStatus = entryCloseStatus;
	}

	public int getSalOrgId() {
		return salOrgId;
	}

	public void setSalOrgId(int salOrgId) {
		this.salOrgId = salOrgId;
	}

	public String getSalOrgNumber() {
		return salOrgNumber;
	}

	public void setSalOrgNumber(String salOrgNumber) {
		this.salOrgNumber = salOrgNumber;
	}

	public String getSalOrgName() {
		return salOrgName;
	}

	public void setSalOrgName(String salOrgName) {
		this.salOrgName = salOrgName;
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

	public String getLeaf() {
		return leaf;
	}

	public void setLeaf(String leaf) {
		this.leaf = leaf;
	}

	public String getLeaf1() {
		return leaf1;
	}

	public void setLeaf1(String leaf1) {
		this.leaf1 = leaf1;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHigh() {
		return high;
	}

	public void setHigh(String high) {
		this.high = high;
	}

	public String getExitType() {
		return exitType;
	}

	public void setExitType(String exitType) {
		this.exitType = exitType;
	}

	public double getFprice() {
		return fprice;
	}

	public void setFprice(double fprice) {
		this.fprice = fprice;
	}

	public double getFamount() {
		return famount;
	}

	public void setFamount(double famount) {
		this.famount = famount;
	}

	public String getExitTypeNumber() {
		return exitTypeNumber;
	}

	public void setExitTypeNumber(String exitTypeNumber) {
		this.exitTypeNumber = exitTypeNumber;
	}

	public String getCustomerService() {
		return customerService;
	}

	public void setCustomerService(String customerService) {
		this.customerService = customerService;
	}

	public String getFreceivetel() {
		return freceivetel;
	}

	public void setFreceivetel(String freceivetel) {
		this.freceivetel = freceivetel;
	}

	public String getFreceive() {
		return freceive;
	}

	public void setFreceive(String freceive) {
		this.freceive = freceive;
	}

	public String getFconsignee() {
		return fconsignee;
	}

	public void setFconsignee(String fconsignee) {
		this.fconsignee = fconsignee;
	}

	public String getDeliverWayNumber() {
		return deliverWayNumber;
	}

	public void setDeliverWayNumber(String deliverWayNumber) {
		this.deliverWayNumber = deliverWayNumber;
	}

	public String getDeliverWayName() {
		return deliverWayName;
	}

	public void setDeliverWayName(String deliverWayName) {
		this.deliverWayName = deliverWayName;
	}

	public String getSalerNumber() {
		return salerNumber;
	}

	public void setSalerNumber(String salerNumber) {
		this.salerNumber = salerNumber;
	}

	public String getSalerName() {
		return salerName;
	}

	public void setSalerName(String salerName) {
		this.salerName = salerName;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getCarrierNumber() {
		return carrierNumber;
	}

	public void setCarrierNumber(String carrierNumber) {
		this.carrierNumber = carrierNumber;
	}

	public String getCarrierName() {
		return carrierName;
	}

	public void setCarrierName(String carrierName) {
		this.carrierName = carrierName;
	}

	public String getSaleDeptNumber() {
		return saleDeptNumber;
	}

	public void setSaleDeptNumber(String saleDeptNumber) {
		this.saleDeptNumber = saleDeptNumber;
	}

	public String getSaleDeptName() {
		return saleDeptName;
	}

	public void setSaleDeptName(String saleDeptName) {
		this.saleDeptName = saleDeptName;
	}

	public int getCoveQty() {
		return coveQty;
	}

	public void setCoveQty(int coveQty) {
		this.coveQty = coveQty;
	}

	public String getEntryRemark() {
		return entryRemark;
	}

	public void setEntryRemark(String entryRemark) {
		this.entryRemark = entryRemark;
	}

	public double getEntryArea() {
		return entryArea;
	}

	public void setEntryArea(double entryArea) {
		this.entryArea = entryArea;
	}

	public int getStockStaffId() {
		return stockStaffId;
	}

	public void setStockStaffId(int stockStaffId) {
		this.stockStaffId = stockStaffId;
	}

	public String getStockStaffNumber() {
		return stockStaffNumber;
	}

	public void setStockStaffNumber(String stockStaffNumber) {
		this.stockStaffNumber = stockStaffNumber;
	}

	public String getStockStaffName() {
		return stockStaffName;
	}

	public void setStockStaffName(String stockStaffName) {
		this.stockStaffName = stockStaffName;
	}

	public Staff getStockStaff() {
		return stockStaff;
	}

	public void setStockStaff(Staff stockStaff) {
		this.stockStaff = stockStaff;
	}

	public double getFlhDbdj() {
		return flhDbdj;
	}

	public void setFlhDbdj(double flhDbdj) {
		this.flhDbdj = flhDbdj;
	}

	public double getFlhDbj() {
		return flhDbj;
	}

	public void setFlhDbj(double flhDbj) {
		this.flhDbj = flhDbj;
	}

	public String getOutStockType() {
		return outStockType;
	}

	public void setOutStockType(String outStockType) {
		this.outStockType = outStockType;
	}

	public int getInStockId() {
		return inStockId;
	}

	public String getInStockName() {
		return inStockName;
	}

	public String getInStockNumber() {
		return inStockNumber;
	}

	public Stock getInStock() {
		return inStock;
	}

	public void setInStockId(int inStockId) {
		this.inStockId = inStockId;
	}

	public void setInStockName(String inStockName) {
		this.inStockName = inStockName;
	}

	public void setInStockNumber(String inStockNumber) {
		this.inStockNumber = inStockNumber;
	}

	public void setInStock(Stock inStock) {
		this.inStock = inStock;
	}

}
