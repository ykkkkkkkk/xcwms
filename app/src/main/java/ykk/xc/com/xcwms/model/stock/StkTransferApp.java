package ykk.xc.com.xcwms.model.stock;

import java.io.Serializable;

import ykk.xc.com.xcwms.model.Material;

/**
 * 调拨申请单实体类
 * @author Administrator
 *
 */
public class StkTransferApp implements Serializable {

	/*k3单据内码*/
	private Integer fId;

	/*k3单据类型id*/
	private String fBillTypeId;

	/*k3单据类型代码*/
	private String fBillTypeNumber;

	/*k3单据类型名称*/
	private String fBillTypeName;

	/*k3单据编码*/
	private String fBillNo;

	/*k3单据日期*/
	private String fDate;

	/*k3调拨申请组织id*/
	private Integer fAppOrgId;

	/*k3调拨申请组织代码*/
	private String fAppOrgNumber;

	/*k3调拨申请组织名称*/
	private String fAppOrgName;

	/*调拨类型*/
	private String fTransType;

	/*调出货主类型id*/
	private String fOwnerTypeId;

	/*调入货主类型id*/
	private String fOwnerTypeInId;

	/*k3单据分录id*/
	private Integer fEntryId;

	/*k3单据字表行号*/
	private Integer fSeq;

	/*物料id*/
	private Integer fMaterialId;

	/*物料代码*/
	private String fMaterialNumber;

	/*物料名称*/
	private String fMaterialName;

	/*调出组织id*/
	private Integer fStockOrgId;

	/*调出组织代码*/
	private String fStockOrgNumber;

	/*调出组织名称*/
	private String fStockOrgName;

	/*调入组织id*/
	private Integer fStockOrgInId;

	/*调入组织代码*/
	private String fStockOrgInNumber;

	/*调入组织名称*/
	private String fStockOrgInName;

	/*调出仓库id*/
	private Integer fStockId;

	/*调出仓库代码*/
	private String fStockNumber;

	/*调出仓库名称*/
	private String fStockName;

	/*调入仓库id*/
	private Integer fStockInId;

	/*调入仓库代码*/
	private String fStockInNumber;

	/*调入仓库名称*/
	private String fStockInName;

	/*调出货主id*/
	private Integer fOwnerId;

	/*调出货主代码*/
	private String fOwnerNumber;

	/*调出货主名称*/
	private String fOwnerName;

	/*调入货主id*/
	private Integer fOwnerInId;

	/*调入货主代码*/
	private String fOwnerInNumber;

	/*调入货主名称*/
	private String fOwnerInName;

	/*单位id*/
	private Integer fUnitId;

	/*单位代码*/
	private String fUnitNumber;

	/*单位名称*/
	private String fUnitName;

	/*调拨申请数量*/
	private double fqty;

	/*累计调出数量---下推单据审核后才更新*/
	private double allTransOutQty;

	/*累计调入数量---下推单据审核后才更新*/
	private double allTransInQty;

	/*单据头备注*/
	private String fDescription;

	/*分录备注*/
	private String fNote;

	/*累计调出关联数量*/
	private double transOutReQty;

    private double usableFqty; // 可用的数量

	// 临时字段，不存表
	private int isCheck; // 新加的是否选中
	/* 物料对象  */
	private Material mtl;

	public StkTransferApp() {
		super();
	}

	public String getfBillTypeId() {
		return fBillTypeId;
	}

	public void setfBillTypeId(String fBillTypeId) {
		this.fBillTypeId = fBillTypeId;
	}

	public Integer getfId() {
		return fId;
	}

	public void setfId(Integer fId) {
		this.fId = fId;
	}

	public String getfBillTypeNumber() {
		return fBillTypeNumber;
	}

	public void setfBillTypeNumber(String fBillTypeNumber) {
		this.fBillTypeNumber = fBillTypeNumber;
	}

	public String getfBillTypeName() {
		return fBillTypeName;
	}

	public void setfBillTypeName(String fBillTypeName) {
		this.fBillTypeName = fBillTypeName;
	}

	public String getfBillNo() {
		return fBillNo;
	}

	public void setfBillNo(String fBillNo) {
		this.fBillNo = fBillNo;
	}

	public String getfDate() {
		return fDate;
	}

	public void setfDate(String fDate) {
		this.fDate = fDate;
	}

	public Integer getfAppOrgId() {
		return fAppOrgId;
	}

	public void setfAppOrgId(Integer fAppOrgId) {
		this.fAppOrgId = fAppOrgId;
	}

	public String getfAppOrgNumber() {
		return fAppOrgNumber;
	}

	public void setfAppOrgNumber(String fAppOrgNumber) {
		this.fAppOrgNumber = fAppOrgNumber;
	}

	public String getfAppOrgName() {
		return fAppOrgName;
	}

	public void setfAppOrgName(String fAppOrgName) {
		this.fAppOrgName = fAppOrgName;
	}

	public String getfTransType() {
		return fTransType;
	}

	public void setfTransType(String fTransType) {
		this.fTransType = fTransType;
	}

	public String getfOwnerTypeId() {
		return fOwnerTypeId;
	}

	public void setfOwnerTypeId(String fOwnerTypeId) {
		this.fOwnerTypeId = fOwnerTypeId;
	}

	public String getfOwnerTypeInId() {
		return fOwnerTypeInId;
	}

	public void setfOwnerTypeInId(String fOwnerTypeInId) {
		this.fOwnerTypeInId = fOwnerTypeInId;
	}

	public Integer getfEntryId() {
		return fEntryId;
	}

	public void setfEntryId(Integer fEntryId) {
		this.fEntryId = fEntryId;
	}

	public Integer getfMaterialId() {
		return fMaterialId;
	}

	public void setfMaterialId(Integer fMaterialId) {
		this.fMaterialId = fMaterialId;
	}

	public String getfMaterialNumber() {
		return fMaterialNumber;
	}

	public void setfMaterialNumber(String fMaterialNumber) {
		this.fMaterialNumber = fMaterialNumber;
	}

	public String getfMaterialName() {
		return fMaterialName;
	}

	public void setfMaterialName(String fMaterialName) {
		this.fMaterialName = fMaterialName;
	}

	public Integer getfStockOrgId() {
		return fStockOrgId;
	}

	public void setfStockOrgId(Integer fStockOrgId) {
		this.fStockOrgId = fStockOrgId;
	}

	public String getfStockOrgNumber() {
		return fStockOrgNumber;
	}

	public void setfStockOrgNumber(String fStockOrgNumber) {
		this.fStockOrgNumber = fStockOrgNumber;
	}

	public String getfStockOrgName() {
		return fStockOrgName;
	}

	public void setfStockOrgName(String fStockOrgName) {
		this.fStockOrgName = fStockOrgName;
	}

	public Integer getfStockOrgInId() {
		return fStockOrgInId;
	}

	public void setfStockOrgInId(Integer fStockOrgInId) {
		this.fStockOrgInId = fStockOrgInId;
	}

	public String getfStockOrgInNumber() {
		return fStockOrgInNumber;
	}

	public void setfStockOrgInNumber(String fStockOrgInNumber) {
		this.fStockOrgInNumber = fStockOrgInNumber;
	}

	public String getfStockOrgInName() {
		return fStockOrgInName;
	}

	public void setfStockOrgInName(String fStockOrgInName) {
		this.fStockOrgInName = fStockOrgInName;
	}

	public Integer getfStockId() {
		return fStockId;
	}

	public void setfStockId(Integer fStockId) {
		this.fStockId = fStockId;
	}

	public String getfStockNumber() {
		return fStockNumber;
	}

	public void setfStockNumber(String fStockNumber) {
		this.fStockNumber = fStockNumber;
	}

	public String getfStockName() {
		return fStockName;
	}

	public void setfStockName(String fStockName) {
		this.fStockName = fStockName;
	}

	public Integer getfStockInId() {
		return fStockInId;
	}

	public void setfStockInId(Integer fStockInId) {
		this.fStockInId = fStockInId;
	}

	public String getfStockInNumber() {
		return fStockInNumber;
	}

	public void setfStockInNumber(String fStockInNumber) {
		this.fStockInNumber = fStockInNumber;
	}

	public String getfStockInName() {
		return fStockInName;
	}

	public void setfStockInName(String fStockInName) {
		this.fStockInName = fStockInName;
	}

	public Integer getfOwnerId() {
		return fOwnerId;
	}

	public void setfOwnerId(Integer fOwnerId) {
		this.fOwnerId = fOwnerId;
	}

	public String getfOwnerNumber() {
		return fOwnerNumber;
	}

	public void setfOwnerNumber(String fOwnerNumber) {
		this.fOwnerNumber = fOwnerNumber;
	}

	public String getfOwnerName() {
		return fOwnerName;
	}

	public void setfOwnerName(String fOwnerName) {
		this.fOwnerName = fOwnerName;
	}

	public Integer getfOwnerInId() {
		return fOwnerInId;
	}

	public void setfOwnerInId(Integer fOwnerInId) {
		this.fOwnerInId = fOwnerInId;
	}

	public String getfOwnerInNumber() {
		return fOwnerInNumber;
	}

	public void setfOwnerInNumber(String fOwnerInNumber) {
		this.fOwnerInNumber = fOwnerInNumber;
	}

	public String getfOwnerInName() {
		return fOwnerInName;
	}

	public void setfOwnerInName(String fOwnerInName) {
		this.fOwnerInName = fOwnerInName;
	}

	public Integer getfUnitId() {
		return fUnitId;
	}

	public void setfUnitId(Integer fUnitId) {
		this.fUnitId = fUnitId;
	}

	public String getfUnitNumber() {
		return fUnitNumber;
	}

	public void setfUnitNumber(String fUnitNumber) {
		this.fUnitNumber = fUnitNumber;
	}

	public String getfUnitName() {
		return fUnitName;
	}

	public void setfUnitName(String fUnitName) {
		this.fUnitName = fUnitName;
	}

	public double getFqty() {
		return fqty;
	}

	public void setFqty(double fqty) {
		this.fqty = fqty;
	}

	public double getAllTransOutQty() {
		return allTransOutQty;
	}

	public void setAllTransOutQty(double allTransOutQty) {
		this.allTransOutQty = allTransOutQty;
	}

	public double getAllTransInQty() {
		return allTransInQty;
	}

	public void setAllTransInQty(double allTransInQty) {
		this.allTransInQty = allTransInQty;
	}

	public String getfDescription() {
		return fDescription;
	}

	public void setfDescription(String fDescription) {
		this.fDescription = fDescription;
	}

	public String getfNote() {
		return fNote;
	}

	public void setfNote(String fNote) {
		this.fNote = fNote;
	}

	public Integer getfSeq() {
		return fSeq;
	}

	public void setfSeq(Integer fSeq) {
		this.fSeq = fSeq;
	}

	public double getTransOutReQty() {
		return transOutReQty;
	}

	public void setTransOutReQty(double transOutReQty) {
		this.transOutReQty = transOutReQty;
	}

	public int getIsCheck() {
		return isCheck;
	}

	public void setIsCheck(int isCheck) {
		this.isCheck = isCheck;
	}

	public Material getMtl() {
		return mtl;
	}

	public void setMtl(Material mtl) {
		this.mtl = mtl;
	}

    public double getUsableFqty() {
        return usableFqty;
    }

    public void setUsableFqty(double usableFqty) {
        this.usableFqty = usableFqty;
    }


}
