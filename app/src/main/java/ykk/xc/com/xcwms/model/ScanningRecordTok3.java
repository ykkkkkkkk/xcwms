package ykk.xc.com.xcwms.model;

import java.io.Serializable;

/**
 * 保存传到k3中的字段
 */
public class ScanningRecordTok3 implements Serializable {

//	单据头部分---
//	销售部门："FSaleDeptID":{"FNumber":""}
//  客服： F_PAEZ_kefu
//	收货地址：FReceive
//	收货人电话：FReceiveTel
//	收货人：F_Consignee
//	交货地点："FHeadLocationId":{"FNumber":""}
//	承运商："FCarrierID":{"FNumber":""}
//	销售员："FSalesManID":{"FNumber":""}
//	运输方式："F_PAEZ_shipping_Type":{"FNumber":""}
//	物流公司："F_PAEZ_LOGISTICSCOMPANY":{"FNumber":""}
//	销售组："FSalesGroupID":{"FNumber":""}
//	出库类型："F_PAEZ_CKLX":{"FNumber":""}

	/*销售部门代码*/
	private String saleDeptNumber;
	/*客服*/
	private String customerService;
	/*销售部门代码*/
	private String freceive;
	/*收货人电话*/
	private String freceivetel;
	/*收货人*/
	private String fconsignee;
	/*承运商代码*/
	private String carrierNumber;
	/*销售员代码*/
	private String salerNumber;
	/*运输方式代码*/
	private String deliverWayNumber;
	/*物流公司代码*/
	private String deliveryCompanyNumber;
	/*出库类型代码*/
	private String exitTypeNumber;
	/* 摘要  */
	private String fpaezBeizhu;
	/* 采购员  */
	private String fpurchaserNumber;
	/* 采购订单号  */
	private String fpaezCgDanhao;
	/* 箱数 */
	private int fboxAmount;
	/* 仓管员 */
	private String stockStaffNumber;
	/* 采购部门代码 */
	private String fpurchaseDeptNumber;
	/* 收料部门代码 */
	private String fstockDeptNumber;
	/* 客户  */
	private String fpaezKehu;
	/* 备注 */
	private String fdescription;


//	单据Entry部分---

	/* 面积  */
	private double fpaezArea;
	/* 宽 */
	private String fpaezWidth;
	/* 高 */
	private String fpaezHigh;
	/* 行备注  */
	private String fentryNote;
	/* 金额 */
	private double famount;
	/* 调拨单价 */
	private double flhDbdj;
	/* 调拨价 */
	private double flhDbj;

	public ScanningRecordTok3() {
		super();
	}

	public String getSaleDeptNumber() {
		return saleDeptNumber;
	}
	public void setSaleDeptNumber(String saleDeptNumber) {
		this.saleDeptNumber = saleDeptNumber;
	}
	public String getCustomerService() {
		return customerService;
	}
	public void setCustomerService(String customerService) {
		this.customerService = customerService;
	}
	public String getFreceive() {
		return freceive;
	}
	public void setFreceive(String freceive) {
		this.freceive = freceive;
	}
	public String getFreceivetel() {
		return freceivetel;
	}
	public void setFreceivetel(String freceivetel) {
		this.freceivetel = freceivetel;
	}
	public String getFconsignee() {
		return fconsignee;
	}
	public void setFconsignee(String fconsignee) {
		this.fconsignee = fconsignee;
	}
	public String getCarrierNumber() {
		return carrierNumber;
	}
	public void setCarrierNumber(String carrierNumber) {
		this.carrierNumber = carrierNumber;
	}
	public String getSalerNumber() {
		return salerNumber;
	}
	public void setSalerNumber(String salerNumber) {
		this.salerNumber = salerNumber;
	}
	public String getDeliverWayNumber() {
		return deliverWayNumber;
	}
	public void setDeliverWayNumber(String deliverWayNumber) {
		this.deliverWayNumber = deliverWayNumber;
	}
	public String getDeliveryCompanyNumber() {
		return deliveryCompanyNumber;
	}
	public void setDeliveryCompanyNumber(String deliveryCompanyNumber) {
		this.deliveryCompanyNumber = deliveryCompanyNumber;
	}
	public String getExitTypeNumber() {
		return exitTypeNumber;
	}
	public void setExitTypeNumber(String exitTypeNumber) {
		this.exitTypeNumber = exitTypeNumber;
	}
	public double getFpaezArea() {
		return fpaezArea;
	}
	public void setFpaezArea(double fpaezArea) {
		this.fpaezArea = fpaezArea;
	}
	public String getFpaezWidth() {
		return fpaezWidth;
	}
	public void setFpaezWidth(String fpaezWidth) {
		this.fpaezWidth = fpaezWidth;
	}
	public String getFpaezHigh() {
		return fpaezHigh;
	}
	public void setFpaezHigh(String fpaezHigh) {
		this.fpaezHigh = fpaezHigh;
	}
	public String getFpaezBeizhu() {
		return fpaezBeizhu;
	}
	public void setFpaezBeizhu(String fpaezBeizhu) {
		this.fpaezBeizhu = fpaezBeizhu;
	}
	public String getFpurchaserNumber() {
		return fpurchaserNumber;
	}
	public void setFpurchaserNumber(String fpurchaserNumber) {
		this.fpurchaserNumber = fpurchaserNumber;
	}
	public String getFpaezCgDanhao() {
		return fpaezCgDanhao;
	}
	public void setFpaezCgDanhao(String fpaezCgDanhao) {
		this.fpaezCgDanhao = fpaezCgDanhao;
	}
	public int getFboxAmount() {
		return fboxAmount;
	}
	public void setFboxAmount(int fboxAmount) {
		this.fboxAmount = fboxAmount;
	}
	public String getStockStaffNumber() {
		return stockStaffNumber;
	}
	public void setStockStaffNumber(String stockStaffNumber) {
		this.stockStaffNumber = stockStaffNumber;
	}
	public String getFentryNote() {
		return fentryNote;
	}
	public void setFentryNote(String fentryNote) {
		this.fentryNote = fentryNote;
	}
	public String getFpurchaseDeptNumber() {
		return fpurchaseDeptNumber;
	}
	public void setFpurchaseDeptNumber(String fpurchaseDeptNumber) {
		this.fpurchaseDeptNumber = fpurchaseDeptNumber;
	}
	public String getFstockDeptNumber() {
		return fstockDeptNumber;
	}
	public void setFstockDeptNumber(String fstockDeptNumber) {
		this.fstockDeptNumber = fstockDeptNumber;
	}
	public double getFamount() {
		return famount;
	}
	public void setFamount(double famount) {
		this.famount = famount;
	}
	public String getFpaezKehu() {
		return fpaezKehu;
	}
	public void setFpaezKehu(String fpaezKehu) {
		this.fpaezKehu = fpaezKehu;
	}
	public String getFdescription() {
		return fdescription;
	}
	public void setFdescription(String fdescription) {
		this.fdescription = fdescription;
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


}
