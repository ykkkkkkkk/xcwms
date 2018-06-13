package ykk.xc.com.xcwms.model;

/**
 * 物料表
 */
public class Material {
	/*物料id*/
	private Integer id ;
	/*k3物料id*/
	private Integer fMaterialId;
	/*k3物料编号*/
	private String fNumber;
	/*k3物料名称*/
	private String fName;
	/*物料简称*/
	private String simpleName;
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
//	private MaterialType materialType;
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
	/*默认零拣仓库*/
//	private Stock fixScatteredStock;
	/*默认零拣库位id*/
	private Integer fixScatteredStockPositionId;
	/*默认零拣库位*/
//	private StockPosition fixScatteredStockPosition;
	/*默认整件仓库id*/
	private Integer fixWholeStockId;
	/*默认整件仓库*/
//	private Stock fixWholeStock;
	/*默认整件库位id*/
	private Integer fixWholeStockPositionId;
	/*默认整件库位*/
//	private StockPosition fixWholeStockPosition;
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
	/*是否启用保质期管理，0代表不启用，1代表启用*/
	private Integer isQualityPeriodManager;
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

//	public MaterialType getMaterialType() {
//		return materialType;
//	}
//
//	public void setMaterialType(MaterialType materialType) {
//		this.materialType = materialType;
//	}

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

//	public Stock getFixScatteredStock() {
//		return fixScatteredStock;
//	}

//	public void setFixScatteredStock(Stock fixScatteredStock) {
//		this.fixScatteredStock = fixScatteredStock;
//	}

	public Integer getFixScatteredStockPositionId() {
		return fixScatteredStockPositionId;
	}

	public void setFixScatteredStockPositionId(Integer fixScatteredStockPositionId) {
		this.fixScatteredStockPositionId = fixScatteredStockPositionId;
	}

//	public StockPosition getFixScatteredStockPosition() {
//		return fixScatteredStockPosition;
//	}

//	public void setFixScatteredStockPosition(StockPosition fixScatteredStockPosition) {
//		this.fixScatteredStockPosition = fixScatteredStockPosition;
//	}

	public Integer getFixWholeStockId() {
		return fixWholeStockId;
	}

	public void setFixWholeStockId(Integer fixWholeStockId) {
		this.fixWholeStockId = fixWholeStockId;
	}

//	public Stock getFixWholeStock() {
//		return fixWholeStock;
//	}
//
//	public void setFixWholeStock(Stock fixWholeStock) {
//		this.fixWholeStock = fixWholeStock;
//	}

	public Integer getFixWholeStockPositionId() {
		return fixWholeStockPositionId;
	}

	public void setFixWholeStockPositionId(Integer fixWholeStockPositionId) {
		this.fixWholeStockPositionId = fixWholeStockPositionId;
	}

//	public StockPosition getFixWholeStockPosition() {
//		return fixWholeStockPosition;
//	}

//	public void setFixWholeStockPosition(StockPosition fixWholeStockPosition) {
//		this.fixWholeStockPosition = fixWholeStockPosition;
//	}

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

	public Integer getIsQualityPeriodManager() {
		return isQualityPeriodManager;
	}

	public void setIsQualityPeriodManager(Integer isQualityPeriodManager) {
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
	
}
