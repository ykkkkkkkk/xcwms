package ykk.xc.com.xcwms.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 客户表t_Orgnaization
 */
public class Organization implements Parcelable {
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
    public Organization(Parcel p) {
        id = p.readInt();
        fOrganiztionId = p.readInt();
        number = p.readString();
        name = p.readString();
        dataStatus = p.readString();
        isDelete = p.readString();
        enabled = p.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel p, int flags) {
        p.writeInt(id);
        p.writeInt(fOrganiztionId);
        p.writeString(number);
        p.writeString(name);
        p.writeString(dataStatus);
        p.writeString(isDelete);
        p.writeString(enabled);
    }

    public static final Creator<Organization> CREATOR = new Creator<Organization>() {
        /**
         * 供外部类反序列化本类数组使用
         */
        @Override
        public Organization[] newArray(int size) {
            return new Organization[size];
        }

        /**
         * 从Parcel中读取数据
         */
        @Override
        public Organization createFromParcel(Parcel source) {
            return new Organization(source);
        }
    };

}
