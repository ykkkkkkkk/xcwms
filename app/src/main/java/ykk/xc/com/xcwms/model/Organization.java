package ykk.xc.com.xcwms.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 客户表t_Orgnaization
 */
public class Organization implements Serializable {
    /*组织id*/
    private Integer id ;
    /*k3组织id*/
    private Integer fOrganiztionId;
    /*组织编码*/
    private String number;
    /*组织名称*/
    private String name;
    /*K3数据状态*/
    private String dataStatus;
    /*wms非物理删除标识*/
    private String isDelete;
    /*k3是否禁用*/
    private String enabled;
    /**
     * seteer/getter方法
     * @return
     */
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getNumber() {
        return number;
    }
    public void setNumber(String number) {
        this.number = number;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
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
    public Integer getfOrganiztionId() {
        return fOrganiztionId;
    }
    public void setfOrganiztionId(Integer fOrganiztionId) {
        this.fOrganiztionId = fOrganiztionId;
    }

    public Organization() {
        super();
    }

}
