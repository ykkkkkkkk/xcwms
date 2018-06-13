package ykk.xc.com.xcwms.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 部门表t_Department
 */
public class Department implements Parcelable {
    private int id;
    //K3部门id
    private int fitemID;
    //部门条码
    private String barcode;
    //K3部门编码
    private String departmentNumber;
    //K3部门名称
    private String departmentName;
    //K3部门使用组织id
    private String departmentUseOrgId;
    //K3创建组织编码
    private String foundDepartment;
    /*K3数据状态*/
    private String dataStatus;
    /*wms非物理删除标识*/
    private String isDelete;
    /*k3是否禁用*/
    private String enabled;
    //K3修改日期
    private String fModifyDate;

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getFitemID() {
        return fitemID;
    }
    public void setFitemID(int fitemID) {
        this.fitemID = fitemID;
    }
    public String getBarcode() {
        return barcode;
    }
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
    public String getDepartmentNumber() {
        return departmentNumber;
    }
    public void setDepartmentNumber(String departmentNumber) {
        this.departmentNumber = departmentNumber;
    }
    public String getDepartmentName() {
        return departmentName;
    }
    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
    public String getDepartmentUseOrgId() {
        return departmentUseOrgId;
    }
    public void setDepartmentUseOrgId(String departmentUseOrgId) {
        this.departmentUseOrgId = departmentUseOrgId;
    }
    public String getFoundDepartment() {
        return foundDepartment;
    }
    public void setFoundDepartment(String foundDepartment) {
        this.foundDepartment = foundDepartment;
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
    public String getfModifyDate() {
        return fModifyDate;
    }
    public void setfModifyDate(String fModifyDate) {
        this.fModifyDate = fModifyDate;
    }

    public Department() {
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
     * @param p
     */
    public Department(Parcel p) {
        id = p.readInt();
        fitemID = p.readInt();
        barcode = p.readString();
        departmentNumber = p.readString();
        departmentName = p.readString();
        departmentUseOrgId = p.readString();
        foundDepartment = p.readString();
        dataStatus = p.readString();
        isDelete = p.readString();
        enabled = p.readString();
        fModifyDate = p.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel p, int flags) {
        p.writeInt(id);
        p.writeInt(fitemID);
        p.writeString(barcode);
        p.writeString(departmentNumber);
        p.writeString(departmentName);
        p.writeString(departmentUseOrgId);
        p.writeString(foundDepartment);
        p.writeString(dataStatus);
        p.writeString(isDelete);
        p.writeString(enabled);
        p.writeString(fModifyDate);
    }

    public static final Creator<Department> CREATOR = new Creator<Department>() {
        /**
         * 供外部类反序列化本类数组使用
         */
        @Override
        public Department[] newArray(int size) {
            return new Department[size];
        }

        /**
         * 从Parcel中读取数据
         */
        @Override
        public Department createFromParcel(Parcel source) {
            return new Department(source);
        }
    };

}
