package ykk.xc.com.xcwms.model;


import java.io.Serializable;

public class MaterialProcedureTimeEntry implements Serializable {

    private int id;
    /* 表头id */
    private int mptId;
    /* 工序id */
    private int procedureId;
    /* 工时 */
    private double workTime;
    /* 工时单价，取当前t_baseSalaryDate中工时单价保存 */
    private double uPrice;

    /* 小计金额，暂不存库，仅用于前端显示 */
    private double subtotalAmount;

    private MaterialProcedureTime materialProcedureTime;

    private Procedure procedure;

    // 临时字段，不存表
    private int seqNo;

    public MaterialProcedureTime getMaterialProcedureTime() {
        return materialProcedureTime;
    }

    public void setMaterialProcedureTime(MaterialProcedureTime materialProcedureTime) {
        this.materialProcedureTime = materialProcedureTime;
    }

    public MaterialProcedureTimeEntry() {
        super();
    }

    public Procedure getProcedure() {
        return procedure;
    }

    public void setProcedure(Procedure procedure) {
        this.procedure = procedure;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMptId() {
        return mptId;
    }

    public void setMptId(int mptId) {
        this.mptId = mptId;
    }

    public int getProcedureId() {
        return procedureId;
    }

    public void setProcedureId(int procedureId) {
        this.procedureId = procedureId;
    }

    public double getWorkTime() {
        return workTime;
    }

    public void setWorkTime(double workTime) {
        this.workTime = workTime;
    }

    public double getSubtotalAmount() {
        return subtotalAmount;
    }

    public void setSubtotalAmount(double subtotalAmount) {
        this.subtotalAmount = subtotalAmount;
    }

    public double getuPrice() {
        return uPrice;
    }

    public void setuPrice(double uPrice) {
        this.uPrice = uPrice;
    }

    public int getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(int seqNo) {
        this.seqNo = seqNo;
    }

}
