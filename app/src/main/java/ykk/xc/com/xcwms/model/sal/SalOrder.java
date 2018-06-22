package ykk.xc.com.xcwms.model.sal;

import java.io.Serializable;

import ykk.xc.com.xcwms.model.Material;
import ykk.xc.com.xcwms.model.Organization;

/**
 * 日期：2018-06-22 16:04
 * 描述：
 * 作者：ykk
 */
public class SalOrder implements Serializable{
    private int fId; // 单据id,
    private String fbillno; // 单据编号,
    private String fbillType; // 单据类型
    private String salDate; // 销售日期
    private int custId; // 客户Id,
    private String custNumber; // 客户代码,
    private String custName; // 客户,
    private int salOrgId; // 销售组织id
    private String salOrgName; // 销售组织
    private Organization salOrg;
    private int inventoryOrgId; // 库存组织id
    private String inventoryOrgName; // 库存组织名称
    private Organization inventoryOrg;
    private int mtlId; // 物料id
    private Material mtl; // 物料对象
    private String mtlFnumber; // 物料编码
    private String mtlFname; // 物料名称
    private String mtlUnitName; // 单位
    private double salFqty; // 销售数量
    private double salFstockoutqty; // 累计出库数量
    private double salFcanoutqty; // 可出数量
    private int isCheck; // 新加的是否选中

    public int getfId() {
        return fId;
    }
    public String getFbillno() {
        return fbillno;
    }
    public String getFbillType() {
        return fbillType;
    }
    public String getSalDate() {
        return salDate;
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
    public int getSalOrgId() {
        return salOrgId;
    }
    public String getSalOrgName() {
        return salOrgName;
    }
    public Organization getSalOrg() {
        return salOrg;
    }
    public int getInventoryOrgId() {
        return inventoryOrgId;
    }
    public String getInventoryOrgName() {
        return inventoryOrgName;
    }
    public Organization getInventoryOrg() {
        return inventoryOrg;
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
    public String getMtlUnitName() {
        return mtlUnitName;
    }
    public double getSalFqty() {
        return salFqty;
    }
    public double getSalFstockoutqty() {
        return salFstockoutqty;
    }
    public double getSalFcanoutqty() {
        return salFcanoutqty;
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
    public void setSalDate(String salDate) {
        this.salDate = salDate;
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
    public void setSalOrgId(int salOrgId) {
        this.salOrgId = salOrgId;
    }
    public void setSalOrgName(String salOrgName) {
        this.salOrgName = salOrgName;
    }
    public void setSalOrg(Organization salOrg) {
        this.salOrg = salOrg;
    }
    public void setInventoryOrgId(int inventoryOrgId) {
        this.inventoryOrgId = inventoryOrgId;
    }
    public void setInventoryOrgName(String inventoryOrgName) {
        this.inventoryOrgName = inventoryOrgName;
    }
    public void setInventoryOrg(Organization inventoryOrg) {
        this.inventoryOrg = inventoryOrg;
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
    public void setMtlUnitNamee(String mtlUnitName) {
        this.mtlUnitName = mtlUnitName;
    }
    public void setSalFqty(double salFqty) {
        this.salFqty = salFqty;
    }
    public void setSalFstockoutqty(double salFstockoutqty) {
        this.salFstockoutqty = salFstockoutqty;
    }
    public void setSalFcanoutqty(double salFcanoutqty) {
        this.salFcanoutqty = salFcanoutqty;
    }
    public int getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(int isCheck) {
        this.isCheck = isCheck;
    }

    public SalOrder() {
        super();
    }
    @Override
    public String toString() {
        return "SalOrder [fId=" + fId + ", fbillno=" + fbillno + ", fbillType=" + fbillType + ", salDate=" + salDate
                + ", custId=" + custId + ", custNumber=" + custNumber + ", custName=" + custName + ", salOrgId="
                + salOrgId + ", salOrgName=" + salOrgName + ", salOrg=" + salOrg + ", inventoryOrgId=" + inventoryOrgId
                + ", inventoryOrgName=" + inventoryOrgName + ", inventoryOrg=" + inventoryOrg + ", mtlId=" + mtlId
                + ", mtl=" + mtl + ", mtlFnumber=" + mtlFnumber + ", mtlFname=" + mtlFname + ", mtlUnitName=" + mtlUnitName
                + ", salFqty=" + salFqty + ", salFstockoutqty=" + salFstockoutqty + ", salFcanoutqty=" + salFcanoutqty
                + "]";
    }
}
