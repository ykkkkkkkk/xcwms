package ykk.xc.com.xcwms.model;

import java.io.Serializable;

/**
 * @author qxp 2018年12月13日 下午2:23:44
 * @Description:计时工作项目
 */
public class TimingJob implements Serializable {

    private int id;
    /* 工作项目编码 */
    private String jobNumber;
    /* 工作项目名称 */
    private String jobName;
    /* 工作描述 */
    private String jobDescription;
    /* 单价（元/小时）。此属性已舍弃，计时单价不在此处计算--2019.01.16 by qxp*/
    private double jobPrice;
    /* 创建时间 */
    private String createDate;
    /* 修改时间 */
    private String fModifyDate;
    /* 创者id */
    private int createrId;
    /* 创建者姓名 */
    private String createrName;

    public TimingJob() {
        super();
    }

    public TimingJob(int id, String jobNumber, String jobName, String jobDescription, double jobPrice,
                     String createDate, String fModifyDate, int createrId, String createrName) {
        super();
        this.id = id;
        this.jobNumber = jobNumber;
        this.jobName = jobName;
        this.jobDescription = jobDescription;
        this.jobPrice = jobPrice;
        this.createDate = createDate;
        this.fModifyDate = fModifyDate;
        this.createrId = createrId;
        this.createrName = createrName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJobNumber() {
        return jobNumber;
    }

    public void setJobNumber(String jobNumber) {
        this.jobNumber = jobNumber;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getfModifyDate() {
        return fModifyDate;
    }

    public void setfModifyDate(String fModifyDate) {
        this.fModifyDate = fModifyDate;
    }

    public int getCreaterId() {
        return createrId;
    }

    public void setCreaterId(int createrId) {
        this.createrId = createrId;
    }

    public String getCreaterName() {
        return createrName;
    }

    public void setCreaterName(String createrName) {
        this.createrName = createrName;
    }

    public double getJobPrice() {
        return jobPrice;
    }

    public void setJobPrice(double jobPrice) {
        this.jobPrice = jobPrice;
    }

    @Override
    public String toString() {
        return "TimingJob [id=" + id + ", jobNumber=" + jobNumber + ", jobName=" + jobName + ", jobDescription="
                + jobDescription + ", jobPrice=" + jobPrice + ", createDate=" + createDate + ", fModifyDate="
                + fModifyDate + ", createrId=" + createrId + ", createrName=" + createrName + "]";
    }

}