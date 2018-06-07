package ykk.xc.com.xcwms.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 用户   (t_user)
 */
public class User implements Parcelable {
    private Integer id;
    private String username;
    private String password;
    private Integer sex;
    private String turename;
    private Integer deptId;
    private String createTime;
    private Integer createrId;
    private String createrName;
    private Department department;
    private String kd_username;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getTurename() {
        return turename;
    }

    public void setTurename(String turename) {
        this.turename = turename;
    }

    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getCreaterId() {
        return createrId;
    }

    public void setCreaterId(Integer createrId) {
        this.createrId = createrId;
    }

    public String getCreaterName() {
        return createrName;
    }

    public void setCreaterName(String createrName) {
        this.createrName = createrName;
    }

    public Department getDept() {
        return department;
    }

    public void setDept(Department department) {
        this.department = department;
    }
    public String getKd_username() {
        return kd_username;
    }

    public void setKd_username(String kd_username) {
        this.kd_username = kd_username;
    }

    public User() {
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
    public User(Parcel source) {
        id = source.readInt();
        username = source.readString();
        password = source.readString();
        sex = source.readInt();
        turename = source.readString();
        deptId = source.readInt();
        createTime = source.readString();
        createrId = source.readInt();
        createrName = source.readString();
        department = source.readParcelable(Department.class.getClassLoader());
        kd_username = source.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(username);
        dest.writeString(password);
        dest.writeInt(sex);
        dest.writeString(turename);
        dest.writeInt(deptId);
        dest.writeString(createTime);
        dest.writeInt(createrId);
        dest.writeString(createrName);
        dest.writeParcelable(department, flags);
        dest.writeString(kd_username);
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        /**
         * 供外部类反序列化本类数组使用
         */
        @Override
        public User[] newArray(int size) {
            return new User[size];
        }

        /**
         * 从Parcel中读取数据
         */
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }
    };

}
