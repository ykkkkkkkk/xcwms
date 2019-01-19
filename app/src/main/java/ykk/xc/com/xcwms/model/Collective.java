package ykk.xc.com.xcwms.model;


import java.io.Serializable;

/**
 * @Description:工资管理——集体
 *
 * @author qxp 2019年1月8日 上午9:42:27
 */
public class Collective implements Serializable {

	private int id;
	/* 集体名称 */
	private String collectiveName;
	/* 班组id */
	private int empId;
	/* 工序 */
	private int procedureId;

	private String createDate;

	private String fModifyDate;

	private int createrId;

	private String createrName;

	/*用于查询时记录集体的汇总工时，只是临时保存汇总工时使用，不用在数据库里加表*/
	private double totalWorkHours;

	/* 班组类 */
	private EmployeeTeam emp;
	/* 工序类 */
	private Procedure procedure;

	public Collective() {
		super();
	}

	public Collective(int id, String collectiveName, int empId, int procedureId, String createDate, String fModifyDate,
					  int createrId, String createrName, EmployeeTeam emp, Procedure procedure) {
		super();
		this.id = id;
		this.collectiveName = collectiveName;
		this.empId = empId;
		this.procedureId = procedureId;
		this.createDate = createDate;
		this.fModifyDate = fModifyDate;
		this.createrId = createrId;
		this.createrName = createrName;
		this.emp = emp;
		this.procedure = procedure;
	}

	public Procedure getProcedure() {
		return procedure;
	}

	public void setProcedure(Procedure procedure) {
		this.procedure = procedure;
	}

	public EmployeeTeam getEmp() {
		return emp;
	}

	public void setEmp(EmployeeTeam emp) {
		this.emp = emp;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCollectiveName() {
		return collectiveName;
	}

	public void setCollectiveName(String collectiveName) {
		this.collectiveName = collectiveName;
	}

	public int getEmpId() {
		return empId;
	}

	public void setEmpId(int empId) {
		this.empId = empId;
	}

	public int getProcedureId() {
		return procedureId;
	}

	public void setProcedureId(int procedureId) {
		this.procedureId = procedureId;
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

	public void setCreaterId(int createrId) {
		this.createrId = createrId;
	}


	public String getCreaterName() {
		return createrName;
	}

	public void setCreaterName(String createrName) {
		this.createrName = createrName;
	}

	public double getTotalWorkHours() {
		return totalWorkHours;
	}

	public void setTotalWorkHours(double totalWorkHours) {
		this.totalWorkHours = totalWorkHours;
	}

	public int getCreaterId() {
		return createrId;
	}

}
