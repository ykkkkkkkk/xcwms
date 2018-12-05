package ykk.xc.com.xcwms.model;

import java.io.Serializable;

/**
 * @Description:排班表体，
 *
 * @author qxp 2018年11月22日 下午2:49:25
 */
public class SchedulTeamEntry implements Serializable {

	private int id;

	/* 排班id */
	private int schedulTeamId;
	/* 员工id */
	private int staffId;
	private Staff staff;
	private Department department;

	/*用于计价工资列表显示分配数量*/
	private int valNumber;

	public SchedulTeamEntry() {
		super();
	}

	public SchedulTeamEntry(int id, int schedulTeamId, int staffId) {
		super();
		this.id = id;
		this.schedulTeamId = schedulTeamId;
		this.staffId = staffId;
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


	public int getValNumber() {
		return valNumber;
	}

	public void setValNumber(int valNumber) {
		this.valNumber = valNumber;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSchedulTeamId() {
		return schedulTeamId;
	}

	public void setSchedulTeamId(int schedulTeamId) {
		this.schedulTeamId = schedulTeamId;
	}

	public int getStaffId() {
		return staffId;
	}

	public void setStaffId(int staffId) {
		this.staffId = staffId;
	}

	@Override
	public String toString() {
		return "SchedulTeamEntry [id=" + id + ", schedulTeamId=" + schedulTeamId + ", staffId=" + staffId + ", staff="
				+ staff + "]";
	}

}