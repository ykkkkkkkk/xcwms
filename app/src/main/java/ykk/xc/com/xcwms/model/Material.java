package ykk.xc.com.xcwms.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 物料表
 */
public class Material implements Serializable {
	/*物料id*/
	private Integer id ;
	/*k3物料id*/
	private Integer fMaterialId;
	/*k3物料编号*/
	private String fNumber;
	/*k3物料名称*/
	private String fName;
	/*使用组织ID*/
	private String userOrgId;
	/*使用组织实体类*/
	private Organization organization;
	/*物料简称*/
	private String simpleName;
	/*基本单位id*/
	private Integer basicUnitId;
	/*基本单位*/
	private Unit unit;
	/*物料条码*/
	private String barcode;
	/*货主名称*/
	private String ownerName;
	/*产品等级*/
	private String materialGrade;
	/*产品规格*/
	private String materialSize;
	/*产品类别id*/
	private Integer materialTypeId;
	/*产品类别*/
	private MaterialType materialType;
	/*有效期*/
	private String validityDate;
	/*贮藏期*/
	private String shelfDate;
	/*安全库存*/
	private double safetyStock;
	/*最少补货数量*/
	private double minLackStock;
	/*默认零拣仓库id*/
	private Integer fixScatteredStockId;
	/*默认零拣库位id*/
	private Integer fixScatteredStockPositionId;
	/*默认整件仓库id*/
	private Integer fixWholeStockId;
	/*默认整件库位id*/
	private Integer fixWholeStockPositionId;
	/*卡板箱数*/
	private Integer baleBoxNumber;
	/*最后同步时间*/
	private String lastSyncDate;
	/*最后更新时间*/
	private String lastUpdateDate;
	/*备注*/
	private String remarks;
	/*是否启用批号管理，0代表不启用，1代表启用*/
	private Integer isBatchManager;
	/*批号规则id*/
	private Integer batchRuleId;
	/*是否启用序列号管理，0代表不启用，1代表启用*/
	private Integer isSnManager;
	/*序列号编码规则id*/
	private Integer snRuleId;
	/*序列号单位id*/
	private Integer snUnitId;
	/*管理序列号方式id*/
	private Integer snManagerTypeId;
	/*是否启用保质期管理，0代表不启用，1代表启用*/
	private Integer isQualityPeriodManager;
	/*质保期单位id*/
	private Integer qualityPeriodUnitId;
	/*质保期*/
	private double qualityPeriod;
	/*K3数据状态*/
	private String dataStatus;
	/*wms非物理删除标识*/
	private String isDelete;
	/*k3是否禁用*/
	private String enabled;
	/*k3是否允许采购超收*/
	private String isOvercharge;
	/*k3超收上限*/
	private double receiveMaxScale;
	/*k3超收下限*/
	private double receiveMinScale;

	/*k3旧物料编码*/
	private String oldNumber;
	/*k3旧物料名称*/
	private String oldName;
	Stock stock;
	StockPosition stockPos;

	/**
	 * 构造方法
	 */
	public Material() {
		super();
	}
	/**
	 * getter/setter方法
	 */
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getfMaterialId() {
		return fMaterialId;
	}
	public void setfMaterialId(Integer fMaterialId) {
		this.fMaterialId = fMaterialId;
	}
	public String getfNumber() {
		return fNumber;
	}
	public void setfNumber(String fNumber) {
		this.fNumber = fNumber;
	}
	public String getfName() {
		return fName;
	}
	public void setfName(String fName) {
		this.fName = fName;
	}
	public String getSimpleName() {
		return simpleName;
	}
	public void setSimpleName(String simpleName) {
		this.simpleName = simpleName;
	}
	public Integer getBasicUnitId() {
		return basicUnitId;
	}
	public void setBasicUnitId(Integer basicUnitId) {
		this.basicUnitId = basicUnitId;
	}
	public Unit getUnit() {
		return unit;
	}
	public void setUnit(Unit unit) {
		this.unit = unit;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
	public String getOwnerName() {
		return ownerName;
	}
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	public String getMaterialGrade() {
		return materialGrade;
	}
	public void setMaterialGrade(String materialGrade) {
		this.materialGrade = materialGrade;
	}
	public String getMaterialSize() {
		return materialSize;
	}
	public void setMaterialSize(String materialSize) {
		this.materialSize = materialSize;
	}
	public Integer getMaterialTypeId() {
		return materialTypeId;
	}
	public void setMaterialTypeId(Integer materialTypeId) {
		this.materialTypeId = materialTypeId;
	}
	public MaterialType getMaterialType() {
		return materialType;
	}
	public void setMaterialType(MaterialType materialType) {
		this.materialType = materialType;
	}
	public String getValidityDate() {
		return validityDate;
	}
	public void setValidityDate(String validityDate) {
		this.validityDate = validityDate;
	}
	public String getShelfDate() {
		return shelfDate;
	}
	public void setShelfDate(String shelfDate) {
		this.shelfDate = shelfDate;
	}
	public double getSafetyStock() {
		return safetyStock;
	}
	public void setSafetyStock(double safetyStock) {
		this.safetyStock = safetyStock;
	}
	public double getMinLackStock() {
		return minLackStock;
	}
	public void setMinLackStock(double minLackStock) {
		this.minLackStock = minLackStock;
	}
	public Integer getFixScatteredStockId() {
		return fixScatteredStockId;
	}
	public void setFixScatteredStockId(Integer fixScatteredStockId) {
		this.fixScatteredStockId = fixScatteredStockId;
	}
	public Integer getFixScatteredStockPositionId() {
		return fixScatteredStockPositionId;
	}
	public void setFixScatteredStockPositionId(Integer fixScatteredStockPositionId) {
		this.fixScatteredStockPositionId = fixScatteredStockPositionId;
	}
	public Integer getFixWholeStockId() {
		return fixWholeStockId;
	}
	public void setFixWholeStockId(Integer fixWholeStockId) {
		this.fixWholeStockId = fixWholeStockId;
	}
	public Integer getFixWholeStockPositionId() {
		return fixWholeStockPositionId;
	}
	public void setFixWholeStockPositionId(Integer fixWholeStockPositionId) {
		this.fixWholeStockPositionId = fixWholeStockPositionId;
	}
	public Integer getBaleBoxNumber() {
		return baleBoxNumber;
	}
	public void setBaleBoxNumber(Integer baleBoxNumber) {
		this.baleBoxNumber = baleBoxNumber;
	}
	public String getLastSyncDate() {
		return lastSyncDate;
	}
	public void setLastSyncDate(String lastSyncDate) {
		this.lastSyncDate = lastSyncDate;
	}
	public String getLastUpdateDate() {
		return lastUpdateDate;
	}
	public void setLastUpdateDate(String lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public Integer getIsBatchManager() {
		return isBatchManager;
	}
	public void setIsBatchManager(Integer isBatchManager) {
		this.isBatchManager = isBatchManager;
	}
	public Integer getBatchRuleId() {
		return batchRuleId;
	}
	public void setBatchRuleId(Integer batchRuleId) {
		this.batchRuleId = batchRuleId;
	}
	public Integer getIsSnManager() {
		return isSnManager;
	}
	public void setIsSnManager(Integer isSnManager) {
		this.isSnManager = isSnManager;
	}
	public Integer getSnRuleId() {
		return snRuleId;
	}
	public void setSnRuleId(Integer snRuleId) {
		this.snRuleId = snRuleId;
	}
	public Integer getSnUnitId() {
		return snUnitId;
	}
	public void setSnUnitId(Integer snUnitId) {
		this.snUnitId = snUnitId;
	}
	public Integer getSnManagerTypeId() {
		return snManagerTypeId;
	}
	public void setSnManagerTypeId(Integer snManagerTypeId) {
		this.snManagerTypeId = snManagerTypeId;
	}
	public Integer getIsQualityPeriodManager() {
		return isQualityPeriodManager;
	}
	public void setIsQualityPeriodManager(Integer isQualityPeriodManager) {
		this.isQualityPeriodManager = isQualityPeriodManager;
	}
	public Integer getQualityPeriodUnitId() {
		return qualityPeriodUnitId;
	}
	public void setQualityPeriodUnitId(Integer qualityPeriodUnitId) {
		this.qualityPeriodUnitId = qualityPeriodUnitId;
	}
	public double getQualityPeriod() {
		return qualityPeriod;
	}
	public void setQualityPeriod(double qualityPeriod) {
		this.qualityPeriod = qualityPeriod;
	}
	public String getDataStatus() {
		return dataStatus;
	}
	public void setDataStatus(String dataStatus) {
		this.dataStatus = dataStatus;
	}
	public String getIsDelete() {
		return isDelete;
	}
	public void setIsDelete(String isDelete) {
		this.isDelete = isDelete;
	}
	public String getEnabled() {
		return enabled;
	}
	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}
	public Organization getOrganization() {
		return organization;
	}
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
	public String getUserOrgId() {
		return userOrgId;
	}
	public void setUserOrgId(String userOrgId) {
		this.userOrgId = userOrgId;
	}
	public String getIsOvercharge() {
		return isOvercharge;
	}
	public void setIsOvercharge(String isOvercharge) {
		this.isOvercharge = isOvercharge;
	}
	public double getReceiveMaxScale() {
		return receiveMaxScale;
	}
	public void setReceiveMaxScale(double receiveMaxScale) {
		this.receiveMaxScale = receiveMaxScale;
	}
	public double getReceiveMinScale() {
		return receiveMinScale;
	}
	public void setReceiveMinScale(double receiveMinScale) {
		this.receiveMinScale = receiveMinScale;
	}

	public String getOldNumber() {
		return oldNumber;
	}
	public void setOldNumber(String oldNumber) {
		this.oldNumber = oldNumber;
	}
	public String getOldName() {
		return oldName;
	}
	public void setOldName(String oldName) {
		this.oldName = oldName;
	}
	public Stock getStock() {
		return stock;
	}
	public StockPosition getStockPos() {
		return stockPos;
	}
	public void setStock(Stock stock) {
		this.stock = stock;
	}
	public void setStockPos(StockPosition stockPos) {
		this.stockPos = stockPos;
	}

	@Override
	public String toString() {
		return "Material [id=" + id + ", fMaterialId=" + fMaterialId + ", fNumber=" + fNumber + ", fName=" + fName
				+ ", userOrgId=" + userOrgId + ", organization=" + organization + ", simpleName=" + simpleName
				+ ", basicUnitId=" + basicUnitId + ", unit=" + unit + ", barcode=" + barcode + ", ownerName="
				+ ownerName + ", materialGrade=" + materialGrade + ", materialSize=" + materialSize
				+ ", materialTypeId=" + materialTypeId + ", materialType=" + materialType + ", validityDate="
				+ validityDate + ", shelfDate=" + shelfDate + ", safetyStock=" + safetyStock + ", minLackStock="
				+ minLackStock + ", fixScatteredStockId=" + fixScatteredStockId + ", fixScatteredStockPositionId="
				+ fixScatteredStockPositionId + ", fixWholeStockId=" + fixWholeStockId + ", fixWholeStockPositionId="
				+ fixWholeStockPositionId + ", baleBoxNumber=" + baleBoxNumber + ", lastSyncDate=" + lastSyncDate
				+ ", lastUpdateDate=" + lastUpdateDate + ", remarks=" + remarks + ", isBatchManager=" + isBatchManager
				+ ", batchRuleId=" + batchRuleId + ", isSnManager=" + isSnManager + ", snRuleId=" + snRuleId
				+ ", snUnitId=" + snUnitId + ", snManagerTypeId=" + snManagerTypeId + ", isQualityPeriodManager="
				+ isQualityPeriodManager + ", qualityPeriodUnitId=" + qualityPeriodUnitId + ", qualityPeriod="
				+ qualityPeriod + ", dataStatus=" + dataStatus + ", isDelete=" + isDelete + ", enabled=" + enabled
				+ ", isOvercharge=" + isOvercharge + ", receiveMaxScale=" + receiveMaxScale + ", receiveMinScale="
				+ receiveMinScale + ", oldNumber=" + oldNumber + ", oldName=" + oldName + ", stock=" + stock
				+ ", stockPos=" + stockPos + "]";
	}

}
