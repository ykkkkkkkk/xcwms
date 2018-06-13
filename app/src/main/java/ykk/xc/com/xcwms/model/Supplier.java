package ykk.xc.com.xcwms.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 供应商表t_Supplier
 */
public class Supplier implements Parcelable {
    /*id*/
    private Integer id;
    /*供应商id*/
    private Integer fsupplierid;
    /*供应商编码*/
    private String fNumber;
    /*供应商名称*/
    private String fName;
    /*创建时间*/
    private String fCreateDate;
    /*修改时间*/
    private String fModifyDate;
    /*创建组织编码*/
    private String orgFnumber;
    /*使用组织编码*/
    private String uorgFnumber;
    /*K3数据状态*/
    private String dataStatus;
    /*wms非物理删除标识*/
    private String isDelete;
    /*k3是否禁用*/
    private String enabled;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getFsupplierid() {
        return fsupplierid;
    }
    public void setFsupplierid(Integer fsupplierid) {
        this.fsupplierid = fsupplierid;
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
    public String getfCreateDate() {
        return fCreateDate;
    }
    public void setfCreateDate(String fCreateDate) {
        this.fCreateDate = fCreateDate;
    }
    public String getfModifyDate() {
        return fModifyDate;
    }
    public void setfModifyDate(String fModifyDate) {
        this.fModifyDate = fModifyDate;
    }
    public String getOrgFnumber() {
        return orgFnumber;
    }
    public void setOrgFnumber(String orgFnumber) {
        this.orgFnumber = orgFnumber;
    }
    public String getUorgFnumber() {
        return uorgFnumber;
    }
    public void setUorgFnumber(String uorgFnumber) {
        this.uorgFnumber = uorgFnumber;
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

    public Supplier() {
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
     * @param source
     */
    public Supplier(Parcel source) {
        id = source.readInt();
        fsupplierid = source.readInt();
        fNumber = source.readString();
        fName = source.readString();
        fCreateDate = source.readString();
        fModifyDate = source.readString();
        orgFnumber = source.readString();
        uorgFnumber = source.readString();
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
        dest.writeInt(fsupplierid);
        dest.writeString(fNumber);
        dest.writeString(fName);
        dest.writeString(fCreateDate);
        dest.writeString(fModifyDate);
        dest.writeString(orgFnumber);
        dest.writeString(uorgFnumber);
        dest.writeString(dataStatus);
        dest.writeString(isDelete);
        dest.writeString(enabled);
    }

    public static final Creator<Supplier> CREATOR = new Creator<Supplier>() {
        /**
         * 供外部类反序列化本类数组使用
         */
        @Override
        public Supplier[] newArray(int size) {
            return new Supplier[size];
        }

        /**
         * 从Parcel中读取数据
         */
        @Override
        public Supplier createFromParcel(Parcel source) {
            return new Supplier(source);
        }
    };

}
