package ykk.xc.com.xcwms.model.pur;

import java.io.Serializable;

import ykk.xc.com.xcwms.model.Material;

/**
 * 生产任务单	ykk
 * @author Administrator
 *
 */
public class ProdOrder implements Serializable {
	private int fId; // 单据id,
	private String fbillno; // 单据编号,
	private String fbillType; // 单据类型
	private String prodFdate; // 生产日期
	private int prodOrgId; // 生产组织Id,
	private String prodOrgNumber; // 生产组织代码,
	private String prodOrgName; // 生产组织名称,
	private int deptId;//生产车间id
	private String deptNumber;//生产车间代码
	private String deptName;//生产车间名称
	private int mtlId; // 物料id
	private Material mtl; // 物料对象
	private String mtlFnumber; // 物料编码
	private String mtlFname; // 物料名称
	private String unitFname; // 单位
	private double prodFqty; // 生产数量
	private double usableFqty; // 可用的数量
	private int custId; // 客户
	private String custNumber; // 客户代码
	private String custName; // 客户名称
	private int salOrderId; // 销售订单Id
	private String salOrderNo; // 销售订单号
	private int salOrderEntryId;//销售订单分录entryId
	/* 对应t_barCodeTable 表中的barcode字段  */
	private String barcode;
	/* 对应t_barCodeTable 表中的batchCode字段  */
	private String batchCode;
	/* 对应t_barCodeTable 表中的snCode字段  */
	private String snCode;

	/*对应k3单据分录号字段*/
	private int entryId;
	/*对应k3单据体里的生产顺序号*/
	private String prodSeqNumber;
	/*k3收货方地址*/
	private String receiveAddress;
	/*k3收货联系人*/
	private String receivePerson;
	/*k3收货人电话*/
	private String receiveTel;
	/*k3物流公司名称*/
	private String deliveryCompanyName;
	/*k3备注*/
	private String remarks;
	/*计划开工时间*/
	private String planStartDate;
	/*叶片*/
	private String leaf;
	/*叶片1*/
	private String leaf1;
	/*物料规格*/
	private String mtlSize;
	/* 单据分录生码状态查询，0是默认值代表未生码，1代表已生码 */
	private int createCodeStatus;
	/*销售订单里的宽，用于打印装箱清单取值*/
	private String width;
	/*销售订单里的高，用于打印装箱清单取值*/
	private String high;
	/*销售订单销售组织ID*/
	private int salOrgId;
	/*销售订单销售组织编码*/
	private String salOrgNumber;
	/*销售订单销售组织名称*/
	private String salOrgName;
	/*物料大类*/
	private String mtlBigClass;
	/*销售订单交货方式*/
	private String deliveryWay;
	/*订单套数*/
	private int coveQty;


	private int isCheck; // 新加的是否选中

	public ProdOrder() {
		super();
	}

	public int getfId() {
		return fId;
	}

	public String getFbillno() {
		return fbillno;
	}

	public String getFbillType() {
		return fbillType;
	}

	public String getProdFdate() {
		return prodFdate;
	}

	public int getProdOrgId() {
		return prodOrgId;
	}

	public String getProdOrgNumber() {
		return prodOrgNumber;
	}

	public String getProdOrgName() {
		return prodOrgName;
	}

	public int getDeptId() {
		return deptId;
	}

	public String getDeptNumber() {
		return deptNumber;
	}

	public String getDeptName() {
		return deptName;
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

	public String getUnitFname() {
		return unitFname;
	}

	public double getProdFqty() {
		return prodFqty;
	}

	public double getUsableFqty() {
		return usableFqty;
	}

	public void setUsableFqty(double usableFqty) {
		this.usableFqty = usableFqty;
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

	public String getSalOrderNo() {
		return salOrderNo;
	}

	public void setfId(int fId) {
		this.fId = fId;
	}

	public void setFbillno(String fbillno) {
		this.fbillno = fbillno;
	}

	public void setFbillType(String fbillType) {
		this.fbillType = fbillType;
	}

	public void setProdFdate(String prodFdate) {
		this.prodFdate = prodFdate;
	}

	public void setProdOrgId(int prodOrgId) {
		this.prodOrgId = prodOrgId;
	}

	public void setProdOrgNumber(String prodOrgNumber) {
		this.prodOrgNumber = prodOrgNumber;
	}

	public void setProdOrgName(String prodOrgName) {
		this.prodOrgName = prodOrgName;
	}

	public void setDeptId(int deptId) {
		this.deptId = deptId;
	}

	public void setDeptNumber(String deptNumber) {
		this.deptNumber = deptNumber;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
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

	public void setUnitFname(String unitFname) {
		this.unitFname = unitFname;
	}

	public void setProdFqty(double prodFqty) {
		this.prodFqty = prodFqty;
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

	public int getSalOrderId() {
		return salOrderId;
	}

	public void setSalOrderId(int salOrderId) {
		this.salOrderId = salOrderId;
	}

	public void setSalOrderNo(String salOrderNo) {
		this.salOrderNo = salOrderNo;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getBatchCode() {
		return batchCode;
	}

	public String getSnCode() {
		return snCode;
	}

	public void setBatchCode(String batchCode) {
		this.batchCode = batchCode;
	}

	public void setSnCode(String snCode) {
		this.snCode = snCode;
	}

	public int getEntryId() {
		return entryId;
	}

	public void setEntryId(int entryId) {
		this.entryId = entryId;
	}

	public String getProdSeqNumber() {
		return prodSeqNumber;
	}

	public void setProdSeqNumber(String prodSeqNumber) {
		this.prodSeqNumber = prodSeqNumber;
	}

	public String getReceiveAddress() {
		return receiveAddress;
	}

	public void setReceiveAddress(String receiveAddress) {
		this.receiveAddress = receiveAddress;
	}

	public String getReceivePerson() {
		return receivePerson;
	}

	public void setReceivePerson(String receivePerson) {
		this.receivePerson = receivePerson;
	}

	public String getReceiveTel() {
		return receiveTel;
	}

	public void setReceiveTel(String receiveTel) {
		this.receiveTel = receiveTel;
	}

	public String getDeliveryCompanyName() {
		return deliveryCompanyName;
	}

	public void setDeliveryCompanyName(String deliveryCompanyName) {
		this.deliveryCompanyName = deliveryCompanyName;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getPlanStartDate() {
		return planStartDate;
	}

	public void setPlanStartDate(String planStartDate) {
		this.planStartDate = planStartDate;
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

	public String getMtlSize() {
		return mtlSize;
	}

	public void setMtlSize(String mtlSize) {
		this.mtlSize = mtlSize;
	}

	public int getCreateCodeStatus() {
		return createCodeStatus;
	}

	public void setCreateCodeStatus(int createCodeStatus) {
		this.createCodeStatus = createCodeStatus;
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

	public Integer getSalOrderEntryId() {
		return salOrderEntryId;
	}

	public void setSalOrderEntryId(Integer salOrderEntryId) {
		this.salOrderEntryId = salOrderEntryId;
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

	public String getMtlBigClass() {
		return mtlBigClass;
	}

	public void setMtlBigClass(String mtlBigClass) {
		this.mtlBigClass = mtlBigClass;
	}

	public String getDeliveryWay() {
		return deliveryWay;
	}

	public void setDeliveryWay(String deliveryWay) {
		this.deliveryWay = deliveryWay;
	}

	public int getCoveQty() {
		return coveQty;
	}

	public void setCoveQty(int coveQty) {
		this.coveQty = coveQty;
	}

	public int getIsCheck() {
		return isCheck;
	}

	public void setIsCheck(int isCheck) {
		this.isCheck = isCheck;
	}


}
