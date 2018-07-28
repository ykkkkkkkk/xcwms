package ykk.xc.com.xcwms.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 物料表
 */
public class Material implements Serializable {
	/*物料id*/
	private int id ;
	/*k3物料id*/
	private int fMaterialId;
	/*k3物料编号*/
	private String fNumber;
	/*k3物料名称*/
	private String fName;
	/*物料简称*/
	private String simpleName;
	/*基本单位id*/
	private int basicUnitId;
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
	private int materialTypeId;
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
	private int fixScatteredStockId;
	/*默认零拣仓库*/
	private Stock fixScatteredStock;
	/*默认零拣库位id*/
	private int fixScatteredStockPositionId;
	/*默认零拣库位*/
	private StockPosition fixScatteredStockPosition;
	/*默认整件仓库id*/
	private int fixWholeStockId;
	/*默认整件仓库*/
	private Stock fixWholeStock;
	/*默认整件库位id*/
	private int fixWholeStockPositionId;
	/*默认整件库位*/
	private StockPosition fixWholeStockPosition;
	/*卡板箱数*/
	private int baleBoxNumber;
	/*最后同步时间*/
	private String lastSyncDate;
	/*最后更新时间*/
	private String lastUpdateDate;
	/*备注*/
	private String remarks;
	/*是否启用批号管理，0代表不启用，1代表启用*/
	private int isBatchManager;
	/*批号规则id*/
	private int batchRuleId;
	/*是否启用序列号管理，0代表不启用，1代表启用*/
	private int isSnManager;
	/*序列号编码规则id*/
	private int snRuleId;
	/*序列号单位id*/
	private int snUnitId;
	/*管理序列号方式id*/
	private int snManagerTypeId;
	/*是否启用保质期管理，0代表不启用，1代表启用*/
	private int isQualityPeriodManager;
	/*质保期单位id*/
	private int qualityPeriodUnitId;
	/*质保期*/
	private double qualityPeriod;
	/*K3数据状态*/
	private String dataStatus;
	/*wms非物理删除标识*/
	private String isDelete;
	/*k3是否禁用*/
	private String enabled;

	/**
	 * 构造方法
	 */
	public Material() {
		super();
	}
	/**
	 * getter/setter方法
	 */

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getfMaterialId() {
		return fMaterialId;
	}

	public void setfMaterialId(int fMaterialId) {
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

	public int getMaterialTypeId() {
		return materialTypeId;
	}

	public void setMaterialTypeId(int materialTypeId) {
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

	public int getFixScatteredStockId() {
		return fixScatteredStockId;
	}

	public void setFixScatteredStockId(int fixScatteredStockId) {
		this.fixScatteredStockId = fixScatteredStockId;
	}

	public Stock getFixScatteredStock() {
		return fixScatteredStock;
	}

	public void setFixScatteredStock(Stock fixScatteredStock) {
		this.fixScatteredStock = fixScatteredStock;
	}

	public int getFixScatteredStockPositionId() {
		return fixScatteredStockPositionId;
	}

	public void setFixScatteredStockPositionId(int fixScatteredStockPositionId) {
		this.fixScatteredStockPositionId = fixScatteredStockPositionId;
	}

	public StockPosition getFixScatteredStockPosition() {
		return fixScatteredStockPosition;
	}

	public void setFixScatteredStockPosition(StockPosition fixScatteredStockPosition) {
		this.fixScatteredStockPosition = fixScatteredStockPosition;
	}

	public int getFixWholeStockId() {
		return fixWholeStockId;
	}

	public void setFixWholeStockId(int fixWholeStockId) {
		this.fixWholeStockId = fixWholeStockId;
	}

	public Stock getFixWholeStock() {
		return fixWholeStock;
	}

	public void setFixWholeStock(Stock fixWholeStock) {
		this.fixWholeStock = fixWholeStock;
	}

	public int getFixWholeStockPositionId() {
		return fixWholeStockPositionId;
	}

	public void setFixWholeStockPositionId(int fixWholeStockPositionId) {
		this.fixWholeStockPositionId = fixWholeStockPositionId;
	}

	public StockPosition getFixWholeStockPosition() {
		return fixWholeStockPosition;
	}

	public void setFixWholeStockPosition(StockPosition fixWholeStockPosition) {
		this.fixWholeStockPosition = fixWholeStockPosition;
	}

	public int getBaleBoxNumber() {
		return baleBoxNumber;
	}

	public void setBaleBoxNumber(int baleBoxNumber) {
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

	public int getIsBatchManager() {
		return isBatchManager;
	}

	public void setIsBatchManager(int isBatchManager) {
		this.isBatchManager = isBatchManager;
	}

	public int getIsQualityPeriodManager() {
		return isQualityPeriodManager;
	}

	public void setIsQualityPeriodManager(int isQualityPeriodManager) {
		this.isQualityPeriodManager = isQualityPeriodManager;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
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

	public int getBasicUnitId() {
		return basicUnitId;
	}
	public void setBasicUnitId(int basicUnitId) {
		this.basicUnitId = basicUnitId;
	}
	public Unit getUnit() {
		return unit;
	}
	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public int getIsSnManager() {
		return isSnManager;
	}
	public void setIsSnManager(int isSnManager) {
		this.isSnManager = isSnManager;
	}

	public int getBatchRuleId() {
		return batchRuleId;
	}
	public void setBatchRuleId(int batchRuleId) {
		this.batchRuleId = batchRuleId;
	}
	public int getSnRuleId() {
		return snRuleId;
	}
	public void setSnRuleId(int snRuleId) {
		this.snRuleId = snRuleId;
	}
	public int getSnUnitId() {
		return snUnitId;
	}
	public void setSnUnitId(int snUnitId) {
		this.snUnitId = snUnitId;
	}
	public int getSnManagerTypeId() {
		return snManagerTypeId;
	}
	public void setSnManagerTypeId(int snManagerTypeId) {
		this.snManagerTypeId = snManagerTypeId;
	}
	public int getQualityPeriodUnitId() {
		return qualityPeriodUnitId;
	}
	public void setQualityPeriodUnitId(int qualityPeriodUnitId) {
		this.qualityPeriodUnitId = qualityPeriodUnitId;
	}
	public double getQualityPeriod() {
		return qualityPeriod;
	}
	public void setQualityPeriod(double qualityPeriod) {
		this.qualityPeriod = qualityPeriod;
	}
	@Override
	public String toString() {
		return "Material [id=" + id + ", fMaterialId=" + fMaterialId + ", fNumber=" + fNumber + ", fName=" + fName
				+ ", simpleName=" + simpleName + ", basicUnitId=" + basicUnitId + ", unit=" + unit + ", barcode="
				+ barcode + ", ownerName=" + ownerName + ", materialGrade=" + materialGrade + ", materialSize="
				+ materialSize + ", materialTypeId=" + materialTypeId + ", materialType=" + materialType
				+ ", validityDate=" + validityDate + ", shelfDate=" + shelfDate + ", safetyStock=" + safetyStock
				+ ", minLackStock=" + minLackStock + ", fixScatteredStockId=" + fixScatteredStockId
				+ ", fixScatteredStock=" + fixScatteredStock + ", fixScatteredStockPositionId="
				+ fixScatteredStockPositionId + ", fixScatteredStockPosition=" + fixScatteredStockPosition
				+ ", fixWholeStockId=" + fixWholeStockId + ", fixWholeStock=" + fixWholeStock
				+ ", fixWholeStockPositionId=" + fixWholeStockPositionId + ", fixWholeStockPosition="
				+ fixWholeStockPosition + ", baleBoxNumber=" + baleBoxNumber + ", lastSyncDate=" + lastSyncDate
				+ ", lastUpdateDate=" + lastUpdateDate + ", remarks=" + remarks + ", isBatchManager=" + isBatchManager
				+ ", batchRuleId=" + batchRuleId + ", isSnManager=" + isSnManager + ", snRuleId=" + snRuleId
				+ ", snUnitId=" + snUnitId + ", snManagerTypeId=" + snManagerTypeId + ", isQualityPeriodManager="
				+ isQualityPeriodManager + ", qualityPeriodUnitId=" + qualityPeriodUnitId + ", qualityPeriod="
				+ qualityPeriod + ", dataStatus=" + dataStatus + ", isDelete=" + isDelete + ", enabled=" + enabled
				+ "]";
	}

}
