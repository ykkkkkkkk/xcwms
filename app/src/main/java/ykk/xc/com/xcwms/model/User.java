package ykk.xc.com.xcwms.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * 用户   (t_user)
 */
public class User implements Serializable {
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

}
