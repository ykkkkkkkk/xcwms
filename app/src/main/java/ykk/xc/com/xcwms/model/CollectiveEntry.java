package ykk.xc.com.xcwms.model;

import java.io.Serializable;

/**
 * @Description:集体成员
 *
 * @author qxp 2019年1月8日 上午9:48:27
 */
public class CollectiveEntry implements Serializable {

	private int id;
	/* 集体id */
	private int collectiveId;
	/* 员工id */
	private int staffId;

	/* 差额分配系数 */
	private double assignModulus;

	/* 分摊系数 */
	private double ApportionModulus;

	private Staff staff;

	private Department department;

	public CollectiveEntry() {
		super();
	}

	public CollectiveEntry(int id, int collectiveId, int staffId, double assignModulus) {
		super();
		this.id = id;
		this.collectiveId = collectiveId;
		this.staffId = staffId;
		this.assignModulus = assignModulus;
	}

	public Department getDepartment() {
		return department;
	}

	public void setDepartment(Department department) {
		this.department = department;
	}

	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCollectiveId() {
		return collectiveId;
	}

	public void setCollectiveId(int collectiveId) {
		this.collectiveId = collectiveId;
	}

	public int getStaffId() {
		return staffId;
	}

	public void setStaffId(int staffId) {
		this.staffId = staffId;
	}

	public double getAssignModulus() {
		return assignModulus;
	}

	public void setAssignModulus(double assignModulus) {
		this.assignModulus = assignModulus;
	}

	public double getApportionModulus() {
		return ApportionModulus;
	}

	public void setApportionModulus(double apportionModulus) {
		ApportionModulus = apportionModulus;
	}

	@Override
	public String toString() {
		return "CollectiveEntry [id=" + id + ", collectiveId=" + collectiveId + ", staffId=" + staffId
				+ ", assignModulus=" + assignModulus + ", staff=" + staff + ", department=" + department + "]";
	}

}
