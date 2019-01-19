package ykk.xc.com.xcwms.model;

import java.io.Serializable;

/**
 * @author qxp 2018年11月23日 下午2:12:29
 * @Description:计价工资表
 */
public class ValuationPayroll implements Serializable {

    private int id;
    /* 班次id */
    private int collectiveId;
    /* 员工id */
    private int staffId;
    /* 部门id */
    private int deptId;
    /* 物料id */
    private int fMaterialId;
    /* 工艺路线id */
    private int processflowId;
    /* 工序id */
    private int procedureId;
    /* 总数量 */
    private int totalNumber;
    /* 计件类型id */
    private int valuationTypeId;
    /* 计时工作项目id */
    private int jobId;
    /* 物料对应工序的单位时间 ， */
    private double jobTime;
    /* 已分配数量 */
    private int assign;
    /* 未分配数量 */
    private int notAssign;
    /* 创建时间 */
    private String createDate;
    /* 创建人id */
    private int createrId;
    /* 创建人名称 */
    private String createrName;
    /* 总金额 */
    private double totalMoney;
    /* 工时单价。取baseSalaryDate中的工时单价 */
    private double uPrice;
    /* 用于临时存值，totalNumber乘以jobTime的时间 */
    private double workTime;
    /* 物料barCode */
    private String barCode;
    /* 工艺路线下的工序顺序号 */
    private int sequent;

    /* 物料产品类 */
    private Material material;
    /* 员工类 */
    private Staff staff;
    /* 部门类 */
    private Department department;
    /* 工序类 */
    private Procedure procedure;
    /* 计件类型 */
    private ValuationType valuationType;
    /* 工序工时类 */
    private MaterialProcedureTime materialProcedureTime;
    /* 计时工作项目类 */
    private TimingJob timingJob;
    /* 集体列 */
    private Collective collective;
    /* 工序工时entry类 */
    private MaterialProcedureTimeEntry materialProcedureTimeEntry;
    /* 工艺路线实体类 */
    private Processflow processflow;

    /* 单据状态，0为作废。默认为1正常 */
    private int valState;
    /* 单据来源。0WMS手动创建，1扫码来源创建 */
    private int createWay;
    /* 分配状态，0无需分配，1,未分配，2已分配 */
    private int assignState;
    /*生产订单号，用于前端显示*/
    private String prodBillNo;
    /*工序名称，用于前端显示*/
    private String procedureName;
    /*物料代码，用于前端显示*/
    private String materialNumber;
    /*物料名称，用于前端显示*/
    private String materialName;


    public Processflow getProcessflow() {
        return processflow;
    }

    public void setProcessflow(Processflow processflow) {
        this.processflow = processflow;
    }

    public ValuationPayroll() {
        super();
    }

    public MaterialProcedureTime getMaterialProcedureTime() {
        return materialProcedureTime;
    }

    public void setMaterialProcedureTime(MaterialProcedureTime materialProcedureTime) {
        this.materialProcedureTime = materialProcedureTime;
    }

    public MaterialProcedureTimeEntry getMaterialProcedureTimeEntry() {
        return materialProcedureTimeEntry;
    }

    public void setMaterialProcedureTimeEntry(MaterialProcedureTimeEntry materialProcedureTimeEntry) {
        this.materialProcedureTimeEntry = materialProcedureTimeEntry;
    }

    public Collective getCollective() {
        return collective;
    }

    public void setCollective(Collective collective) {
        this.collective = collective;
    }

    public TimingJob getTimingJob() {
        return timingJob;
    }

    public void setTimingJob(TimingJob timingJob) {
        this.timingJob = timingJob;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public Staff getStaff() {
        return staff;
    }

    public void setStaff(Staff staff) {
        this.staff = staff;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Procedure getProcedure() {
        return procedure;
    }

    public void setProcedure(Procedure procedure) {
        this.procedure = procedure;
    }

    public ValuationType getValuationType() {
        return valuationType;
    }

    public void setValuationType(ValuationType valuationType) {
        this.valuationType = valuationType;
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

    public int getDeptId() {
        return deptId;
    }

    public void setDeptId(int deptId) {
        this.deptId = deptId;
    }

    public int getfMaterialId() {
        return fMaterialId;
    }

    public void setfMaterialId(int fMaterialId) {
        this.fMaterialId = fMaterialId;
    }

    public int getProcedureId() {
        return procedureId;
    }

    public void setProcedureId(int procedureId) {
        this.procedureId = procedureId;
    }

    public int getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(int totalNumber) {
        this.totalNumber = totalNumber;
    }

    public int getValuationTypeId() {
        return valuationTypeId;
    }

    public void setValuationTypeId(int valuationTypeId) {
        this.valuationTypeId = valuationTypeId;
    }

    public int getAssign() {
        return assign;
    }

    public void setAssign(int assign) {
        this.assign = assign;
    }

    public int getNotAssign() {
        return notAssign;
    }

    public void setNotAssign(int notAssign) {
        this.notAssign = notAssign;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
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

    public int getValState() {
        return valState;
    }

    public void setValState(int valState) {
        this.valState = valState;
    }

    public int getCreateWay() {
        return createWay;
    }

    public void setCreateWay(int createWay) {
        this.createWay = createWay;
    }

    public double getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(double totalMoney) {
        this.totalMoney = totalMoney;
    }

    public int getAssignState() {
        return assignState;
    }

    public void setAssignState(int assignState) {
        this.assignState = assignState;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public double getJobTime() {
        return jobTime;
    }

    public void setJobTime(double jobTime) {
        this.jobTime = jobTime;
    }

    public double getuPrice() {
        return uPrice;
    }

    public void setuPrice(double uPrice) {
        this.uPrice = uPrice;
    }

    public double getWorkTime() {
        return workTime;
    }

    public void setWorkTime(double workTime) {
        this.workTime = workTime;
    }

    public int getProcessflowId() {
        return processflowId;
    }

    public void setProcessflowId(int processflowId) {
        this.processflowId = processflowId;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public int getSequent() {
        return sequent;
    }

    public void setSequent(int sequent) {
        this.sequent = sequent;
    }

    public String getProdBillNo() {
        return prodBillNo;
    }

    public void setProdBillNo(String prodBillNo) {
        this.prodBillNo = prodBillNo;
    }

    public String getProcedureName() {
        return procedureName;
    }

    public void setProcedureName(String procedureName) {
        this.procedureName = procedureName;
    }

    public String getMaterialNumber() {
        return materialNumber;
    }

    public void setMaterialNumber(String materialNumber) {
        this.materialNumber = materialNumber;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }
}