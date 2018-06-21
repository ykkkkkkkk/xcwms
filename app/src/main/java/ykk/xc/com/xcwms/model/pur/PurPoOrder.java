package ykk.xc.com.xcwms.model.pur;

import java.io.Serializable;

import ykk.xc.com.xcwms.model.Material;
import ykk.xc.com.xcwms.model.Organization;

public class PurPoOrder implements Serializable {
    private int fId; // 单据id,
    private String fbillno; // 单据编号,
    private int supplierId; // 供应商Id,
    private String supplierName; // 供应商,
    private String purPerson; // 采购员,
    private int purOrgId; // 采购组织id
    private String purOrgName; // 采购组织
    private Organization purOrg;
    private int deptId; // 采购部门id
    private String deptName; // 采购部门
    private String poFdate; // 采购日期
    private int mtlId; // 物料id
    private Material mtl; // 物料对象
    private String mtlFnumber; // 物料编码
    private String mtlFname; // 物料名称
    private String mtlType; // 规格型号
    private String unitFname; // 单位
    private double poFqty; // 采购数量
    private double poFstockinqty; // 累计入库数量
    private int receiveOrgId; // 收料组织
    private String receiveOrgName; // 收料组织
    private Organization receiveOrg;
    private int isCheck; // 新加的是否选中

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

    public int getPurOrgId() {
        return purOrgId;
    }

    public String getPurOrgName() {
        return purOrgName;
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

    public int getReceiveOrgId() {
        return receiveOrgId;
    }

    public String getReceiveOrgName() {
        return receiveOrgName;
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

    public void setPurOrgId(int purOrgId) {
        this.purOrgId = purOrgId;
    }

    public void setPurOrgName(String purOrgName) {
        this.purOrgName = purOrgName;
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

    public void setReceiveOrgId(int receiveOrgId) {
        this.receiveOrgId = receiveOrgId;
    }

    public void setReceiveOrgName(String receiveOrgName) {
        this.receiveOrgName = receiveOrgName;
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

    public Organization getPurOrg() {
        return purOrg;
    }

    public Organization getReceiveOrg() {
        return receiveOrg;
    }

    public void setPurOrg(Organization purOrg) {
        this.purOrg = purOrg;
    }

    public void setReceiveOrg(Organization receiveOrg) {
        this.receiveOrg = receiveOrg;
    }

    public int getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(int isCheck) {
        this.isCheck = isCheck;
    }

    @Override
    public String toString() {
        return "PurPoOrder [fId=" + fId + ", fbillno=" + fbillno + ", supplierId=" + supplierId + ", supplierName="
                + supplierName + ", purPerson=" + purPerson + ", purOrgId=" + purOrgId
                + ", purOrgName=" + purOrgName + ", purOrg=" + purOrg + ", deptId="
                + deptId + ", deptName=" + deptName + ", poFdate=" + poFdate + ", mtlId=" + mtlId + ", mtl=" + mtl
                + ", mtlFnumber=" + mtlFnumber + ", mtlFname=" + mtlFname + ", mtlType=" + mtlType + ", unitFname="
                + unitFname + ", poFqty=" + poFqty + ", poFstockinqty=" + poFstockinqty + ", receiveOrgId="
                + receiveOrgId + ", receiveOrgName=" + receiveOrgName
                + ", receiveOrg=" + receiveOrg + "]";
    }

}
