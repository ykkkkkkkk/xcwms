package ykk.xc.com.xcwms.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 物料表
 */
public class Material implements Parcelable {
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
	private int fixScatteredStockId;
	/*默认零拣仓库*/
//	private Stock fixScatteredStock;
	/*默认零拣库位id*/
	private int fixScatteredStockPositionId;
	/*默认零拣库位*/
//	private StockPosition fixScatteredStockPosition;
	/*默认整件仓库id*/
	private int fixWholeStockId;
	/*默认整件仓库*/
//	private Stock fixWholeStock;
	/*默认整件库位id*/
	private int fixWholeStockPositionId;
	/*默认整件库位*/
//	private StockPosition fixWholeStockPosition;
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
	/*是否启用序列号管理，0代表不启用，1代表启用*/
	private int isSnManager;
	/*是否启用保质期管理，0代表不启用，1代表启用*/
	private int isQualityPeriodManager;
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

	public int getFixScatteredStockId() {
		return fixScatteredStockId;
	}

	public void setFixScatteredStockId(int fixScatteredStockId) {
		this.fixScatteredStockId = fixScatteredStockId;
	}

//	public Stock getFixScatteredStock() {
//		return fixScatteredStock;
//	}

//	public void setFixScatteredStock(Stock fixScatteredStock) {
//		this.fixScatteredStock = fixScatteredStock;
//	}

	public int getFixScatteredStockPositionId() {
		return fixScatteredStockPositionId;
	}

	public void setFixScatteredStockPositionId(int fixScatteredStockPositionId) {
		this.fixScatteredStockPositionId = fixScatteredStockPositionId;
	}

//	public StockPosition getFixScatteredStockPosition() {
//		return fixScatteredStockPosition;
//	}

//	public void setFixScatteredStockPosition(StockPosition fixScatteredStockPosition) {
//		this.fixScatteredStockPosition = fixScatteredStockPosition;
//	}

	public int getFixWholeStockId() {
		return fixWholeStockId;
	}

	public void setFixWholeStockId(int fixWholeStockId) {
		this.fixWholeStockId = fixWholeStockId;
	}

//	public Stock getFixWholeStock() {
//		return fixWholeStock;
//	}
//
//	public void setFixWholeStock(Stock fixWholeStock) {
//		this.fixWholeStock = fixWholeStock;
//	}

	public int getFixWholeStockPositionId() {
		return fixWholeStockPositionId;
	}

	public void setFixWholeStockPositionId(int fixWholeStockPositionId) {
		this.fixWholeStockPositionId = fixWholeStockPositionId;
	}

//	public StockPosition getFixWholeStockPosition() {
//		return fixWholeStockPosition;
//	}

//	public void setFixWholeStockPosition(StockPosition fixWholeStockPosition) {
//		this.fixWholeStockPosition = fixWholeStockPosition;
//	}

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

	public int getIsSnManager() {
		return isSnManager;
	}
	public void setIsSnManager(int isSnManager) {
		this.isSnManager = isSnManager;
	}

	/**
	 * 这里的读的顺序必须与writeToParcel(Parcel dest, int flags)方法中
	 * 写的顺序一致，否则数据会有差错，比如你的读取顺序如果是：
	 * nickname = source.readString();
	 * username=source.readString();
	 * age = source.readInt();
	 * 即调换了username和nickname的读取顺序，那么你会发现你拿到的username是nickname的数据，
	 * 而你拿到的nickname是username的数据
	 *
	 * @param source
	 */
	public Material(Parcel source) {
		id = source.readInt();
		fMaterialId = source.readInt();
		fNumber = source.readString();
		fName = source.readString();
		simpleName = source.readString();
		barcode = source.readString();
		ownerName = source.readString();
		materialGrade = source.readString();
		materialSize = source.readString();
		materialTypeId = source.readInt();
//		materialType = source.readParcelable(MeasureUnit.class.getClassLoader());
		validityDate = source.readString();
		shelfDate = source.readString();
		safetyStock = source.readDouble();
		minLackStock = source.readDouble();
		fixScatteredStockId = source.readInt();
//		fixScatteredStock = source.readParcelable(MeasureUnit.class.getClassLoader());
		fixScatteredStockPositionId = source.readInt();
//		fixScatteredStockPosition = source.readParcelable(MeasureUnit.class.getClassLoader());
		fixWholeStockId = source.readInt();
//		fixWholeStock = source.readParcelable(MeasureUnit.class.getClassLoader());
		fixWholeStockPositionId = source.readInt();
//		fixWholeStockPosition = source.readParcelable(MeasureUnit.class.getClassLoader());
		baleBoxNumber = source.readInt();
		lastSyncDate = source.readString();
		lastUpdateDate = source.readString();
		remarks = source.readString();
		isBatchManager = source.readInt();
		isSnManager = source.readInt();
		isQualityPeriodManager = source.readInt();
		dataStatus = source.readString();
		isDelete = source.readString();
		enabled = source.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeInt(fMaterialId);
		dest.writeString(fNumber);
		dest.writeString(fName);
		dest.writeString(simpleName);
		dest.writeString(barcode);
		dest.writeString(ownerName);
		dest.writeString(materialGrade);
		dest.writeString(materialSize);
		dest.writeInt(materialTypeId);
//		dest.writeParcelable(materialType, flags);
		dest.writeString(validityDate);
		dest.writeString(shelfDate);
		dest.writeDouble(safetyStock);
		dest.writeDouble(minLackStock);
		dest.writeInt(fixScatteredStockId);
		//		dest.writeParcelable(fixScatteredStock, flags);
		dest.writeInt(fixScatteredStockPositionId);
		//		dest.writeParcelable(fixScatteredStockPosition, flags);
		dest.writeInt(fixWholeStockId);
		//		dest.writeParcelable(fixWholeStock, flags);
		dest.writeInt(fixWholeStockPositionId);
		//		dest.writeParcelable(fixWholeStockPosition, flags);
		dest.writeInt(baleBoxNumber);
		dest.writeString(lastSyncDate);
		dest.writeString(lastUpdateDate);
		dest.writeString(remarks);
		dest.writeInt(isBatchManager);
		dest.writeInt(isSnManager);
		dest.writeInt(isQualityPeriodManager);
		dest.writeString(dataStatus);
		dest.writeString(isDelete);
		dest.writeString(enabled);
	}

	public static final Creator<Material> CREATOR = new Creator<Material>() {
		/**
		 * 供外部类反序列化本类数组使用
		 */
		@Override
		public Material[] newArray(int size) {
			return new Material[size];
		}

		/**
		 * 从Parcel中读取数据
		 */
		@Override
		public Material createFromParcel(Parcel source) {
			return new Material(source);
		}
	};
	
}
