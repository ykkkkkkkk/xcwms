package ykk.xc.com.xcwms.model.pur;

import android.os.Parcel;
import android.os.Parcelable;

import ykk.xc.com.xcwms.model.Material;
import ykk.xc.com.xcwms.model.Mtl;

public class PurPoOrder implements Parcelable {
	private int fId; // 单据id,
	private String fbillno; // 单据编号,
	private int supplierId; // 供应商Id,
	private String supplierName; // 供应商,
	private String purPerson; // 采购员,
	private int purOrganizationNameId; // 采购组织id
	private String purOrganizationName; // 采购组织
	private int deptId; // 采购部门id
	private String deptName; // 采购部门
	private String poFdate; // 采购日期
	private int mtlId; // 物料id
	private Material mtl;
	private String mtlFnumber; // 物料编码
	private String mtlFname; // 物料名称
	private String mtlType; // 规格型号
	private String unitFname; // 单位
	private double poFqty; // 采购数量
	private double poFstockinqty; // 累计入库数量
	private int receiveOrganizationId; // 收料组织
	private String receiveOrganizationName; // 收料组织

	public int getIsCheck() {
		return isCheck;
	}

	public void setIsCheck(int isCheck) {
		this.isCheck = isCheck;
	}

	private int isCheck;

	public int getfId() {
		return fId;
	}

	public void setfId(int fId) {
		this.fId = fId;
	}

	public String getFbillno() {
		return fbillno;
	}

	public int getSupplierId() {
		return supplierId;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public String getPurPerson() {
		return purPerson;
	}

	public int getPurOrganizationNameId() {
		return purOrganizationNameId;
	}

	public String getPurOrganizationName() {
		return purOrganizationName;
	}

	public int getDeptId() {
		return deptId;
	}

	public String getDeptName() {
		return deptName;
	}

	public String getPoFdate() {
		return poFdate;
	}

	public int getMtlId() {
		return mtlId;
	}

	public String getMtlFnumber() {
		return mtlFnumber;
	}

	public String getMtlFname() {
		return mtlFname;
	}

	public String getMtlType() {
		return mtlType;
	}

	public String getUnitFname() {
		return unitFname;
	}

	public double getPoFqty() {
		return poFqty;
	}

	public double getPoFstockinqty() {
		return poFstockinqty;
	}

	public int getReceiveOrganizationId() {
		return receiveOrganizationId;
	}

	public String getReceiveOrganizationName() {
		return receiveOrganizationName;
	}

	public void setFbillno(String fbillno) {
		this.fbillno = fbillno;
	}

	public void setSupplierId(int supplierId) {
		this.supplierId = supplierId;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public void setPurPerson(String purPerson) {
		this.purPerson = purPerson;
	}

	public void setPurOrganizationNameId(int purOrganizationNameId) {
		this.purOrganizationNameId = purOrganizationNameId;
	}

	public void setPurOrganizationName(String purOrganizationName) {
		this.purOrganizationName = purOrganizationName;
	}

	public void setDeptId(int deptId) {
		this.deptId = deptId;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public void setPoFdate(String poFdate) {
		this.poFdate = poFdate;
	}

	public void setMtlId(int mtlId) {
		this.mtlId = mtlId;
	}

	public void setMtlFnumber(String mtlFnumber) {
		this.mtlFnumber = mtlFnumber;
	}

	public void setMtlFname(String mtlFname) {
		this.mtlFname = mtlFname;
	}

	public void setMtlType(String mtlType) {
		this.mtlType = mtlType;
	}

	public void setUnitFname(String unitFname) {
		this.unitFname = unitFname;
	}

	public void setPoFqty(double poFqty) {
		this.poFqty = poFqty;
	}

	public void setPoFstockinqty(double poFstockinqty) {
		this.poFstockinqty = poFstockinqty;
	}

	public void setReceiveOrganizationId(int receiveOrganizationId) {
		this.receiveOrganizationId = receiveOrganizationId;
	}

	public void setReceiveOrganizationName(String receiveOrganizationName) {
		this.receiveOrganizationName = receiveOrganizationName;
	}

	public Material getMtl() {
		return mtl;
	}

	public void setMtl(Material mtl) {
		this.mtl = mtl;
	}


	public PurPoOrder() {
		super();
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
	 * @param p
	 */
	public PurPoOrder(Parcel p) {
		fId = p.readInt();
		fbillno = p.readString();
		supplierId = p.readInt();
		supplierId = p.readInt();
		supplierName = p.readString();
		purPerson = p.readString();
		purOrganizationNameId = p.readInt();
		purOrganizationName = p.readString();
		deptId = p.readInt();
		poFdate = p.readString();
		mtlId = p.readInt();
		mtl = p.readParcelable(Material.class.getClassLoader());
		mtlFnumber = p.readString();
		mtlFname = p.readString();
		mtlType = p.readString();
		unitFname = p.readString();
		poFqty = p.readDouble();
		poFstockinqty = p.readDouble();
		receiveOrganizationId = p.readInt();
		receiveOrganizationName = p.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel p, int flags) {
		p.writeInt(fId);
		p.writeString(fbillno);
		p.writeInt(supplierId);
		p.writeString(supplierName);
		p.writeString(purPerson);
		p.writeInt(purOrganizationNameId);
		p.writeString(purOrganizationName);
		p.writeInt(deptId);
		p.writeString(deptName);
		p.writeString(poFdate);
		p.writeInt(mtlId);
		p.writeParcelable(mtl, flags);
		p.writeString(mtlFnumber);
		p.writeString(mtlFname);
		p.writeString(mtlType);
		p.writeString(unitFname);
		p.writeDouble(poFqty);
		p.writeDouble(poFstockinqty);
		p.writeInt(receiveOrganizationId);
		p.writeString(receiveOrganizationName);
	}

	public static final Creator<PurPoOrder> CREATOR = new Creator<PurPoOrder>() {
		/**
		 * 供外部类反序列化本类数组使用
		 */
		@Override
		public PurPoOrder[] newArray(int size) {
			return new PurPoOrder[size];
		}

		/**
		 * 从Parcel中读取数据
		 */
		@Override
		public PurPoOrder createFromParcel(Parcel source) {
			return new PurPoOrder(source);
		}
	};
}
