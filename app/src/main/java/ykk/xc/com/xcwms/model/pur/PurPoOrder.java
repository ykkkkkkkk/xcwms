package ykk.xc.com.xcwms.model.pur;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import ykk.xc.com.xcwms.model.Material;

public class PurPoOrder implements Serializable {
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
    private int isCheck; // 新加的，是否选中

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


    public PurPoOrder() {
        super();
    }

}
