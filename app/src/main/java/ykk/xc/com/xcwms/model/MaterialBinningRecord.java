package ykk.xc.com.xcwms.model;

import java.io.Serializable;

import ykk.xc.com.xcwms.model.sal.BoxBarCode;

/**
 * 物料装箱记录类
 * @author Administrator
 *
 */
public class MaterialBinningRecord implements Serializable {

	/*id*/
	private Integer id;
	/*包装物id*/
	private Integer boxBarCodeId;
	/*包装物条码类*/
	private BoxBarCode boxBarCode;
	/*物料id*/
	private Integer materialId;
	/*物料类*/
	private Material material;
	/*箱子里装入物料的数量*/
	private double number;
	/*关联单据id*/
	private Integer relationBillId;
	/*关联单据号*/
	private String relationBillNumber;
	/*客户id*/
	private Integer customerId;
	/*客户*/
	private Customer customer;
	/**物流方式
	 * 1代表快递
	 * 2代表物流
	 * */
	private Integer expressType;
	private String deliveryWay; // 交货方式
	/**装箱业务类型
	 * 1代表外购入库
	 * 2代表销售出库
	 * */
	private Integer packageWorkType;
	/* 物料包装类型（1：单色装，2：混色装 */
	private char binningType;

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getBoxBarCodeId() {
		return boxBarCodeId;
	}
	public void setBoxBarCodeId(Integer boxBarCodeId) {
		this.boxBarCodeId = boxBarCodeId;
	}
	public BoxBarCode getBoxBarCode() {
		return boxBarCode;
	}
	public void setBoxBarCode(BoxBarCode boxBarCode) {
		this.boxBarCode = boxBarCode;
	}
	public Integer getMaterialId() {
		return materialId;
	}
	public void setMaterialId(Integer materialId) {
		this.materialId = materialId;
	}
	public Material getMaterial() {
		return material;
	}
	public void setMaterial(Material material) {
		this.material = material;
	}
	public double getNumber() {
		return number;
	}
	public void setNumber(double number) {
		this.number = number;
	}
	public Integer getRelationBillId() {
		return relationBillId;
	}
	public void setRelationBillId(Integer relationBillId) {
		this.relationBillId = relationBillId;
	}
	public String getRelationBillNumber() {
		return relationBillNumber;
	}
	public void setRelationBillNumber(String relationBillNumber) {
		this.relationBillNumber = relationBillNumber;
	}
	public Integer getCustomerId() {
		return customerId;
	}
	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	public Integer getExpressType() {
		return expressType;
	}
	public void setExpressType(Integer expressType) {
		this.expressType = expressType;
	}
	public Integer getPackageWorkType() {
		return packageWorkType;
	}
	public void setPackageWorkType(Integer packageWorkType) {
		this.packageWorkType = packageWorkType;
	}
	public String getDeliveryWay() {
		return deliveryWay;
	}
	public void setDeliveryWay(String deliveryWay) {
		this.deliveryWay = deliveryWay;
	}
	public char getBinningType() {
		return binningType;
	}
	public void setBinningType(char binningType) {
		this.binningType = binningType;
	}

	@Override
	public String toString() {
		return "MaterialBinningRecord [id=" + id + ", boxBarCodeId=" + boxBarCodeId + ", boxBarCode=" + boxBarCode
				+ ", materialId=" + materialId + ", material=" + material + ", number=" + number + ", relationBillId="
				+ relationBillId + ", relationBillNumber=" + relationBillNumber + ", customerId=" + customerId
				+ ", customer=" + customer + ", expressType=" + expressType + ", deliveryWay=" + deliveryWay
				+ ", packageWorkType=" + packageWorkType + ", binningType=" + binningType + "]";
	}

	public MaterialBinningRecord() {
		super();
	}

}
